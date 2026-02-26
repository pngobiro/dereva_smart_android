package com.dereva.smart.data.repository

import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.MockTestRepository
import com.dereva.smart.domain.repository.PerformanceAnalytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class MockTestRepositoryImpl(
    private val apiService: DerevaApiService
) : MockTestRepository {
    
    override suspend fun generateTest(
        userId: String,
        licenseCategory: String,
        questionCount: Int
    ): Result<MockTest> = runCatching {
        val response = apiService.getQuestions(licenseCategory)
        if (!response.isSuccessful) throw Exception("Failed to load questions")
        
        val allQuestions = response.body()?.map { dto ->
            Question(
                id = dto.id,
                textEn = dto.question,
                textSw = dto.question, 
                options = dto.options.map { QuestionOption(textEn = it, textSw = it) },
                correctOptionIndex = dto.correctAnswer,
                explanationEn = dto.explanation ?: "",
                explanationSw = dto.explanation ?: "",
                imageUrl = dto.imageUrl,
                curriculumTopicId = "general",
                difficultyLevel = DifficultyLevel.MEDIUM,
                licenseCategories = listOf(try { LicenseCategory.valueOf(dto.category.uppercase()) } catch(e: Exception) { LicenseCategory.B1 }),
                isCommonCore = true
            )
        } ?: emptyList()
        
        val selectedQuestions = allQuestions.shuffled().take(questionCount)
        
        MockTest(
            id = UUID.randomUUID().toString(),
            userId = userId,
            questions = selectedQuestions,
            userAnswers = emptyMap(),
            createdAt = Date(),
            status = TestStatus.NOT_STARTED,
            durationMinutes = 45,
            licenseCategory = licenseCategory
        )
    }
    
    override suspend fun getTestById(testId: String): Result<MockTest> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getUserTests(userId: String): Result<List<MockTest>> = Result.success(emptyList())
    
    override fun getUserTestsFlow(userId: String): Flow<List<MockTest>> = flowOf(emptyList())
    
    override suspend fun updateTestAnswer(
        testId: String,
        questionId: String,
        selectedOptionIndex: Int
    ): Result<Unit> = Result.success(Unit)
    
    override suspend fun submitTest(testId: String): Result<TestResult> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getTestResult(testId: String): Result<TestResult> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getUserTestResults(userId: String): Result<List<TestResult>> = Result.success(emptyList())
    
    override suspend fun getUserAnalytics(userId: String): Result<PerformanceAnalytics> = Result.failure(Exception("Not implemented"))
}
