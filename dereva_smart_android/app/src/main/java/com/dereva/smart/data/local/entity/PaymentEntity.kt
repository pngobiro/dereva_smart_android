package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dereva.smart.domain.model.*
import java.util.Date

@Entity(tableName = "payment_requests")
data class PaymentRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val phoneNumber: String,
    val subscriptionTier: String,
    val promoCode: String?,
    val referralCode: String?,
    val schoolId: String?,
    val createdAt: Long,
    val expiresAt: Long
)

@Entity(tableName = "payment_results")
data class PaymentResultEntity(
    @PrimaryKey val id: String,
    val paymentRequestId: String,
    val userId: String,
    val amount: Double,
    val mpesaReceiptNumber: String?,
    val transactionId: String?,
    val status: String,
    val errorMessage: String?,
    val completedAt: Long?,
    val metadata: String?
)

@Entity(tableName = "user_subscriptions")
data class UserSubscriptionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val tier: String,
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean,
    val autoRenew: Boolean,
    val paymentResultId: String
)

@Entity(tableName = "promo_codes")
data class PromoCodeEntity(
    @PrimaryKey val code: String,
    val discountPercentage: Double,
    val discountAmount: Double?,
    val validUntil: Long,
    val maxUses: Int,
    val currentUses: Int,
    val isActive: Boolean
)

@Entity(tableName = "referral_codes")
data class ReferralCodeEntity(
    @PrimaryKey val code: String,
    val referrerId: String,
    val discountPercentage: Double,
    val referrerBonusPercentage: Double,
    val usageCount: Int,
    val createdAt: Long
)

@Entity(tableName = "school_commissions")
data class SchoolCommissionEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val paymentId: String,
    val amount: Double,
    val percentage: Double,
    val status: String,
    val createdAt: Long
)

// Converters
fun PaymentRequestEntity.toDomain(): PaymentRequest {
    return PaymentRequest(
        id = id,
        userId = userId,
        amount = amount,
        phoneNumber = phoneNumber,
        subscriptionTier = SubscriptionTier.valueOf(subscriptionTier),
        promoCode = promoCode,
        referralCode = referralCode,
        schoolId = schoolId,
        createdAt = Date(createdAt),
        expiresAt = Date(expiresAt)
    )
}

fun PaymentRequest.toEntity(): PaymentRequestEntity {
    return PaymentRequestEntity(
        id = id,
        userId = userId,
        amount = amount,
        phoneNumber = phoneNumber,
        subscriptionTier = subscriptionTier.name,
        promoCode = promoCode,
        referralCode = referralCode,
        schoolId = schoolId,
        createdAt = createdAt.time,
        expiresAt = expiresAt.time
    )
}

fun PaymentResultEntity.toDomain(): PaymentResult {
    return PaymentResult(
        id = id,
        paymentRequestId = paymentRequestId,
        userId = userId,
        amount = amount,
        mpesaReceiptNumber = mpesaReceiptNumber,
        transactionId = transactionId,
        status = PaymentStatus.valueOf(status),
        errorMessage = errorMessage,
        completedAt = completedAt?.let { Date(it) },
        metadata = metadata
    )
}

fun PaymentResult.toEntity(): PaymentResultEntity {
    return PaymentResultEntity(
        id = id,
        paymentRequestId = paymentRequestId,
        userId = userId,
        amount = amount,
        mpesaReceiptNumber = mpesaReceiptNumber,
        transactionId = transactionId,
        status = status.name,
        errorMessage = errorMessage,
        completedAt = completedAt?.time,
        metadata = metadata
    )
}

fun UserSubscriptionEntity.toDomain(): UserSubscription {
    return UserSubscription(
        id = id,
        userId = userId,
        tier = SubscriptionTier.valueOf(tier),
        startDate = Date(startDate),
        endDate = endDate?.let { Date(it) },
        isActive = isActive,
        autoRenew = autoRenew,
        paymentResultId = paymentResultId
    )
}

fun UserSubscription.toEntity(): UserSubscriptionEntity {
    return UserSubscriptionEntity(
        id = id,
        userId = userId,
        tier = tier.name,
        startDate = startDate.time,
        endDate = endDate?.time,
        isActive = isActive,
        autoRenew = autoRenew,
        paymentResultId = paymentResultId
    )
}

fun PromoCodeEntity.toDomain(): PromoCode {
    return PromoCode(
        code = code,
        discountPercentage = discountPercentage,
        discountAmount = discountAmount,
        validUntil = Date(validUntil),
        maxUses = maxUses,
        currentUses = currentUses,
        isActive = isActive
    )
}

fun PromoCode.toEntity(): PromoCodeEntity {
    return PromoCodeEntity(
        code = code,
        discountPercentage = discountPercentage,
        discountAmount = discountAmount,
        validUntil = validUntil.time,
        maxUses = maxUses,
        currentUses = currentUses,
        isActive = isActive
    )
}

fun ReferralCodeEntity.toDomain(): ReferralCode {
    return ReferralCode(
        code = code,
        referrerId = referrerId,
        discountPercentage = discountPercentage,
        referrerBonusPercentage = referrerBonusPercentage,
        usageCount = usageCount,
        createdAt = Date(createdAt)
    )
}

fun ReferralCode.toEntity(): ReferralCodeEntity {
    return ReferralCodeEntity(
        code = code,
        referrerId = referrerId,
        discountPercentage = discountPercentage,
        referrerBonusPercentage = referrerBonusPercentage,
        usageCount = usageCount,
        createdAt = createdAt.time
    )
}

fun SchoolCommissionEntity.toDomain(): SchoolCommission {
    return SchoolCommission(
        id = id,
        schoolId = schoolId,
        paymentId = paymentId,
        amount = amount,
        percentage = percentage,
        status = status,
        createdAt = Date(createdAt)
    )
}

fun SchoolCommission.toEntity(): SchoolCommissionEntity {
    return SchoolCommissionEntity(
        id = id,
        schoolId = schoolId,
        paymentId = paymentId,
        amount = amount,
        percentage = percentage,
        status = status,
        createdAt = createdAt.time
    )
}
