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

data class UpdateCategoryRequest(
    @SerializedName("targetCategory")
    val targetCategory: String
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

// Quiz
data class QuizBanksResponse(
    val quizzes: List<QuizBankDto>
)

data class QuizBankDto(
    val id: String,
    val title: String,
    val description: String,
    @SerializedName("licenseCategory")
    val licenseCategory: String,
    @SerializedName("topicArea")
    val topicArea: String,
    val difficulty: String,
    @SerializedName("totalQuestions")
    val totalQuestions: Int,
    @SerializedName("timeLimit")
    val timeLimit: Int,
    @SerializedName("passingScore")
    val passingScore: Int,
    @SerializedName("isPremium")
    val isPremium: Boolean,
    @SerializedName("jsonUrl")
    val jsonUrl: String,
    val version: Int,
    val order: Int,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("updatedAt")
    val updatedAt: Long
)

data class QuizContentDto(
    val id: String,
    val title: String,
    val description: String,
    @SerializedName("licenseCategory")
    val licenseCategory: String,
    @SerializedName("topicArea")
    val topicArea: String,
    val difficulty: String,
    @SerializedName("timeLimit")
    val timeLimit: Int,
    @SerializedName("passingScore")
    val passingScore: Int,
    @SerializedName("isPaid")
    val isPaid: Boolean,
    val version: Int,
    val order: Int,
    val questions: List<QuizQuestionDto>
)

data class QuizQuestionDto(
    val id: String,
    val type: String,
    val question: Any, // Can be String or ContentObject
    val points: Int,
    val options: List<QuizOptionDto>? = null,
    @SerializedName("correctAnswer")
    val correctAnswer: Boolean? = null,
    val blanks: List<QuizBlankDto>? = null,
    val pairs: List<QuizPairDto>? = null,
    val items: List<QuizItemDto>? = null,
    @SerializedName("acceptedAnswers")
    val acceptedAnswers: List<String>? = null,
    @SerializedName("partialCredit")
    val partialCredit: Boolean? = null,
    @SerializedName("caseSensitive")
    val caseSensitive: Boolean? = null,
    val explanation: Any, // Can be String or ContentObject
    val hint: Any? = null, // Can be String or ContentObject
    val context: ContentObjectDto? = null, // New: educational background
    val media: QuizMediaDto? = null,
    @SerializedName("richContent")
    val richContent: QuizRichContentDto? = null
)

data class QuizOptionDto(
    val id: String,
    val text: String,
    @SerializedName("isCorrect")
    val isCorrect: Boolean
)

data class QuizBlankDto(
    val id: String,
    @SerializedName("acceptedAnswers")
    val acceptedAnswers: List<String>
)

data class QuizPairDto(
    val id: String,
    val left: String,
    val right: String
)

data class QuizItemDto(
    val id: String,
    val text: String,
    @SerializedName("correctPosition")
    val correctPosition: Int
)

data class QuizMediaDto(
    val type: String,
    val url: String,
    val caption: String? = null,
    val position: String? = "before" // before or after
)

data class QuizRichContentDto(
    val type: String,
    val content: String
)

// New unified content model
data class ContentObjectDto(
    val format: String, // text, html, latex (REQUIRED)
    val value: String,
    val media: QuizMediaDto? = null
)

data class SubmitQuizRequest(
    val answers: List<QuizAnswerDto>,
    @SerializedName("timeTaken")
    val timeTaken: Int
)

data class QuizAnswerDto(
    @SerializedName("questionId")
    val questionId: String,
    val answer: Any?
)

data class QuizAttemptResponse(
    @SerializedName("attemptId")
    val attemptId: String?,
    val score: Int,
    val passed: Boolean,
    @SerializedName("correctAnswers")
    val correctAnswers: Int,
    @SerializedName("totalQuestions")
    val totalQuestions: Int,
    @SerializedName("timeTaken")
    val timeTaken: Int,
    val feedback: List<QuizFeedbackDto>,
    @SerializedName("isGuest")
    val isGuest: Boolean
)

data class QuizFeedbackDto(
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("isCorrect")
    val isCorrect: Boolean,
    val explanation: String,
    @SerializedName("userAnswer")
    val userAnswer: Any? = null // Can be String, Boolean, or List<String>
)

data class QuizAttemptsResponse(
    val attempts: List<QuizAttemptHistoryDto>
)

data class QuizAttemptHistoryDto(
    val id: String,
    @SerializedName("quizBankId")
    val quizBankId: String,
    @SerializedName("startedAt")
    val startedAt: Long,
    @SerializedName("completedAt")
    val completedAt: Long,
    @SerializedName("timeTaken")
    val timeTaken: Int,
    @SerializedName("totalQuestions")
    val totalQuestions: Int,
    @SerializedName("correctAnswers")
    val correctAnswers: Int,
    val score: Int,
    val passed: Boolean
)
