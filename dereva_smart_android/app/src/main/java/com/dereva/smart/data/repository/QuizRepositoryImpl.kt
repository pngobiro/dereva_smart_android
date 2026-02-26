package com.dereva.smart.data.repository

import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.data.remote.dto.*
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.QuizRepository

class QuizRepositoryImpl(
    private val apiService: DerevaApiService
) : QuizRepository {
    
    override suspend fun getQuizBanks(category: String?, isPremium: Boolean?): Result<List<QuizBank>> {
        return try {
            val response = apiService.getQuizBanks(category, isPremium)
            if (response.isSuccessful && response.body() != null) {
                val quizzes = response.body()!!.quizzes.map { it.toDomain() }
                Result.success(quizzes)
            } else {
                Result.failure(Exception("Failed to fetch quiz banks: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getQuizBank(quizId: String): Result<QuizBank> {
        return try {
            val response = apiService.getQuizBank(quizId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch quiz bank: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getQuizContent(quizId: String, token: String?): Result<QuizContent> {
        return try {
            val authHeader = token?.let { "Bearer $it" }
            val response = apiService.getQuizContent(quizId, authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch quiz content: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun submitQuizAttempt(
        quizId: String,
        answers: List<QuizAnswer>,
        timeTaken: Int,
        token: String?
    ): Result<QuizAttempt> {
        return try {
            val authHeader = token?.let { "Bearer $it" }
            val request = SubmitQuizRequest(
                answers = answers.map { QuizAnswerDto(it.questionId, it.answer) },
                timeTaken = timeTaken
            )
            val response = apiService.submitQuizAttempt(quizId, request, authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to submit quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getQuizAttempts(quizId: String, token: String): Result<List<QuizAttemptHistory>> {
        return try {
            val authHeader = "Bearer $token"
            val response = apiService.getQuizAttempts(quizId, authHeader)
            if (response.isSuccessful && response.body() != null) {
                val attempts = response.body()!!.attempts.map { it.toDomain() }
                Result.success(attempts)
            } else {
                Result.failure(Exception("Failed to fetch quiz attempts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension functions to convert DTOs to domain models
private fun QuizBankDto.toDomain() = QuizBank(
    id = id,
    title = title,
    description = description,
    licenseCategory = licenseCategory,
    topicArea = topicArea,
    difficulty = difficulty,
    totalQuestions = totalQuestions,
    timeLimit = timeLimit,
    passingScore = passingScore,
    isPremium = isPremium,
    jsonUrl = jsonUrl,
    version = version,
    order = order,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun QuizContentDto.toDomain() = QuizContent(
    id = id,
    title = title,
    description = description,
    licenseCategory = licenseCategory,
    topicArea = topicArea,
    difficulty = difficulty,
    timeLimit = timeLimit,
    passingScore = passingScore,
    isPaid = isPaid,
    version = version,
    order = order,
    questions = questions.map { it.toDomain() }
)

private fun QuizQuestionDto.toDomain() = QuizQuestion(
    id = id,
    type = QuestionType.fromString(type),
    question = question,
    points = points,
    options = options?.map { it.toDomain() },
    correctAnswer = correctAnswer,
    blanks = blanks?.map { it.toDomain() },
    pairs = pairs?.map { it.toDomain() },
    items = items?.map { it.toDomain() },
    acceptedAnswers = acceptedAnswers,
    partialCredit = partialCredit == true,
    caseSensitive = caseSensitive == true,
    explanation = explanation,
    hint = hint,
    context = context?.toDomain(),
    media = media?.toDomain(),
    richContent = richContent?.toDomain()
)

private fun QuizOptionDto.toDomain() = QuizOption(
    id = id,
    text = text,
    isCorrect = isCorrect
)

private fun QuizBlankDto.toDomain() = QuizBlank(
    id = id,
    acceptedAnswers = acceptedAnswers
)

private fun QuizPairDto.toDomain() = QuizPair(
    id = id,
    left = left,
    right = right
)

private fun QuizItemDto.toDomain() = QuizItem(
    id = id,
    text = text,
    correctPosition = correctPosition
)

private fun QuizMediaDto.toDomain() = QuizMedia(
    type = type,
    url = url,
    caption = caption,
    position = position ?: "before"
)

private fun QuizRichContentDto.toDomain() = QuizRichContent(
    type = type,
    content = content
)

private fun ContentObjectDto.toDomain() = com.dereva.smart.domain.model.ContentObject(
    format = format,
    value = value,
    media = media?.toDomain()
)

private fun QuizAttemptResponse.toDomain() = QuizAttempt(
    attemptId = attemptId,
    score = score,
    passed = passed,
    correctAnswers = correctAnswers,
    totalQuestions = totalQuestions,
    timeTaken = timeTaken,
    feedback = feedback.map { it.toDomain() },
    isGuest = isGuest
)

private fun QuizFeedbackDto.toDomain() = QuizFeedback(
    questionId = questionId,
    isCorrect = isCorrect,
    explanation = explanation,
    userAnswer = userAnswer
)

private fun QuizAttemptHistoryDto.toDomain() = QuizAttemptHistory(
    id = id,
    quizBankId = quizBankId,
    startedAt = startedAt,
    completedAt = completedAt,
    timeTaken = timeTaken,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    score = score,
    passed = passed
)
