package com.dereva.smart.ui.screens.mocktest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(
    navController: NavController,
    viewModel: MockTestViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authViewModel: com.dereva.smart.ui.screens.auth.AuthViewModel = koinViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    
    val quizViewModel: com.dereva.smart.ui.screens.quiz.QuizViewModel = koinViewModel()
    val quizUiState by quizViewModel.uiState.collectAsState()
    
    // Load quiz banks when screen loads
    LaunchedEffect(currentUser?.targetCategory) {
        currentUser?.targetCategory?.let { category ->
            quizViewModel.loadQuizBanks(category.name)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Quiz Banks")
                        currentUser?.let {
                            Text(
                                text = "Category: ${it.targetCategory.name}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (quizUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (quizUiState.quizBanks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No quiz banks available",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quiz banks for ${currentUser?.targetCategory?.name ?: "this category"} will be available soon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quizUiState.quizBanks) { quizBank ->
                        QuizBankCard(
                            quizBank = quizBank,
                            isGuest = currentUser?.isGuestMode == true,
                            onClick = {
                                // Check access control
                                if (quizBank.isPremium && currentUser?.isGuestMode == true) {
                                    // Premium quiz, guest user -> redirect to registration
                                    navController.navigate(Screen.Register.route)
                                } else if (quizBank.isPremium && currentUser?.isSubscriptionActive == false) {
                                    // Premium quiz, registered but no subscription -> redirect to payment
                                    navController.navigate(Screen.Payment.route)
                                } else {
                                    // Start quiz
                                    quizViewModel.startQuiz(quizBank.id, authViewModel, currentUser)
                                }
                            }
                        )
                    }
                }
            }
            
            quizUiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
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
    
    // Access dialog for premium content
    if (quizUiState.showAccessDialog) {
        AlertDialog(
            onDismissRequest = { quizViewModel.dismissAccessDialog() },
            title = { Text("Access Restricted") },
            text = { Text(quizUiState.accessDialogMessage) },
            confirmButton = {
                if (currentUser?.isGuestMode == true) {
                    TextButton(
                        onClick = {
                            quizViewModel.dismissAccessDialog()
                            navController.navigate(com.dereva.smart.ui.navigation.Screen.Register.route)
                        }
                    ) {
                        Text("Register Now")
                    }
                } else {
                    TextButton(
                        onClick = {
                            quizViewModel.dismissAccessDialog()
                            navController.navigate(com.dereva.smart.ui.navigation.Screen.Payment.route)
                        }
                    ) {
                        Text("Subscribe")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { quizViewModel.dismissAccessDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Navigate to quiz taking screen when quiz is loaded
    LaunchedEffect(quizUiState.currentQuiz) {
        if (quizUiState.currentQuiz != null) {
            navController.navigate(Screen.QuizTaking.route)
        }
    }
}

@Composable
fun ColumnScope.TestInstructionsView(
    onStartTest: () -> Unit,
    previousTests: List<com.dereva.smart.domain.model.MockTest>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Test Instructions",
                style = MaterialTheme.typography.titleLarge
            )
            TestInstruction("✓ 50 questions total")
            TestInstruction("✓ 60 minutes duration")
            TestInstruction("✓ 80% pass mark (40/50 correct)")
            TestInstruction("✓ Questions cover all curriculum topics")
            TestInstruction("✓ Review explanations after submission")
        }
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
            Text(
                text = "This test simulates the actual NTSA theory test format.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    
    if (previousTests.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Previous Tests: ${previousTests.size}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.weight(1f))
    
    Button(
        onClick = onStartTest,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = "Start New Test",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ColumnScope.TestInProgressView(
    test: com.dereva.smart.domain.model.MockTest,
    onSubmit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Test in Progress",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Questions answered: ${test.userAnswers.size} / ${test.questions.size}",
                style = MaterialTheme.typography.bodyLarge
            )
            LinearProgressIndicator(
                progress = { test.progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    Spacer(modifier = Modifier.weight(1f))
    
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = test.userAnswers.size == test.questions.size
    ) {
        Text(
            text = "Submit Test",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ColumnScope.TestResultView(
    result: com.dereva.smart.domain.model.TestResult,
    onNewTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.passed) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (result.passed) "✓ PASSED" else "✗ FAILED",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "${String.format("%.1f", result.scorePercentage)}%",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Results Summary",
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()
            ResultRow("Correct", result.correctAnswers.toString())
            ResultRow("Incorrect", result.incorrectAnswers.toString())
            ResultRow("Unanswered", result.unanswered.toString())
            ResultRow("Total", result.totalQuestions.toString())
        }
    }
    
    Spacer(modifier = Modifier.weight(1f))
    
    Button(
        onClick = onNewTest,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = "Take Another Test",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TestInstruction(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizBankCard(
    quizBank: com.dereva.smart.domain.model.QuizBank,
    isGuest: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (quizBank.isPremium && isGuest) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quizBank.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (quizBank.isPremium && isGuest) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Premium",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = quizBank.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${quizBank.totalQuestions} questions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "⏱ ${quizBank.timeLimit} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Difficulty badge
                Surface(
                    color = when (quizBank.difficulty.lowercase()) {
                        "easy" -> MaterialTheme.colorScheme.primaryContainer
                        "medium" -> MaterialTheme.colorScheme.secondaryContainer
                        "hard" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = quizBank.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
