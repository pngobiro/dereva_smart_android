package com.dereva.smart.data.remote

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Service for downloading content from Cloudflare R2
 * 
 * R2 URL Format: https://{account_id}.r2.cloudflarestorage.com/{bucket}/{key}
 * Or with custom domain: https://cdn.derevasmart.com/{key}
 */
class CloudflareR2Service(
    private val context: Context,
    private val baseUrl: String = "https://cdn.derevasmart.com" // Replace with your R2 URL
) {
    
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }
    
    /**
     * Download a file from R2 with progress callback
     * 
     * @param remotePath Path in R2 bucket (e.g., "videos/module1/lesson1.mp4")
     * @param localFile Local file to save to
     * @param onProgress Progress callback (bytesDownloaded, totalBytes)
     * @return Result with local file path or error
     */
    suspend fun downloadFile(
        remotePath: String,
        localFile: File,
        onProgress: ((Long, Long) -> Unit)? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Ensure parent directory exists
            localFile.parentFile?.mkdirs()
            
            // Build URL
            val url = if (remotePath.startsWith("http")) {
                remotePath
            } else {
                "$baseUrl/$remotePath"
            }
            
            // Create request
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            // Execute request
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Download failed: ${response.code} ${response.message}")
                )
            }
            
            val body = response.body ?: return@withContext Result.failure(
                Exception("Empty response body")
            )
            
            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(localFile)
            
            try {
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead = 0L
                
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    
                    // Report progress
                    onProgress?.invoke(totalBytesRead, contentLength)
                }
                
                outputStream.flush()
                Result.success(localFile)
            } finally {
                inputStream.close()
                outputStream.close()
            }
        } catch (e: Exception) {
            // Clean up partial download
            if (localFile.exists()) {
                localFile.delete()
            }
            Result.failure(e)
        }
    }
    
    /**
     * Check if a file exists in R2
     */
    suspend fun fileExists(remotePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = if (remotePath.startsWith("http")) {
                remotePath
            } else {
                "$baseUrl/$remotePath"
            }
            
            val request = Request.Builder()
                .url(url)
                .head() // HEAD request to check existence
                .build()
            
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get file size from R2 without downloading
     */
    suspend fun getFileSize(remotePath: String): Long? = withContext(Dispatchers.IO) {
        try {
            val url = if (remotePath.startsWith("http")) {
                remotePath
            } else {
                "$baseUrl/$remotePath"
            }
            
            val request = Request.Builder()
                .url(url)
                .head()
                .build()
            
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.contentLength()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Download multiple files in sequence
     */
    suspend fun downloadFiles(
        files: List<Pair<String, File>>,
        onFileProgress: ((Int, Int, String) -> Unit)? = null,
        onProgress: ((Long, Long) -> Unit)? = null
    ): Result<List<File>> = withContext(Dispatchers.IO) {
        try {
            val downloadedFiles = mutableListOf<File>()
            
            files.forEachIndexed { index, (remotePath, localFile) ->
                onFileProgress?.invoke(index + 1, files.size, remotePath)
                
                val result = downloadFile(remotePath, localFile, onProgress)
                
                if (result.isSuccess) {
                    downloadedFiles.add(result.getOrThrow())
                } else {
                    // Clean up already downloaded files on failure
                    downloadedFiles.forEach { it.delete() }
                    return@withContext Result.failure(
                        result.exceptionOrNull() ?: Exception("Download failed")
                    )
                }
            }
            
            Result.success(downloadedFiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get download directory for content
     */
    fun getDownloadDirectory(moduleId: String): File {
        return File(context.filesDir, "downloads/modules/$moduleId").apply {
            mkdirs()
        }
    }
    
    /**
     * Get local file path for a remote path
     */
    fun getLocalFilePath(moduleId: String, remotePath: String): File {
        val fileName = remotePath.substringAfterLast('/')
        return File(getDownloadDirectory(moduleId), fileName)
    }
    
    /**
     * Calculate total size of files to download
     */
    suspend fun calculateTotalSize(remotePaths: List<String>): Long {
        var totalSize = 0L
        remotePaths.forEach { path ->
            getFileSize(path)?.let { size ->
                totalSize += size
            }
        }
        return totalSize
    }
}
