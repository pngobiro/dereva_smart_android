package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    
    suspend fun recordStudySession(session: StudySession): Result<Unit>
    
    suspend fun getUserSessions(userId: String): Result<List<StudySession>>
    
    fun getUserSessionsFlow(userId: String): Flow<List<StudySession>>
    
    suspend fun getProgressSummary(userId: String): Result<ProgressSummary>
    
    suspend fun getUserAchievements(userId: String): Result<List<Achievement>>
    
    suspend fun awardAchievement(achievement: Achievement): Result<Unit>
    
    suspend fun calculateStreak(userId: String): Result<StreakInfo>
    
    suspend fun getTotalStudyTime(userId: String): Result<Int>
    
    suspend fun getCompletionPercentage(userId: String, totalLessons: Int): Result<Double>
}
