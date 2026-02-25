package com.dereva.smart.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    text = "Your Learning Progress",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                // Study Stats Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Study Statistics",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider()
                        
                        val summary = uiState.progressSummary
                        StatRow("Total Study Time", "${summary?.totalStudyTimeMinutes ?: 0} minutes")
                        StatRow("Completion", "${String.format("%.1f", summary?.completionPercentage ?: 0.0)}%")
                        StatRow("Current Streak", "${summary?.currentStreak ?: 0} days")
                        StatRow("Longest Streak", "${summary?.longestStreak ?: 0} days")
                        
                        summary?.lastStudyDate?.let { date ->
                            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            StatRow("Last Study", formatter.format(date))
                        }
                    }
                }
                
                // Test Performance Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Test Performance",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider()
                        
                        val analytics = uiState.performanceAnalytics
                        StatRow("Tests Taken", "${analytics?.totalTestsTaken ?: 0}")
                        StatRow("Tests Passed", "${analytics?.totalTestsPassed ?: 0}")
                        StatRow("Average Score", "${String.format("%.1f", analytics?.averageScore ?: 0.0)}%")
                        StatRow("Pass Rate", "${String.format("%.1f", analytics?.passRate ?: 0.0)}%")
                        StatRow("Consecutive Passes", "${analytics?.consecutivePasses ?: 0}")
                    }
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
                
                // Recent Tests Card
                if (uiState.recentTestResults.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Recent Tests",
                                style = MaterialTheme.typography.titleMedium
                            )
                            HorizontalDivider()
                            
                            uiState.recentTestResults.forEach { result ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (result.passed) "✓ Passed" else "✗ Failed",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (result.passed) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "${String.format("%.0f", result.scorePercentage)}%",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
                
                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
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

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
