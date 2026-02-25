# Cloudflare R2 Download System - Complete Guide

## Overview

Successfully implemented a complete download system for fetching content from Cloudflare R2 storage with WorkManager for background downloads, progress tracking, and offline support.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   DOWNLOAD SYSTEM                        │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  CloudflareR2Service                                    │
│  ├─ Download files from R2                              │
│  ├─ Progress callbacks                                  │
│  ├─ File existence checks                               │
│  └─ Size calculations                                   │
│                                                          │
│  DownloadWorker (WorkManager)                           │
│  ├─ Background downloads                                │
│  ├─ Foreground notifications                            │
│  ├─ Progress updates                                    │
│  └─ Error handling                                      │
│                                                          │
│  DownloadManager                                        │
│  ├─ Queue management                                    │
│  ├─ WiFi-only option                                    │
│  ├─ Pause/Resume/Cancel                                 │
│  └─ Storage management                                  │
│                                                          │
│  ContentRepository                                      │
│  └─ High-level download API                             │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Components

### 1. CloudflareR2Service ✅

**File:** `app/src/main/java/com/dereva/smart/data/remote/CloudflareR2Service.kt`

**Purpose:** Direct communication with Cloudflare R2 storage

**Features:**
- Download files with progress tracking
- Check file existence (HEAD request)
- Get file size without downloading
- Download multiple files in sequence
- Automatic retry on failure
- Local file path management

**Configuration:**
```kotlin
val r2Service = CloudflareR2Service(
    context = context,
    baseUrl = "https://cdn.derevasmart.com" // Your R2 URL
)
```

**Usage:**
```kotlin
// Download single file
val result = r2Service.downloadFile(
    remotePath = "videos/module1/lesson1.mp4",
    localFile = File(context.filesDir, "downloads/lesson1.mp4"),
    onProgress = { current, total ->
        val progress = (current * 100 / total).toInt()
        println("Progress: $progress%")
    }
)

// Check if file exists
val exists = r2Service.fileExists("videos/module1/lesson1.mp4")

// Get file size
val size = r2Service.getFileSize("videos/module1/lesson1.mp4")
```

### 2. DownloadWorker ✅

**File:** `app/src/main/java/com/dereva/smart/data/download/DownloadWorker.kt`

**Purpose:** WorkManager worker for background downloads

**Features:**
- Runs as foreground service with notification
- Survives app restarts
- Respects network constraints (WiFi-only)
- Updates database with progress
- Handles errors gracefully
- Cleans up on failure

**Input Data:**
```kotlin
workDataOf(
    KEY_MODULE_ID to "module_1",
    KEY_LESSON_ID to "lesson_1",
    KEY_REMOTE_URL to "https://cdn.derevasmart.com/videos/lesson1.mp4",
    KEY_CONTENT_TYPE to "VIDEO"
)
```

**Notification:**
- Shows download progress
- Displays file name
- Updates in real-time
- Completion/error notifications

### 3. DownloadManager ✅

**File:** `app/src/main/java/com/dereva/smart/data/download/DownloadManager.kt`

**Purpose:** High-level download management

**Features:**
- Download single lessons
- Download entire modules
- Pause/Resume/Cancel downloads
- WiFi-only option
- Progress tracking with Flow
- Storage management
- Cleanup failed downloads

**Usage:**
```kotlin
val downloadManager = DownloadManager(context, contentDao)

// Download a lesson
val workId = downloadManager.downloadLesson(
    moduleId = "module_1",
    lesson = lesson,
    onlyOnWiFi = true
)

// Download entire module
val workIds = downloadManager.downloadModule(
    module = module,
    lessons = lessons,
    onlyOnWiFi = true
)

// Monitor progress
downloadManager.getDownloadProgress(workId).collect { workInfo ->
    when (workInfo?.state) {
        WorkInfo.State.RUNNING -> {
            val current = workInfo.progress.getLong(PROGRESS_CURRENT, 0)
            val max = workInfo.progress.getLong(PROGRESS_MAX, 1)
            val progress = (current * 100 / max).toInt()
            println("Progress: $progress%")
        }
        WorkInfo.State.SUCCEEDED -> println("Download complete!")
        WorkInfo.State.FAILED -> println("Download failed")
        else -> {}
    }
}

// Cancel download
downloadManager.cancelModuleDownload("module_1")

// Delete downloaded content
downloadManager.deleteModuleDownload("module_1")
```

## Setup Cloudflare R2

### Step 1: Create R2 Bucket

1. Log in to Cloudflare Dashboard
2. Navigate to R2 Object Storage
3. Click "Create bucket"
4. Name: `dereva-smart-content`
5. Location: Auto (or choose specific region)

### Step 2: Upload Content

```bash
# Using Wrangler CLI
npm install -g wrangler

# Login
wrangler login

# Upload files
wrangler r2 object put dereva-smart-content/videos/module1/lesson1.mp4 --file=./lesson1.mp4
wrangler r2 object put dereva-smart-content/images/signs/stop.png --file=./stop.png
```

