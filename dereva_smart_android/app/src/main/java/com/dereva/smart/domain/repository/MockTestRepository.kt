package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*
import kotlinx.coroutines.flow.Flow

interface MockTestRepository {
    
    suspend fun generateTest(
        userId: String,
        licenseCategory: String,
        questionCount: Int = 50
    ): Result<MockTest>
    
    suspend fun getTestById(testId: String): Result<MockTest>
    
    suspend fun getUserTests(userId: String): Result<List<MockTest>>
    
    fun getUserTestsFlow(userId: String): Flow<List<MockTest>>
    
    suspend fun updateTestAnswer(
        testId: String,
        questionId: String,
        selectedOptionIndex: Int
    ): Result<Unit>
    
    suspend fun submitTest(testId: String): Result<TestResult>
    
    suspend fun getTestResult(testId: String): Result<TestResult>
    
    suspend fun getUserTestResults(userId: String): Result<List<TestResult>>
    
    suspend fun getUserAnalytics(userId: String): Result<PerformanceAnalytics>
}

data class PerformanceAnalytics(
    val userId: String,
    val totalTestsTaken: Int,
    val totalTestsPassed: Int,
    val averageScore: Double,
    val passRate: Double,
    val weakAreas: List<WeakArea>,
    val recentScores: List<Double>,
    val consecutivePasses: Int,
    val lastTestDate: java.util.Date? = null
)

data class WeakArea(
    val topicId: String,
    val topicNameEn: String,
    val topicNameSw: String,
    val accuracyPercentage: Double,
    val totalAttempts: Int,
    val correctAttempts: Int
)
