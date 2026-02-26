package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

// ---------------------------------------------------------------------------
// Quiz bank metadata (loaded from server list, not the full JSON)
// ---------------------------------------------------------------------------

@Parcelize
data class QuizBank(
    val id: String,
    val title: String,
    val description: String,
    val licenseCategory: String,
    val topicArea: String,
    val difficulty: String,
    val totalQuestions: Int,
    val timeLimit: Int,        // minutes
    val passingScore: Int,     // percentage (0–100)
    val isPremium: Boolean,
    val jsonUrl: String,
    val version: Int,
    val order: Int,
    val createdAt: Long,
    val updatedAt: Long
) : Parcelable

// ---------------------------------------------------------------------------
// Full quiz content (loaded from JSON file)
// ---------------------------------------------------------------------------

@Parcelize
data class QuizContent(
    val id: String,
    val title: String,
    val description: String,
    val licenseCategory: String,
    val topicArea: String,
    val difficulty: String,
    val timeLimit: Int,        // minutes
    val passingScore: Int,     // percentage (0–100)
    val isPaid: Boolean,
    val version: Int,
    val order: Int,
    val questions: List<QuizQuestion>
) : Parcelable

// ---------------------------------------------------------------------------
// Question
// ---------------------------------------------------------------------------

@Parcelize
data class QuizQuestion(
    val id: String,
    val type: QuestionType,

    /** Plain string or ContentObject (Map when deserialised from JSON). */
    val question: @RawValue Any,

    val points: Int,

    // -- Type-specific answer fields --

    /** multiple-choice / multiple-select option list. */
    val options: List<QuizOption>? = null,

    /** true-false correct answer. */
    val correctAnswer: Boolean? = null,

    /** fill-blank correct answer (single blank; for multi-blank use [blanks]). */
    val correctAnswerText: String? = null,

    /** fill-blank: per-blank accepted answers (multi-blank questions). */
    val blanks: List<QuizBlank>? = null,

    /** matching pairs. */
    val pairs: List<QuizPair>? = null,

    /** ordering items. */
    val items: List<QuizItem>? = null,

    /** short-answer: accepted answers. */
    val acceptedAnswers: List<String>? = null,

    // -- Scoring modifiers --

    /** multiple-select: award proportional marks. */
    val partialCredit: Boolean = false,

    /** fill-blank / short-answer: honour exact capitalisation. */
    val caseSensitive: Boolean = false,

    // -- Supporting content --

    /** Shown after submission. Plain string or ContentObject. */
    val explanation: @RawValue Any,

    /** Optional nudge shown during the quiz. Plain string or ContentObject. */
    val hint: @RawValue Any? = null,

    /**
     * Educational background shown WITH the question.
     * Must NOT reveal the correct answer.
     */
    val context: ContentObject? = null,

    // -- Deprecated fields kept for backwards compatibility --

    /** @deprecated Use [ContentObject.media] instead. */
    @Deprecated("Attach media via ContentObject.media")
    val media: QuizMedia? = null,

    /** @deprecated Use ContentObject with format=\"html\" instead. */
    @Deprecated("Use ContentObject with format=html")
    val richContent: QuizRichContent? = null
) : Parcelable

// ---------------------------------------------------------------------------
// Option
// ---------------------------------------------------------------------------

@Parcelize
data class QuizOption(
    val id: String,

    /** Plain string or ContentObject. */
    val text: @RawValue Any,

    val isCorrect: Boolean
) : Parcelable

// ---------------------------------------------------------------------------
// Type-specific sub-models
// ---------------------------------------------------------------------------

@Parcelize
data class QuizBlank(
    val id: String,
    val acceptedAnswers: List<String>,
    val caseSensitive: Boolean = false
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

// ---------------------------------------------------------------------------
// Media and rich-content models
// ---------------------------------------------------------------------------

@Parcelize
data class QuizMedia(
    /** image | video | audio */
    val type: String,
    val url: String,
    val caption: String? = null,
    /** before | after */
    val position: String = "before"
) : Parcelable

/** @deprecated Superseded by [ContentObject]. Retained for legacy JSON compatibility. */
@Deprecated("Use ContentObject with format=html")
@Parcelize
data class QuizRichContent(
    /** html | latex */
    val type: String,
    val content: String
) : Parcelable

/**
 * Unified content model.
 * Used for questions, hints, explanations, context, and option text.
 *
 * @property format REQUIRED — "text", "html", or "latex"
 * @property value  The content string (plain text, HTML markup, or LaTeX source)
 * @property media  Optional media attachment
 */
@Parcelize
data class ContentObject(
    val format: String,
    val value: String,
    val media: QuizMedia? = null
) : Parcelable

// ---------------------------------------------------------------------------
// Question type enum
// ---------------------------------------------------------------------------

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    MULTIPLE_SELECT,
    FILL_BLANK,
    MATCHING,
    ORDERING,
    SHORT_ANSWER;

    companion object {
        fun fromString(value: String): QuestionType =
            when (value.lowercase().replace("-", "_").replace(" ", "_")) {
                "multiple_choice"  -> MULTIPLE_CHOICE
                "true_false"       -> TRUE_FALSE
                "multiple_select"  -> MULTIPLE_SELECT
                "fill_blank"       -> FILL_BLANK
                "matching"         -> MATCHING
                "ordering"         -> ORDERING
                "short_answer"     -> SHORT_ANSWER
                else               -> MULTIPLE_CHOICE
            }
    }
}

// ---------------------------------------------------------------------------
// Attempt and result models
// ---------------------------------------------------------------------------

@Parcelize
data class QuizAttempt(
    val attemptId: String?,
    val score: Int,                // percentage (0–100)
    val passed: Boolean,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val timeTaken: Int,            // seconds
    val feedback: List<QuizFeedback>,
    val isGuest: Boolean
) : Parcelable

@Parcelize
data class QuizFeedback(
    val questionId: String,
    val isCorrect: Boolean,

    /**
     * Explanation shown in the results screen.
     * Stored as a plain string here (HTML stripped at submission time if needed).
     */
    val explanation: String,

    /** The answer the user submitted. String, Boolean, or List<String>. */
    val userAnswer: @RawValue Any? = null
) : Parcelable

/**
 * Used only for API communication — not Parcelable by design.
 * Do not pass between Android components.
 */
data class QuizAnswer(
    val questionId: String,
    val answer: Any?   // String | Boolean | List<String>
)

@Parcelize
data class QuizAttemptHistory(
    val id: String,
    val quizBankId: String,
    val startedAt: Long,
    val completedAt: Long,
    val timeTaken: Int,            // seconds
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,                // percentage
    val passed: Boolean
) : Parcelable
