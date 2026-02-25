package com.dereva.smart.data.repository

import android.util.Log
import com.dereva.smart.data.local.dao.MockTestDao
import com.dereva.smart.data.local.dao.QuestionDao
import com.dereva.smart.data.local.entity.*
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.MockTestRepository
import com.dereva.smart.domain.repository.PerformanceAnalytics
import com.dereva.smart.domain.repository.WeakArea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.random.Random

class MockTestRepositoryImpl(
    private val questionDao: QuestionDao,
    private val mockTestDao: MockTestDao,
    private val apiService: com.dereva.smart.data.remote.DerevaApiService
) : MockTestRepository {
    
    companion object {
        private const val TAG = "MockTestRepository"
    }
    
    // Sync questions from backend
    suspend fun syncQuestions(category: String): Result<Unit> = runCatching {
        val response = apiService.getQuestions(category)
        if (response.isSuccessful) {
            response.body()?.forEach { questionDto ->
                // Map API response to QuestionEntity structure
                val options = questionDto.options.map { text ->
                    QuestionOption(
                        textEn = text,
                        textSw = text, // TODO: Add Swahili translations
                        imageUrl = null
                    )
                }
                
                val question = QuestionEntity(
                    id = questionDto.id,
                    textEn = questionDto.question,
                    textSw = questionDto.question, // TODO: Add Swahili translation
                    optionsJson = com.google.gson.Gson().toJson(options),
                    correctOptionIndex = questionDto.correctAnswer,
                    explanationEn = questionDto.explanation ?: "",
                    explanationSw = questionDto.explanation ?: "", // TODO: Add Swahili translation
                    imageUrl = questionDto.imageUrl,
                    curriculumTopicId = "topic_${questionDto.category}",
                    difficultyLevel = "MEDIUM",
                    licenseCategoriesJson = com.google.gson.Gson().toJson(listOf(category)),
                    isCommonCore = false
                )
                questionDao.insertQuestion(question)
            }
        }
    }
    
    override suspend fun generateTest(
        userId: String,
        licenseCategory: String,
        questionCount: Int
    ): Result<MockTest> = runCatching {
        Log.d(TAG, "generateTest called: userId=$userId, category=$licenseCategory, count=$questionCount")
        
        // Load questions directly from API
        Log.d(TAG, "Calling API: getQuestions($licenseCategory)")
        val response = apiService.getQuestions(licenseCategory)
        
        Log.d(TAG, "Questions API response: isSuccessful=${response.isSuccessful}, code=${response.code()}")
        
        if (!response.isSuccessful || response.body() == null) {
            val errorMsg = "Failed to load questions from server: ${response.code()} - ${response.message()}"
            Log.e(TAG, errorMsg)
            throw IllegalStateException(errorMsg)
        }
        
        val apiQuestions = response.body()!!
        Log.d(TAG, "Received ${apiQuestions.size} questions from API")
        
        if (apiQuestions.size < questionCount) {
            val errorMsg = "Not enough questions available. Found ${apiQuestions.size}, need $questionCount"
            Log.e(TAG, errorMsg)
            throw IllegalStateException(errorMsg)
        }
        
        // Convert API questions to domain model
        val questions = apiQuestions.map { dto ->
            Log.d(TAG, "Question: id=${dto.id}, category=${dto.category}, text=${dto.question.take(50)}...")
            val options = dto.options.map { text ->
                QuestionOption(textEn = text, textSw = text, imageUrl = null)
            }
            
            Question(
                id = dto.id,
                textEn = dto.question,
                textSw = dto.question,
                options = options,
                correctOptionIndex = dto.correctAnswer,
                explanationEn = dto.explanation ?: "",
                explanationSw = dto.explanation ?: "",
                imageUrl = dto.imageUrl,
                curriculumTopicId = "topic_${dto.category}",
                difficultyLevel = DifficultyLevel.MEDIUM,
                licenseCategories = listOf(LicenseCategory.valueOf(licenseCategory)),
                isCommonCore = false
            )
        }
        
        // Shuffle and select questions
        val selectedQuestions = questions.shuffled(Random).take(questionCount)
        Log.d(TAG, "Selected ${selectedQuestions.size} questions for test")
        
        val test = MockTest(
            id = "test_${System.currentTimeMillis()}",
            userId = userId,
            questions = selectedQuestions,
            userAnswers = emptyMap(),
            createdAt = Date(),
            status = TestStatus.NOT_STARTED,
            durationMinutes = 60,
            licenseCategory = licenseCategory
        )
        
        Log.d(TAG, "Test generated successfully: testId=${test.id}")
        test
    }
    
    override suspend fun getTestById(testId: String): Result<MockTest> = runCatching {
        val entity = mockTestDao.getTestById(testId)
            ?: throw NoSuchElementException("Test not found")
        
        val questionIds = entity.questionIdsJson.toStringList()
        val questions = questionDao.getQuestionsByIds(questionIds).map { it.toDomain() }
        
        entity.toDomain(questions)
    }
    
    override suspend fun getUserTests(userId: String): Result<List<MockTest>> = runCatching {
        val entities = mockTestDao.getUserTests(userId)
        entities.map { entity ->
            val questionIds = entity.questionIdsJson.toStringList()
            val questions = questionDao.getQuestionsByIds(questionIds).map { it.toDomain() }
            entity.toDomain(questions)
        }
    }
    
    override fun getUserTestsFlow(userId: String): Flow<List<MockTest>> {
        return mockTestDao.getUserTestsFlow(userId).map { entities ->
            entities.map { entity ->
                val questionIds = entity.questionIdsJson.toStringList()
                val questions = questionDao.getQuestionsByIds(questionIds).map { it.toDomain() }
                entity.toDomain(questions)
            }
        }
    }
    
    override suspend fun updateTestAnswer(
        testId: String,
        questionId: String,
        selectedOptionIndex: Int
    ): Result<Unit> = runCatching {
        val test = getTestById(testId).getOrThrow()
        val updatedAnswers = test.userAnswers.toMutableMap()
        updatedAnswers[questionId] = selectedOptionIndex
        
        val updatedTest = test.copy(
            userAnswers = updatedAnswers,
            status = if (test.status == TestStatus.NOT_STARTED) TestStatus.IN_PROGRESS else test.status,
            startedAt = test.startedAt ?: Date()
        )
        
        mockTestDao.updateTest(updatedTest.toEntity())
    }
    
    override suspend fun submitTest(testId: String): Result<TestResult> = runCatching {
        val test = getTestById(testId).getOrThrow()
        
        var correctAnswers = 0
        var incorrectAnswers = 0
        val topicScores = mutableMapOf<String, Int>()
        val topicTotals = mutableMapOf<String, Int>()
        
        test.questions.forEach { question ->
            val topicId = question.curriculumTopicId
            topicTotals[topicId] = (topicTotals[topicId] ?: 0) + 1
            
            val userAnswer = test.userAnswers[question.id]
            if (userAnswer != null) {
                if (userAnswer == question.correctOptionIndex) {
                    correctAnswers++
                    topicScores[topicId] = (topicScores[topicId] ?: 0) + 1
                } else {
                    incorrectAnswers++
                }
            }
        }
        
        val unanswered = test.questions.size - correctAnswers - incorrectAnswers
        val scorePercentage = (correctAnswers.toDouble() / test.questions.size) * 100
        val passed = scorePercentage >= TestResult.PASS_MARK
        
        val result = TestResult(
            id = "result_$testId",
            testId = testId,
            userId = test.userId,
            totalQuestions = test.questions.size,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            unanswered = unanswered,
            scorePercentage = scorePercentage,
            passed = passed,
            topicScores = topicScores,
            topicTotals = topicTotals,
            completedAt = Date(),
            timeTakenSeconds = 0
        )
        
        // Update test status
        val updatedTest = test.copy(
            status = TestStatus.COMPLETED,
            completedAt = Date()
        )
        mockTestDao.updateTest(updatedTest.toEntity())
        
        // Save result
        mockTestDao.insertTestResult(result.toEntity())
        
        result
    }
    
    override suspend fun getTestResult(testId: String): Result<TestResult> = runCatching {
        val entity = mockTestDao.getTestResult(testId)
            ?: throw NoSuchElementException("Test result not found")
        entity.toDomain()
    }
    
    override suspend fun getUserTestResults(userId: String): Result<List<TestResult>> = runCatching {
        val entities = mockTestDao.getUserTestResults(userId)
        entities.map { it.toDomain() }
    }
    
    override suspend fun getUserAnalytics(userId: String): Result<PerformanceAnalytics> = runCatching {
        val results = getUserTestResults(userId).getOrThrow()
        
        if (results.isEmpty()) {
            return@runCatching PerformanceAnalytics(
                userId = userId,
                totalTestsTaken = 0,
                totalTestsPassed = 0,
                averageScore = 0.0,
                passRate = 0.0,
                weakAreas = emptyList(),
                recentScores = emptyList(),
                consecutivePasses = 0
            )
        }
        
        val totalTestsTaken = results.size
        val totalTestsPassed = results.count { it.passed }
        val averageScore = results.map { it.scorePercentage }.average()
        val passRate = (totalTestsPassed.toDouble() / totalTestsTaken) * 100
        val recentScores = results.take(10).map { it.scorePercentage }
        
        var consecutivePasses = 0
        for (result in results) {
            if (result.passed) consecutivePasses++ else break
        }
        
        PerformanceAnalytics(
            userId = userId,
            totalTestsTaken = totalTestsTaken,
            totalTestsPassed = totalTestsPassed,
            averageScore = averageScore,
            passRate = passRate,
            weakAreas = emptyList(),
            recentScores = recentScores,
            consecutivePasses = consecutivePasses,
            lastTestDate = results.firstOrNull()?.completedAt
        )
    }
}
