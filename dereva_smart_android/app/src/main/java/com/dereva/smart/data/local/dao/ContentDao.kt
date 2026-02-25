package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    
    // Modules
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: ModuleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)
    
    @Query("SELECT * FROM modules ORDER BY orderIndex ASC")
    fun getAllModulesFlow(): Flow<List<ModuleEntity>>
    
    @Query("SELECT * FROM modules WHERE id = :moduleId")
    suspend fun getModuleById(moduleId: String): ModuleEntity?
    
    @Query("SELECT * FROM modules WHERE licenseCategory = :category ORDER BY orderIndex ASC")
    fun getModulesByCategory(category: String): Flow<List<ModuleEntity>>
    
    @Query("SELECT * FROM modules WHERE isDownloaded = 1")
    fun getDownloadedModules(): Flow<List<ModuleEntity>>
    
    @Query("UPDATE modules SET isDownloaded = :isDownloaded WHERE id = :moduleId")
    suspend fun updateModuleDownloadStatus(moduleId: String, isDownloaded: Boolean)
    
    @Query("UPDATE modules SET status = :status, completionPercentage = :percentage WHERE id = :moduleId")
    suspend fun updateModuleProgress(moduleId: String, status: String, percentage: Int)
    
    @Delete
    suspend fun deleteModule(module: ModuleEntity)
    
    @Query("DELETE FROM modules WHERE licenseCategory = :category")
    suspend fun deleteModulesByCategory(category: String)
    
    // Lessons
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)
    
    @Query("SELECT * FROM lessons WHERE moduleId = :moduleId ORDER BY orderIndex ASC")
    fun getLessonsByModule(moduleId: String): Flow<List<LessonEntity>>
    
    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: String): LessonEntity?
    
    @Query("UPDATE lessons SET isCompleted = :isCompleted, lastAccessedAt = :timestamp WHERE id = :lessonId")
    suspend fun updateLessonCompletion(lessonId: String, isCompleted: Boolean, timestamp: Long)
    
    @Query("UPDATE lessons SET isDownloaded = :isDownloaded WHERE id = :lessonId")
    suspend fun updateLessonDownloadStatus(lessonId: String, isDownloaded: Boolean)
    
    @Query("SELECT COUNT(*) FROM lessons WHERE moduleId = :moduleId AND isCompleted = 1")
    suspend fun getCompletedLessonsCount(moduleId: String): Int
    
    @Query("SELECT COUNT(*) FROM lessons WHERE moduleId = :moduleId")
    suspend fun getTotalLessonsCount(moduleId: String): Int
    
    // Media Assets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaAsset(asset: MediaAssetEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaAssets(assets: List<MediaAssetEntity>)
    
    @Query("SELECT * FROM media_assets WHERE lessonId = :lessonId")
    suspend fun getMediaAssetsByLesson(lessonId: String): List<MediaAssetEntity>
    
    @Query("UPDATE media_assets SET isDownloaded = :isDownloaded, localPath = :localPath WHERE id = :assetId")
    suspend fun updateMediaAssetDownload(assetId: String, isDownloaded: Boolean, localPath: String?)
    
    @Query("UPDATE media_assets SET downloadProgress = :progress WHERE id = :assetId")
    suspend fun updateMediaAssetProgress(assetId: String, progress: Int)
    
    // Lesson Progress
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonProgress(progress: LessonProgressEntity)
    
    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun getLessonProgress(userId: String, lessonId: String): LessonProgressEntity?
    
    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND moduleId = :moduleId")
    fun getModuleLessonProgress(userId: String, moduleId: String): Flow<List<LessonProgressEntity>>
    
    @Query("UPDATE lesson_progress SET timeSpent = :timeSpent, lastPosition = :position, updatedAt = :timestamp WHERE id = :progressId")
    suspend fun updateLessonProgress(progressId: String, timeSpent: Int, position: Int, timestamp: Long)
    
    @Query("UPDATE lesson_progress SET isCompleted = 1, completionPercentage = 100, completedAt = :timestamp WHERE id = :progressId")
    suspend fun markLessonCompleted(progressId: String, timestamp: Long)
    
    // Module Downloads
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleDownload(download: ModuleDownloadEntity)
    
    @Query("SELECT * FROM module_downloads WHERE moduleId = :moduleId AND userId = :userId ORDER BY startedAt DESC LIMIT 1")
    suspend fun getModuleDownload(moduleId: String, userId: String): ModuleDownloadEntity?
    
    @Query("UPDATE module_downloads SET status = :status, progress = :progress, downloadedSize = :downloadedSize WHERE id = :downloadId")
    suspend fun updateDownloadProgress(downloadId: String, status: String, progress: Int, downloadedSize: Long)
    
    @Query("UPDATE module_downloads SET status = :status, completedAt = :timestamp WHERE id = :downloadId")
    suspend fun updateDownloadStatus(downloadId: String, status: String, timestamp: Long?)
    
    @Query("SELECT * FROM module_downloads WHERE userId = :userId AND status = 'DOWNLOADING'")
    fun getActiveDownloads(userId: String): Flow<List<ModuleDownloadEntity>>
    
    // Content Sync
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncMetadata(metadata: ContentSyncMetadataEntity)
    
    @Query("SELECT * FROM content_sync_metadata WHERE entityType = :type AND entityId = :id")
    suspend fun getSyncMetadata(type: String, id: String): ContentSyncMetadataEntity?
    
    @Query("SELECT * FROM content_sync_metadata WHERE needsSync = 1")
    suspend fun getPendingSyncItems(): List<ContentSyncMetadataEntity>
    
    @Query("UPDATE content_sync_metadata SET needsSync = 0, syncStatus = 'SYNCED', lastSyncedAt = :timestamp WHERE id = :metadataId")
    suspend fun markSynced(metadataId: String, timestamp: Long)
    
    // Statistics
    @Query("SELECT COUNT(*) FROM modules")
    suspend fun getTotalModulesCount(): Int
    
    @Query("SELECT COUNT(*) FROM modules WHERE status = 'COMPLETED'")
    suspend fun getCompletedModulesCount(): Int
    
    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getTotalLessonsCount(): Int
    
    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    suspend fun getCompletedLessonsCountTotal(): Int
    
    @Query("SELECT SUM(timeSpent) FROM lesson_progress WHERE userId = :userId")
    suspend fun getTotalStudyTime(userId: String): Int?
    
    @Query("SELECT SUM(downloadSize) FROM modules WHERE isDownloaded = 1")
    suspend fun getTotalDownloadedSize(): Long?
    
    // Additional download management methods
    @Query("DELETE FROM module_downloads WHERE moduleId = :moduleId")
    suspend fun deleteModuleDownload(moduleId: String)
    
    @Query("UPDATE lessons SET contentUrl = :localPath WHERE id = :lessonId")
    suspend fun updateLessonLocalPath(lessonId: String, localPath: String)
    
    @Query("UPDATE modules SET isDownloaded = :isDownloaded WHERE id = :moduleId")
    suspend fun updateModuleDownloadedStatus(moduleId: String, isDownloaded: Boolean)
    
    @Query("UPDATE lessons SET contentUrl = NULL WHERE moduleId = :moduleId")
    suspend fun clearModuleLessonPaths(moduleId: String)
    
    @Query("SELECT * FROM module_downloads WHERE status = 'FAILED'")
    suspend fun getFailedDownloads(): List<ModuleDownloadEntity>
    
    @Query("UPDATE module_downloads SET status = :status, progress = :progress, errorMessage = :errorMessage WHERE moduleId = :moduleId")
    suspend fun updateModuleDownloadStatus(
        moduleId: String,
        status: String,
        progress: Int,
        errorMessage: String? = null
    )
}
