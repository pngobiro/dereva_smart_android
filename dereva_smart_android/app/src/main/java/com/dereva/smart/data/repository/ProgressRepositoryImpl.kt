package com.dereva.smart.data.repository

import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class ProgressRepositoryImpl(
    private val apiService: DerevaApiService
) : ProgressRepository {
    
    override suspend fun recordStudySession(session: StudySession): Result<Unit> = Result.success(Unit)
    
    override suspend fun getUserSessions(userId: String): Result<List<StudySession>> = Result.success(emptyList())
    
    override fun getUserSessionsFlow(userId: String): Flow<List<StudySession>> = flowOf(emptyList())
    
    override suspend fun getProgressSummary(userId: String): Result<ProgressSummary> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getUserAchievements(userId: String): Result<List<Achievement>> = Result.success(emptyList())
    
    override suspend fun awardAchievement(achievement: Achievement): Result<Unit> = Result.success(Unit)
    
    override suspend fun calculateStreak(userId: String): Result<StreakInfo> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getTotalStudyTime(userId: String): Result<Int> = Result.success(0)
    
    override suspend fun getCompletionPercentage(userId: String, totalLessons: Int): Result<Double> = Result.success(0.0)
}
