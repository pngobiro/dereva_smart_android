package com.dereva.smart.ui.screens.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = false,
    val quizBanks: List<QuizBank> = emptyList(),
    val currentQuiz: QuizContent? = null,
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, Any?> = emptyMap(),
    val quizResult: QuizAttempt? = null,
    val attemptHistory: List<QuizAttemptHistory> = emptyList(),
    val error: String? = null,
    val startTime: Long = 0,
    val showAccessDialog: Boolean = false,
    val accessDialogMessage: String = ""
)

class QuizViewModel(
    private val repository: QuizRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "QuizViewModel"
    }
    
    fun loadQuizBanks(category: String? = null, isPremium: Boolean? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getQuizBanks(category, isPremium)
                .onSuccess { quizzes ->
                    _uiState.value = _uiState.value.copy(
                        quizBanks = quizzes,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to load quiz banks", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load quizzes",
                        isLoading = false
                    )
                }
        }
    }
    
    fun startQuiz(quizId: String, token: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getQuizContent(quizId, token)
                .onSuccess { quiz ->
                    _uiState.value = _uiState.value.copy(
                        currentQuiz = quiz,
                        currentQuestionIndex = 0,
                        userAnswers = emptyMap(),
                        quizResult = null,
                        startTime = System.currentTimeMillis(),
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to load quiz content", error)
                    val errorMessage = error.message ?: "Failed to load quiz"
                    
                    // Check if it's an access error
                    if (errorMessage.contains("authentication", ignoreCase = true) ||
                        errorMessage.contains("subscription", ignoreCase = true)) {
                        _uiState.value = _uiState.value.copy(
                            showAccessDialog = true,
                            accessDialogMessage = errorMessage,
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = errorMessage,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun answerQuestion(questionId: String, answer: Any?) {
        val currentAnswers = _uiState.value.userAnswers.toMutableMap()
        currentAnswers[questionId] = answer
        _uiState.value = _uiState.value.copy(userAnswers = currentAnswers)
    }
    
    fun nextQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        val totalQuestions = _uiState.value.currentQuiz?.questions?.size ?: 0
        
        if (currentIndex < totalQuestions - 1) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = currentIndex + 1)
        }
    }
    
    fun previousQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        if (currentIndex > 0) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = currentIndex - 1)
        }
    }
    
    fun goToQuestion(index: Int) {
        val totalQuestions = _uiState.value.currentQuiz?.questions?.size ?: 0
        if (index in 0 until totalQuestions) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = index)
        }
    }
    
    fun submitQuiz(token: String? = null) {
        val quiz = _uiState.value.currentQuiz ?: return
        val startTime = _uiState.value.startTime
        val timeTaken = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val answers = _uiState.value.userAnswers.map { (questionId, answer) ->
                QuizAnswer(questionId, answer)
            }
            
            repository.submitQuizAttempt(quiz.id, answers, timeTaken, token)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        quizResult = result,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to submit quiz", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to submit quiz",
                        isLoading = false
                    )
                }
        }
    }
    
    fun loadAttemptHistory(quizId: String, token: String) {
        viewModelScope.launch {
            repository.getQuizAttempts(quizId, token)
                .onSuccess { attempts ->
                    _uiState.value = _uiState.value.copy(attemptHistory = attempts)
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to load attempt history", error)
                }
        }
    }
    
    fun resetQuiz() {
        _uiState.value = _uiState.value.copy(
            currentQuiz = null,
            currentQuestionIndex = 0,
            userAnswers = emptyMap(),
            quizResult = null,
            startTime = 0
        )
    }
    
    fun dismissAccessDialog() {
        _uiState.value = _uiState.value.copy(showAccessDialog = false, accessDialogMessage = "")
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun getProgress(): Float {
        val totalQuestions = _uiState.value.currentQuiz?.questions?.size ?: 0
        val answeredQuestions = _uiState.value.userAnswers.size
        return if (totalQuestions > 0) answeredQuestions.toFloat() / totalQuestions else 0f
    }
    
    fun isQuestionAnswered(questionId: String): Boolean {
        return _uiState.value.userAnswers.containsKey(questionId)
    }
    
    fun getCurrentQuestion(): QuizQuestion? {
        val quiz = _uiState.value.currentQuiz ?: return null
        val index = _uiState.value.currentQuestionIndex
        return quiz.questions.getOrNull(index)
    }
    
    fun getElapsedTime(): Int {
        val startTime = _uiState.value.startTime
        if (startTime == 0L) return 0
        return ((System.currentTimeMillis() - startTime) / 1000).toInt()
    }
}
