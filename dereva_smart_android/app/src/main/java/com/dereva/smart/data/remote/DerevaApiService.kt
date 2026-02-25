package com.dereva.smart.data.remote

import com.dereva.smart.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Dereva Smart API Service - Cloudflare Workers Backend
 */
interface DerevaApiService {
    
    // Health Check
    @GET("/")
    suspend fun healthCheck(): Response<HealthResponse>
    
    // Authentication
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("/api/auth/verify")
    suspend fun verifyCode(@Body request: VerifyRequest): Response<AuthResponse>
    
    @POST("/api/auth/resend-code")
    suspend fun resendCode(@Body request: ResendCodeRequest): Response<MessageResponse>
    
    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>
    
    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>
    
    // User
    @GET("/api/users/{id}")
    suspend fun getUser(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Response<UserResponse>
    
    // Content
    @GET("/api/content/modules")
    suspend fun getModules(
        @Query("category") category: String? = null
    ): Response<List<ModuleResponse>>
    
    @GET("/api/content/lessons/{moduleId}")
    suspend fun getLessons(
        @Path("moduleId") moduleId: String
    ): Response<List<LessonResponse>>
    
    @GET("/api/content/lesson/{lessonId}")
    suspend fun getLesson(
        @Path("lessonId") lessonId: String
    ): Response<LessonResponse>
    
    // Questions
    @GET("/api/questions")
    suspend fun getQuestions(
        @Query("category") category: String
    ): Response<List<QuestionResponse>>
    
    // Progress
    @GET("/api/progress/{userId}")
    suspend fun getProgress(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<List<ProgressResponse>>
    
    @POST("/api/progress")
    suspend fun updateProgress(
        @Body request: UpdateProgressRequest,
        @Header("Authorization") token: String
    ): Response<MessageResponse>
    
    // Payments
    @GET("/api/payments/config")
    suspend fun getPaymentConfig(): retrofit2.Response<PaymentConfigResponse>

    @POST("/api/payments/initiate")
    suspend fun initiatePayment(
        @Body request: com.dereva.smart.data.remote.dto.PaymentRequestDto,
        @Header("Authorization") token: String
    ): retrofit2.Response<PaymentResponse>
    
    @POST("/api/payments/link-checkout")
    suspend fun linkCheckout(
        @Body request: com.dereva.smart.data.remote.dto.LinkCheckoutRequestDto,
        @Header("Authorization") token: String
    ): retrofit2.Response<MessageResponse>
    
    @GET("/api/payments/status/{paymentId}")
    suspend fun getPaymentStatus(
        @Path("paymentId") paymentId: String
    ): retrofit2.Response<CloudflarePaymentStatus>
    
    // Schools
    @GET("/api/schools")
    suspend fun getSchools(): Response<List<SchoolResponse>>
    
    // AI Tutor
    @POST("/api/tutor/ask")
    suspend fun askTutor(
        @Body request: TutorRequest,
        @Header("Authorization") token: String
    ): Response<TutorResponse>
}
