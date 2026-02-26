# Media Storage Guide for Dereva Smart

## Overview

This guide explains where media files (videos, audio, images, HTML content) will reside and how to organize them for the Dereva Smart learning app.

## Storage Options

### 1. Assets Folder (Bundled with APK) ✅ RECOMMENDED FOR INITIAL CONTENT

**Location:** `app/src/main/assets/`

**Best For:**
- Initial/default content bundled with the app
- Small files that don't change frequently
- HTML/JavaScript interactive content
- Sample lessons and demos

**Advantages:**
- ✅ Available immediately after installation
- ✅ No internet required
- ✅ Fast access
- ✅ Secure (read-only)
- ✅ No storage permissions needed

**Disadvantages:**
- ❌ Increases APK size
- ❌ Cannot be updated without app update
- ❌ Limited to ~100MB for Google Play

**Structure:**
```
app/src/main/assets/
├── lessons/
│   ├── interactive/
│   │   ├── index.html
│   │   ├── quiz1.html
│   │   └── quiz2.html
│   ├── videos/
│   │   ├── module1/
│   │   │   ├── lesson1.mp4
│   │   │   └── lesson2.mp4
│   │   └── module2/
│   │       └── lesson1.mp4
│   ├── audio/
│   │   ├── pronunciation/
│   │   │   ├── term1.mp3
│   │   │   └── term2.mp3
│   │   └── instructions/
│   │       └── intro.mp3
│   └── images/
│       ├── signs/
│       │   ├── stop.png
│       │   ├── yield.png
│       │   └── warning.png
│       └── diagrams/
│           └── intersection.png
└── README.md
```

**Usage in Code:**
```kotlin
// HTML content
contentUrl = "lessons/interactive/index.html"
// Loads: file:///android_asset/lessons/interactive/index.html

// Video
contentUrl = "lessons/videos/module1/lesson1.mp4"
// Loads: file:///android_asset/lessons/videos/module1/lesson1.mp4

// Audio
contentUrl = "lessons/audio/pronunciation/term1.mp3"

// Image
contentUrl = "lessons/images/signs/stop.png"
```

---

### 2. Internal Storage (App Private Directory) ✅ RECOMMENDED FOR DOWNLOADED CONTENT

**Location:** `/data/data/com.dereva.smart/files/`

**Best For:**
- Downloaded content from server
- User-specific content
- Cached media files
- Content that updates frequently

**Advantages:**
- ✅ Private to the app
- ✅ No storage permissions needed (Android 10+)
- ✅ Automatically deleted when app is uninstalled
- ✅ Can be updated dynamically
- ✅ Unlimited size (within device storage)

**Disadvantages:**
- ❌ Requires download
- ❌ Takes up device storage
- ❌ Lost if app is uninstalled

**Structure:**
```
/data/data/com.dereva.smart/files/
├── downloads/
│   ├── modules/
│   │   ├── module_1/
│   │   │   ├── lessons/
│   │   │   │   ├── lesson_1/
│   │   │   │   │   ├── video.mp4
│   │   │   │   │   ├── content.html
│   │   │   │   │   └── images/
│   │   │   │   │       └── diagram.png
│   │   │   │   └── lesson_2/
│   │   │   │       └── video.mp4
│   │   │   └── metadata.json
│   │   └── module_2/
│   │       └── ...
│   └── cache/
│       ├── thumbnails/
│       └── temp/
└── user_content/
    └── notes/
```

**Usage in Code:**
```kotlin
// Get internal storage directory
val filesDir = context.filesDir

// Download and save video
val moduleDir = File(filesDir, "downloads/modules/module_1/lessons/lesson_1")
moduleDir.mkdirs()
val videoFile = File(moduleDir, "video.mp4")

// Download file
downloadFile(remoteUrl, videoFile)

// Use in lesson
contentUrl = "file://${videoFile.absolutePath}"
```

**Implementation Example:**
```kotlin
class ContentDownloadManager(private val context: Context) {
    
    private val downloadDir = File(context.filesDir, "downloads/modules")
    
    suspend fun downloadModule(moduleId: String, lessons: List<Lesson>) {
        val moduleDir = File(downloadDir, moduleId)
        moduleDir.mkdirs()
        
        lessons.forEach { lesson ->
            val lessonDir = File(moduleDir, "lessons/${lesson.id}")
            lessonDir.mkdirs()
            
            // Download video/audio/images
            lesson.contentUrl?.let { url ->
                if (url.startsWith("http")) {
                    val fileName = when (lesson.contentType) {
                        ContentType.VIDEO -> "video.mp4"
                        ContentType.AUDIO -> "audio.mp3"
                        ContentType.IMAGE -> "image.png"
                        ContentType.INTERACTIVE -> "index.html"
                        else -> "content.txt"
                    }
                    
                    val file = File(lessonDir, fileName)
                    downloadFile(url, file)
                    
                    // Update lesson with local path
                    lesson.contentUrl = "file://${file.absolutePath}"
                }
            }
        }
    }
    
    private suspend fun downloadFile(url: String, destination: File) {
        // Use OkHttp or Retrofit to download
        // Show progress in notification
    }
    
    fun getModuleSize(moduleId: String): Long {
        val moduleDir = File(downloadDir, moduleId)
        return moduleDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }
    
    fun deleteModule(moduleId: String) {
        val moduleDir = File(downloadDir, moduleId)
        moduleDir.deleteRecursively()
    }
}
```

