package com.dereva.smart.data.remote.dto

import com.google.gson.annotations.SerializedName

// Common
data class HealthResponse(
    val status: String,
    val message: String,
    val version: String,
    val environment: String,
    val timestamp: String
)

data class MessageResponse(
    val message: String,
    val success: Boolean = true
)

// Auth
data class RegisterRequest(
    @SerializedName("phoneNumber")
    val phone: String,
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("licenseCategory")
    val category: String
)

data class LoginRequest(
    @SerializedName("phoneNumber")
    val phone: String,
    val password: String
)

data class VerifyRequest(
    @SerializedName("phoneNumber")
    val phone: String,
    val code: String
)

data class ResendCodeRequest(
    @SerializedName("phoneNumber")
    val phone: String
)

data class ForgotPasswordRequest(
    @SerializedName("phoneNumber")
    val phone: String
)

data class ResetPasswordRequest(
    @SerializedName("phoneNumber")
    val phone: String,
    val code: String,
    val newPassword: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: UserData? = null,
    val token: String? = null,
    @SerializedName("requires_verification")
    val requiresVerification: Boolean = false
)

data class UserData(
    val id: String,
    @SerializedName("phone_number", alternate = ["phoneNumber", "phone"])
    val phone: String,
    val name: String,
    @SerializedName("target_category", alternate = ["targetCategory", "category"])
    val category: String,
    @SerializedName("subscription_status", alternate = ["subscriptionStatus"])
    val subscriptionStatus: String,
    @SerializedName("subscription_expiry_date", alternate = ["subscriptionExpiresAt", "subscriptionExpiryDate"])
    val subscriptionExpiryDate: Long? = null,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: Long? = null
)

// User
data class UserResponse(
    val id: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val name: String,
    val email: String? = null,
    @SerializedName("target_category")
    val targetCategory: String,
    @SerializedName("driving_school_id")
    val drivingSchoolId: String? = null,
    @SerializedName("subscription_status")
    val subscriptionStatus: String,
    @SerializedName("subscription_expiry_date")
    val subscriptionExpiryDate: Long? = null,
    @SerializedName("is_phone_verified")
    val isPhoneVerifiedInt: Int = 0,
    @SerializedName("created_at")
    val createdAt: Long = 0
) {
    val isPhoneVerified: Boolean
        get() = isPhoneVerifiedInt == 1
}

// Content
data class ModuleResponse(
    val id: String,
    val title: String,
    val description: String,
    @SerializedName("license_category")
    val category: String,
    @SerializedName("order_index")
    val orderIndex: Int,
    @SerializedName("requires_subscription")
    val requiresSubscriptionInt: Int,
    @SerializedName("thumbnail_url")
    val iconUrl: String? = null,
    @SerializedName("estimated_duration")
    val estimatedDuration: Int = 0,
    @SerializedName("lesson_count")
    val lessonCount: Int = 0
) {
    val requiresSubscription: Boolean
        get() = requiresSubscriptionInt == 1
}

data class LessonResponse(
    val id: String,
    @SerializedName("module_id")
    val moduleId: String,
    val title: String,
    val description: String,
    @SerializedName("content_type")
    val type: String?,
    @SerializedName("content_url")
    val contentUrl: String? = null,
    @SerializedName("content_text")
    val contentText: String? = null,
    @SerializedName("order_index")
    val orderIndex: Int,
    val duration: Int,
    @SerializedName("requires_subscription")
    val requiresSubscriptionInt: Int
) {
    val requiresSubscription: Boolean
        get() = requiresSubscriptionInt == 1
    
    val durationMinutes: Int
        get() = duration
}

// Questions
data class QuestionResponse(
    val id: String,
    val category: String,
    val question: String,
    val options: List<String>,
    @SerializedName("correct_answer")
    val correctAnswer: Int,
    val explanation: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)

// Progress
data class ProgressResponse(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("lesson_id")
    val lessonId: String? = null,
    @SerializedName("module_id")
    val moduleId: String? = null,
    val type: String,
    val status: String,
    @SerializedName("score_percentage")
    val scorePercentage: Int? = null,
    @SerializedName("time_spent_minutes")
    val timeSpentMinutes: Int? = null,
    @SerializedName("completed_at")
    val completedAt: String? = null
)

data class UpdateProgressRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("lesson_id")
    val lessonId: String? = null,
    @SerializedName("module_id")
    val moduleId: String? = null,
    val type: String,
    val status: String,
    @SerializedName("score_percentage")
    val scorePercentage: Int? = null,
    @SerializedName("time_spent_minutes")
    val timeSpentMinutes: Int? = null
)

// Payments
data class PaymentConfigResponse(
    val success: Boolean,
    val monthlyPrice: Double,
    val currency: String,
    val durationDays: Int,
    val features: List<String>
)

data class PaymentRequestDto(
    val id: String,
    val phone: String,
    val amount: Double,
    @SerializedName("user_id")
    val userId: String,
    val type: String
)

data class PaymentResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("checkout_request_id")
    val paymentId: String
)

data class LinkCheckoutRequestDto(
    val id: String,
    val checkout_request_id: String
)

data class CloudflarePaymentStatus(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    val amount: Double,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val status: String,
    @SerializedName("mpesa_receipt_number")
    val mpesaReceiptNumber: String?,
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("completed_at")
    val completedAt: Long? = null
)

// Schools
data class SchoolResponse(
    val id: String,
    val name: String,
    val location: String,
    val phone: String,
    val email: String? = null,
    val rating: Double,
    @SerializedName("license_types")
    val licenseTypes: List<String>,
    @SerializedName("price_range")
    val priceRange: String,
    val verified: Boolean
)

// AI Tutor
data class TutorRequest(
    val question: String,
    val category: String,
    val context: String? = null
)

data class TutorResponse(
    val answer: String,
    val question: String,
    val category: String? = null
)
