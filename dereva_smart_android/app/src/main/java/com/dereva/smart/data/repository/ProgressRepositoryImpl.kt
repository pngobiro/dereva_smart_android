package com.dereva.smart.data.repository

import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.data.remote.dto.StudySessionDto
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.AuthRepository
import com.dereva.smart.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class ProgressRepositoryImpl(
    private val apiService: DerevaApiService,
    private val authRepository: AuthRepository
) : ProgressRepository {
    
    override suspend fun recordStudySession(session: StudySession): Result<Unit> = runCatching {
        val token = authRepository.getAuthState().token ?: throw Exception("Not authenticated")
        
        val dto = StudySessionDto(
            userId = session.userId,
            lessonId = session.lessonId,
            startTime = session.startTime.time,
            endTime = session.endTime.time,
            durationMinutes = session.durationMinutes,
            completed = session.completed
        )
        
        val response = apiService.recordStudySession(dto, "Bearer $token")
        if (!response.isSuccessful) {
            throw Exception("Failed to record session: ${response.message()}")
        }
    }
    
    override suspend fun getUserSessions(userId: String): Result<List<StudySession>> = Result.success(emptyList())
    
    override fun getUserSessionsFlow(userId: String): Flow<List<StudySession>> = flowOf(emptyList())
    
    override suspend fun getProgressSummary(userId: String): Result<ProgressSummary> = runCatching {
        val token = authRepository.getAuthState().token ?: throw Exception("Not authenticated")
        
        val response = apiService.getProgressSummary(userId, "Bearer $token")
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch progress: ${response.message()}")
        }
        
        val dto = response.body() ?: throw Exception("Empty response")
        
        ProgressSummary(
            userId = dto.userId,
            totalStudyTimeMinutes = dto.totalStudyTimeMinutes,
            completionPercentage = dto.completionPercentage,
            currentStreak = dto.currentStreak,
            longestStreak = dto.longestStreak,
            lastStudyDate = dto.lastStudyDate?.let { Date(it) },
            badges = dto.badges.map { badge ->
                Achievement(
                    id = badge.id,
                    userId = badge.userId,
                    badgeType = parseBadgeType(badge.badgeType),
                    earnedAt = Date(badge.earnedAt),
                    titleEn = badge.titleEn,
                    titleSw = badge.titleSw,
                    descriptionEn = badge.descriptionEn,
                    descriptionSw = badge.descriptionSw
                )
            }
        )
    }
    
    private fun parseBadgeType(type: String): BadgeType {
        return try {
            BadgeType.valueOf(type.uppercase())
        } catch (e: Exception) {
            BadgeType.FIRST_LESSON
        }
    }
    
    override suspend fun getUserAchievements(userId: String): Result<List<Achievement>> = Result.success(emptyList())
    
    override suspend fun awardAchievement(achievement: Achievement): Result<Unit> = Result.success(Unit)
    
    override suspend fun calculateStreak(userId: String): Result<StreakInfo> = runCatching {
        val summary = getProgressSummary(userId).getOrThrow()
        StreakInfo(
            currentStreak = summary.currentStreak,
            longestStreak = summary.longestStreak,
            lastStudyDate = summary.lastStudyDate
        )
    }
    
    override suspend fun getTotalStudyTime(userId: String): Result<Int> = runCatching {
        val summary = getProgressSummary(userId).getOrThrow()
        summary.totalStudyTimeMinutes
    }
    
    override suspend fun getCompletionPercentage(userId: String, totalLessons: Int): Result<Double> = runCatching {
        val summary = getProgressSummary(userId).getOrThrow()
        summary.completionPercentage
    }
    
    override suspend fun getUserQuizAttempts(userId: String): Result<List<QuizAttemptRecord>> = runCatching {
        val token = authRepository.getAuthState().token ?: throw Exception("Not authenticated")
        
        val response = apiService.getUserQuizAttempts(userId, "Bearer $token")
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch quiz attempts: ${response.message()}")
        }
        
        val dto = response.body() ?: throw Exception("Empty response")
        
        dto.attempts.map { attempt ->
            QuizAttemptRecord(
                id = attempt.id,
                quizBankId = attempt.quizBankId,
                quizTitle = attempt.quizTitle,
                category = attempt.category,
                startedAt = Date(attempt.startedAt),
                completedAt = if (attempt.completedAt > 0) Date(attempt.completedAt) else null,
                timeTakenSeconds = attempt.timeTaken,
                totalQuestions = attempt.totalQuestions,
                correctAnswers = attempt.correctAnswers,
                scorePercentage = attempt.score,
                passed = attempt.passed
            )
        }
    }
}
