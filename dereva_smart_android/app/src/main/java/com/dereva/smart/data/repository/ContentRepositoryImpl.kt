package com.dereva.smart.data.repository

import com.dereva.smart.data.download.DownloadManager
import com.dereva.smart.data.local.dao.ContentDao
import com.dereva.smart.data.local.entity.*
import com.dereva.smart.data.remote.DerevaApiService
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class ContentRepositoryImpl(
    private val contentDao: ContentDao,
    private val downloadManager: DownloadManager,
    private val apiService: DerevaApiService
) : ContentRepository {
    
    override fun getAllModules(): Flow<List<Module>> {
        return contentDao.getAllModulesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getModulesByCategory(category: LicenseCategory): Flow<List<Module>> {
        return contentDao.getModulesByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    // Sync modules from API
    suspend fun syncModules(category: String? = null): Result<Unit> = runCatching {
        val response = apiService.getModules(category)
        if (response.isSuccessful) {
            // Clear existing modules for this category first
            category?.let {
                contentDao.deleteModulesByCategory(it)
            }
            
            response.body()?.forEach { moduleDto ->
                val module = ModuleEntity(
                    id = moduleDto.id,
                    title = moduleDto.title,
                    description = moduleDto.description,
                    orderIndex = moduleDto.orderIndex,
                    licenseCategory = moduleDto.category,
                    thumbnailUrl = moduleDto.iconUrl,
                    estimatedDuration = 0,
                    lessonCount = 0,
                    isDownloaded = false,
                    downloadSize = 0L,
                    status = "AVAILABLE",
                    completionPercentage = 0,
                    requiresSubscription = moduleDto.requiresSubscription,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                contentDao.insertModule(module)
            }
        }
    }
    
    // Sync lessons from API
    suspend fun syncLessons(moduleId: String): Result<Unit> = runCatching {
        val response = apiService.getLessons(moduleId)
        if (response.isSuccessful) {
            response.body()?.forEach { lessonDto ->
                val lesson = LessonEntity(
                    id = lessonDto.id,
                    moduleId = lessonDto.moduleId,
                    title = lessonDto.title,
                    description = lessonDto.description,
                    orderIndex = lessonDto.orderIndex,
                    contentType = lessonDto.type ?: "TEXT",
                    contentUrl = lessonDto.contentUrl,
                    contentText = lessonDto.contentText,
                    duration = lessonDto.duration,
                    isDownloaded = false,
                    isCompleted = false,
                    requiresSubscription = lessonDto.requiresSubscription,
                    lastAccessedAt = null,
                    createdAt = System.currentTimeMillis()
                )
                contentDao.insertLesson(lesson)
            }
        }
    }
    
    override fun getDownloadedModules(): Flow<List<Module>> {
        return contentDao.getDownloadedModules().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getModuleById(moduleId: String): Result<Module?> = runCatching {
        contentDao.getModuleById(moduleId)?.toDomain()
    }
    
    override suspend fun updateModuleProgress(
        moduleId: String,
        status: ModuleStatus,
        percentage: Int
    ): Result<Unit> = runCatching {
        contentDao.updateModuleProgress(moduleId, status.name, percentage)
    }
    
    override fun getLessonsByModule(moduleId: String): Flow<List<Lesson>> {
        return contentDao.getLessonsByModule(moduleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getLessonById(lessonId: String): Result<Lesson?> = runCatching {
        contentDao.getLessonById(lessonId)?.toDomain()
    }
    
    override suspend fun updateLessonCompletion(
        lessonId: String,
        isCompleted: Boolean
    ): Result<Unit> = runCatching {
        contentDao.updateLessonCompletion(lessonId, isCompleted, System.currentTimeMillis())
    }
    
    override suspend fun getMediaAssetsByLesson(lessonId: String): Result<List<MediaAsset>> = runCatching {
        contentDao.getMediaAssetsByLesson(lessonId).map { it.toDomain() }
    }
    
    override suspend fun getLessonProgress(
        userId: String,
        lessonId: String
    ): Result<LessonProgress?> = runCatching {
        contentDao.getLessonProgress(userId, lessonId)?.toDomain()
    }
    
    override suspend fun startLesson(
        userId: String,
        lessonId: String,
        moduleId: String
    ): Result<LessonProgress> = runCatching {
        val existing = contentDao.getLessonProgress(userId, lessonId)
        if (existing != null) {
            return@runCatching existing.toDomain()
        }
        
        val progress = LessonProgress(
            id = UUID.randomUUID().toString(),
            userId = userId,
            lessonId = lessonId,
            moduleId = moduleId,
            isCompleted = false,
            completionPercentage = 0,
            timeSpent = 0,
            lastPosition = 0,
            startedAt = Date(),
            completedAt = null,
            updatedAt = Date()
        )
        
        contentDao.insertLessonProgress(progress.toEntity())
        progress
    }
    
    override suspend fun updateLessonProgress(
        progressId: String,
        timeSpent: Int,
        position: Int
    ): Result<Unit> = runCatching {
        contentDao.updateLessonProgress(progressId, timeSpent, position, System.currentTimeMillis())
    }
    
    override suspend fun completeLessonProgress(progressId: String): Result<Unit> = runCatching {
        contentDao.markLessonCompleted(progressId, System.currentTimeMillis())
    }
    
    override fun getModuleLessonProgress(
        userId: String,
        moduleId: String
    ): Flow<List<LessonProgress>> {
        return contentDao.getModuleLessonProgress(userId, moduleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun downloadModule(
        moduleId: String,
        userId: String
    ): Result<ModuleDownload> = runCatching {
        val module = contentDao.getModuleById(moduleId)
            ?: throw Exception("Module not found")
        
        // Get lessons as list
        val lessonsList = mutableListOf<Lesson>()
        contentDao.getLessonsByModule(moduleId).collect { lessons ->
            lessonsList.addAll(lessons.map { it.toDomain() })
        }
        
        // Start download using DownloadManager
        val workIds = downloadManager.downloadModule(
            module = module.toDomain(),
            lessons = lessonsList,
            onlyOnWiFi = true
        )
        
        val download = ModuleDownload(
            id = UUID.randomUUID().toString(),
            moduleId = moduleId,
            userId = userId,
            status = DownloadStatus.DOWNLOADING,
            progress = 0,
            downloadedSize = 0,
            totalSize = module.downloadSize,
            startedAt = Date(),
            completedAt = null,
            errorMessage = null
        )
        
        download
    }
    
    override suspend fun getModuleDownload(
        moduleId: String,
        userId: String
    ): Result<ModuleDownload?> = runCatching {
        contentDao.getModuleDownload(moduleId, userId)?.toDomain()
    }
    
    override fun getActiveDownloads(userId: String): Flow<List<ModuleDownload>> {
        return contentDao.getActiveDownloads(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun pauseDownload(downloadId: String): Result<Unit> = runCatching {
        downloadManager.pauseDownload(downloadId)
    }
    
    override suspend fun resumeDownload(downloadId: String): Result<Unit> = runCatching {
        // Get download info from database
        // Resume using download manager
        // This is simplified - in production, you'd need to get module and lesson info
    }
    
    override suspend fun cancelDownload(downloadId: String): Result<Unit> = runCatching {
        // Get module ID from download
        // Cancel using download manager
        downloadManager.cancelModuleDownload(downloadId)
    }
    
    override suspend fun getContentStats(userId: String): Result<ContentStats> = runCatching {
        val totalModules = contentDao.getTotalModulesCount()
        val completedModules = contentDao.getCompletedModulesCount()
        val totalLessons = contentDao.getTotalLessonsCount()
        val completedLessons = contentDao.getCompletedLessonsCountTotal()
        val totalStudyTime = contentDao.getTotalStudyTime(userId) ?: 0
        val downloadedSize = contentDao.getTotalDownloadedSize() ?: 0L
        
        val completionPercentage = if (totalModules > 0) {
            (completedModules * 100) / totalModules
        } else 0
        
        ContentStats(
            totalModules = totalModules,
            completedModules = completedModules,
            totalLessons = totalLessons,
            completedLessons = completedLessons,
            totalStudyTime = totalStudyTime / 60, // Convert seconds to minutes
            downloadedSize = downloadedSize,
            completionPercentage = completionPercentage
        )
    }
    
    override suspend fun calculateModuleCompletion(moduleId: String): Result<Int> = runCatching {
        val totalLessons = contentDao.getTotalLessonsCount(moduleId)
        val completedLessons = contentDao.getCompletedLessonsCount(moduleId)
        
        if (totalLessons > 0) {
            (completedLessons * 100) / totalLessons
        } else 0
    }
    
    override suspend fun syncContent(): Result<Unit> = runCatching {
        // TODO: Implement actual sync with server
        // For now, this is a placeholder
    }
    
    override suspend fun checkForUpdates(): Result<List<Module>> = runCatching {
        // TODO: Implement update checking
        emptyList()
    }
    
    override suspend fun loadSampleContent(): Result<Unit> = runCatching {
        // Load sample modules
        val sampleModules = createSampleModules()
        contentDao.insertModules(sampleModules.map { it.toEntity() })
        
        // Load sample lessons for each module
        sampleModules.forEach { module ->
            val lessons = createSampleLessons(module.id)
            contentDao.insertLessons(lessons.map { it.toEntity() })
        }
    }
    
    private fun createSampleModules(): List<Module> {
        return listOf(
            Module(
                id = "module_1",
                title = "Road Signs and Markings",
                description = "Learn about all NTSA road signs, traffic lights, and road markings",
                orderIndex = 1,
                licenseCategory = LicenseCategory.B1,
                thumbnailUrl = null,
                estimatedDuration = 45,
                lessonCount = 5,
                isDownloaded = false,
                downloadSize = 15 * 1024 * 1024, // 15MB
                status = ModuleStatus.UNLOCKED
            ),
            Module(
                id = "module_2",
                title = "Traffic Rules and Regulations",
                description = "Understanding Kenya traffic laws and regulations",
                orderIndex = 2,
                licenseCategory = LicenseCategory.B1,
                thumbnailUrl = null,
                estimatedDuration = 60,
                lessonCount = 6,
                isDownloaded = false,
                downloadSize = 20 * 1024 * 1024, // 20MB
                status = ModuleStatus.LOCKED
            ),
            Module(
                id = "module_3",
                title = "Defensive Driving",
                description = "Learn defensive driving techniques for safe driving",
                orderIndex = 3,
                licenseCategory = LicenseCategory.B1,
                thumbnailUrl = null,
                estimatedDuration = 50,
                lessonCount = 5,
                isDownloaded = false,
                downloadSize = 25 * 1024 * 1024, // 25MB
                status = ModuleStatus.LOCKED
            ),
            Module(
                id = "module_4",
                title = "Vehicle Controls and Maintenance",
                description = "Understanding vehicle controls and basic maintenance",
                orderIndex = 4,
                licenseCategory = LicenseCategory.B1,
                thumbnailUrl = null,
                estimatedDuration = 40,
                lessonCount = 4,
                isDownloaded = false,
                downloadSize = 18 * 1024 * 1024, // 18MB
                status = ModuleStatus.LOCKED
            )
        )
    }
    
    private fun createSampleLessons(moduleId: String): List<Lesson> {
        return when (moduleId) {
            "module_1" -> listOf(
                Lesson(
                    id = "lesson_1_1",
                    moduleId = moduleId,
                    title = "Warning Signs",
                    description = "Learn about triangular warning signs",
                    orderIndex = 1,
                    contentType = ContentType.TEXT,
                    contentUrl = null,
                    contentText = """
                        # Warning Signs
                        
                        Warning signs are triangular with a red border and white background. They alert drivers to potential hazards ahead.
                        
                        ## Common Warning Signs:
                        - Pedestrian Crossing
                        - School Zone
                        - Sharp Bend
                        - Slippery Road
                        - Animals Crossing
                        
                        Always reduce speed when you see a warning sign and be prepared to stop if necessary.
                    """.trimIndent(),
                    duration = 10,
                    isDownloaded = false,
                    isCompleted = false
                ),
                Lesson(
                    id = "lesson_1_2",
                    moduleId = moduleId,
                    title = "Regulatory Signs",
                    description = "Understanding mandatory and prohibitory signs",
                    orderIndex = 2,
                    contentType = ContentType.TEXT,
                    contentUrl = null,
                    contentText = """
                        # Regulatory Signs
                        
                        Regulatory signs are circular and tell you what you must or must not do.
                        
                        ## Mandatory Signs (Blue):
                        - Minimum Speed
                        - Turn Left/Right
                        - Keep Left/Right
                        
                        ## Prohibitory Signs (Red):
                        - No Entry
                        - No Parking
                        - Speed Limit
                        - No Overtaking
                        
                        Disobeying regulatory signs is a traffic offense.
                    """.trimIndent(),
                    duration = 12,
                    isDownloaded = false,
                    isCompleted = false
                ),
                Lesson(
                    id = "lesson_1_3",
                    moduleId = moduleId,
                    title = "Information Signs",
                    description = "Learn about directional and information signs",
                    orderIndex = 3,
                    contentType = ContentType.TEXT,
                    contentUrl = null,
                    contentText = """
                        # Information Signs
                        
                        Information signs are rectangular and provide helpful information to drivers.
                        
                        ## Types:
                        - Direction Signs (Blue/Green)
                        - Service Signs (Blue)
                        - Tourist Signs (Brown)
                        
                        These signs help you navigate and find services along the road.
                    """.trimIndent(),
                    duration = 8,
                    isDownloaded = false,
                    isCompleted = false
                )
            )
            else -> emptyList()
        }
    }
}