---

### 3. External Storage (Shared/Public) ⚠️ USE WITH CAUTION

**Location:** `/storage/emulated/0/Android/data/com.dereva.smart/files/`

**Best For:**
- Large files that users might want to keep
- Content shared between apps
- User-exported content

**Advantages:**
- ✅ Persists after app uninstall (Android 10-)
- ✅ Can be accessed by other apps
- ✅ Good for large files

**Disadvantages:**
- ❌ Requires storage permissions (Android 9 and below)
- ❌ Can be deleted by user
- ❌ Security concerns
- ❌ Not recommended for sensitive content

**Not Recommended** for this app due to security and privacy concerns.

---

### 4. Remote Server (CDN/Cloud Storage) ✅ RECOMMENDED FOR PRODUCTION

**Location:** Remote server (e.g., AWS S3, Firebase Storage, Cloudflare R2)

**Best For:**
- Production content delivery
- Large media libraries
- Frequently updated content
- Scalable storage

**Advantages:**
- ✅ No APK size increase
- ✅ Easy to update content
- ✅ Scalable
- ✅ Can use CDN for fast delivery
- ✅ Reduces device storage usage

**Disadvantages:**
- ❌ Requires internet connection
- ❌ Data costs for users
- ❌ Slower initial load
- ❌ Hosting costs

**Recommended Services:**
- **AWS S3** - Reliable, scalable, global CDN
- **Firebase Storage** - Easy integration with Firebase
- **Cloudflare R2** - No egress fees, fast CDN
- **DigitalOcean Spaces** - Affordable, S3-compatible

**Structure:**
```
https://cdn.derevasmart.com/
├── videos/
│   ├── module1/
│   │   ├── lesson1_360p.mp4
│   │   ├── lesson1_720p.mp4
│   │   └── lesson1_1080p.mp4
│   └── module2/
│       └── ...
├── audio/
│   └── ...
├── images/
│   └── ...
└── interactive/
    └── ...
```

**Usage in Code:**
```kotlin
// Remote URL
contentUrl = "https://cdn.derevasmart.com/videos/module1/lesson1_720p.mp4"

// With authentication (if needed)
contentUrl = "https://cdn.derevasmart.com/videos/module1/lesson1.mp4?token=xyz"
```

---

## Recommended Hybrid Approach

### Strategy: Assets + Internal Storage + Remote Server

