package com.dereva.smart.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import com.dereva.smart.data.local.dao.PaymentDao
import com.dereva.smart.data.local.entity.*
import com.dereva.smart.data.remote.MpesaService
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.PaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*

import com.dereva.smart.data.remote.DerevaApiService

private val Context.paymentDataStore: DataStore<Preferences> by preferencesDataStore(name = "payment_prefs")

class PaymentRepositoryImpl(
    private val context: Context,
    private val paymentDao: PaymentDao,
    private val mpesaService: MpesaService,
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
            throw Exception("Failed to load subscription plans: ${response.message()}")
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
        var finalAmount = plan.price
        
        // Apply promo code discount
        if (promoCode != null) {
            val discountResult = applyPromoCode(promoCode, finalAmount)
            if (discountResult.isSuccess) {
                finalAmount = discountResult.getOrThrow()
            }
        }
        
        // Apply referral code discount
        if (referralCode != null) {
            val discountResult = applyReferralCode(referralCode, finalAmount)
            if (discountResult.isSuccess) {
                finalAmount = discountResult.getOrThrow()
            }
        }
        
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
            expiresAt = Date(System.currentTimeMillis() + 5 * 60 * 1000) // 5 minutes
        )
        
        paymentDao.insertPaymentRequest(paymentRequest.toEntity())
        
        val formattedPhone = if (phoneNumber.startsWith("0")) "254${phoneNumber.drop(1)}" else phoneNumber
        
        // Notify backend of pending payment before UI prompts STK push
        try {
            val token = "Bearer DUMMY_TOKEN" // Replace with actual token retrieval if needed
            val dto = com.dereva.smart.data.remote.dto.PaymentRequestDto(
                id = paymentRequest.id,
                phone = formattedPhone,
                amount = finalAmount,
                userId = userId,
                type = "subscription"
            )
            val apiRes = apiService.initiatePayment(dto, token)
            if (!apiRes.isSuccessful) {
                // We'll proceed locally, but logging the error might be helpful
                Timber.e("Backend initiate payment failed: ${apiRes.code()}")
            }
        } catch (e: Exception) {
            Timber.e("Failed to post payment initiation to backend", e)
        }
        
        // Return the prepared request so the UI can launch Mpesa STK Push via bdhobare SDK
        paymentRequest
    }

    override suspend fun checkPaymentStatus(requestId: String, checkoutRequestId: String): Result<PaymentResult> = runCatching {
        val request = paymentDao.getPaymentRequest(requestId)
            ?: throw Exception("Payment request not found")
        
        val statusResult = apiService.getPaymentStatus(requestId)
        
        if (!statusResult.isSuccessful || statusResult.body() == null) {
            val errorBody = statusResult.errorBody()?.string()
            if (statusResult.code() == 404) {
                 // Payment record doesn't exist yet or is pending. We treat it as PROCESSING so it continues to poll.
                 return@runCatching PaymentResult(
                    id = UUID.randomUUID().toString(),
                    paymentRequestId = requestId,
                    userId = request.userId,
                    amount = request.amount,
                    mpesaReceiptNumber = null,
                    transactionId = null,
                    status = PaymentStatus.PROCESSING,
                    errorMessage = null,
                    completedAt = null,
                    metadata = null
                )
            }
            throw Exception("Failed to check payment status on backend: ${statusResult.code()} $errorBody")
        }
        
        val statusResponse = statusResult.body()!!
        
        val paymentResult = PaymentResult(
            id = UUID.randomUUID().toString(),
            paymentRequestId = requestId,
            userId = request.userId,
            amount = request.amount,
            mpesaReceiptNumber = statusResponse.mpesaReceiptNumber,
            transactionId = statusResponse.mpesaReceiptNumber ?: requestId,
            status = when (statusResponse.status.lowercase()) {
                "completed", "successful", "success" -> PaymentStatus.COMPLETED
                "failed", "error" -> PaymentStatus.FAILED
                "cancelled" -> PaymentStatus.CANCELLED
                else -> PaymentStatus.PROCESSING
            },
            errorMessage = if (statusResponse.status.lowercase() == "failed") "Payment failed" else null,
            completedAt = if (statusResponse.status.lowercase() == "completed") Date() else null,
            metadata = null
        )
        
        paymentDao.insertPaymentResult(paymentResult.toEntity())
        
        // Create subscription if payment successful
        if (paymentResult.status == PaymentStatus.COMPLETED) {
            createSubscription(request.toDomain(), paymentResult)
            
            // Handle school commission
            if (request.schoolId != null) {
                calculateSchoolCommission(request.schoolId, request.amount)
            }
            
            // Increment promo/referral usage
            request.promoCode?.let { paymentDao.incrementPromoCodeUsage(it) }
            request.referralCode?.let { paymentDao.incrementReferralUsage(it) }
        }
        
        paymentResult
    }
    
    override suspend fun pollPaymentStatus(
        requestId: String,
        checkoutRequestId: String,
        maxAttempts: Int,
        delayMillis: Long
    ): Result<PaymentResult> = runCatching {
        // Link the Safaricom checkout request ID to the pending backend record
        try {
            val token = "Bearer DUMMY_TOKEN" // Replace with actual token retrieval if needed
            val dto = com.dereva.smart.data.remote.dto.LinkCheckoutRequestDto(
                id = requestId,
                checkout_request_id = checkoutRequestId
            )
            val apiRes = apiService.linkCheckout(dto, token)
            if (!apiRes.isSuccessful) {
                Timber.e("Backend link checkout failed: ${apiRes.code()}")
            }
        } catch (e: Exception) {
            Timber.e("Failed to link checkout ID to backend", e)
        }

        repeat(maxAttempts) { attempt ->
            val result = checkPaymentStatus(requestId, checkoutRequestId)
            
            if (result.isSuccess) {
                val paymentResult = result.getOrThrow()
                if (paymentResult.status != PaymentStatus.PENDING && 
                    paymentResult.status != PaymentStatus.PROCESSING) {
                    return@runCatching paymentResult
                }
            }
            
            if (attempt < maxAttempts - 1) {
                delay(delayMillis)
            }
        }
        
        throw Exception("Payment status check timed out")
    }
    
    private suspend fun createSubscription(
        request: PaymentRequest,
        paymentResult: PaymentResult
    ) {
        val plansResult = getAvailablePlans()
        if (plansResult.isFailure) return
        val plan = plansResult.getOrDefault(emptyList()).find { it.tier == request.subscriptionTier }
            ?: return
        
        // Deactivate existing subscriptions
        paymentDao.deactivateAllSubscriptions(request.userId)
        
        val subscription = UserSubscription(
            id = UUID.randomUUID().toString(),
            userId = request.userId,
            tier = request.subscriptionTier,
            startDate = Date(),
            endDate = plan.durationDays?.let {
                Date(System.currentTimeMillis() + it * 24 * 60 * 60 * 1000L)
            },
            isActive = true,
            autoRenew = request.subscriptionTier == SubscriptionTier.MONTHLY,
            paymentResultId = paymentResult.id
        )
        
        paymentDao.insertSubscription(subscription.toEntity())
    }
    
    override suspend fun getActiveSubscription(userId: String): Result<UserSubscription?> = runCatching {
        paymentDao.getActiveSubscription(userId)?.toDomain()
    }
    
    override fun getActiveSubscriptionFlow(userId: String): Flow<UserSubscription?> {
        return paymentDao.getActiveSubscriptionFlow(userId).map { it?.toDomain() }
    }
    
    override suspend fun getUserSubscriptions(userId: String): Result<List<UserSubscription>> = runCatching {
        paymentDao.getUserSubscriptions(userId).map { it.toDomain() }
    }
    
    override suspend fun updateAutoRenew(subscriptionId: String, autoRenew: Boolean): Result<Unit> = runCatching {
        paymentDao.updateAutoRenew(subscriptionId, autoRenew)
    }
    
    override suspend fun cancelSubscription(userId: String): Result<Unit> = runCatching {
        paymentDao.deactivateAllSubscriptions(userId)
    }
    
    override suspend fun getPaymentHistory(userId: String): Result<List<PaymentHistory>> = runCatching {
        val results = paymentDao.getUserPaymentHistory(userId)
        val requests = paymentDao.getUserPaymentRequests(userId).associateBy { it.id }
        
        results.map { result ->
            val request = requests[result.paymentRequestId]
            PaymentHistory(
                id = result.id,
                userId = result.userId,
                amount = result.amount,
                status = PaymentStatus.valueOf(result.status),
                subscriptionTier = request?.let { SubscriptionTier.valueOf(it.subscriptionTier) } 
                    ?: SubscriptionTier.FREE,
                mpesaReceiptNumber = result.mpesaReceiptNumber,
                date = result.completedAt?.let { Date(it) } ?: Date(),
                promoCode = request?.promoCode,
                referralCode = request?.referralCode
            )
        }
    }
    
    override suspend fun validatePromoCode(code: String): Result<PromoCode> = runCatching {
        val promoEntity = paymentDao.getPromoCode(code)
            ?: throw Exception("Promo code not found")
        
        val promo = promoEntity.toDomain()
        
        if (!promo.isValid) {
            throw Exception("Promo code is invalid or expired")
        }
        
        promo
    }
    
    override suspend fun applyPromoCode(code: String, originalAmount: Double): Result<Double> = runCatching {
        val promo = validatePromoCode(code).getOrThrow()
        val discount = promo.calculateDiscount(originalAmount)
        (originalAmount - discount).coerceAtLeast(0.0)
    }
    
    override suspend fun getUserReferralCode(userId: String): Result<ReferralCode?> = runCatching {
        paymentDao.getUserReferralCode(userId)?.toDomain()
    }
    
    override suspend fun generateReferralCode(userId: String): Result<ReferralCode> = runCatching {
        val existing = paymentDao.getUserReferralCode(userId)
        if (existing != null) {
            return@runCatching existing.toDomain()
        }
        
        val code = "REF${userId.take(6).uppercase()}${Random().nextInt(1000)}"
        val referralCode = ReferralCode(
            code = code,
            referrerId = userId,
            discountPercentage = 10.0,
            referrerBonusPercentage = 5.0,
            usageCount = 0,
            createdAt = Date()
        )
        
        paymentDao.insertReferralCode(referralCode.toEntity())
        referralCode
    }
    
    override suspend fun validateReferralCode(code: String): Result<ReferralCode> = runCatching {
        paymentDao.getReferralCode(code)?.toDomain()
            ?: throw Exception("Referral code not found")
    }
    
    override suspend fun applyReferralCode(code: String, originalAmount: Double): Result<Double> = runCatching {
        val referral = validateReferralCode(code).getOrThrow()
        val discount = originalAmount * (referral.discountPercentage / 100.0)
        (originalAmount - discount).coerceAtLeast(0.0)
    }
    
    override suspend fun calculateSchoolCommission(
        schoolId: String,
        paymentAmount: Double
    ): Result<SchoolCommission> = runCatching {
        val commissionPercentage = 20.0
        val commissionAmount = paymentAmount * (commissionPercentage / 100.0)
        
        val commission = SchoolCommission(
            id = UUID.randomUUID().toString(),
            schoolId = schoolId,
            paymentId = UUID.randomUUID().toString(),
            amount = commissionAmount,
            percentage = commissionPercentage,
            status = "COMPLETED",
            createdAt = Date()
        )
        
        paymentDao.insertSchoolCommission(commission.toEntity())
        commission
    }
    
    override suspend fun getSchoolCommissions(schoolId: String): Result<List<SchoolCommission>> = runCatching {
        paymentDao.getSchoolCommissions(schoolId).map { it.toDomain() }
    }
    
    // Pending Payment Persistence
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
