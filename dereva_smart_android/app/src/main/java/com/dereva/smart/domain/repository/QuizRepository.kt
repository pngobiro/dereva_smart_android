package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*

interface QuizRepository {
    suspend fun getQuizBanks(category: String? = null, isPremium: Boolean? = null): Result<List<QuizBank>>
    suspend fun getQuizBank(quizId: String): Result<QuizBank>
    suspend fun getQuizContent(quizId: String, token: String? = null): Result<QuizContent>
    suspend fun submitQuizAttempt(
        quizId: String,
        answers: List<QuizAnswer>,
        timeTaken: Int,
        token: String? = null
    ): Result<QuizAttempt>
    suspend fun getQuizAttempts(quizId: String, token: String): Result<List<QuizAttemptHistory>>
}