```
┌─────────────────────────────────────────────────────────┐
│                    CONTENT DELIVERY                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  1. BUNDLED (Assets)                                    │
│     - Sample lessons (1-2 per module)                   │
│     - Interactive HTML content                          │
│     - Essential images                                  │
│     Size: ~20-50MB                                      │
│                                                          │
│  2. DOWNLOADED (Internal Storage)                       │
│     - User-selected modules                             │
│     - Offline content                                   │
│     - Cached media                                      │
│     Size: User-controlled                               │
│                                                          │
│  3. STREAMED (Remote Server)                            │
│     - Full content library                              │
│     - High-quality videos                               │
│     - Updated content                                   │
│     Size: Unlimited                                     │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Implementation:

```kotlin
class ContentManager(
    private val context: Context,
    private val contentDao: ContentDao
) {
    
    suspend fun getContentUrl(lesson: Lesson): String {
        return when {
            // 1. Check if downloaded locally
            isDownloaded(lesson.id) -> {
                getLocalPath(lesson.id)
            }
            
            // 2. Check if available in assets
            isInAssets(lesson.contentUrl) -> {
                "file:///android_asset/${lesson.contentUrl}"
            }
            
            // 3. Use remote URL
            else -> {
                lesson.contentUrl ?: ""
            }
        }
    }
    
    private fun isDownloaded(lessonId: String): Boolean {
        val file = File(context.filesDir, "downloads/lessons/$lessonId")
        return file.exists()
    }
    
    private fun getLocalPath(lessonId: String): String {
        val file = File(context.filesDir, "downloads/lessons/$lessonId")
        return "file://${file.absolutePath}"
    }
    
    private fun isInAssets(path: String?): Boolean {
        if (path == null) return false
        return try {
            context.assets.open(path).close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
```

---

## Content Organization Best Practices

### 1. Module Structure
```
module_1/
├── metadata.json          # Module info
├── thumbnail.png          # Module thumbnail
├── lessons/
│   ├── lesson_1/
│   │   ├── content.json   # Lesson metadata
│   │   ├── video.mp4      # Main video
│   │   ├── audio.mp3      # Optional audio
│   │   ├── transcript.txt # Video transcript
│   │   ├── index.html     # Interactive content
│   │   └── images/        # Lesson images
│   │       ├── diagram1.png
│   │       └── diagram2.png
│   └── lesson_2/
│       └── ...
└── quiz/
    └── quiz.json
```

### 2. Naming Conventions
```
Videos:    module{id}_lesson{id}_video_{quality}.mp4
           Example: module1_lesson1_video_720p.mp4

Audio:     module{id}_lesson{id}_audio.mp3
           Example: module1_lesson1_audio.mp3

Images:    module{id}_lesson{id}_{description}.png
           Example: module1_lesson1_diagram.png

HTML:      module{id}_lesson{id}_interactive.html
           Example: module1_lesson1_interactive.html
```

### 3. File Size Guidelines
```
Videos:
  - 360p:  ~10-20MB per 10 minutes
  - 720p:  ~50-100MB per 10 minutes
  - 1080p: ~150-300MB per 10 minutes

Audio:
  - 64kbps:  ~5MB per 10 minutes
  - 128kbps: ~10MB per 10 minutes

Images:
  - Thumbnails: <100KB
  - Diagrams:   <500KB
  - Photos:     <1MB

HTML:
  - Content: <1MB (including assets)
```

---

## Database Schema for Media Tracking

```kotlin
@Entity(tableName = "media_assets")
data class MediaAssetEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val type: String, // VIDEO, AUDIO, IMAGE, HTML
    val remoteUrl: String?,
    val localPath: String?,
    val fileSize: Long,
    val isDownloaded: Boolean,
    val downloadedAt: Long?,
    val lastAccessedAt: Long?
)
```

---

## Download Management

### Priority System
```kotlin
enum class DownloadPriority {
    HIGH,    // User explicitly requested
    MEDIUM,  // Next lesson in sequence
    LOW      // Background prefetch
}
```

### Download Strategy
```kotlin
class SmartDownloadManager {
    
    fun shouldDownload(lesson: Lesson): Boolean {
        return when {
            // Always download if user requested
            userRequested -> true
            
            // Download if on WiFi and storage available
            isOnWiFi() && hasStorageSpace() -> true
            
            // Don't download on mobile data unless user allows
            isOnMobileData() && !userAllowsMobileDownload -> false
            
            else -> false
        }
    }
    
    fun getOptimalQuality(): VideoQuality {
        return when {
            hasLimitedStorage() -> VideoQuality.P360
            hasModerateStorage() -> VideoQuality.P720
            else -> VideoQuality.P1080
        }
    }
}
```

---

## Storage Management

### Monitor Storage Usage
```kotlin
class StorageManager(private val context: Context) {
    
    fun getAppStorageUsage(): StorageInfo {
        val filesDir = context.filesDir
        val cacheDir = context.cacheDir
        
        return StorageInfo(
            totalUsed = getDirectorySize(filesDir) + getDirectorySize(cacheDir),
            downloads = getDirectorySize(File(filesDir, "downloads")),
            cache = getDirectorySize(cacheDir),
            available = getAvailableSpace()
        )
    }
    
    fun cleanupCache() {
        val cacheDir = context.cacheDir
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
    }
    
    fun deleteOldContent(daysOld: Int) {
        val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000)
        // Delete files not accessed since cutoffTime
    }
}
```

---

## Example: Complete Content Flow

```kotlin
// 1. User opens lesson
viewModel.selectLesson(lessonId)

// 2. ContentManager determines source
val contentUrl = contentManager.getContentUrl(lesson)

// 3. Load content based on type
when (lesson.contentType) {
    ContentType.VIDEO -> {
        // ExoPlayer loads from:
        // - file:///android_asset/... (bundled)
        // - file:///data/data/.../... (downloaded)
        // - https://cdn.../... (streamed)
        exoPlayer.setMediaItem(MediaItem.fromUri(contentUrl))
    }
    
    ContentType.IMAGE -> {
        // Coil loads from any source
        Image(painter = rememberAsyncImagePainter(contentUrl))
    }
    
    ContentType.INTERACTIVE -> {
        // WebView loads HTML
        webView.loadUrl(contentUrl)
    }
}

// 4. Track usage
contentManager.recordAccess(lessonId)

// 5. Prefetch next lesson (if on WiFi)
if (isOnWiFi()) {
    contentManager.prefetchNextLesson()
}
```

---

## Summary & Recommendations

### For Development/Testing:
✅ Use **Assets folder** for sample content
- Quick to test
- No server setup needed
- Include in APK

### For Production:
✅ Use **Hybrid approach**:
1. **Assets** - Essential content (~20-50MB)
2. **Internal Storage** - Downloaded modules
3. **Remote Server** - Full content library

### Storage Locations:
```
Assets:           app/src/main/assets/lessons/
Internal:         /data/data/com.dereva.smart/files/downloads/
Remote:           https://cdn.derevasmart.com/
```

### Next Steps:
1. ✅ Start with assets folder for development
2. ⏳ Set up remote CDN for production
3. ⏳ Implement download manager
4. ⏳ Add storage management UI
5. ⏳ Implement caching strategy

