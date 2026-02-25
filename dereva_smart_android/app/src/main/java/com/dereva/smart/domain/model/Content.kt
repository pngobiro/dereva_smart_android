package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

enum class ContentType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    INTERACTIVE
}

enum class ModuleStatus {
    LOCKED,
    UNLOCKED,
    IN_PROGRESS,
    COMPLETED
}

@Parcelize
data class Module(
    val id: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val licenseCategory: LicenseCategory,
    val thumbnailUrl: String?,
    val estimatedDuration: Int, // minutes
    val lessonCount: Int,
    val isDownloaded: Boolean = false,
    val downloadSize: Long = 0, // bytes
    val status: ModuleStatus = ModuleStatus.LOCKED,
    val completionPercentage: Int = 0,
    val requiresSubscription: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable {
    
    val isAvailableOffline: Boolean
        get() = isDownloaded
    
    val formattedSize: String
        get() {
            val mb = downloadSize / (1024.0 * 1024.0)
            return String.format("%.1f MB", mb)
        }
}

@Parcelize
data class Lesson(
    val id: String,
    val moduleId: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val contentType: ContentType,
    val contentUrl: String?,
    val contentText: String?,
    val duration: Int, // minutes
    val isDownloaded: Boolean = false,
    val isCompleted: Boolean = false,
    val requiresSubscription: Boolean = false,
    val lastAccessedAt: Date? = null,
    val createdAt: Date = Date()
) : Parcelable {
    
    val isAvailableOffline: Boolean
        get() = isDownloaded || contentType == ContentType.TEXT
}

@Parcelize
data class MediaAsset(
    val id: String,
    val lessonId: String,
    val type: ContentType,
    val url: String,
    val localPath: String?,
    val title: String?,
    val description: String?,
    val fileSize: Long,
    val duration: Int?, // seconds for video/audio
    val thumbnailUrl: String?,
    val isDownloaded: Boolean = false,
    val downloadProgress: Int = 0,
    val createdAt: Date = Date()
) : Parcelable

@Parcelize
data class CurriculumTopic(
    val id: String,
    val name: String,
    val description: String,
    val moduleId: String,
    val orderIndex: Int,
    val keyPoints: List<String>,
    val relatedLessons: List<String>,
    val relatedQuestions: List<String>
) : Parcelable

@Parcelize
data class LessonProgress(
    val id: String,
    val userId: String,
    val lessonId: String,
    val moduleId: String,
    val isCompleted: Boolean,
    val completionPercentage: Int,
    val timeSpent: Int, // seconds
    val lastPosition: Int, // for video/audio
    val startedAt: Date,
    val completedAt: Date?,
    val updatedAt: Date
) : Parcelable

@Parcelize
data class ModuleDownload(
    val id: String,
    val moduleId: String,
    val userId: String,
    val status: DownloadStatus,
    val progress: Int,
    val downloadedSize: Long,
    val totalSize: Long,
    val startedAt: Date,
    val completedAt: Date?,
    val errorMessage: String?
) : Parcelable {
    
    val progressPercentage: Int
        get() = if (totalSize > 0) ((downloadedSize * 100) / totalSize).toInt() else 0
}

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

@Parcelize
data class ContentSyncMetadata(
    val id: String,
    val entityType: String, // "module", "lesson", "media"
    val entityId: String,
    val lastSyncedAt: Date,
    val localVersion: Int,
    val serverVersion: Int,
    val needsSync: Boolean,
    val syncStatus: SyncStatus
) : Parcelable

enum class SyncStatus {
    SYNCED,
    PENDING,
    SYNCING,
    CONFLICT,
    ERROR
}

// Content filters and search
data class ContentFilter(
    val licenseCategory: LicenseCategory? = null,
    val moduleStatus: ModuleStatus? = null,
    val isDownloaded: Boolean? = null,
    val searchQuery: String? = null
)

// Content statistics
data class ContentStats(
    val totalModules: Int,
    val completedModules: Int,
    val totalLessons: Int,
    val completedLessons: Int,
    val totalStudyTime: Int, // minutes
    val downloadedSize: Long,
    val completionPercentage: Int
) {
    val formattedStudyTime: String
        get() {
            val hours = totalStudyTime / 60
            val minutes = totalStudyTime % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
}
