package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dereva.smart.domain.model.*
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phoneNumber: String,
    val passwordHash: String,
    val name: String,
    val email: String?,
    val targetCategory: String,
    val drivingSchoolId: String?,
    val subscriptionStatus: String,
    val subscriptionExpiryDate: Long?,
    val isPhoneVerified: Boolean,
    val isGuestMode: Boolean = false,
    val createdAt: Long,
    val lastActiveAt: Long,
    val lastLoginAt: Long?
)

@Entity(tableName = "verification_codes")
data class VerificationCodeEntity(
    @PrimaryKey val code: String,
    val phoneNumber: String,
    val expiresAt: Long,
    val isUsed: Boolean,
    val createdAt: Long
)

@Entity(tableName = "auth_sessions")
data class AuthSessionEntity(
    @PrimaryKey val token: String,
    val userId: String,
    val createdAt: Long,
    val expiresAt: Long,
    val deviceId: String?
)

// Converters
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        phoneNumber = phoneNumber,
        name = name,
        email = email,
        targetCategory = LicenseCategory.valueOf(targetCategory),
        drivingSchoolId = drivingSchoolId,
        subscriptionStatus = SubscriptionStatus.valueOf(subscriptionStatus),
        subscriptionExpiryDate = subscriptionExpiryDate?.let { Date(it) },
        isPhoneVerified = isPhoneVerified,
        isGuestMode = isGuestMode,
        createdAt = Date(createdAt),
        lastActiveAt = Date(lastActiveAt),
        lastLoginAt = lastLoginAt?.let { Date(it) }
    )
}

fun User.toEntity(passwordHash: String): UserEntity {
    return UserEntity(
        id = id,
        phoneNumber = phoneNumber,
        passwordHash = passwordHash,
        name = name,
        email = email,
        targetCategory = targetCategory.name,
        drivingSchoolId = drivingSchoolId,
        subscriptionStatus = subscriptionStatus.name,
        subscriptionExpiryDate = subscriptionExpiryDate?.time,
        isPhoneVerified = isPhoneVerified,
        isGuestMode = isGuestMode,
        createdAt = createdAt.time,
        lastActiveAt = lastActiveAt.time,
        lastLoginAt = lastLoginAt?.time
    )
}

fun VerificationCodeEntity.toDomain(): VerificationCode {
    return VerificationCode(
        code = code,
        phoneNumber = phoneNumber,
        expiresAt = Date(expiresAt),
        isUsed = isUsed
    )
}

fun VerificationCode.toEntity(): VerificationCodeEntity {
    return VerificationCodeEntity(
        code = code,
        phoneNumber = phoneNumber,
        expiresAt = expiresAt.time,
        isUsed = isUsed,
        createdAt = System.currentTimeMillis()
    )
}
