package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dereva.smart.domain.model.TestStatus
import java.util.Date

@Entity(tableName = "mock_tests")
data class MockTestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val questionIdsJson: String,
    val userAnswersJson: String,
    val createdAt: Long,
    val startedAt: Long?,
    val completedAt: Long?,
    val status: String,
    val durationMinutes: Int,
    val licenseCategory: String
)

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val userId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val unanswered: Int,
    val scorePercentage: Double,
    val passed: Boolean,
    val topicScoresJson: String,
    val topicTotalsJson: String,
    val completedAt: Long,
    val timeTakenSeconds: Int
)
