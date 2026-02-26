package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

enum class SubscriptionTier {
    FREE,           // Limited features
    MONTHLY         // Monthly subscription
}

enum class PaymentStatus {
    PENDING,        // Payment initiated
    PROCESSING,     // STK push sent
    COMPLETED,      // Payment successful
    FAILED,         // Payment failed
    CANCELLED,      // User cancelled
    EXPIRED         // Payment request expired
}

@Parcelize
data class SubscriptionPlan(
    val tier: SubscriptionTier,
    val name: String,
    val price: Double,
    val currency: String = "KES",
    val features: List<String>,
    val durationDays: Int? = 30 // 30 for monthly
) : Parcelable

@Parcelize
data class PaymentRequest(
    val id: String,
    val userId: String,
    val amount: Double,
    val phoneNumber: String,
    val subscriptionTier: SubscriptionTier,
    val promoCode: String? = null,
    val referralCode: String? = null,
    val schoolId: String? = null,
    val createdAt: Date,
    val expiresAt: Date
) : Parcelable

@Parcelize
data class PaymentResult(
    val id: String,
    val paymentRequestId: String,
    val userId: String,
    val amount: Double,
    val mpesaReceiptNumber: String?,
    val transactionId: String?,
    val status: PaymentStatus,
    val errorMessage: String? = null,
    val completedAt: Date?,
    val metadata: String? = null
) : Parcelable

@Parcelize
data class UserSubscription(
    val id: String,
    val userId: String,
    val tier: SubscriptionTier,
    val startDate: Date,
    val endDate: Date?,
    val isActive: Boolean,
    val autoRenew: Boolean = false,
    val paymentResultId: String
) : Parcelable {
    
    val isExpired: Boolean
        get() = endDate?.let { it.before(Date()) } ?: false
    
    val daysRemaining: Int
        get() = endDate?.let {
            val diff = it.time - Date().time
            (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        } ?: Int.MAX_VALUE
}

@Parcelize
data class PromoCode(
    val code: String,
    val discountPercentage: Double,
    val discountAmount: Double?,
    val validUntil: Date,
    val maxUses: Int,
    val currentUses: Int,
    val isActive: Boolean
) : Parcelable {
    
    val isValid: Boolean
        get() = isActive && 
                validUntil.after(Date()) && 
                currentUses < maxUses
    
    fun calculateDiscount(originalAmount: Double): Double {
        return if (discountAmount != null) {
            discountAmount.coerceAtMost(originalAmount)
        } else {
            originalAmount * (discountPercentage / 100.0)
        }
    }
}

@Parcelize
data class ReferralCode(
    val code: String,
    val referrerId: String,
    val discountPercentage: Double,
    val referrerBonusPercentage: Double,
    val usageCount: Int,
    val createdAt: Date
) : Parcelable

@Parcelize
data class PaymentHistory(
    val id: String,
    val userId: String,
    val amount: Double,
    val status: PaymentStatus,
    val subscriptionTier: SubscriptionTier,
    val mpesaReceiptNumber: String?,
    val date: Date,
    val promoCode: String?,
    val referralCode: String?
) : Parcelable

@Parcelize
data class SchoolCommission(
    val id: String,
    val schoolId: String,
    val paymentId: String,
    val amount: Double,
    val percentage: Double = 20.0,
    val status: String,
    val createdAt: Date
) : Parcelable
