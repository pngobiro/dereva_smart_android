package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    
    // School Management
    suspend fun getSchoolByCode(code: String): Result<DrivingSchool?>
    
    suspend fun getSchoolById(schoolId: String): Result<DrivingSchool?>
    
    suspend fun getVerifiedSchools(): Result<List<DrivingSchool>>
    
    // Update user's school
    suspend fun updateUserSchool(userId: String, schoolId: String?): Result<Unit>
    
    // School Linking
    suspend fun linkToSchool(userId: String, schoolCode: String): Result<SchoolLinking>
    
    suspend fun getActiveSchoolLinking(userId: String): Result<SchoolLinking?>
    
    fun getUserSchoolLinkingsFlow(userId: String): Flow<List<SchoolLinking>>
    
    suspend fun unlinkFromSchool(userId: String): Result<Unit>
    
    suspend fun updateProgressSharing(linkingId: String, enabled: Boolean): Result<Unit>
    
    // Module Schedules
    suspend fun getSchoolSchedules(schoolId: String): Result<List<ModuleSchedule>>
    
    fun getSchoolSchedulesFlow(schoolId: String): Flow<List<ModuleSchedule>>
    
    suspend fun getUnlockedModules(schoolId: String): Result<List<ModuleSchedule>>
    
    suspend fun checkAndUnlockModules(schoolId: String): Result<List<ModuleSchedule>>
    
    // Progress Reporting
    suspend fun generateProgressReport(userId: String, schoolId: String): Result<ProgressReport>
    
    suspend fun getLatestProgressReport(userId: String, schoolId: String): Result<ProgressReport?>
    
    suspend fun shareProgressWithSchool(userId: String, schoolId: String): Result<Unit>
    
    // Leaderboard
    suspend fun getSchoolLeaderboard(schoolId: String, limit: Int = 10): Result<List<LeaderboardEntry>>
    
    suspend fun getSchoolStats(schoolId: String): Result<SchoolStats>
    
    suspend fun getSchoolProgress(schoolId: String, category: String? = null, limit: Int = 50): Result<List<SchoolProgressRecord>>
}
