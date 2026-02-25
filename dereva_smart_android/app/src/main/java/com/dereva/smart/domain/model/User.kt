package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

enum class LicenseCategory {
    A1, A2, A3, B1, B2, B3, C, D, E, F, G
}

enum class SubscriptionStatus {
    FREE, PREMIUM_ONE_TIME, PREMIUM_MONTHLY, EXPIRED
}

@Parcelize
data class User(
    val id: String,
    val phoneNumber: String,
    val name: String,
    val email: String? = null,
    val targetCategory: LicenseCategory,
    val drivingSchoolId: String? = null,
    val subscriptionStatus: SubscriptionStatus,
    val subscriptionExpiryDate: Date? = null,
    val isPhoneVerified: Boolean = false,
    val isGuestMode: Boolean = false,
    val createdAt: Date,
    val lastActiveAt: Date,
    val lastLoginAt: Date? = null
) : Parcelable {
    
    val isPremium: Boolean
        get() = subscriptionStatus == SubscriptionStatus.PREMIUM_ONE_TIME ||
                subscriptionStatus == SubscriptionStatus.PREMIUM_MONTHLY
    
    val isSubscriptionActive: Boolean
        get() {
            if (subscriptionStatus == SubscriptionStatus.FREE ||
                subscriptionStatus == SubscriptionStatus.EXPIRED) {
                return false
            }
            if (subscriptionStatus == SubscriptionStatus.PREMIUM_ONE_TIME) {
                return true
            }
            return subscriptionExpiryDate?.let { Date().before(it) } ?: false
        }
    
    val licenseCategory: String
        get() = targetCategory.name
    
    companion object {
        fun createGuestUser(): User {
            return User(
                id = "guest_${System.currentTimeMillis()}",
                phoneNumber = "",
                name = "Guest User",
                email = null,
                targetCategory = LicenseCategory.B1,
                drivingSchoolId = null,
                subscriptionStatus = SubscriptionStatus.FREE,
                subscriptionExpiryDate = null,
                isPhoneVerified = false,
                isGuestMode = true,
                createdAt = Date(),
                lastActiveAt = Date(),
                lastLoginAt = null
            )
        }
    }
}
