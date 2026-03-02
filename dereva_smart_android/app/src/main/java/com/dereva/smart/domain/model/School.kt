package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class DrivingSchool(
    val id: String,
    val name: String,
    val code: String,
    val location: String,
    val phoneNumber: String,
    val email: String,
    val isVerified: Boolean,
    val totalStudents: Int,
    val averagePassRate: Double,
    val createdAt: Date
) : Parcelable

@Parcelize
data class ModuleSchedule(
    val id: String,
    val schoolId: String,
    val moduleId: String,
    val moduleName: String,
    val unlockDate: Date,
    val dueDate: Date?,
    val isUnlocked: Boolean,
    val order: Int
) : Parcelable

@Parcelize
data class ProgressReport(
    val id: String,
    val userId: String,
    val schoolId: String,
    val reportDate: Date,
    val completedModules: Int,
    val totalModules: Int,
    val averageTestScore: Double,
    val totalStudyTime: Int,
    val currentStreak: Int,
    val lastActivityDate: Date
) : Parcelable

@Parcelize
data class SchoolLinking(
    val id: String,
    val userId: String,
    val schoolId: String,
    val linkedAt: Date,
    val isActive: Boolean,
    val progressSharingEnabled: Boolean
) : Parcelable

@Parcelize
data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val averageScore: Double,
    val testsCompleted: Int,
    val isCurrentUser: Boolean
) : Parcelable

@Parcelize
data class SchoolStats(
    val schoolId: String,
    val totalStudents: Int,
    val totalAttempts: Int,
    val averageScore: Double,
    val passRate: Double,
    val topPerformers: List<LeaderboardEntry>,
    val categoryStats: List<CategoryStat>
) : Parcelable

@Parcelize
data class CategoryStat(
    val category: String,
    val attempts: Int,
    val avgScore: Double,
    val passRate: Double
) : Parcelable

@Parcelize
data class SchoolProgressRecord(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val quizName: String,
    val category: String,
    val score: Int,
    val passed: Boolean,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeTaken: Int,
    val completedAt: Date
) : Parcelable
