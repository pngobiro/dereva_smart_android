package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    
    // Payment Requests
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRequest(request: PaymentRequestEntity)
    
    @Query("SELECT * FROM payment_requests WHERE id = :requestId")
    suspend fun getPaymentRequest(requestId: String): PaymentRequestEntity?
    
    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserPaymentRequests(userId: String): List<PaymentRequestEntity>
    
    // Payment Results
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentResult(result: PaymentResultEntity)
    
    @Query("SELECT * FROM payment_results WHERE id = :resultId")
    suspend fun getPaymentResult(resultId: String): PaymentResultEntity?
    
    @Query("SELECT * FROM payment_results WHERE paymentRequestId = :requestId")
    suspend fun getPaymentResultByRequest(requestId: String): PaymentResultEntity?
    
    @Query("SELECT * FROM payment_results WHERE userId = :userId ORDER BY completedAt DESC")
    suspend fun getUserPaymentHistory(userId: String): List<PaymentResultEntity>
    
    @Query("SELECT * FROM payment_results WHERE userId = :userId AND status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getUserCompletedPaymentsFlow(userId: String): Flow<List<PaymentResultEntity>>
    
    // Subscriptions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: UserSubscriptionEntity)
    
    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId AND isActive = 1 LIMIT 1")
    suspend fun getActiveSubscription(userId: String): UserSubscriptionEntity?
    
    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId AND isActive = 1 LIMIT 1")
    fun getActiveSubscriptionFlow(userId: String): Flow<UserSubscriptionEntity?>
    
    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId ORDER BY startDate DESC")
    suspend fun getUserSubscriptions(userId: String): List<UserSubscriptionEntity>
    
    @Query("UPDATE user_subscriptions SET isActive = 0 WHERE userId = :userId")
    suspend fun deactivateAllSubscriptions(userId: String)
    
    @Query("UPDATE user_subscriptions SET autoRenew = :autoRenew WHERE id = :subscriptionId")
    suspend fun updateAutoRenew(subscriptionId: String, autoRenew: Boolean)
    
    // Promo Codes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromoCode(promoCode: PromoCodeEntity)
    
    @Query("SELECT * FROM promo_codes WHERE code = :code")
    suspend fun getPromoCode(code: String): PromoCodeEntity?
    
    @Query("UPDATE promo_codes SET currentUses = currentUses + 1 WHERE code = :code")
    suspend fun incrementPromoCodeUsage(code: String)
    
    // Referral Codes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferralCode(referralCode: ReferralCodeEntity)
    
    @Query("SELECT * FROM referral_codes WHERE code = :code")
    suspend fun getReferralCode(code: String): ReferralCodeEntity?
    
    @Query("SELECT * FROM referral_codes WHERE referrerId = :userId")
    suspend fun getUserReferralCode(userId: String): ReferralCodeEntity?
    
    @Query("UPDATE referral_codes SET usageCount = usageCount + 1 WHERE code = :code")
    suspend fun incrementReferralUsage(code: String)
    
    // School Commissions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchoolCommission(commission: SchoolCommissionEntity)
    
    @Query("SELECT * FROM school_commissions WHERE schoolId = :schoolId ORDER BY createdAt DESC")
    suspend fun getSchoolCommissions(schoolId: String): List<SchoolCommissionEntity>
    
    @Query("SELECT SUM(amount) FROM school_commissions WHERE schoolId = :schoolId AND status = 'COMPLETED'")
    suspend fun getTotalSchoolCommissions(schoolId: String): Double?
}
