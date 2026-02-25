# Current Media Setup - Quick Reference

## Where Media Files Are Currently Located

### ✅ Currently Implemented: Assets Folder

```
dereva_smart_android/
└── app/
    └── src/
        └── main/
            └── assets/
                └── lessons/
                    ├── README.md                    # Development guide
                    └── interactive/
                        └── index.html               # Sample quiz
```

## How to Add Your Media Files

### 1. Create Folder Structure

```bash
cd dereva_smart_android/app/src/main/assets/

# Create folders
mkdir -p lessons/videos/module1
mkdir -p lessons/audio/module1
mkdir -p lessons/images/signs
mkdir -p lessons/interactive/module1
```

### 2. Add Your Files

```
app/src/main/assets/lessons/
├── videos/
│   └── module1/
│       ├── lesson1.mp4          # Your video file
│       └── lesson2.mp4
├── audio/
│   └── module1/
│       ├── pronunciation1.mp3   # Your audio file
│       └── pronunciation2.mp3
├── images/
│   └── signs/
│       ├── stop_sign.png        # Your image file
│       ├── yield_sign.png
│       └── warning_sign.png
└── interactive/
    ├── index.html               # ✅ Already exists (sample quiz)
    └── module1/
        ├── lesson1.html         # Your HTML content
        └── assets/
            ├── style.css
            └── script.js
```

### 3. Reference in Database

When creating lessons in your repository, use these paths:

```kotlin
// Video lesson
Lesson(
    id = "lesson_1_1",
    moduleId = "module_1",
    contentType = ContentType.VIDEO,
    contentUrl = "lessons/videos/module1/lesson1.mp4",  // ← Path in assets
    // ... other fields
)

// Audio lesson
Lesson(
    id = "lesson_1_2",
    moduleId = "module_1",
    contentType = ContentType.AUDIO,
    contentUrl = "lessons/audio/module1/pronunciation1.mp3",
    // ... other fields
)

// Image lesson
Lesson(
    id = "lesson_1_3",
    moduleId = "module_1",
    contentType = ContentType.IMAGE,
    contentUrl = "lessons/images/signs/stop_sign.png",
    // ... other fields
)

// Interactive HTML lesson
Lesson(
    id = "lesson_1_4",
    moduleId = "module_1",
    contentType = ContentType.INTERACTIVE,
    contentUrl = "lessons/interactive/module1/lesson1.html",
    // ... other fields
)
```

## How It Works

### Loading Flow

```
User opens lesson
       ↓
ContentViewModel.selectLesson()
       ↓
LessonViewerScreen displays content
       ↓
Based on ContentType:
       ↓
┌──────┴──────┬──────────┬──────────┬──────────────┐
│             │          │          │              │
VIDEO       AUDIO      IMAGE    INTERACTIVE      TEXT
│             │          │          │              │
ExoPlayer   ExoPlayer   Coil     WebView      Compose Text
│             │          │          │              │
Loads: file:///android_asset/lessons/videos/...
       file:///android_asset/lessons/audio/...
       file:///android_asset/lessons/images/...
       file:///android_asset/lessons/interactive/...
```

### URL Resolution

The app automatically converts your paths:

```kotlin
// You specify in database:
contentUrl = "lessons/videos/module1/lesson1.mp4"

// App converts to:
"file:///android_asset/lessons/videos/module1/lesson1.mp4"

// Players load from assets folder
```

## Example: Adding a New Module

### Step 1: Add Media Files

```bash
# Navigate to assets folder
cd app/src/main/assets/lessons/

# Create module folders
mkdir -p videos/road_signs
mkdir -p images/road_signs
mkdir -p interactive/road_signs

# Copy your files
cp ~/Downloads/stop_sign_video.mp4 videos/road_signs/
cp ~/Downloads/stop_sign.png images/road_signs/
cp ~/Downloads/quiz.html interactive/road_signs/
```

### Step 2: Update Repository

In `ContentRepositoryImpl.kt`, add to `createSampleModules()`:

```kotlin
Module(
    id = "module_road_signs",
    title = "Road Signs",
    description = "Learn all NTSA road signs",
    orderIndex = 5,
    licenseCategory = LicenseCategory.B1,
    thumbnailUrl = "lessons/images/road_signs/thumbnail.png",
    estimatedDuration = 30,
    lessonCount = 3,
    isDownloaded = false,
    downloadSize = 25 * 1024 * 1024,
    status = ModuleStatus.UNLOCKED
)
```

### Step 3: Add Lessons

In `createSampleLessons()`:

