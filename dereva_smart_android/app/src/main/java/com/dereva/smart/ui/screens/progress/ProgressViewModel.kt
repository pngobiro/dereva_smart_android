package com.dereva.smart.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.ProgressSummary
import com.dereva.smart.domain.model.QuizAttemptRecord
import com.dereva.smart.domain.model.TestResult
import com.dereva.smart.domain.repository.AuthRepository
import com.dereva.smart.domain.repository.MockTestRepository
import com.dereva.smart.domain.repository.PerformanceAnalytics
import com.dereva.smart.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProgressUiState(
    val isLoading: Boolean = false,
    val progressSummary: ProgressSummary? = null,
    val performanceAnalytics: PerformanceAnalytics? = null,
    val recentTestResults: List<TestResult> = emptyList(),
    val quizAttempts: List<QuizAttemptRecord> = emptyList(),
    val error: String? = null
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val mockTestRepository: MockTestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
    
    init {
        loadProgress()
    }
    
    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Get current user ID
            val currentUser = authRepository.getCurrentUser().getOrNull()
            if (currentUser == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Please log in to view your progress"
                )
                return@launch
            }
            
            val userId = currentUser.id
            
            // Load progress summary
            progressRepository.getProgressSummary(userId)
                .onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(progressSummary = summary)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            
            // Load quiz attempts and dynamically compute analytics
            progressRepository.getUserQuizAttempts(userId)
                .onSuccess { attempts ->
                    val totalTestsTaken = attempts.size
                    val totalTestsPassed = attempts.count { it.passed }
                    val averageScore = if (totalTestsTaken > 0) {
                        attempts.sumOf { it.scorePercentage }.toDouble() / totalTestsTaken
                    } else 0.0
                    val passRate = if (totalTestsTaken > 0) {
                        (totalTestsPassed.toDouble() / totalTestsTaken) * 100
                    } else 0.0
                    
                    var consecutivePasses = 0
                    for (attempt in attempts) {
                        if (attempt.passed) consecutivePasses++ else break
                    }
                    
                    val analytics = PerformanceAnalytics(
                        userId = userId,
                        totalTestsTaken = totalTestsTaken,
                        totalTestsPassed = totalTestsPassed,
                        averageScore = averageScore,
                        passRate = passRate,
                        weakAreas = emptyList(),
                        recentScores = attempts.take(5).map { it.scorePercentage.toDouble() },
                        consecutivePasses = consecutivePasses,
                        lastTestDate = attempts.firstOrNull()?.completedAt
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        quizAttempts = attempts,
                        performanceAnalytics = analytics,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    // Don't override error if previous calls already failed
                    if (_uiState.value.error == null) {
                        _uiState.value = _uiState.value.copy(error = error.message)
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