### Step 3: Configure Public Access

**Option A: Custom Domain (Recommended)**
1. Go to R2 bucket settings
2. Click "Connect Domain"
3. Add custom domain: `cdn.derevasmart.com`
4. Update DNS records as instructed
5. Wait for SSL certificate

**Option B: Public Bucket URL**
1. Go to R2 bucket settings
2. Enable "Public Access"
3. Get public URL: `https://<account_id>.r2.cloudflarestorage.com/dereva-smart-content`

### Step 4: Update App Configuration

```kotlin
// In CloudflareR2Service.kt or via DI
val baseUrl = "https://cdn.derevasmart.com" // Your custom domain
// OR
val baseUrl = "https://<account_id>.r2.cloudflarestorage.com/dereva-smart-content"
```

## Content Organization in R2

### Recommended Structure

```
dereva-smart-content/
├── videos/
│   ├── module1/
│   │   ├── lesson1_360p.mp4
│   │   ├── lesson1_720p.mp4
│   │   └── lesson1_1080p.mp4
│   └── module2/
│       └── lesson1_720p.mp4
├── audio/
│   ├── module1/
│   │   ├── pronunciation1.mp3
│   │   └── pronunciation2.mp3
│   └── instructions/
│       └── intro.mp3
├── images/
│   ├── signs/
│   │   ├── stop.png
│   │   ├── yield.png
│   │   └── warning.png
│   └── diagrams/
│       └── intersection.png
└── interactive/
    ├── module1/
    │   └── quiz.html
    └── module2/
        └── simulation.html
```

### URL Format

```
https://cdn.derevasmart.com/videos/module1/lesson1_720p.mp4
https://cdn.derevasmart.com/audio/module1/pronunciation1.mp3
https://cdn.derevasmart.com/images/signs/stop.png
https://cdn.derevasmart.com/interactive/module1/quiz.html
```

## Database Schema

### Lessons Table
```sql
CREATE TABLE lessons (
    id TEXT PRIMARY KEY,
    moduleId TEXT,
    contentUrl TEXT,  -- R2 URL or local path
    isDownloaded INTEGER DEFAULT 0,
    ...
)
```

### Module Downloads Table
```sql
CREATE TABLE module_downloads (
    id TEXT PRIMARY KEY,
    moduleId TEXT,
    userId TEXT,
    status TEXT,  -- PENDING, DOWNLOADING, COMPLETED, FAILED, PAUSED, CANCELLED
    progress INTEGER,
    downloadedSize INTEGER,
    totalSize INTEGER,
    startedAt INTEGER,
    completedAt INTEGER,
    errorMessage TEXT
)
```

## Usage in App

### 1. Update Lesson URLs in Database

```kotlin
// When creating lessons, use R2 URLs
Lesson(
    id = "lesson_1_1",
    moduleId = "module_1",
    contentType = ContentType.VIDEO,
    contentUrl = "https://cdn.derevasmart.com/videos/module1/lesson1_720p.mp4",
    // ... other fields
)
```

### 2. Download Module

```kotlin
// In ViewModel
fun downloadModule(moduleId: String) {
    viewModelScope.launch {
        val result = contentRepository.downloadModule(moduleId, userId)
        result.onSuccess {
            _uiState.update { it.copy(downloadStatus = "Downloading...") }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message) }
        }
    }
}
```

### 3. Monitor Progress

```kotlin
// In ViewModel
init {
    downloadManager.getModuleTotalProgress(moduleId).collect { progress ->
        _uiState.update { it.copy(downloadProgress = progress) }
    }
}
```

### 4. Play Content

```kotlin
// ContentManager determines source
suspend fun getContentUrl(lesson: Lesson): String {
    return when {
        // Check if downloaded locally
        isDownloaded(lesson.id) -> {
            "file://${getLocalPath(lesson.id)}"
        }
        // Use R2 URL for streaming
        else -> {
            lesson.contentUrl ?: ""
        }
    }
}
```

## UI Integration

### Download Button

```kotlin
@Composable
fun ModuleCard(module: Module, onDownload: () -> Unit) {
    Card {
        // ... module info
        
        if (!module.isDownloaded) {
            Button(onClick = onDownload) {
                Icon(Icons.Default.Add, "Download")
                Text("Download")
            }
        } else {
            Icon(Icons.Default.CheckCircle, "Downloaded")
        }
    }
}
```

### Progress Indicator

```kotlin
@Composable
fun DownloadProgress(moduleId: String, downloadManager: DownloadManager) {
    val progress by downloadManager.getModuleTotalProgress(moduleId)
        .collectAsState(initial = 0)
    
    if (progress > 0 && progress < 100) {
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth()
        )
        Text("$progress% downloaded")
    }
}
```

