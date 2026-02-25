package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.AchievementEntity
import com.dereva.smart.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY startTime DESC")
    suspend fun getUserSessions(userId: String): List<StudySessionEntity>
    
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getUserSessionsFlow(userId: String): Flow<List<StudySessionEntity>>
    
    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE userId = :userId")
    suspend fun getTotalStudyTime(userId: String): Int?
    
    @Query("SELECT COUNT(DISTINCT lessonId) FROM study_sessions WHERE userId = :userId AND completed = 1")
    suspend fun getCompletedLessonsCount(userId: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)
    
    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY earnedAt DESC")
    suspend fun getUserAchievements(userId: String): List<AchievementEntity>
    
    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY earnedAt DESC")
    fun getUserAchievementsFlow(userId: String): Flow<List<AchievementEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE userId = :userId AND badgeType = :badgeType)")
    suspend fun hasAchievement(userId: String, badgeType: String): Boolean
}
