# Interactive HTML Content Guide

## Overview

The Dereva Smart app supports interactive HTML/JavaScript content for lessons. This allows you to create rich, interactive learning experiences using web technologies.

## Content Types Supported

### 1. Text Content
- Plain text with markdown-style formatting
- Displayed in a scrollable view
- Automatic progress tracking

### 2. Image Content
- Remote URLs (https://)
- Local assets (file:///android_asset/)
- Automatic loading with Coil
- Error handling and retry

### 3. Video Content
- MP4, WebM, and other formats supported by ExoPlayer
- Remote URLs (https://)
- Local assets (file:///android_asset/)
- Full playback controls (play/pause, seek, rewind/forward)
- Automatic position tracking

### 4. Audio Content
- MP3, AAC, and other formats supported by ExoPlayer
- Remote URLs (https://)
- Local assets (file:///android_asset/)
- Full playback controls with progress bar
- Time display (current/total)

### 5. Interactive Content (HTML/JavaScript)
- Full HTML5 support
- JavaScript enabled
- DOM storage enabled
- File access enabled
- Zoom controls

## HTML Content Structure

### Folder Structure
```
app/src/main/assets/lessons/
├── interactive/
│   ├── index.html          # Main HTML file
│   ├── styles.css          # Optional CSS
│   ├── script.js           # Optional JS
│   └── images/             # Optional images
├── module1/
│   ├── lesson1/
│   │   └── index.html
│   └── lesson2/
│       └── index.html
└── README.md
```

### Loading HTML Content

#### Option 1: From Assets Folder
```kotlin
// In your lesson data
contentUrl = "lessons/interactive/index.html"
// Will load: file:///android_asset/lessons/interactive/index.html
```

#### Option 2: Full File URL
```kotlin
contentUrl = "file:///android_asset/lessons/module1/lesson1/index.html"
```

#### Option 3: Remote URL
```kotlin
contentUrl = "https://example.com/lessons/lesson1.html"
```

## HTML Template

### Basic Template
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lesson Title</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            padding: 20px;
            margin: 0;
        }
        /* Your styles here */
    </style>
</head>
<body>
    <h1>Lesson Title</h1>
    <div id="content">
        <!-- Your content here -->
    </div>
    
    <script>
        // Your JavaScript here
    </script>
</body>
</html>
```

### Interactive Quiz Template
See `interactive/index.html` for a complete example of an interactive quiz with:
- Multiple choice questions
- Score tracking
- Visual feedback
- Progress indicators
- Responsive design

## Best Practices

### 1. Mobile-First Design
```css
body {
    font-size: 16px; /* Minimum for mobile readability */
    padding: 20px;
    max-width: 100%;
}

/* Use viewport units */
.container {
    width: 100vw;
    min-height: 100vh;
}
```

### 2. Touch-Friendly Interactions
```css
button {
    min-height: 44px; /* Minimum touch target */
    min-width: 44px;
    padding: 12px 20px;
}
```

### 3. Responsive Images
```css
img {
    max-width: 100%;
    height: auto;
}
```

### 4. Performance
- Minimize external dependencies
- Use inline CSS/JS when possible
- Optimize images
- Avoid heavy animations

### 5. Accessibility
```html
<!-- Use semantic HTML -->
<button aria-label="Next Question">Next →</button>

<!-- Provide alt text -->
<img src="sign.png" alt="Stop sign">

<!-- Use proper heading hierarchy -->
<h1>Main Title</h1>
<h2>Section Title</h2>
```

## JavaScript Features

### Available APIs
- DOM manipulation
- Event listeners
- Local Storage
- Fetch API (for remote content)
- Canvas API
- Audio/Video APIs

### Example: Progress Tracking
```javascript
// Track user progress
let progress = {
    questionsAnswered: 0,
    correctAnswers: 0,
    timeSpent: 0
};

// Save to localStorage
localStorage.setItem('lessonProgress', JSON.stringify(progress));

// Load from localStorage
const savedProgress = JSON.parse(localStorage.getItem('lessonProgress'));
```

### Example: Interactive Quiz
```javascript
const questions = [
    {
        question: "What does this sign mean?",
        options: ["Stop", "Yield", "No Entry"],
        correct: 0
    }
];

function checkAnswer(selected) {
    if (selected === questions[currentQuestion].correct) {
        score++;
        showFeedback("Correct!", "success");
    } else {
        showFeedback("Incorrect", "error");
    }
}
```

## Content Types in Database

### Setting Content Type
```kotlin
// In your lesson creation
Lesson(
    id = "lesson_1",
    contentType = ContentType.INTERACTIVE,
    contentUrl = "lessons/interactive/index.html",
    // ... other fields
)
```

### Content Type Enum
```kotlin
enum class ContentType {
    TEXT,        // Plain text content
    IMAGE,       // Image viewer
    VIDEO,       // Video player
    AUDIO,       // Audio player
    INTERACTIVE  // HTML/JavaScript content
}
```

## Testing HTML Content

### 1. Test in Browser First
- Open your HTML file in Chrome/Firefox
- Test all interactions
- Check responsive design
- Verify on mobile viewport

### 2. Test in App
- Add lesson to database with INTERACTIVE type
- Set contentUrl to your HTML file path
- Navigate to lesson viewer
- Test all interactions in WebView

### 3. Debug WebView
```kotlin
// Enable WebView debugging (in development)
if (BuildConfig.DEBUG) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

Then use Chrome DevTools:
1. Open Chrome on desktop
2. Navigate to `chrome://inspect`
3. Find your app's WebView
4. Click "inspect"

## Security Considerations

### Enabled Features
- JavaScript: ✅ Enabled
- DOM Storage: ✅ Enabled
- File Access: ✅ Enabled (for local assets)
- Content Access: ✅ Enabled

### Disabled Features
- Geolocation: ❌ Disabled
- Camera/Microphone: ❌ Disabled
- File Upload: ❌ Disabled

### Content Security
- Only load trusted HTML content
- Sanitize user input if accepting external content
- Use HTTPS for remote content
- Validate URLs before loading

## Examples

### 1. Simple Quiz
See `interactive/index.html` for a complete quiz implementation

### 2. Flashcards
```html
<div class="flashcard" onclick="this.classList.toggle('flipped')">
    <div class="front">Question</div>
    <div class="back">Answer</div>
</div>
```

### 3. Drag and Drop
```javascript
element.addEventListener('dragstart', (e) => {
    e.dataTransfer.setData('text/plain', e.target.id);
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    const id = e.dataTransfer.getData('text/plain');
    // Handle drop
});
```

### 4. Progress Bar
```javascript
function updateProgress(current, total) {
    const percentage = (current / total) * 100;
    document.getElementById('progress').style.width = percentage + '%';
}
```

## Troubleshooting

### Content Not Loading
1. Check file path is correct
2. Verify file exists in assets folder
3. Check WebView console for errors
4. Ensure contentType is set to INTERACTIVE

### JavaScript Not Working
1. Verify JavaScript is enabled in WebView settings
2. Check for syntax errors in console
3. Test in browser first

### Styling Issues
1. Use viewport meta tag
2. Test on different screen sizes
3. Avoid fixed widths
4. Use relative units (%, em, rem)

### Performance Issues
1. Minimize DOM operations
2. Use CSS animations instead of JS
3. Optimize images
4. Lazy load content

## Resources

- [MDN Web Docs](https://developer.mozilla.org/)
- [WebView Documentation](https://developer.android.com/reference/android/webkit/WebView)
- [ExoPlayer Documentation](https://exoplayer.dev/)
- [Material Design Guidelines](https://material.io/design)

## Support

For questions or issues with HTML content:
1. Check this README
2. Review example files
3. Test in browser first
4. Check WebView console logs
