package com.dereva.smart.data.repository

import com.dereva.smart.data.local.dao.ProgressDao
import com.dereva.smart.data.local.entity.*
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import java.util.concurrent.TimeUnit

class ProgressRepositoryImpl(
    private val progressDao: ProgressDao
) : ProgressRepository {
    
    override suspend fun recordStudySession(session: StudySession): Result<Unit> = runCatching {
        progressDao.insertSession(session.toEntity())
    }
    
    override suspend fun getUserSessions(userId: String): Result<List<StudySession>> = runCatching {
        progressDao.getUserSessions(userId).map { it.toDomain() }
    }
    
    override fun getUserSessionsFlow(userId: String): Flow<List<StudySession>> {
        return progressDao.getUserSessionsFlow(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getProgressSummary(userId: String): Result<ProgressSummary> = runCatching {
        val sessions = getUserSessions(userId).getOrThrow()
        val achievements = getUserAchievements(userId).getOrThrow()
        val totalStudyTime = getTotalStudyTime(userId).getOrThrow()
        val streakInfo = calculateStreak(userId).getOrThrow()
        
        // For now, assume 100 total lessons (this should come from content repository)
        val completionPercentage = getCompletionPercentage(userId, 100).getOrThrow()
        
        ProgressSummary(
            userId = userId,
            totalStudyTimeMinutes = totalStudyTime,
            completionPercentage = completionPercentage,
            currentStreak = streakInfo.currentStreak,
            longestStreak = streakInfo.longestStreak,
            lastStudyDate = streakInfo.lastStudyDate,
            badges = achievements
        )
    }
    
    override suspend fun getUserAchievements(userId: String): Result<List<Achievement>> = runCatching {
        progressDao.getUserAchievements(userId).map { it.toDomain() }
    }
    
    override suspend fun awardAchievement(achievement: Achievement): Result<Unit> = runCatching {
        progressDao.insertAchievement(achievement.toEntity())
    }
    
    override suspend fun calculateStreak(userId: String): Result<StreakInfo> = runCatching {
        val sessions = getUserSessions(userId).getOrThrow()
            .sortedByDescending { it.startTime }
        
        if (sessions.isEmpty()) {
            return@runCatching StreakInfo(
                currentStreak = 0,
                longestStreak = 0,
                lastStudyDate = null
            )
        }
        
        val lastStudyDate = sessions.first().startTime
        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0
        
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val today = calendar.time
        val studyDates = sessions.map { session ->
            calendar.time = session.startTime
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }.distinct().sortedDescending()
        
        // Calculate current streak
        var expectedDate = today
        for (date in studyDates) {
            val daysDiff = TimeUnit.MILLISECONDS.toDays(expectedDate.time - date.time)
            if (daysDiff == 0L || daysDiff == 1L) {
                currentStreak++
                calendar.time = date
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                expectedDate = calendar.time
            } else {
                break
            }
        }
        
        // Calculate longest streak
        tempStreak = 1
        for (i in 1 until studyDates.size) {
            val daysDiff = TimeUnit.MILLISECONDS.toDays(
                studyDates[i - 1].time - studyDates[i].time
            )
            if (daysDiff == 1L) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        longestStreak = maxOf(longestStreak, tempStreak, currentStreak)
        
        StreakInfo(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastStudyDate = lastStudyDate
        )
    }
    
    override suspend fun getTotalStudyTime(userId: String): Result<Int> = runCatching {
        progressDao.getTotalStudyTime(userId) ?: 0
    }
    
    override suspend fun getCompletionPercentage(
        userId: String,
        totalLessons: Int
    ): Result<Double> = runCatching {
        if (totalLessons == 0) return@runCatching 0.0
        
        val completedLessons = progressDao.getCompletedLessonsCount(userId)
        (completedLessons.toDouble() / totalLessons) * 100
    }
}
