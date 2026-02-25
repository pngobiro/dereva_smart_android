package com.dereva.smart.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.ProgressSummary
import com.dereva.smart.domain.model.TestResult
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
    val error: String? = null
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val mockTestRepository: MockTestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
    
    // Hardcoded user ID for now (should come from auth)
    private val userId = "demo_user"
    
    init {
        loadProgress()
    }
    
    fun loadProgress() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Load progress summary
            progressRepository.getProgressSummary(userId)
                .onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(progressSummary = summary)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            
            // Load performance analytics
            mockTestRepository.getUserAnalytics(userId)
                .onSuccess { analytics ->
                    _uiState.value = _uiState.value.copy(performanceAnalytics = analytics)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            
            // Load recent test results
            mockTestRepository.getUserTestResults(userId)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        recentTestResults = results.take(5),
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
