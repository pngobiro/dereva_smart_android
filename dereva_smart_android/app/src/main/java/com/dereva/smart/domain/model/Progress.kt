package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class StudySession(
    val id: String,
    val userId: String,
    val lessonId: String,
    val startTime: Date,
    val endTime: Date,
    val durationMinutes: Int,
    val completed: Boolean
) : Parcelable

@Parcelize
data class ProgressSummary(
    val userId: String,
    val totalStudyTimeMinutes: Int,
    val completionPercentage: Double,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastStudyDate: Date?,
    val badges: List<Achievement>
) : Parcelable

@Parcelize
data class Achievement(
    val id: String,
    val userId: String,
    val badgeType: BadgeType,
    val earnedAt: Date,
    val titleEn: String,
    val titleSw: String,
    val descriptionEn: String,
    val descriptionSw: String
) : Parcelable {
    
    fun getTitle(language: Language): String {
        return when (language) {
            Language.ENGLISH -> titleEn
            Language.SWAHILI -> titleSw
        }
    }
    
    fun getDescription(language: Language): String {
        return when (language) {
            Language.ENGLISH -> descriptionEn
            Language.SWAHILI -> descriptionSw
        }
    }
}

enum class BadgeType {
    FIRST_LESSON,
    WEEK_STREAK,
    MONTH_STREAK,
    HUNDRED_LESSONS,
    FIRST_TEST_PASS,
    FIVE_TESTS_PASS,
    PERFECT_SCORE,
    SPEED_LEARNER,
    NIGHT_OWL,
    EARLY_BIRD,
    WEEKEND_WARRIOR,
    CONSISTENT_LEARNER
}

@Parcelize
data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastStudyDate: Date?
) : Parcelable
