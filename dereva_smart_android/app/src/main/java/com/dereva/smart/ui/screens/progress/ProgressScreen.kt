package com.dereva.smart.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Quizzes", "Learning Modules")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadProgress() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.isLoading && uiState.progressSummary == null) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (selectedTabIndex == 0) {
                        // Quizzes Tab Content
                        
                        val analytics = uiState.performanceAnalytics
                        
                        // Modern Stat Cards for Quizzes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Attempts",
                                value = "${analytics?.totalTestsTaken ?: 0}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Passed",
                                value = "${analytics?.totalTestsPassed ?: 0}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Avg Score",
                                value = "${String.format("%.0f", analytics?.averageScore ?: 0.0)}%",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Pass Rate",
                                value = "${String.format("%.0f", analytics?.passRate ?: 0.0)}%",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        // Quiz Attempts History
                        if (uiState.quizAttempts.isNotEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Quiz Attempts History",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    uiState.quizAttempts.forEach { attempt ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = attempt.quizTitle ?: "Quiz Attempt",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = attempt.category ?: "General",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Surface(
                                                    color = if (attempt.passed) 
                                                        MaterialTheme.colorScheme.primaryContainer 
                                                    else 
                                                        MaterialTheme.colorScheme.errorContainer,
                                                    shape = MaterialTheme.shapes.small
                                                ) {
                                                    Text(
                                                        text = "${attempt.scorePercentage}%",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = if (attempt.passed) 
                                                            MaterialTheme.colorScheme.onPrimaryContainer 
                                                        else 
                                                            MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                }
                                            }
                                            
                                            val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                                            Text(
                                                text = "${attempt.correctAnswers}/${attempt.totalQuestions} • ${attempt.timeTakenSeconds / 60}m ${attempt.timeTakenSeconds % 60}s • ${attempt.completedAt?.let { formatter.format(it) } ?: "N/A"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (attempt != uiState.quizAttempts.last()) {
                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No quiz attempts yet. Start taking quizzes to see your history here!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )
                        }
                    } else {
                        // Learning Modules Tab Content
                        
                        val summary = uiState.progressSummary
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Study Time",
                                value = "${summary?.totalStudyTimeMinutes ?: 0}m",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Completion",
                                value = "${String.format("%.1f", summary?.completionPercentage ?: 0.0)}%",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Current Streak",
                                value = "${summary?.currentStreak ?: 0}d",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Longest Streak",
                                value = "${summary?.longestStreak ?: 0}d",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Achievements Card
                        if (!uiState.progressSummary?.badges.isNullOrEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Achievements",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    HorizontalDivider()
                                    
                                    uiState.progressSummary?.badges?.take(5)?.forEach { badge ->
                                        Text(
                                            text = "🏆 ${badge.titleEn}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Coming Soon",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "More Learning Stats Soon",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Detailed tracking for learning modules will be available here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    uiState.error?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
