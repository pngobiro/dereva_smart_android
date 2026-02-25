package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    
    // Modules
    fun getAllModules(): Flow<List<Module>>
    
    fun getModulesByCategory(category: LicenseCategory): Flow<List<Module>>
    
    fun getDownloadedModules(): Flow<List<Module>>
    
    suspend fun getModuleById(moduleId: String): Result<Module?>
    
    suspend fun updateModuleProgress(moduleId: String, status: ModuleStatus, percentage: Int): Result<Unit>
    
    // Lessons
    fun getLessonsByModule(moduleId: String): Flow<List<Lesson>>
    
    suspend fun getLessonById(lessonId: String): Result<Lesson?>
    
    suspend fun updateLessonCompletion(lessonId: String, isCompleted: Boolean): Result<Unit>
    
    suspend fun getMediaAssetsByLesson(lessonId: String): Result<List<MediaAsset>>
    
    // Progress
    suspend fun getLessonProgress(userId: String, lessonId: String): Result<LessonProgress?>
    
    suspend fun startLesson(userId: String, lessonId: String, moduleId: String): Result<LessonProgress>
    
    suspend fun updateLessonProgress(
        progressId: String,
        timeSpent: Int,
        position: Int
    ): Result<Unit>
    
    suspend fun completeLessonProgress(progressId: String): Result<Unit>
    
    fun getModuleLessonProgress(userId: String, moduleId: String): Flow<List<LessonProgress>>
    
    // Downloads
    suspend fun downloadModule(moduleId: String, userId: String): Result<ModuleDownload>
    
    suspend fun getModuleDownload(moduleId: String, userId: String): Result<ModuleDownload?>
    
    fun getActiveDownloads(userId: String): Flow<List<ModuleDownload>>
    
    suspend fun pauseDownload(downloadId: String): Result<Unit>
    
    suspend fun resumeDownload(downloadId: String): Result<Unit>
    
    suspend fun cancelDownload(downloadId: String): Result<Unit>
    
    // Statistics
    suspend fun getContentStats(userId: String): Result<ContentStats>
    
    suspend fun calculateModuleCompletion(moduleId: String): Result<Int>
    
    // Sync
    suspend fun syncContent(): Result<Unit>
    
    suspend fun checkForUpdates(): Result<List<Module>>
    
    // Sample Data (for development)
    suspend fun loadSampleContent(): Result<Unit>
}
