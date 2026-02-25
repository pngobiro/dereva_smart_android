package com.dereva.smart.ui.screens.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.domain.model.ContentType
import com.dereva.smart.domain.model.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    navController: NavController,
    viewModel: ContentViewModel,
    moduleId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load lessons when screen is first displayed
    androidx.compose.runtime.LaunchedEffect(moduleId) {
        android.util.Log.d("LessonListScreen", "LaunchedEffect triggered for moduleId: $moduleId")
        viewModel.selectModule(moduleId)
    }
    
    android.util.Log.d("LessonListScreen", "Rendering: lessons=${uiState.lessons.size}, isLoading=${uiState.isLoading}")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.currentModule?.title ?: "Lessons") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Module Info Card
            uiState.currentModule?.let { module ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = module.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { module.completionPercentage / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${module.completionPercentage}% Complete",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Lessons List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.lessons) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            onLessonClick = {
                                viewModel.selectLesson(lesson.id)
                                navController.navigate("lesson_viewer/${lesson.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCard(
    lesson: Lesson,
    onLessonClick: () -> Unit
) {
    Card(
        onClick = onLessonClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${lesson.orderIndex}. ${lesson.title}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${lesson.duration} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = when (lesson.contentType) {
                            ContentType.TEXT -> "📄 Text"
                            ContentType.VIDEO -> "🎥 Video"
                            ContentType.IMAGE -> "🖼️ Image"
                            ContentType.AUDIO -> "🎵 Audio"
                            ContentType.INTERACTIVE -> "🎮 Interactive"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Status Icon
            if (lesson.isCompleted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
