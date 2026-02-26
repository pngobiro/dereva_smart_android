package com.dereva.smart.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.PaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import java.util.*

private val Context.paymentDataStore: DataStore<Preferences> by preferencesDataStore(name = "payment_prefs")

class PaymentRepositoryImpl(
    private val context: Context,
    private val apiService: DerevaApiService
) : PaymentRepository {
    
    companion object {
        private val CHECKOUT_REQUEST_ID_KEY = stringPreferencesKey("checkout_request_id")
        private val PAYMENT_REQUEST_ID_KEY = stringPreferencesKey("payment_request_id")
    }
    
    override suspend fun getAvailablePlans(): Result<List<SubscriptionPlan>> = runCatching {
        val response = apiService.getPaymentConfig()
        if (response.isSuccessful && response.body()?.success == true) {
            val config = response.body()!!
            listOf(
                SubscriptionPlan(
                    tier = SubscriptionTier.MONTHLY,
                    name = "Monthly Subscription",
                    price = config.monthlyPrice,
                    features = config.features,
                    durationDays = config.durationDays
                )
            )
        } else {
            throw Exception("Failed to load subscription plans")
        }
    }
    
    override suspend fun initiatePayment(
        userId: String,
        phoneNumber: String,
        plan: SubscriptionPlan,
        promoCode: String?,
        referralCode: String?,
        schoolId: String?
    ): Result<PaymentRequest> = runCatching {
        val finalAmount = plan.price
        val paymentRequest = PaymentRequest(
            id = UUID.randomUUID().toString(),
            userId = userId,
            amount = finalAmount,
            phoneNumber = phoneNumber,
            subscriptionTier = plan.tier,
            promoCode = promoCode,
            referralCode = referralCode,
            schoolId = schoolId,
            createdAt = Date(),
            expiresAt = Date(System.currentTimeMillis() + 5 * 60 * 1000)
        )
        
        val formattedPhone = if (phoneNumber.startsWith("0")) "254${phoneNumber.drop(1)}" else phoneNumber
        
        val dto = com.dereva.smart.data.remote.dto.PaymentRequestDto(
            id = paymentRequest.id,
            phone = formattedPhone,
            amount = finalAmount,
            userId = userId,
            type = "monthly"
        )
        apiService.initiatePayment(dto, "Bearer DUMMY")
        
        paymentRequest
    }

    override suspend fun checkPaymentStatus(requestId: String, checkoutRequestId: String): Result<PaymentResult> = runCatching {
        val response = apiService.getPaymentStatus(requestId)
        
        if (!response.isSuccessful || response.body() == null) {
            return@runCatching PaymentResult(
                id = UUID.randomUUID().toString(),
                paymentRequestId = requestId,
                userId = "unknown",
                amount = 0.0,
                mpesaReceiptNumber = null,
                transactionId = null,
                status = PaymentStatus.PROCESSING,
                completedAt = null
            )
        }
        
        val statusResponse = response.body()!!
        
        PaymentResult(
            id = UUID.randomUUID().toString(),
            paymentRequestId = requestId,
            userId = statusResponse.userId ?: "unknown",
            amount = statusResponse.amount ?: 0.0,
            mpesaReceiptNumber = statusResponse.mpesaReceiptNumber,
            transactionId = statusResponse.mpesaReceiptNumber ?: requestId,
            status = when (statusResponse.status.lowercase()) {
                "completed", "successful", "success" -> PaymentStatus.COMPLETED
                "failed", "error" -> PaymentStatus.FAILED
                "cancelled" -> PaymentStatus.CANCELLED
                else -> PaymentStatus.PROCESSING
            },
            completedAt = statusResponse.completedAt?.let { Date(it) }
        )
    }
    
    override suspend fun pollPaymentStatus(
        requestId: String,
        checkoutRequestId: String,
        maxAttempts: Int,
        delayMillis: Long
    ): Result<PaymentResult> = runCatching {
        val dto = com.dereva.smart.data.remote.dto.LinkCheckoutRequestDto(id = requestId, checkout_request_id = checkoutRequestId)
        apiService.linkCheckout(dto, "Bearer DUMMY")

        repeat(maxAttempts) { attempt ->
            val result = checkPaymentStatus(requestId, checkoutRequestId)
            if (result.isSuccess) {
                val paymentResult = result.getOrThrow()
                if (paymentResult.status != PaymentStatus.PENDING && paymentResult.status != PaymentStatus.PROCESSING) {
                    return@runCatching paymentResult
                }
            }
            if (attempt < maxAttempts - 1) delay(delayMillis)
        }
        throw Exception("Payment status check timed out")
    }
    
    override suspend fun getActiveSubscription(userId: String): Result<UserSubscription?> = Result.success(null)
    
    override fun getActiveSubscriptionFlow(userId: String): Flow<UserSubscription?> = flowOf(null)
    
    override suspend fun getUserSubscriptions(userId: String): Result<List<UserSubscription>> = Result.success(emptyList())
    
    override suspend fun updateAutoRenew(subscriptionId: String, autoRenew: Boolean): Result<Unit> = Result.success(Unit)
    
    override suspend fun cancelSubscription(userId: String): Result<Unit> = Result.success(Unit)
    
    override suspend fun getPaymentHistory(userId: String): Result<List<PaymentHistory>> = runCatching {
        val response = apiService.getPayments(userId)
        if (!response.isSuccessful) return@runCatching emptyList()
        
        response.body()?.map { dto ->
            PaymentHistory(
                id = dto.id,
                userId = dto.userId ?: userId,
                amount = dto.amount ?: 0.0,
                status = try { PaymentStatus.valueOf(dto.status.uppercase()) } catch(e: Exception) { PaymentStatus.PROCESSING },
                subscriptionTier = SubscriptionTier.MONTHLY,
                mpesaReceiptNumber = dto.mpesaReceiptNumber,
                date = dto.completedAt?.let { Date(it) } ?: Date(),
                promoCode = null,
                referralCode = null
            )
        } ?: emptyList()
    }
    
    override suspend fun validatePromoCode(code: String): Result<PromoCode> = Result.failure(Exception("Not implemented"))
    
    override suspend fun applyPromoCode(code: String, originalAmount: Double): Result<Double> = Result.success(originalAmount)
    
    override suspend fun getUserReferralCode(userId: String): Result<ReferralCode?> = Result.success(null)
    
    override suspend fun generateReferralCode(userId: String): Result<ReferralCode> = Result.failure(Exception("Not implemented"))
    
    override suspend fun validateReferralCode(code: String): Result<ReferralCode> = Result.failure(Exception("Not implemented"))
    
    override suspend fun applyReferralCode(code: String, originalAmount: Double): Result<Double> = Result.success(originalAmount)
    
    override suspend fun calculateSchoolCommission(schoolId: String, paymentAmount: Double): Result<SchoolCommission> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getSchoolCommissions(schoolId: String): Result<List<SchoolCommission>> = Result.success(emptyList())
    
    override suspend fun savePendingPayment(requestId: String, checkoutRequestId: String) {
        context.paymentDataStore.edit { prefs ->
            prefs[PAYMENT_REQUEST_ID_KEY] = requestId
            prefs[CHECKOUT_REQUEST_ID_KEY] = checkoutRequestId
        }
    }

    override suspend fun getPendingPayment(): Pair<String?, String?> {
        val prefs = context.paymentDataStore.data.first()
        return Pair(prefs[PAYMENT_REQUEST_ID_KEY], prefs[CHECKOUT_REQUEST_ID_KEY])
    }

    override suspend fun clearPendingPayment() {
        context.paymentDataStore.edit { prefs ->
            prefs.remove(PAYMENT_REQUEST_ID_KEY)
            prefs.remove(CHECKOUT_REQUEST_ID_KEY)
        }
    }
}
