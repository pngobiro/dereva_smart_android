package com.dereva.smart.ui.screens.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.domain.model.QuizBank
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    navController: NavController,
    viewModel: QuizViewModel = koinViewModel(),
    authViewModel: com.dereva.smart.ui.screens.auth.AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    val category = currentUser?.targetCategory?.name ?: "B1"
    
    LaunchedEffect(category) {
        viewModel.loadQuizBanks(category = category)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Mock Tests")
                        Text(
                            text = "Category: $category",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadQuizBanks(category) }) {
                            Text("Retry")
                        }
                    }
                }
                
                uiState.quizBanks.isEmpty() -> {
                    Text(
                        text = "No quizzes available for $category",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.quizBanks) { quiz ->
                            QuizBankCard(
                                quiz = quiz,
                                onClick = {
                                    // Start quiz with auth token if available
                                    viewModel.startQuiz(quiz.id, authViewModel, authState.currentUser)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Access dialog
    if (uiState.showAccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAccessDialog() },
            title = { Text("Access Restricted") },
            text = { Text(uiState.accessDialogMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissAccessDialog() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun QuizBankCard(
    quiz: QuizBank,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (quiz.isPremium) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Premium",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = quiz.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuizInfoChip(
                    label = "${quiz.totalQuestions} questions",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                QuizInfoChip(
                    label = "${quiz.timeLimit} min",
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                QuizInfoChip(
                    label = "${quiz.passingScore}% to pass",
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
            
            Surface(
                color = when (quiz.difficulty) {
                    "EASY" -> MaterialTheme.colorScheme.primaryContainer
                    "MEDIUM" -> MaterialTheme.colorScheme.secondaryContainer
                    "HARD" -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = quiz.difficulty,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun QuizInfoChip(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
