package com.dereva.smart.ui.screens.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.AITutor
import com.dereva.smart.domain.model.Language
import com.dereva.smart.domain.repository.AITutorRepository
import com.dereva.smart.domain.repository.StudyRecommendation
import com.dereva.smart.domain.repository.TutorResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TutorUiState(
    val isLoading: Boolean = false,
    val conversationHistory: List<AITutor> = emptyList(),
    val currentLanguage: Language = Language.ENGLISH,
    val recommendations: List<StudyRecommendation> = emptyList(),
    val error: String? = null
)

class TutorViewModel(
    private val repository: AITutorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TutorUiState())
    val uiState: StateFlow<TutorUiState> = _uiState.asStateFlow()
    
    // Hardcoded user ID for now (should come from auth)
    private val userId = "demo_user"
    
    init {
        loadConversationHistory()
    }
    
    fun loadConversationHistory() {
        viewModelScope.launch {
            repository.getConversationHistory(userId, limit = 20)
                .onSuccess { history ->
                    _uiState.value = _uiState.value.copy(
                        conversationHistory = history
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message
                    )
                }
        }
    }
    
    fun askQuestion(question: String) {
        if (question.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Detect language
            val detectedLanguage = repository.detectLanguage(question)
            _uiState.value = _uiState.value.copy(currentLanguage = detectedLanguage)
            
            repository.askQuestion(
                userId = userId,
                question = question,
                language = detectedLanguage
            )
                .onSuccess { response ->
                    // Reload conversation history to include new Q&A
                    loadConversationHistory()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to get response",
                        isLoading = false
                    )
                }
        }
    }
    
    fun setLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(currentLanguage = language)
    }
    
    fun loadRecommendations(weakAreas: List<String>) {
        viewModelScope.launch {
            repository.getPersonalizedRecommendations(userId, weakAreas)
                .onSuccess { recommendations ->
                    _uiState.value = _uiState.value.copy(
                        recommendations = recommendations
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message
                    )
                }
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearConversationHistory(userId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        conversationHistory = emptyList()
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
