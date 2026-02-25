package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val lessonId: String,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val completed: Boolean
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val badgeType: String,
    val earnedAt: Long,
    val titleEn: String,
    val titleSw: String,
    val descriptionEn: String,
    val descriptionSw: String
)
