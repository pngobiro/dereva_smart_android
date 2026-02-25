package com.dereva.smart.data.download

import android.content.Context
import androidx.work.*
import com.dereva.smart.data.local.dao.ContentDao
import com.dereva.smart.domain.model.DownloadStatus
import com.dereva.smart.domain.model.Lesson
import com.dereva.smart.domain.model.Module
import com.dereva.smart.domain.model.ModuleDownload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Manager for handling content downloads from Cloudflare R2
 */
class DownloadManager(
    private val context: Context,
    private val contentDao: ContentDao
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Download a single lesson
     */
    fun downloadLesson(
        moduleId: String,
        lesson: Lesson,
        onlyOnWiFi: Boolean = true
    ): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (onlyOnWiFi) NetworkType.UNMETERED else NetworkType.CONNECTED
            )
            .setRequiresStorageNotLow(true)
            .build()
        
        val inputData = workDataOf(
            DownloadWorker.KEY_MODULE_ID to moduleId,
            DownloadWorker.KEY_LESSON_ID to lesson.id,
            DownloadWorker.KEY_REMOTE_URL to lesson.contentUrl,
            DownloadWorker.KEY_CONTENT_TYPE to lesson.contentType.name
        )
        
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag("download_${lesson.id}")
            .addTag("module_$moduleId")
            .build()
        
        workManager.enqueue(downloadRequest)
        
        return downloadRequest.id
    }
    
    /**
     * Download an entire module (all lessons)
     */
    suspend fun downloadModule(
        module: Module,
        lessons: List<Lesson>,
        onlyOnWiFi: Boolean = true
    ): List<UUID> {
        val workIds = mutableListOf<UUID>()
        
        // Create download record
        val download = ModuleDownload(
            id = UUID.randomUUID().toString(),
            moduleId = module.id,
            userId = "user123", // TODO: Get from auth
            status = DownloadStatus.PENDING,
            progress = 0,
            downloadedSize = 0,
            totalSize = module.downloadSize,
            startedAt = Date(),
            completedAt = null,
            errorMessage = null
        )
        
        contentDao.insertModuleDownload(download.toEntity())
        
        // Queue all lessons for download
        lessons.forEach { lesson ->
            if (lesson.contentUrl?.startsWith("http") == true) {
                val workId = downloadLesson(module.id, lesson, onlyOnWiFi)
                workIds.add(workId)
            }
        }
        
        return workIds
    }
    
    /**
     * Cancel a lesson download
     */
    fun cancelLessonDownload(lessonId: String) {
        workManager.cancelAllWorkByTag("download_$lessonId")
    }
    
    /**
     * Cancel all downloads for a module
     */
    suspend fun cancelModuleDownload(moduleId: String) {
        workManager.cancelAllWorkByTag("module_$moduleId")
        
        // Update status in database
        contentDao.updateModuleDownloadStatus(
            moduleId = moduleId,
            status = DownloadStatus.CANCELLED.name,
            progress = 0,
            errorMessage = "Cancelled by user"
        )
    }
    
    /**
     * Pause a download (cancel and mark as paused)
     */
    suspend fun pauseDownload(downloadId: String) {
        workManager.cancelWorkById(UUID.fromString(downloadId))
        
        // Update status in database
        contentDao.updateDownloadStatus(
            downloadId = downloadId,
            status = DownloadStatus.PAUSED.name,
            timestamp = null
        )
    }
    
    /**
     * Resume a paused download
     */
    suspend fun resumeDownload(
        moduleId: String,
        lesson: Lesson,
        onlyOnWiFi: Boolean = true
    ): UUID {
        // Update status to pending
        contentDao.updateModuleDownloadStatus(
            moduleId = moduleId,
            status = DownloadStatus.PENDING.name,
            progress = 0,
            errorMessage = null
        )
        
        // Start download
        return downloadLesson(moduleId, lesson, onlyOnWiFi)
    }
    
    /**
     * Get download progress for a work request
     */
    fun getDownloadProgress(workId: UUID): Flow<WorkInfo?> {
        return workManager.getWorkInfoByIdFlow(workId)
    }
    
    /**
     * Get all active downloads
     */
    fun getActiveDownloads(): Flow<List<WorkInfo>> {
        return workManager.getWorkInfosByTagFlow("download")
    }
    
    /**
     * Get module download progress
     */
    fun getModuleDownloadProgress(moduleId: String): Flow<List<WorkInfo>> {
        return workManager.getWorkInfosByTagFlow("module_$moduleId")
    }
    
    /**
     * Check if a module is being downloaded
     */
    fun isModuleDownloading(moduleId: String): Flow<Boolean> {
        return getModuleDownloadProgress(moduleId).map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }
    }
    
    /**
     * Get total download progress for a module
     */
    fun getModuleTotalProgress(moduleId: String): Flow<Int> {
        return getModuleDownloadProgress(moduleId).map { workInfos ->
            if (workInfos.isEmpty()) return@map 0
            
            val totalProgress = workInfos.sumOf { workInfo ->
                val current = workInfo.progress.getLong(DownloadWorker.PROGRESS_CURRENT, 0)
                val max = workInfo.progress.getLong(DownloadWorker.PROGRESS_MAX, 1)
                if (max > 0) ((current.toFloat() / max.toFloat()) * 100).toInt() else 0
            }
            
            totalProgress / workInfos.size
        }
    }
    
    /**
     * Delete downloaded module content
     */
    suspend fun deleteModuleDownload(moduleId: String) {
        // Cancel any active downloads
        cancelModuleDownload(moduleId)
        
        // Delete local files
        val downloadDir = context.filesDir.resolve("downloads/modules/$moduleId")
        if (downloadDir.exists()) {
            downloadDir.deleteRecursively()
        }
        
        // Update database
        contentDao.deleteModuleDownload(moduleId)
        contentDao.updateModuleDownloadedStatus(moduleId, false)
        
        // Clear lesson local paths
        contentDao.clearModuleLessonPaths(moduleId)
    }
    
    /**
     * Get storage usage for downloads
     */
    fun getDownloadStorageUsage(): Long {
        val downloadDir = context.filesDir.resolve("downloads")
        return if (downloadDir.exists()) {
            downloadDir.walkTopDown()
                .filter { it.isFile }
                .map { it.length() }
                .sum()
        } else {
            0L
        }
    }
    
    /**
     * Clean up failed downloads
     */
    suspend fun cleanupFailedDownloads() {
        val failedDownloads = contentDao.getFailedDownloads()
        
        failedDownloads.forEach { download ->
            val downloadDir = context.filesDir.resolve("downloads/modules/${download.moduleId}")
            if (downloadDir.exists()) {
                downloadDir.deleteRecursively()
            }
            contentDao.deleteModuleDownload(download.moduleId)
        }
    }
    
    /**
     * Retry failed download
     */
    suspend fun retryFailedDownload(
        moduleId: String,
        lesson: Lesson,
        onlyOnWiFi: Boolean = true
    ): UUID {
        // Clear error status
        contentDao.updateModuleDownloadStatus(
            moduleId = moduleId,
            status = DownloadStatus.PENDING.name,
            progress = 0,
            errorMessage = null
        )
        
        // Start download
        return downloadLesson(moduleId, lesson, onlyOnWiFi)
    }
}

// Extension function to convert ModuleDownload to Entity
private fun ModuleDownload.toEntity(): com.dereva.smart.data.local.entity.ModuleDownloadEntity {
    return com.dereva.smart.data.local.entity.ModuleDownloadEntity(
        id = id,
        moduleId = moduleId,
        userId = userId,
        status = status.name,
        progress = progress,
        downloadedSize = downloadedSize,
        totalSize = totalSize,
        startedAt = startedAt.time,
        completedAt = completedAt?.time,
        errorMessage = errorMessage
    )
}
