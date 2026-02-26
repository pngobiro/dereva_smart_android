package com.dereva.smart.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.domain.model.QuizContent
import com.dereva.smart.domain.model.QuizQuestion
import com.dereva.smart.domain.model.QuestionType
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    navController: NavController,
    viewModel: QuizViewModel = koinViewModel(),
    authViewModel: com.dereva.smart.ui.screens.auth.AuthViewModel = koinViewModel(),
    quizId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    val quiz = uiState.currentQuiz
    val currentQuestionIndex = uiState.currentQuestionIndex
    
    // Load quiz when screen is first displayed
    LaunchedEffect(quizId) {
        if (quiz == null || quiz.id != quizId) {
            viewModel.startQuiz(quizId, authViewModel, currentUser)
        }
    }
    
    // Timer
    var timeRemaining by remember { mutableStateOf(quiz?.timeLimit?.times(60) ?: 0) }
    var isTimeUp by remember { mutableStateOf(false) }
    
    LaunchedEffect(quiz) {
        if (quiz != null) {
            timeRemaining = quiz.timeLimit * 60
            while (timeRemaining > 0 && !isTimeUp) {
                delay(1000)
                timeRemaining--
            }
            if (timeRemaining == 0) {
                isTimeUp = true
            }
        }
    }
    
    // Auto-submit when time is up
    LaunchedEffect(isTimeUp) {
        if (isTimeUp && quiz != null) {
            // For guest users, pass null token
            // For registered users, the repository will handle token internally
            viewModel.submitQuiz(null)
        }
    }
    
    // Show error if quiz failed to load
    if (uiState.error != null && quiz == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "⚠️ Quiz Not Available",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when {
                        uiState.error?.contains("404", ignoreCase = true) == true || 
                        uiState.error?.contains("not found", ignoreCase = true) == true -> 
                            "Quiz content file (quiz.json) is not available on the server. Please contact support."
                        uiState.error != null -> uiState.error!!
                        else -> "Questions are not available for this quiz"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = { navController.navigateUp() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    // Show loading spinner while quiz is being loaded
    if (quiz == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Loading quiz...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        return
    }
    
    // Check if quiz has no questions
    if (quiz.questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "⚠️ No Questions Available",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "This quiz bank does not have any questions yet. Please try another quiz.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = { navController.navigateUp() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(quiz.title)
                        Text(
                            text = "Question ${currentQuestionIndex + 1}/${quiz.questions.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Show confirmation dialog before exiting
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Timer
                    Text(
                        text = formatTime(timeRemaining),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (timeRemaining < 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        enabled = currentQuestionIndex > 0
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Previous")
                    }
                    
                    if (currentQuestionIndex < quiz.questions.size - 1) {
                        Button(
                            onClick = { viewModel.nextQuestion() }
                        ) {
                            Text("Next")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        }
                    } else {
                        Button(
                            onClick = {
                                // Submit quiz - token will be fetched in coroutine
                                viewModel.submitQuiz(null)
                            }
                        ) {
                            Text("Submit Quiz")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / quiz.questions.size },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (currentQuestion != null) {
                QuestionView(
                    question = currentQuestion,
                    userAnswer = uiState.userAnswers[currentQuestion.id],
                    onAnswerSelected = { answer ->
                        viewModel.answerQuestion(currentQuestion.id, answer)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                )
            }
        }
    }
    
    // Show results when quiz is submitted
    if (uiState.quizResult != null) {
        QuizResultDialog(
            result = uiState.quizResult!!,
            onDismiss = {
                viewModel.resetQuiz()
                navController.navigateUp()
            }
        )
    }
    
    // Show error dialog if submission fails
    if (uiState.error != null && uiState.quizResult == null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Submission Error") },
            text = { Text(uiState.error ?: "Failed to submit quiz") },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Show loading overlay during submission
    if (uiState.isLoading && quiz != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Submitting quiz...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun QuestionView(
    question: QuizQuestion,
    userAnswer: Any?,
    onAnswerSelected: (Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Question text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (question.hint != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "💡 Hint: ${question.hint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Answer options based on question type
        when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                MultipleChoiceOptions(
                    options = question.options ?: emptyList(),
                    selectedOption = userAnswer as? String,
                    onOptionSelected = onAnswerSelected
                )
            }
            QuestionType.TRUE_FALSE -> {
                TrueFalseOptions(
                    selectedAnswer = userAnswer as? Boolean,
                    onAnswerSelected = onAnswerSelected
                )
            }
            QuestionType.MULTIPLE_SELECT -> {
                MultipleSelectOptions(
                    options = question.options ?: emptyList(),
                    selectedOptions = (userAnswer as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    onOptionsSelected = onAnswerSelected
                )
            }
            else -> {
                Text("Question type not yet supported")
            }
        }
    }
}

@Composable
fun MultipleChoiceOptions(
    options: List<com.dereva.smart.domain.model.QuizOption>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedOption == option.id,
                        onClick = { onOptionSelected(option.id) },
                        role = Role.RadioButton
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == option.id)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == option.id,
                        onClick = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun TrueFalseOptions(
    selectedAnswer: Boolean?,
    onAnswerSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(true to "True", false to "False").forEach { (value, label) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedAnswer == value,
                        onClick = { onAnswerSelected(value) },
                        role = Role.RadioButton
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedAnswer == value)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == value,
                        onClick = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun MultipleSelectOptions(
    options: List<com.dereva.smart.domain.model.QuizOption>,
    selectedOptions: List<String>,
    onOptionsSelected: (List<String>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select all that apply:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        options.forEach { option ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOptions.contains(option.id))
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedOptions.contains(option.id),
                        onCheckedChange = { checked ->
                            val newSelection = if (checked) {
                                selectedOptions + option.id
                            } else {
                                selectedOptions - option.id
                            }
                            onOptionsSelected(newSelection)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun QuizResultDialog(
    result: com.dereva.smart.domain.model.QuizAttempt,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (result.passed) "🎉 Congratulations!" else "Keep Practicing!",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Score: ${result.score}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (result.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text("Correct: ${result.correctAnswers}/${result.totalQuestions}")
                Text("Time taken: ${formatTime(result.timeTaken)}")
                Spacer(Modifier.height(8.dp))
                if (result.passed) {
                    Text(
                        "You passed! Great job!",
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        "You need ${result.totalQuestions * 70 / 100} correct answers to pass. Keep studying!",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
