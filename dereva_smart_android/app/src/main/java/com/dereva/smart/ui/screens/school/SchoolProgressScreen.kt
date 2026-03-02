package com.dereva.smart.ui.screens.school

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.domain.model.CategoryStat
import com.dereva.smart.domain.model.LeaderboardEntry
import com.dereva.smart.domain.model.SchoolProgressRecord
import com.dereva.smart.domain.model.SchoolStats
import com.dereva.smart.ui.screens.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolProgressScreen(
    navController: NavController,
    schoolId: String,
    authViewModel: AuthViewModel,
    viewModel: SchoolViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.uiState.collectAsState()
    
    LaunchedEffect(schoolId) {
        viewModel.loadSchoolStats(schoolId)
        viewModel.loadSchoolProgress(schoolId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.loadSchoolStats(schoolId)
                        viewModel.loadSchoolProgress(schoolId)
                    }) {
                        Icon(Icons.Default.Refresh, "Refresh")
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.schoolStats?.let { stats ->
                    StatsCards(stats)
                    
                    if (stats.topPerformers.isNotEmpty()) {
                        TopPerformersCard(stats.topPerformers, currentUser.currentUser?.id)
                    }
                    
                    if (stats.categoryStats.isNotEmpty()) {
                        CategoryStatsCard(stats.categoryStats)
                    }
                }
                
                if (uiState.schoolProgress.isNotEmpty()) {
                    RecentAttemptsCard(uiState.schoolProgress)
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
fun StatsCards(stats: SchoolStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Students",
            value = stats.totalStudents.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Attempts",
            value = stats.totalAttempts.toString(),
            modifier = Modifier.weight(1f)
        )
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Avg Score",
            value = "${stats.averageScore.toInt()}%",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Pass Rate",
            value = "${stats.passRate.toInt()}%",
            modifier = Modifier.weight(1f)
        )
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

@Composable
fun TopPerformersCard(performers: List<LeaderboardEntry>, currentUserId: String?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Top Performers",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            performers.forEach { performer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = when (performer.rank) {
                                1 -> MaterialTheme.colorScheme.tertiaryContainer
                                2 -> MaterialTheme.colorScheme.secondaryContainer
                                3 -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "#${performer.rank}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Column {
                            Text(
                                text = performer.displayName + if (performer.userId == currentUserId) " (You)" else "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${performer.testsCompleted} attempts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "${performer.averageScore.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStatsCard(categoryStats: List<CategoryStat>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Performance by Category",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            categoryStats.forEach { stat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stat.category,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${stat.attempts} attempts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${stat.avgScore.toInt()}% avg",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${stat.passRate.toInt()}% pass",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (stat.passRate >= 70) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentAttemptsCard(progress: List<SchoolProgressRecord>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recent Quiz Attempts",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            progress.take(10).forEach { record ->
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
                                text = record.userName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = record.quizName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = if (record.passed) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "${record.score}%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (record.passed) 
                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                else 
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Text(
                        text = "${record.correctAnswers}/${record.totalQuestions} • ${formatTime(record.timeTaken)} • ${formatDate(record.completedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (record != progress.last()) {
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "${mins}m ${secs}s"
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return formatter.format(date)
}
