package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class AuthResult(
    val success: Boolean,
    val user: User?,
    val token: String?,
    val errorMessage: String?
) : Parcelable

@Parcelize
data class AuthState(
    val isAuthenticated: Boolean,
    val user: User?,
    val token: String?
) : Parcelable

@Parcelize
data class VerificationCode(
    val code: String,
    val phoneNumber: String,
    val expiresAt: Date,
    val isUsed: Boolean = false
) : Parcelable {
    val isExpired: Boolean
        get() = Date().after(expiresAt)
    
    val isValid: Boolean
        get() = !isUsed && !isExpired
}

@Parcelize
data class PasswordResetRequest(
    val phoneNumber: String,
    val verificationCode: String,
    val newPassword: String
) : Parcelable

enum class AuthError {
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    INVALID_VERIFICATION_CODE,
    VERIFICATION_CODE_EXPIRED,
    WEAK_PASSWORD,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

data class RegistrationRequest(
    val phoneNumber: String,
    val password: String,
    val fullName: String,
    val licenseCategory: LicenseCategory = LicenseCategory.B1
)

data class LoginRequest(
    val phoneNumber: String,
    val password: String
)
