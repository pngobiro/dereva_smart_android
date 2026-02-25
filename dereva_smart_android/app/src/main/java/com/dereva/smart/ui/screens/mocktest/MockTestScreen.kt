package com.dereva.smart.ui.screens.mocktest

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Mock Test")
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.testResult != null) {
                // Show test results
                TestResultView(
                    result = uiState.testResult!!,
                    onNewTest = { viewModel.generateNewTest() }
                )
            } else if (uiState.currentTest != null) {
                // Show test in progress (simplified for now)
                TestInProgressView(
                    test = uiState.currentTest!!,
                    onSubmit = { viewModel.submitTest() }
                )
            } else {
                // Show test instructions and start button
                TestInstructionsView(
                    onStartTest = { viewModel.generateNewTest() },
                    previousTests = uiState.userTests
                )
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
