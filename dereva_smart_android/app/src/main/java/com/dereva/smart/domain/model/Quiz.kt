package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizBank(
    val id: String,
    val title: String,
    val description: String,
    val licenseCategory: String,
    val topicArea: String,
    val difficulty: String,
    val totalQuestions: Int,
    val timeLimit: Int, // in minutes
    val passingScore: Int, // percentage
    val isPremium: Boolean,
    val jsonUrl: String,
    val version: Int,
    val order: Int,
    val createdAt: Long,
    val updatedAt: Long
) : Parcelable

@Parcelize
data class QuizContent(
    val id: String,
    val title: String,
    val description: String,
    val licenseCategory: String,
    val topicArea: String,
    val difficulty: String,
    val timeLimit: Int,
    val passingScore: Int,
    val isPaid: Boolean,
    val version: Int,
    val order: Int,
    val questions: List<QuizQuestion>
) : Parcelable

@Parcelize
data class QuizQuestion(
    val id: String,
    val type: QuestionType,
    val question: String,
    val points: Int,
    val options: List<QuizOption>? = null,
    val correctAnswer: Boolean? = null, // for true-false
    val blanks: List<QuizBlank>? = null, // for fill-blank
    val pairs: List<QuizPair>? = null, // for matching
    val items: List<QuizItem>? = null, // for ordering
    val acceptedAnswers: List<String>? = null, // for short-answer
    val partialCredit: Boolean? = null,
    val caseSensitive: Boolean? = null,
    val explanation: String,
    val hint: String? = null,
    val media: QuizMedia? = null,
    val richContent: QuizRichContent? = null
) : Parcelable

@Parcelize
data class QuizOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean
) : Parcelable

@Parcelize
data class QuizBlank(
    val id: String,
    val acceptedAnswers: List<String>
) : Parcelable

@Parcelize
data class QuizPair(
    val id: String,
    val left: String,
    val right: String
) : Parcelable

@Parcelize
data class QuizItem(
    val id: String,
    val text: String,
    val correctPosition: Int
) : Parcelable

@Parcelize
data class QuizMedia(
    val type: String, // image, video, audio
    val url: String,
    val caption: String? = null
) : Parcelable

@Parcelize
data class QuizRichContent(
    val type: String, // html, table, latex
    val content: String
) : Parcelable

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    MULTIPLE_SELECT,
    FILL_BLANK,
    MATCHING,
    ORDERING,
    SHORT_ANSWER;
    
    companion object {
        fun fromString(value: String): QuestionType {
            return when (value.lowercase().replace("-", "_")) {
                "multiple_choice" -> MULTIPLE_CHOICE
                "true_false" -> TRUE_FALSE
                "multiple_select" -> MULTIPLE_SELECT
                "fill_blank" -> FILL_BLANK
                "matching" -> MATCHING
                "ordering" -> ORDERING
                "short_answer" -> SHORT_ANSWER
                else -> MULTIPLE_CHOICE
            }
        }
    }
}

@Parcelize
data class QuizAttempt(
    val attemptId: String?,
    val score: Int,
    val passed: Boolean,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val timeTaken: Int, // in seconds
    val feedback: List<QuizFeedback>,
    val isGuest: Boolean
) : Parcelable

@Parcelize
data class QuizFeedback(
    val questionId: String,
    val isCorrect: Boolean,
    val explanation: String,
    val userAnswer: String? = null
) : Parcelable

@Parcelize
data class QuizAnswer(
    val questionId: String,
    val answer: Any? // Can be String, Boolean, List<String>, etc.
) : Parcelable

@Parcelize
data class QuizAttemptHistory(
    val id: String,
    val quizBankId: String,
    val startedAt: Long,
    val completedAt: Long,
    val timeTaken: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val passed: Boolean
) : Parcelable
