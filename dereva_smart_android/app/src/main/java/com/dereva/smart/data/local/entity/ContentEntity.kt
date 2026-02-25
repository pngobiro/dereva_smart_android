package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dereva.smart.domain.model.*
import java.util.Date

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val licenseCategory: String,
    val thumbnailUrl: String?,
    val estimatedDuration: Int,
    val lessonCount: Int,
    val isDownloaded: Boolean,
    val downloadSize: Long,
    val status: String,
    val completionPercentage: Int,
    val requiresSubscription: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val moduleId: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val contentType: String,
    val contentUrl: String?,
    val contentText: String?,
    val duration: Int,
    val isDownloaded: Boolean,
    val isCompleted: Boolean,
    val requiresSubscription: Boolean = false,
    val lastAccessedAt: Long?,
    val createdAt: Long
)

@Entity(tableName = "media_assets")
data class MediaAssetEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val type: String,
    val url: String,
    val localPath: String?,
    val title: String?,
    val description: String?,
    val fileSize: Long,
    val duration: Int?,
    val thumbnailUrl: String?,
    val isDownloaded: Boolean,
    val downloadProgress: Int,
    val createdAt: Long
)

@Entity(tableName = "curriculum_topics")
data class CurriculumTopicEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val moduleId: String,
    val orderIndex: Int,
    val keyPoints: String, // JSON array
    val relatedLessons: String, // JSON array
    val relatedQuestions: String // JSON array
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val lessonId: String,
    val moduleId: String,
    val isCompleted: Boolean,
    val completionPercentage: Int,
    val timeSpent: Int,
    val lastPosition: Int,
    val startedAt: Long,
    val completedAt: Long?,
    val updatedAt: Long
)

@Entity(tableName = "module_downloads")
data class ModuleDownloadEntity(
    @PrimaryKey val id: String,
    val moduleId: String,
    val userId: String,
    val status: String,
    val progress: Int,
    val downloadedSize: Long,
    val totalSize: Long,
    val startedAt: Long,
    val completedAt: Long?,
    val errorMessage: String?
)

@Entity(tableName = "content_sync_metadata")
data class ContentSyncMetadataEntity(
    @PrimaryKey val id: String,
    val entityType: String,
    val entityId: String,
    val lastSyncedAt: Long,
    val localVersion: Int,
    val serverVersion: Int,
    val needsSync: Boolean,
    val syncStatus: String
)

// Converters
fun ModuleEntity.toDomain(): Module {
    return Module(
        id = id,
        title = title,
        description = description,
        orderIndex = orderIndex,
        licenseCategory = LicenseCategory.valueOf(licenseCategory),
        thumbnailUrl = thumbnailUrl,
        estimatedDuration = estimatedDuration,
        lessonCount = lessonCount,
        isDownloaded = isDownloaded,
        downloadSize = downloadSize,
        status = ModuleStatus.valueOf(status),
        completionPercentage = completionPercentage,
        requiresSubscription = requiresSubscription,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt)
    )
}

fun Module.toEntity(): ModuleEntity {
    return ModuleEntity(
        id = id,
        title = title,
        description = description,
        orderIndex = orderIndex,
        licenseCategory = licenseCategory.name,
        thumbnailUrl = thumbnailUrl,
        estimatedDuration = estimatedDuration,
        lessonCount = lessonCount,
        isDownloaded = isDownloaded,
        downloadSize = downloadSize,
        status = status.name,
        completionPercentage = completionPercentage,
        requiresSubscription = requiresSubscription,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time
    )
}

fun LessonEntity.toDomain(): Lesson {
    return Lesson(
        id = id,
        moduleId = moduleId,
        title = title,
        description = description,
        orderIndex = orderIndex,
        contentType = ContentType.valueOf(contentType),
        contentUrl = contentUrl,
        contentText = contentText,
        duration = duration,
        isDownloaded = isDownloaded,
        isCompleted = isCompleted,
        requiresSubscription = requiresSubscription,
        lastAccessedAt = lastAccessedAt?.let { Date(it) },
        createdAt = Date(createdAt)
    )
}

fun Lesson.toEntity(): LessonEntity {
    return LessonEntity(
        id = id,
        moduleId = moduleId,
        title = title,
        description = description,
        orderIndex = orderIndex,
        contentType = contentType.name,
        contentUrl = contentUrl,
        contentText = contentText,
        duration = duration,
        isDownloaded = isDownloaded,
        isCompleted = isCompleted,
        requiresSubscription = requiresSubscription,
        lastAccessedAt = lastAccessedAt?.time,
        createdAt = createdAt.time
    )
}

fun MediaAssetEntity.toDomain(): MediaAsset {
    return MediaAsset(
        id = id,
        lessonId = lessonId,
        type = ContentType.valueOf(type),
        url = url,
        localPath = localPath,
        title = title,
        description = description,
        fileSize = fileSize,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        isDownloaded = isDownloaded,
        downloadProgress = downloadProgress,
        createdAt = Date(createdAt)
    )
}

fun MediaAsset.toEntity(): MediaAssetEntity {
    return MediaAssetEntity(
        id = id,
        lessonId = lessonId,
        type = type.name,
        url = url,
        localPath = localPath,
        title = title,
        description = description,
        fileSize = fileSize,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        isDownloaded = isDownloaded,
        downloadProgress = downloadProgress,
        createdAt = createdAt.time
    )
}

fun LessonProgressEntity.toDomain(): LessonProgress {
    return LessonProgress(
        id = id,
        userId = userId,
        lessonId = lessonId,
        moduleId = moduleId,
        isCompleted = isCompleted,
        completionPercentage = completionPercentage,
        timeSpent = timeSpent,
        lastPosition = lastPosition,
        startedAt = Date(startedAt),
        completedAt = completedAt?.let { Date(it) },
        updatedAt = Date(updatedAt)
    )
}

fun LessonProgress.toEntity(): LessonProgressEntity {
    return LessonProgressEntity(
        id = id,
        userId = userId,
        lessonId = lessonId,
        moduleId = moduleId,
        isCompleted = isCompleted,
        completionPercentage = completionPercentage,
        timeSpent = timeSpent,
        lastPosition = lastPosition,
        startedAt = startedAt.time,
        completedAt = completedAt?.time,
        updatedAt = updatedAt.time
    )
}

fun ModuleDownloadEntity.toDomain(): ModuleDownload {
    return ModuleDownload(
        id = id,
        moduleId = moduleId,
        userId = userId,
        status = DownloadStatus.valueOf(status),
        progress = progress,
        downloadedSize = downloadedSize,
        totalSize = totalSize,
        startedAt = Date(startedAt),
        completedAt = completedAt?.let { Date(it) },
        errorMessage = errorMessage
    )
}

fun ModuleDownload.toEntity(): ModuleDownloadEntity {
    return ModuleDownloadEntity(
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
