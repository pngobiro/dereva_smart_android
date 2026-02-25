package com.dereva.smart.data.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.dereva.smart.R
import com.dereva.smart.data.local.dao.ContentDao
import com.dereva.smart.data.remote.CloudflareR2Service
import com.dereva.smart.domain.model.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * WorkManager worker for downloading content from Cloudflare R2
 */
class DownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    
    private val r2Service: CloudflareR2Service by inject()
    private val contentDao: ContentDao by inject()
    
    private val notificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        const val KEY_MODULE_ID = "module_id"
        const val KEY_LESSON_ID = "lesson_id"
        const val KEY_REMOTE_URL = "remote_url"
        const val KEY_CONTENT_TYPE = "content_type"
        
        const val PROGRESS_CURRENT = "progress_current"
        const val PROGRESS_MAX = "progress_max"
        
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "Content Downloads"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val moduleId = inputData.getString(KEY_MODULE_ID) ?: return@withContext Result.failure()
            val lessonId = inputData.getString(KEY_LESSON_ID) ?: return@withContext Result.failure()
            val remoteUrl = inputData.getString(KEY_REMOTE_URL) ?: return@withContext Result.failure()
            val contentType = inputData.getString(KEY_CONTENT_TYPE) ?: "video"
            
            // Create notification channel
            createNotificationChannel()
            
            // Set foreground service
            setForeground(createForegroundInfo("Starting download...", 0, 100))
            
            // Update download status to DOWNLOADING
            withContext(Dispatchers.IO) {
                updateDownloadStatus(moduleId, DownloadStatus.DOWNLOADING, 0)
            }
            
            // Get local file path
            val localFile = r2Service.getLocalFilePath(moduleId, remoteUrl)
            
            // Download file with progress
            val result = r2Service.downloadFile(
                remotePath = remoteUrl,
                localFile = localFile,
                onProgress = { current, total ->
                    val progress = if (total > 0) {
                        ((current.toFloat() / total.toFloat()) * 100).toInt()
                    } else 0
                    
                    // Update notification
                    setForegroundAsync(
                        createForegroundInfo(
                            "Downloading ${localFile.name}",
                            current,
                            total
                        )
                    )
                    
                    // Update work progress
                    setProgressAsync(workDataOf(
                        PROGRESS_CURRENT to current,
                        PROGRESS_MAX to total
                    ))
                }
            )
            
            if (result.isSuccess) {
                // Update lesson with local path
                contentDao.updateLessonLocalPath(lessonId, localFile.absolutePath)
                
                // Update download status to COMPLETED
                updateDownloadStatus(moduleId, DownloadStatus.COMPLETED, 100)
                
                // Show completion notification
                showCompletionNotification("Download complete", localFile.name)
                
                Result.success(workDataOf(
                    "local_path" to localFile.absolutePath
                ))
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                
                // Update download status to FAILED
                updateDownloadStatus(moduleId, DownloadStatus.FAILED, 0, error)
                
                // Show error notification
                showCompletionNotification("Download failed", error)
                
                Result.failure(workDataOf(
                    "error" to error
                ))
            }
        } catch (e: Exception) {
            val error = e.message ?: "Unknown error"
            showCompletionNotification("Download failed", error)
            Result.failure(workDataOf("error" to error))
        }
    }
    
    private suspend fun updateDownloadStatus(
        moduleId: String,
        status: DownloadStatus,
        progress: Int,
        errorMessage: String? = null
    ) {
        withContext(Dispatchers.IO) {
            contentDao.updateModuleDownloadStatus(
                moduleId = moduleId,
                status = status.name,
                progress = progress,
                errorMessage = errorMessage
            )
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of content downloads"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createForegroundInfo(
        title: String,
        current: Long,
        max: Long
    ): ForegroundInfo {
        val progress = if (max > 0) {
            ((current.toFloat() / max.toFloat()) * 100).toInt()
        } else 0
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$progress% complete")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, max == 0L)
            .setOngoing(true)
            .build()
        
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }
    
    private fun showCompletionNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }
}