```kotlin
when (moduleId) {
    "module_road_signs" -> listOf(
        Lesson(
            id = "lesson_rs_1",
            moduleId = moduleId,
            title = "Stop Signs",
            description = "Understanding stop signs",
            orderIndex = 1,
            contentType = ContentType.VIDEO,
            contentUrl = "lessons/videos/road_signs/stop_sign_video.mp4",
            duration = 5,
            isDownloaded = false,
            isCompleted = false
        ),
        Lesson(
            id = "lesson_rs_2",
            moduleId = moduleId,
            title = "Stop Sign Image",
            description = "Visual reference",
            orderIndex = 2,
            contentType = ContentType.IMAGE,
            contentUrl = "lessons/images/road_signs/stop_sign.png",
            duration = 2,
            isDownloaded = false,
            isCompleted = false
        ),
        Lesson(
            id = "lesson_rs_3",
            moduleId = moduleId,
            title = "Road Signs Quiz",
            description = "Test your knowledge",
            orderIndex = 3,
            contentType = ContentType.INTERACTIVE,
            contentUrl = "lessons/interactive/road_signs/quiz.html",
            duration = 10,
            isDownloaded = false,
            isCompleted = false
        )
    )
    // ... other modules
}
```

### Step 4: Test

```bash
# Rebuild app
./gradlew clean assembleDebug

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk

# Test in app:
# 1. Open app
# 2. Navigate to Learning Modules
# 3. Click "Load Sample Content"
# 4. Find your new module
# 5. Open lessons and verify media plays
```

## File Size Considerations

### APK Size Impact

```
Assets folder content is bundled in APK:
- Empty app: ~10MB
- + 50MB assets = 60MB APK
- + 100MB assets = 110MB APK

Google Play limits:
- APK: 100MB
- With expansion files: 2GB
```

### Recommendations

**For Development:**
- ✅ Use small sample files
- ✅ Compress videos (360p)
- ✅ Optimize images (<500KB)
- ✅ Keep total assets <50MB

**For Production:**
- ✅ Move large files to CDN
- ✅ Keep only essential content in assets
- ✅ Implement download manager
- ✅ Use remote URLs for most content

## Quick Commands

### Check Assets Folder Size
```bash
du -sh app/src/main/assets/
```

### List All Media Files
```bash
find app/src/main/assets/lessons/ -type f
```

### Check APK Size
```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### Extract APK to See Contents
```bash
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep assets
```

## Current Status

### ✅ What's Working Now:
- Assets folder structure created
- Sample interactive quiz included
- All media players implemented
- Can load from assets folder
- Can load from remote URLs

### ⏳ What's Next:
- Add more sample content
- Set up remote CDN
- Implement download manager
- Add storage management UI

## Testing Your Media

### Test Video
```kotlin
// Add test video to assets/lessons/videos/test.mp4
// Then in app, create lesson with:
contentUrl = "lessons/videos/test.mp4"
```

### Test Audio
```kotlin
// Add test audio to assets/lessons/audio/test.mp3
contentUrl = "lessons/audio/test.mp3"
```

### Test Image
```kotlin
// Add test image to assets/lessons/images/test.png
contentUrl = "lessons/images/test.png"
```

### Test HTML
```kotlin
// Add test HTML to assets/lessons/interactive/test.html
contentUrl = "lessons/interactive/test.html"
```

## Troubleshooting

### Media Not Loading?

1. **Check file exists:**
   ```bash
   ls app/src/main/assets/lessons/videos/yourfile.mp4
   ```

2. **Check path in database:**
   ```kotlin
   // Should be relative to assets folder
   contentUrl = "lessons/videos/yourfile.mp4"
   // NOT: "file:///android_asset/lessons/videos/yourfile.mp4"
   // NOT: "/lessons/videos/yourfile.mp4"
   ```

3. **Rebuild app:**
   ```bash
   ./gradlew clean assembleDebug
   ```

4. **Check logs:**
   ```bash
   adb logcat | grep -i "dereva\|exoplayer\|coil\|webview"
   ```

### File Too Large?

1. **Compress video:**
   ```bash
   ffmpeg -i input.mp4 -vcodec h264 -acodec aac -b:v 500k output.mp4
   ```

2. **Optimize image:**
   ```bash
   convert input.png -quality 85 -resize 1920x1080 output.png
   ```

3. **Compress audio:**
   ```bash
   ffmpeg -i input.mp3 -b:a 64k output.mp3
   ```

## Summary

**Current Setup:**
- ✅ Media stored in: `app/src/main/assets/lessons/`
- ✅ Bundled with APK
- ✅ Available offline immediately
- ✅ All media types supported

**To Add Content:**
1. Copy files to `app/src/main/assets/lessons/`
2. Update database with relative path
3. Rebuild app
4. Test

**For Production:**
- Move to CDN for scalability
- Keep only essential content in assets
- Implement download manager

