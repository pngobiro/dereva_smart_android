package com.dereva.smart.ui.screens.quiz

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dereva.smart.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

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

    // Load quiz once on first composition for this quizId
    LaunchedEffect(quizId) {
        if (quiz == null || quiz.id != quizId) {
            viewModel.startQuiz(quizId, authViewModel, currentUser)
        }
    }

    // ---------------------------------------------------------------------------
    // Timer — only starts once quiz is loaded; does not restart on recomposition
    // ---------------------------------------------------------------------------
    val initialSeconds = remember(quiz?.id) { quiz?.timeLimit?.times(60) ?: 0 }
    var timeRemaining by remember(quiz?.id) { mutableIntStateOf(initialSeconds) }
    var isTimeUp by remember(quiz?.id) { mutableStateOf(false) }

    LaunchedEffect(quiz?.id) {
        if (quiz != null && initialSeconds > 0) {
            while (timeRemaining > 0) {
                delay(1000L)
                timeRemaining--
            }
            isTimeUp = true
        }
    }

    val scope = rememberCoroutineScope()

    // Auto-submit on time-out
    LaunchedEffect(isTimeUp) {
        if (isTimeUp && quiz != null) {
            scope.launch {
                val token = authViewModel.getAuthToken()
                viewModel.submitQuiz(token)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Guard states: error / loading / empty
    // ---------------------------------------------------------------------------

    if (uiState.error != null && quiz == null) {
        QuizErrorScreen(
            message = when {
                uiState.error?.contains("404", ignoreCase = true) == true ||
                uiState.error?.contains("not found", ignoreCase = true) == true ->
                    "Quiz content file (quiz.json) is not available on the server. Please contact support."
                else -> uiState.error ?: "Failed to load quiz."
            },
            onBack = { navController.navigateUp() }
        )
        return
    }

    if (quiz == null) {
        FullScreenLoading(message = "Loading quiz…")
        return
    }

    if (quiz.questions.isEmpty()) {
        QuizErrorScreen(
            message = "This quiz has no questions yet. Please try another quiz.",
            onBack = { navController.navigateUp() }
        )
        return
    }

    // ---------------------------------------------------------------------------
    // Main quiz UI
    // ---------------------------------------------------------------------------

    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = quiz.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Question ${currentQuestionIndex + 1} / ${quiz.questions.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = formatTime(timeRemaining),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (timeRemaining < 60)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onPrimary,
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Previous")
                    }

                    if (currentQuestionIndex < quiz.questions.size - 1) {
                        Button(onClick = { viewModel.nextQuestion() }) {
                            Text("Next")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    } else {
                        Button(onClick = { 
                            scope.launch {
                                val token = authViewModel.getAuthToken()
                                viewModel.submitQuiz(token)
                            }
                        }) {
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

    // Results dialog
    if (uiState.quizResult != null) {
        QuizResultDialog(
            result = uiState.quizResult!!,
            passingScore = quiz.passingScore,
            questions = quiz.questions,
            onDismiss = {
                viewModel.resetQuiz()
                navController.navigateUp()
            }
        )
    }

    // Submission error dialog
    if (uiState.error != null && uiState.quizResult == null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Submission Error") },
            text = { Text(uiState.error ?: "Failed to submit quiz.") },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) { Text("OK") }
            }
        )
    }

    // Submission loading overlay
    if (uiState.isLoading) {
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
                Text("Submitting quiz…", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Guard state composables
// ---------------------------------------------------------------------------

@Composable
private fun FullScreenLoading(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun QuizErrorScreen(message: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack) { Text("Go Back") }
        }
    }
}

// ---------------------------------------------------------------------------
// Question view
// ---------------------------------------------------------------------------

@Composable
fun QuestionView(
    question: QuizQuestion,
    userAnswer: Any?,
    onAnswerSelected: (Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RenderContentField(
                    content = question.question,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Legacy: top-level media field
        @Suppress("DEPRECATION")
        question.media?.let { media ->
            Spacer(Modifier.height(16.dp))
            MediaContent(media = media)
        }

        // Legacy: richContent field
        @Suppress("DEPRECATION")
        question.richContent?.let { rc ->
            Spacer(Modifier.height(16.dp))
            LegacyRichContent(richContent = rc)
        }

        Spacer(Modifier.height(24.dp))

        // Answer input
        when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> MultipleChoiceOptions(
                options = question.options ?: emptyList(),
                selectedOption = userAnswer as? String,
                onOptionSelected = onAnswerSelected
            )
            QuestionType.TRUE_FALSE -> TrueFalseOptions(
                selectedAnswer = userAnswer as? Boolean,
                onAnswerSelected = onAnswerSelected
            )
            QuestionType.MULTIPLE_SELECT -> MultipleSelectOptions(
                options = question.options ?: emptyList(),
                selectedOptions = (userAnswer as? List<*>)?.filterIsInstance<String>()
                    ?: emptyList(),
                onOptionsSelected = onAnswerSelected
            )
            else -> {
                Text(
                    text = "Question type '${question.type.name}' is not yet supported.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Educational context — shown at the bottom after answer options
        question.context?.let { ctx ->
            Spacer(Modifier.height(16.dp))
            RenderContent(ctx)
        }
    }
}

// ---------------------------------------------------------------------------
// Content rendering helpers
// ---------------------------------------------------------------------------

/**
 * Renders a field that can be a plain [String] or a deserialised [Map] (ContentObject).
 */
@Composable
fun RenderContentField(
    content: Any,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight? = null
) {
    when (content) {
        is String -> Text(text = content, style = style, fontWeight = fontWeight, color = color)
        is Map<*, *> -> {
            val obj = parseContentObject(content)
            if (obj != null) {
                RenderContent(obj)
            } else {
                Text(text = content.toString(), style = style, fontWeight = fontWeight, color = color)
            }
        }
        else -> Text(text = content.toString(), style = style, fontWeight = fontWeight, color = color)
    }
}

/** Parses a raw [Map] (from JSON deserialisation) into a [ContentObject]. Returns null if invalid. */
fun parseContentObject(map: Map<*, *>): ContentObject? {
    val format = map["format"] as? String ?: return null
    val value  = map["value"]  as? String ?: return null
    val mediaMap = map["media"] as? Map<*, *>
    val media = mediaMap?.let {
        val url = it["url"] as? String
        if (url != null) {
            QuizMedia(
                type     = it["type"]     as? String ?: "image",
                url      = url,
                caption  = it["caption"]  as? String,
                position = it["position"] as? String ?: "before"
            )
        } else {
            null
        }
    }
    return ContentObject(format = format, value = value, media = media)
}

/** Renders a [ContentObject] — media (before), content, media (after). */
@Composable
fun RenderContent(contentObject: ContentObject) {
    Column {
        if (contentObject.media?.position == "before") {
            MediaContent(media = contentObject.media)
            Spacer(Modifier.height(12.dp))
        }

        when (contentObject.format.lowercase()) {
            "html"  -> HtmlContent(html = contentObject.value)
            "latex" -> Text(
                // LaTeX rendering planned — display raw source for now
                text = contentObject.value,
                style = MaterialTheme.typography.bodyMedium
            )
            else    -> Text(
                text = contentObject.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (contentObject.media?.position == "after") {
            Spacer(Modifier.height(12.dp))
            MediaContent(media = contentObject.media)
        }
    }
}

// ---------------------------------------------------------------------------
// Media
// ---------------------------------------------------------------------------

@Composable
fun MediaContent(media: QuizMedia) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            when (media.type.lowercase()) {
                "image" -> {
                    AsyncImage(
                        model = media.url,
                        contentDescription = media.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image),
                        placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery)
                    )
                    media.caption?.let { caption ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = caption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                "video" -> {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Text(
                        text = "📹 ${media.caption ?: "Watch video"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(media.url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Video")
                    }
                }
                "audio" -> {
                    Text(
                        text = "🔊 ${media.caption ?: "Audio content"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Legacy rich content (deprecated)
// ---------------------------------------------------------------------------

@Suppress("DEPRECATION")
@Composable
private fun LegacyRichContent(richContent: QuizRichContent) {
    when (richContent.type.lowercase()) {
        "html" -> HtmlContent(html = richContent.content)
        else   -> Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = richContent.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// HTML renderer
// ---------------------------------------------------------------------------

@Composable
fun HtmlContent(html: String) {
    // wrapHeight tracks the content height reported by the WebView so we
    // don't clip content or waste space with a fixed height.
    var contentHeightDp by remember { mutableIntStateOf(120) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = false
                    loadWithOverviewMode = true
                    useWideViewPort = false
                    setSupportZoom(false)
                }
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                // Report content height back to Compose
                viewTreeObserver.addOnGlobalLayoutListener {
                    val h = (contentHeight * resources.displayMetrics.density).toInt()
                    if (h > 0) contentHeightDp = h
                }
            }
        },
        update = { webView ->
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            padding: 12px;
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                            font-size: 14px;
                            line-height: 1.5;
                        }
                        table {
                            border-collapse: collapse;
                            width: 100%;
                            margin: 8px 0;
                        }
                        th, td {
                            padding: 8px;
                            text-align: left;
                            border: 1px solid #ddd;
                        }
                        th { background-color: #f5f5f5; font-weight: bold; }
                        h3, h4 { margin: 0 0 8px 0; }
                        ul, ol { margin: 8px 0; padding-left: 20px; }
                        li { margin-bottom: 4px; }
                        p { margin: 8px 0; }
                    </style>
                </head>
                <body>$html</body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 500.dp)
            // Expand to measured content height (capped above)
            .height(contentHeightDp.coerceIn(80, 500).dp)
    )
}

// ---------------------------------------------------------------------------
// Answer option composables
// ---------------------------------------------------------------------------

@Composable
fun MultipleChoiceOptions(
    options: List<QuizOption>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = { onOptionSelected(option.id) },
                        role = Role.RadioButton
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    OptionText(text = option.text)
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
            val isSelected = selectedAnswer == value
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = { onAnswerSelected(value) },
                        role = Role.RadioButton
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun MultipleSelectOptions(
    options: List<QuizOption>,
    selectedOptions: List<String>,
    onOptionsSelected: (List<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Select all that apply:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        options.forEach { option ->
            val isChecked = selectedOptions.contains(option.id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    // Make the whole card tappable — not just the checkbox
                    .clickable {
                        val updated = if (isChecked) {
                            selectedOptions - option.id
                        } else {
                            selectedOptions + option.id
                        }
                        onOptionsSelected(updated)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked)
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
                        checked = isChecked,
                        onCheckedChange = null  // handled by card clickable above
                    )
                    Spacer(Modifier.width(8.dp))
                    OptionText(text = option.text)
                }
            }
        }
    }
}

/** Renders option text — handles plain String or ContentObject. */
@Composable
private fun OptionText(text: Any) {
    when (text) {
        is String    -> Text(text = text, style = MaterialTheme.typography.bodyLarge)
        is Map<*, *> -> {
            val obj = parseContentObject(text)
            if (obj != null) RenderContent(obj)
            else Text(text = text.toString(), style = MaterialTheme.typography.bodyLarge)
        }
        else -> Text(text = text.toString(), style = MaterialTheme.typography.bodyLarge)
    }
}

// ---------------------------------------------------------------------------
// Results dialog
// ---------------------------------------------------------------------------

@Composable
fun QuizResultDialog(
    result: QuizAttempt,
    passingScore: Int,
    questions: List<QuizQuestion>,
    onDismiss: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (result.passed) "🎉 Congratulations!" else "Keep Practicing!",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Score: ${result.score}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (result.passed)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text("Correct answers: ${result.correctAnswers} / ${result.totalQuestions}")
                Text("Time taken: ${formatTime(result.timeTaken)}")
                Text("Passing score: $passingScore%")
                Spacer(Modifier.height(8.dp))

                if (result.passed) {
                    Text("You passed! Great job!", color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(
                        text = "You need $passingScore% to pass. Keep studying!",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = { showDetails = !showDetails },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (showDetails) "Hide Explanations ▲" else "Show Explanations ▼",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (showDetails && result.feedback.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    result.feedback.forEachIndexed { index, feedback ->
                        val questionText = questions
                            .find { it.id == feedback.questionId }
                            ?.let { extractQuestionText(it.question) }
                        QuestionFeedbackCard(
                            questionNumber = index + 1,
                            feedback = feedback,
                            questionText = questionText
                        )
                        if (index < result.feedback.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}

// ---------------------------------------------------------------------------
// Feedback card
// ---------------------------------------------------------------------------

@Composable
fun QuestionFeedbackCard(
    questionNumber: Int,
    feedback: QuizFeedback,
    questionText: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (feedback.isCorrect)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q$questionNumber",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (feedback.isCorrect) "✓ Correct" else "✗ Incorrect",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (feedback.isCorrect)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            }

            questionText?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            feedback.userAnswer?.let { answer ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Your answer: ${formatUserAnswer(answer)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (feedback.explanation.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "💡 ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = feedback.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Pure utility functions
// ---------------------------------------------------------------------------

fun formatUserAnswer(answer: Any?): String = when (answer) {
    is Boolean    -> if (answer) "True" else "False"
    is List<*>    -> answer.joinToString(", ")
    else          -> answer?.toString() ?: "No answer"
}

fun extractQuestionText(question: Any): String = when (question) {
    is String    -> question
    is Map<*, *> -> parseContentObject(question)?.value ?: question.toString()
    else         -> question.toString()
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
