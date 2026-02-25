package com.dereva.smart.data.repository

import com.dereva.smart.data.local.dao.SchoolDao
import com.dereva.smart.data.local.entity.*
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.MockTestRepository
import com.dereva.smart.domain.repository.ProgressRepository
import com.dereva.smart.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class SchoolRepositoryImpl(
    private val schoolDao: SchoolDao,
    private val progressRepository: ProgressRepository,
    private val mockTestRepository: MockTestRepository
) : SchoolRepository {
    
    override suspend fun getSchoolByCode(code: String): Result<DrivingSchool?> = runCatching {
        schoolDao.getSchoolByCode(code)?.toDomain()
    }
    
    override suspend fun getSchoolById(schoolId: String): Result<DrivingSchool?> = runCatching {
        schoolDao.getSchoolById(schoolId)?.toDomain()
    }
    
    override suspend fun getVerifiedSchools(): Result<List<DrivingSchool>> = runCatching {
        schoolDao.getVerifiedSchools().map { it.toDomain() }
    }
    
    override suspend fun linkToSchool(userId: String, schoolCode: String): Result<SchoolLinking> = runCatching {
        // Validate school code
        val school = schoolDao.getSchoolByCode(schoolCode)
            ?: throw IllegalArgumentException("Invalid school code")
        
        if (!school.isVerified) {
            throw IllegalArgumentException("School is not verified")
        }
        
        // Deactivate any existing linkings
        schoolDao.deactivateAllLinkings(userId)
        
        // Create new linking
        val linking = SchoolLinking(
            id = "linking_${System.currentTimeMillis()}",
            userId = userId,
            schoolId = school.id,
            linkedAt = Date(),
            isActive = true,
            progressSharingEnabled = true
        )
        
        schoolDao.insertSchoolLinking(linking.toEntity())
        
        // Sync module schedules
        syncModuleSchedules(school.id)
        
        linking
    }
    
    override suspend fun getActiveSchoolLinking(userId: String): Result<SchoolLinking?> = runCatching {
        schoolDao.getActiveSchoolLinking(userId)?.toDomain()
    }
    
    override fun getUserSchoolLinkingsFlow(userId: String): Flow<List<SchoolLinking>> {
        return schoolDao.getUserSchoolLinkingsFlow(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun unlinkFromSchool(userId: String): Result<Unit> = runCatching {
        schoolDao.deactivateAllLinkings(userId)
    }
    
    override suspend fun updateProgressSharing(linkingId: String, enabled: Boolean): Result<Unit> = runCatching {
        schoolDao.updateProgressSharing(linkingId, enabled)
    }
    
    override suspend fun getSchoolSchedules(schoolId: String): Result<List<ModuleSchedule>> = runCatching {
        schoolDao.getSchoolSchedules(schoolId).map { it.toDomain() }
    }
    
    override fun getSchoolSchedulesFlow(schoolId: String): Flow<List<ModuleSchedule>> {
        return schoolDao.getSchoolSchedulesFlow(schoolId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getUnlockedModules(schoolId: String): Result<List<ModuleSchedule>> = runCatching {
        schoolDao.getUnlockedModules(schoolId).map { it.toDomain() }
    }
    
    override suspend fun checkAndUnlockModules(schoolId: String): Result<List<ModuleSchedule>> = runCatching {
        val schedules = schoolDao.getSchoolSchedules(schoolId)
        val now = Date()
        val unlockedModules = mutableListOf<ModuleSchedule>()
        
        schedules.forEach { schedule ->
            if (!schedule.isUnlocked && Date(schedule.unlockDate) <= now) {
                schoolDao.unlockModule(schedule.id)
                unlockedModules.add(schedule.toDomain().copy(isUnlocked = true))
            }
        }
        
        unlockedModules
    }
    
    override suspend fun generateProgressReport(
        userId: String,
        schoolId: String
    ): Result<ProgressReport> = runCatching {
        // Get progress summary
        val progressSummary = progressRepository.getProgressSummary(userId).getOrThrow()
        
        // Get test analytics
        val analytics = mockTestRepository.getUserAnalytics(userId).getOrThrow()
        
        // Get module schedules
        val schedules = schoolDao.getSchoolSchedules(schoolId)
        val completedModules = schedules.count { it.isUnlocked }
        
        val report = ProgressReport(
            id = "report_${System.currentTimeMillis()}",
            userId = userId,
            schoolId = schoolId,
            reportDate = Date(),
            completedModules = completedModules,
            totalModules = schedules.size,
            averageTestScore = analytics.averageScore,
            totalStudyTime = progressSummary.totalStudyTimeMinutes,
            currentStreak = progressSummary.currentStreak,
            lastActivityDate = progressSummary.lastStudyDate ?: Date()
        )
        
        schoolDao.insertProgressReport(report.toEntity())
        report
    }
    
    override suspend fun getLatestProgressReport(
        userId: String,
        schoolId: String
    ): Result<ProgressReport?> = runCatching {
        schoolDao.getLatestProgressReport(userId, schoolId)?.toDomain()
    }
    
    override suspend fun shareProgressWithSchool(
        userId: String,
        schoolId: String
    ): Result<Unit> = runCatching {
        // Generate and save progress report
        generateProgressReport(userId, schoolId).getOrThrow()
        
        // In a real app, this would sync to backend
        // For now, just save locally
    }
    
    override suspend fun getSchoolLeaderboard(
        schoolId: String,
        limit: Int
    ): Result<List<LeaderboardEntry>> = runCatching {
        // This would typically come from backend
        // For now, return mock data
        emptyList()
    }
    
    override suspend fun getSchoolStats(schoolId: String): Result<SchoolStats> = runCatching {
        val school = schoolDao.getSchoolById(schoolId)
            ?: throw NoSuchElementException("School not found")
        
        SchoolStats(
            schoolId = schoolId,
            totalStudents = school.totalStudents,
            averageScore = school.averagePassRate,
            passRate = school.averagePassRate,
            topPerformers = emptyList()
        )
    }
    
    private suspend fun syncModuleSchedules(schoolId: String) {
        // In a real app, this would fetch from backend
        // For now, create sample schedules
        val sampleModules = listOf(
            "Road Signs and Markings",
            "Traffic Rules and Regulations",
            "Safe Driving Practices",
            "Vehicle Control",
            "Parking and Maneuvering",
            "Emergency Procedures"
        )
        
        val schedules = sampleModules.mapIndexed { index, moduleName ->
            val unlockDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, index * 7) // Unlock one per week
            }.time
            
            ModuleScheduleEntity(
                id = "schedule_${schoolId}_$index",
                schoolId = schoolId,
                moduleId = "module_$index",
                moduleName = moduleName,
                unlockDate = unlockDate.time,
                dueDate = null,
                isUnlocked = index == 0, // First module unlocked by default
                order = index + 1
            )
        }
        
        schoolDao.insertSchedules(schedules)
    }
}
