package com.dereva.smart.ui.screens.content

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dereva.smart.domain.model.ContentType
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonViewerScreen(
    navController: NavController,
    viewModel: ContentViewModel,
    lessonId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val lesson = uiState.currentLesson
    
    var timeSpent by remember { mutableStateOf(0) }
    var scrollPosition by remember { mutableStateOf(0) }
    
    LaunchedEffect(lessonId) {
        viewModel.selectLesson(lessonId)
    }
    
    // Track time spent
    LaunchedEffect(lessonId) {
        while (true) {
            delay(1000)
            timeSpent++
            viewModel.updateLessonProgress(timeSpent, scrollPosition)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson?.title ?: "Lesson") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (lesson?.isCompleted == false) {
                        IconButton(
                            onClick = {
                                viewModel.completeLesson()
                                navController.popBackStack()
                            }
                        ) {
                            Icon(Icons.Default.CheckCircle, "Complete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        // Subscription Required Dialog
        if (uiState.showSubscriptionRequired) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissSubscriptionDialog() },
                title = { Text(if (uiState.isGuestMode) "Account Required" else "Premium Content") },
                text = { 
                    Text(if (uiState.isGuestMode) "This content requires an account. Please login or register to continue." else "This content requires a premium subscription to access.")
                },
                confirmButton = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.isGuestMode) {
                            TextButton(
                                onClick = {
                                    viewModel.dismissSubscriptionDialog()
                                    navController.navigate("register")
                                }
                            ) {
                                Text("Register")
                            }
                            TextButton(
                                onClick = {
                                    viewModel.dismissSubscriptionDialog()
                                    navController.navigate("login")
                                }
                            ) {
                                Text("Login")
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    viewModel.dismissSubscriptionDialog()
                                    navController.navigate("payment")
                                }
                            ) {
                                Text("Subscribe Now")
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            viewModel.dismissSubscriptionDialog()
                            navController.popBackStack()
                        }
                    ) {
                        Text("Go Back")
                    }
                }
            )
        }
        
        if (lesson == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Progress Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Time Spent",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${timeSpent / 60}:${(timeSpent % 60).toString().padStart(2, '0')}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${lesson.duration} min",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = if (lesson.isCompleted) "✓ Done" else "In Progress",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (lesson.isCompleted) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Content
                when (lesson.contentType) {
                    ContentType.TEXT -> {
                        TextContent(
                            content = lesson.contentText ?: "No content available",
                            onScrollPositionChanged = { position ->
                                scrollPosition = position
                            }
                        )
                    }
                    ContentType.VIDEO -> {
                        VideoContent(
                            videoUrl = lesson.contentUrl ?: "",
                            onPositionChanged = { position ->
                                scrollPosition = position
                            }
                        )
                    }
                    ContentType.IMAGE -> {
                        ImageContent(imageUrl = lesson.contentUrl ?: "")
                    }
                    ContentType.AUDIO -> {
                        AudioContent(audioUrl = lesson.contentUrl ?: "")
                    }
                    ContentType.INTERACTIVE -> {
                        InteractiveContent(contentUrl = lesson.contentUrl ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun TextContent(
    content: String,
    onScrollPositionChanged: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    
    LaunchedEffect(scrollState.value) {
        onScrollPositionChanged(scrollState.value)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun VideoContent(
    videoUrl: String,
    onPositionChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    
    // Create ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false
        }
    }
    
    // Track playback position
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(1000)
            onPositionChanged(exoPlayer.currentPosition.toInt() / 1000)
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Video Player
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Video Controls
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        exoPlayer.seekTo(maxOf(0, exoPlayer.currentPosition - 10000))
                    }
                ) {
                    Icon(Icons.Default.Refresh, "Rewind 10s")
                }
                
                IconButton(
                    onClick = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    }
                ) {
                    Icon(
                        if (exoPlayer.isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                        "Play/Pause"
                    )
                }
                
                IconButton(
                    onClick = {
                        exoPlayer.seekTo(
                            minOf(exoPlayer.duration, exoPlayer.currentPosition + 10000)
                        )
                    }
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, "Forward 10s")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Video Info
        Text(
            text = "Video URL: $videoUrl",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ImageContent(imageUrl: String) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageUrl,
                            onLoading = { isLoading = true },
                            onSuccess = { isLoading = false },
                            onError = { 
                                isLoading = false
                                hasError = true
                            }
                        ),
                        contentDescription = "Lesson Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                    
                    if (hasError) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Failed to load image",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No Image",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No image available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Image Info
        if (imageUrl.isNotEmpty()) {
            Text(
                text = "Image URL: $imageUrl",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AudioContent(audioUrl: String) {
    val context = LocalContext.current
    
    // Create ExoPlayer for audio
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
            playWhenReady = false
        }
    }
    
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    
    // Update playback state
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(100)
            isPlaying = exoPlayer.isPlaying
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.takeIf { it > 0 } ?: 0L
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Audio Icon
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Audio",
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Progress Bar
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { 
                            if (duration > 0) currentPosition.toFloat() / duration.toFloat() 
                            else 0f 
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatTime(duration),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            exoPlayer.seekTo(maxOf(0, exoPlayer.currentPosition - 10000))
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Rewind 10s",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    FloatingActionButton(
                        onClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        }
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            exoPlayer.seekTo(
                                minOf(exoPlayer.duration, exoPlayer.currentPosition + 10000)
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Forward 10s",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Audio Info
                Text(
                    text = "Audio URL: $audioUrl",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

@Composable
fun InteractiveContent(
    contentUrl: String = ""
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (contentUrl.isNotEmpty()) {
            // WebView for HTML/JS content
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            
                            // Enable loading external content (images, videos, etc.)
                            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            
                            // Enable media playback
                            mediaPlaybackRequiresUserGesture = false
                            
                            // Enable caching for better performance
                            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                            
                            // Enable loading images
                            loadsImagesAutomatically = true
                            blockNetworkImage = false
                            blockNetworkLoads = false
                        }
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                android.util.Log.d("LessonViewer", "Page loaded: $url")
                            }
                            
                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                isLoading = false
                                hasError = true
                                errorMessage = "Error $errorCode: $description"
                                android.util.Log.e("LessonViewer", "WebView error: $errorMessage for URL: $failingUrl")
                            }
                        }
                        
                        // Load content
                        val finalUrl = if (contentUrl.endsWith("/")) {
                            "${contentUrl}index.html"
                        } else if (!contentUrl.contains(".html")) {
                            "$contentUrl/index.html"
                        } else {
                            contentUrl
                        }
                        
                        android.util.Log.d("LessonViewer", "Loading URL: $finalUrl")
                        
                        if (finalUrl.startsWith("http://") || finalUrl.startsWith("https://")) {
                            // Load from URL
                            loadUrl(finalUrl)
                        } else if (finalUrl.startsWith("file://")) {
                            // Load from local file
                            loadUrl(finalUrl)
                        } else {
                            // Assume it's a path to assets folder
                            loadUrl("file:///android_asset/$finalUrl")
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error message
            if (hasError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Error",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Failed to load content",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // Placeholder when no content URL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Interactive",
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Interactive Content",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "HTML/JavaScript interactive learning module",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Supports:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "• HTML5 content\n• JavaScript interactions\n• Local file:// URLs\n• Assets folder content\n• Remote HTTPS URLs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