## Network Constraints

### WiFi-Only Downloads

```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
    .setRequiresStorageNotLow(true)
    .build()
```

### Allow Mobile Data

```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED) // Any network
    .setRequiresStorageNotLow(true)
    .build()
```

## Storage Management

### Check Storage Usage

```kotlin
val usage = downloadManager.getDownloadStorageUsage()
println("Downloads using: ${usage / (1024 * 1024)} MB")
```

### Delete Module

```kotlin
downloadManager.deleteModuleDownload("module_1")
```

### Cleanup Failed Downloads

```kotlin
downloadManager.cleanupFailedDownloads()
```

## Error Handling

### Common Errors

1. **Network Error**
   - Check internet connection
   - Verify R2 URL is accessible
   - Check CORS settings

2. **Storage Full**
   - Check available storage
   - Clean up old downloads
   - Reduce video quality

3. **File Not Found**
   - Verify file exists in R2
   - Check URL is correct
   - Check bucket permissions

### Retry Logic

```kotlin
// Automatic retry in DownloadWorker
if (result.isFailure) {
    // WorkManager will retry based on backoff policy
    return Result.retry()
}
```

## Testing

### Test Download

```kotlin
@Test
fun testDownload() = runTest {
    val r2Service = CloudflareR2Service(context)
    
    val result = r2Service.downloadFile(
        remotePath = "videos/test.mp4",
        localFile = File(context.filesDir, "test.mp4")
    )
    
    assertTrue(result.isSuccess)
    assertTrue(File(context.filesDir, "test.mp4").exists())
}
```

### Test WorkManager

```kotlin
@Test
fun testDownloadWorker() {
    val inputData = workDataOf(
        DownloadWorker.KEY_MODULE_ID to "module_1",
        DownloadWorker.KEY_LESSON_ID to "lesson_1",
        DownloadWorker.KEY_REMOTE_URL to "https://cdn.derevasmart.com/test.mp4"
    )
    
    val request = OneTimeWorkRequestBuilder<DownloadWorker>()
        .setInputData(inputData)
        .build()
    
    workManager.enqueue(request)
    
    // Wait for completion
    val workInfo = workManager.getWorkInfoById(request.id).get()
    assertEquals(WorkInfo.State.SUCCEEDED, workInfo.state)
}
```

## Performance Optimization

### 1. Parallel Downloads

```kotlin
// Download multiple lessons in parallel
lessons.forEach { lesson ->
    downloadManager.downloadLesson(moduleId, lesson)
}
```

### 2. Adaptive Quality

```kotlin
fun getOptimalQuality(): String {
    return when {
        hasLimitedStorage() -> "360p"
        isOnMobileData() -> "480p"
        else -> "720p"
    }
}
```

### 3. Prefetching

```kotlin
// Prefetch next lesson
if (isOnWiFi() && hasStorageSpace()) {
    downloadManager.downloadLesson(moduleId, nextLesson)
}
```

## Security

### 1. Signed URLs (Optional)

```kotlin
// Generate signed URL with expiration
fun getSignedUrl(path: String): String {
    val expiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
    val signature = generateSignature(path, expiry)
    return "$baseUrl/$path?expires=$expiry&signature=$signature"
}
```

### 2. Content Encryption (Optional)

```kotlin
// Encrypt downloaded files
fun encryptFile(file: File, key: ByteArray) {
    // Use AES encryption
}
```

## Monitoring

### Track Downloads

```kotlin
// Log download events
downloadManager.getActiveDownloads().collect { downloads ->
    downloads.forEach { workInfo ->
        Log.d("Download", "Status: ${workInfo.state}")
    }
}
```

### Analytics

```kotlin
// Track download metrics
analytics.logEvent("content_download", bundleOf(
    "module_id" to moduleId,
    "file_size" to fileSize,
    "duration" to downloadDuration
))
```

## Troubleshooting

### Downloads Not Starting

1. Check network constraints
2. Verify WorkManager is initialized
3. Check storage permissions
4. Review logs for errors

### Slow Downloads

1. Check network speed
2. Reduce video quality
3. Use CDN closer to user
4. Enable compression

### Files Not Playing

1. Verify file downloaded completely
2. Check file format is supported
3. Verify local path is correct
4. Check ExoPlayer logs

## Summary

✅ **Implemented:**
- CloudflareR2Service for R2 communication
- DownloadWorker for background downloads
- DownloadManager for queue management
- Database integration
- Progress tracking
- Error handling
- Storage management

✅ **Features:**
- WiFi-only downloads
- Pause/Resume/Cancel
- Progress notifications
- Offline playback
- Storage cleanup
- Retry on failure

✅ **Build Status:** SUCCESSFUL

**Next Steps:**
1. Set up Cloudflare R2 bucket
2. Upload content to R2
3. Configure custom domain
4. Update app with R2 URLs
5. Test downloads
6. Monitor performance

