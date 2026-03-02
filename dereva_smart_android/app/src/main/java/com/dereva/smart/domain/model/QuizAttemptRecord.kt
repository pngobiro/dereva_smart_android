package com.dereva.smart.domain.model

import java.util.Date

data class QuizAttemptRecord(
    val id: String,
    val quizBankId: String,
    val quizTitle: String?,
    val category: String?,
    val startedAt: Date,
    val completedAt: Date?,
    val timeTakenSeconds: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercentage: Int,
    val passed: Boolean
)
