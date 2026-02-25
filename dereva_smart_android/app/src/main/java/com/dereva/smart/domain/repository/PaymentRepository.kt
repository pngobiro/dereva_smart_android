package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    
    // Subscription Plans
    suspend fun getAvailablePlans(): Result<List<SubscriptionPlan>>
    
    // Payment Processing
    suspend fun initiatePayment(
        userId: String,
        phoneNumber: String,
        plan: SubscriptionPlan,
        promoCode: String? = null,
        referralCode: String? = null,
        schoolId: String? = null
    ): Result<PaymentRequest>
    
    suspend fun checkPaymentStatus(requestId: String, checkoutRequestId: String): Result<PaymentResult>
    
    suspend fun pollPaymentStatus(
        requestId: String,
        checkoutRequestId: String,
        maxAttempts: Int = 12,
        delayMillis: Long = 5000
    ): Result<PaymentResult>
    
    // Pending Payment Persistence
    suspend fun savePendingPayment(requestId: String, checkoutRequestId: String)
    suspend fun getPendingPayment(): Pair<String?, String?>
    suspend fun clearPendingPayment()
    
    // Subscription Management
    suspend fun getActiveSubscription(userId: String): Result<UserSubscription?>
    
    fun getActiveSubscriptionFlow(userId: String): Flow<UserSubscription?>
    
    suspend fun getUserSubscriptions(userId: String): Result<List<UserSubscription>>
    
    suspend fun updateAutoRenew(subscriptionId: String, autoRenew: Boolean): Result<Unit>
    
    suspend fun cancelSubscription(userId: String): Result<Unit>
    
    // Payment History
    suspend fun getPaymentHistory(userId: String): Result<List<PaymentHistory>>
    
    // Promo Codes
    suspend fun validatePromoCode(code: String): Result<PromoCode>
    
    suspend fun applyPromoCode(code: String, originalAmount: Double): Result<Double>
    
    // Referral Codes
    suspend fun getUserReferralCode(userId: String): Result<ReferralCode?>
    
    suspend fun generateReferralCode(userId: String): Result<ReferralCode>
    
    suspend fun validateReferralCode(code: String): Result<ReferralCode>
    
    suspend fun applyReferralCode(code: String, originalAmount: Double): Result<Double>
    
    // School Commissions
    suspend fun calculateSchoolCommission(
        schoolId: String,
        paymentAmount: Double
    ): Result<SchoolCommission>
    
    suspend fun getSchoolCommissions(schoolId: String): Result<List<SchoolCommission>>
}
