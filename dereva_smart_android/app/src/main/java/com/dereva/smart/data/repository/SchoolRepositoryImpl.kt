package com.dereva.smart.data.repository

import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.*

class SchoolRepositoryImpl(
    private val progressRepository: ProgressRepository,
    private val mockTestRepository: MockTestRepository,
    private val apiService: DerevaApiService,
    private val authRepository: AuthRepository
) : SchoolRepository {
    
    // In-memory cache to simulate "Sync" behavior without local DB
    private val _schedulesFlow = MutableStateFlow<Map<String, List<ModuleSchedule>>>(emptyMap())
    private val _linkingsFlow = MutableStateFlow<Map<String, List<SchoolLinking>>>(emptyMap())
    private var progressSharingEnabled = true

    override suspend fun getSchoolByCode(code: String): Result<DrivingSchool?> = runCatching {
        val schools = getVerifiedSchools().getOrThrow()
        schools.find { it.code.equals(code, ignoreCase = true) }
    }
    
    override suspend fun getSchoolById(schoolId: String): Result<DrivingSchool?> = runCatching {
        val schools = getVerifiedSchools().getOrThrow()
        schools.find { it.id == schoolId }
    }
    
    override suspend fun getVerifiedSchools(): Result<List<DrivingSchool>> = runCatching {
        val response = apiService.getSchools(verified = true)
        if (!response.isSuccessful) {
            return@runCatching emptyList()
        }
        
        response.body()?.map { dto ->
            DrivingSchool(
                id = dto.id,
                name = dto.name,
                code = dto.id.take(6).uppercase(),
                location = dto.location,
                phoneNumber = dto.phone,
                email = dto.email ?: "",
                isVerified = dto.verified,
                totalStudents = 0,
                averagePassRate = 0.0,
                createdAt = Date()
            )
        } ?: emptyList()
    }
    
    override suspend fun updateUserSchool(userId: String, schoolId: String?): Result<Unit> = runCatching {
        val authState = authRepository.getAuthState()
        val token = authState.token ?: throw Exception("Not authenticated")
        
        val request = com.dereva.smart.data.remote.dto.UpdateSchoolRequest(schoolId)
        val response = apiService.updateUserSchool(userId, request, "Bearer $token")
        
        if (!response.isSuccessful) {
            throw Exception(response.errorBody()?.string() ?: "Failed to update school")
        }
    }
    
    override suspend fun linkToSchool(userId: String, schoolCode: String): Result<SchoolLinking> = runCatching {
        // Validate school code
        val school = getSchoolByCode(schoolCode).getOrThrow()
            ?: throw IllegalArgumentException("Invalid school code")
        
        if (!school.isVerified) {
            throw IllegalArgumentException("School is not verified")
        }
        
        // Update user on remote backend
        updateUserSchool(userId, school.id).getOrThrow()
        
        // Create new linking
        val linking = SchoolLinking(
            id = "linking_${System.currentTimeMillis()}",
            userId = userId,
            schoolId = school.id,
            linkedAt = Date(),
            isActive = true,
            progressSharingEnabled = progressSharingEnabled
        )
        
        // Update local state
        _linkingsFlow.value = _linkingsFlow.value.toMutableMap().apply {
            put(userId, listOf(linking))
        }
        
        syncModuleSchedules(school.id)
        
        linking
    }
    
    override suspend fun getActiveSchoolLinking(userId: String): Result<SchoolLinking?> = runCatching {
        // Try to get from local state first
        val linkings = _linkingsFlow.value[userId]
        val activeLinking = linkings?.find { it.isActive }
        
        if (activeLinking != null) {
            return@runCatching activeLinking
        }
        
        // If not in state, check if user has a school assigned in their profile
        val user = authRepository.getAuthState().user
        if (user?.drivingSchoolId != null) {
            val linking = SchoolLinking(
                id = "linking_${user.id}_${user.drivingSchoolId}",
                userId = user.id,
                schoolId = user.drivingSchoolId,
                linkedAt = Date(), // We don't have the exact date from backend, use now
                isActive = true,
                progressSharingEnabled = progressSharingEnabled
            )
            
            _linkingsFlow.value = _linkingsFlow.value.toMutableMap().apply {
                put(user.id, listOf(linking))
            }
            
            syncModuleSchedules(user.drivingSchoolId)
            
            return@runCatching linking
        }
        
        null
    }
    
    override fun getUserSchoolLinkingsFlow(userId: String): Flow<List<SchoolLinking>> {
        return _linkingsFlow.map { it[userId] ?: emptyList() }
    }
    
    override suspend fun unlinkFromSchool(userId: String): Result<Unit> = runCatching {
        updateUserSchool(userId, null).getOrThrow()
        
        _linkingsFlow.value = _linkingsFlow.value.toMutableMap().apply {
            put(userId, emptyList())
        }
    }
    
    override suspend fun updateProgressSharing(linkingId: String, enabled: Boolean): Result<Unit> = runCatching {
        progressSharingEnabled = enabled
        
        // Update all linkings in state to match the new preference
        val updatedMap = _linkingsFlow.value.mapValues { (_, linkings) ->
            linkings.map { if (it.id == linkingId) it.copy(progressSharingEnabled = enabled) else it }
        }
        _linkingsFlow.value = updatedMap
    }
    
    override suspend fun getSchoolSchedules(schoolId: String): Result<List<ModuleSchedule>> = runCatching {
        _schedulesFlow.value[schoolId] ?: emptyList()
    }
    
    override fun getSchoolSchedulesFlow(schoolId: String): Flow<List<ModuleSchedule>> {
        return _schedulesFlow.map { it[schoolId] ?: emptyList() }
    }
    
    override suspend fun getUnlockedModules(schoolId: String): Result<List<ModuleSchedule>> = runCatching {
        val schedules = _schedulesFlow.value[schoolId] ?: emptyList()
        schedules.filter { it.isUnlocked }
    }
    
    override suspend fun checkAndUnlockModules(schoolId: String): Result<List<ModuleSchedule>> = runCatching {
        val schedules = _schedulesFlow.value[schoolId] ?: emptyList()
        val now = Date()
        val unlockedModules = mutableListOf<ModuleSchedule>()
        var hasChanges = false
        
        val updatedSchedules = schedules.map { schedule ->
            if (!schedule.isUnlocked && schedule.unlockDate <= now) {
                hasChanges = true
                val unlocked = schedule.copy(isUnlocked = true)
                unlockedModules.add(unlocked)
                unlocked
            } else {
                schedule
            }
        }
        
        if (hasChanges) {
            _schedulesFlow.value = _schedulesFlow.value.toMutableMap().apply {
                put(schoolId, updatedSchedules)
            }
        }
        
        unlockedModules
    }
    
    override suspend fun generateProgressReport(
        userId: String,
        schoolId: String
    ): Result<ProgressReport> = runCatching {
        val progressSummary = progressRepository.getProgressSummary(userId).getOrThrow()
        val analytics = mockTestRepository.getUserAnalytics(userId).getOrThrow()
        val schedules = _schedulesFlow.value[schoolId] ?: emptyList()
        val completedModules = schedules.count { it.isUnlocked }
        
        ProgressReport(
            id = "report_${System.currentTimeMillis()}",
            userId = userId,
            schoolId = schoolId,
            reportDate = Date(),
            completedModules = completedModules,
            totalModules = schedules.size.takeIf { it > 0 } ?: 6,
            averageTestScore = analytics.averageScore,
            totalStudyTime = progressSummary.totalStudyTimeMinutes,
            currentStreak = progressSummary.currentStreak,
            lastActivityDate = progressSummary.lastStudyDate ?: Date()
        )
    }
    
    override suspend fun getLatestProgressReport(
        userId: String,
        schoolId: String
    ): Result<ProgressReport?> = runCatching {
        generateProgressReport(userId, schoolId).getOrThrow()
    }
    
    override suspend fun shareProgressWithSchool(
        userId: String,
        schoolId: String
    ): Result<Unit> = runCatching {
        // Mock remote sharing by just generating the report
        generateProgressReport(userId, schoolId).getOrThrow()
    }
    
    override suspend fun getSchoolLeaderboard(
        schoolId: String,
        limit: Int
    ): Result<List<LeaderboardEntry>> = runCatching {
        listOf(
            LeaderboardEntry(rank = 1, userId = "student_1", displayName = "John Doe", averageScore = 95.0, testsCompleted = 10, isCurrentUser = false),
            LeaderboardEntry(rank = 2, userId = "student_2", displayName = "Jane Smith", averageScore = 92.0, testsCompleted = 8, isCurrentUser = false),
            LeaderboardEntry(rank = 3, userId = "student_3", displayName = "Sam Johnson", averageScore = 88.0, testsCompleted = 12, isCurrentUser = false)
        ).take(limit)
    }
    
    override suspend fun getSchoolStats(schoolId: String): Result<SchoolStats> = runCatching {
        val token = authRepository.getAuthState().token ?: throw Exception("Not authenticated")
        
        val response = apiService.getSchoolStats(schoolId, "Bearer $token")
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch school stats: ${response.message()}")
        }
        
        val dto = response.body() ?: throw Exception("Empty response")
        
        SchoolStats(
            schoolId = schoolId,
            totalStudents = dto.totalStudents,
            totalAttempts = dto.totalAttempts,
            averageScore = dto.averageScore.toDouble(),
            passRate = dto.passRate.toDouble(),
            topPerformers = dto.topPerformers.mapIndexed { index, performer ->
                LeaderboardEntry(
                    rank = index + 1,
                    userId = performer.id,
                    displayName = performer.name,
                    averageScore = performer.avgScore.toDouble(),
                    testsCompleted = performer.attempts,
                    isCurrentUser = false
                )
            },
            categoryStats = dto.categoryStats.map { stat ->
                CategoryStat(
                    category = stat.category,
                    attempts = stat.attempts,
                    avgScore = stat.avgScore.toDouble(),
                    passRate = stat.passRate.toDouble()
                )
            }
        )
    }
    
    override suspend fun getSchoolProgress(schoolId: String, category: String?, limit: Int): Result<List<SchoolProgressRecord>> = runCatching {
        val token = authRepository.getAuthState().token ?: throw Exception("Not authenticated")
        
        val response = apiService.getSchoolProgress(schoolId, limit, category, "Bearer $token")
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch school progress: ${response.message()}")
        }
        
        val dto = response.body() ?: throw Exception("Empty response")
        
        dto.progress.map { record ->
            SchoolProgressRecord(
                id = record.id,
                userId = record.userId,
                userName = record.userName,
                userPhone = record.userPhone,
                quizName = record.quizName,
                category = record.category,
                score = record.score,
                passed = record.passed,
                totalQuestions = record.totalQuestions,
                correctAnswers = record.correctAnswers,
                timeTaken = record.timeTaken,
                completedAt = Date(record.completedAt)
            )
        }
    }
    
    private suspend fun syncModuleSchedules(schoolId: String) {
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
            
            ModuleSchedule(
                id = "schedule_${schoolId}_$index",
                schoolId = schoolId,
                moduleId = "module_$index",
                moduleName = moduleName,
                unlockDate = unlockDate,
                dueDate = null,
                isUnlocked = index == 0, // First module unlocked by default
                order = index + 1
            )
        }
        
        _schedulesFlow.value = _schedulesFlow.value.toMutableMap().apply {
            put(schoolId, schedules)
        }
    }
}
