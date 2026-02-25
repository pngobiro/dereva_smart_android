package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

enum class TestStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED, ABANDONED
}

@Parcelize
data class MockTest(
    val id: String,
    val userId: String,
    val questions: List<Question>,
    val userAnswers: Map<String, Int>,
    val createdAt: Date,
    val startedAt: Date? = null,
    val completedAt: Date? = null,
    val status: TestStatus,
    val durationMinutes: Int,
    val licenseCategory: String
) : Parcelable {
    
    val progress: Float
        get() = if (questions.isEmpty()) 0f 
                else userAnswers.size.toFloat() / questions.size.toFloat()
    
    val timeRemaining: Long?
        get() = startedAt?.let { start ->
            val elapsed = (Date().time - start.time) / 1000 / 60
            (durationMinutes - elapsed).coerceAtLeast(0)
        }
}

@Parcelize
data class TestResult(
    val id: String,
    val testId: String,
    val userId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val unanswered: Int,
    val scorePercentage: Double,
    val passed: Boolean,
    val topicScores: Map<String, Int>,
    val topicTotals: Map<String, Int>,
    val completedAt: Date,
    val timeTakenSeconds: Int
) : Parcelable {
    
    companion object {
        const val PASS_MARK = 80.0
    }
}
