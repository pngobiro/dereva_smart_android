package com.dereva.smart.ui.screens.mocktest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.MockTest
import com.dereva.smart.domain.model.TestResult
import com.dereva.smart.domain.repository.MockTestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MockTestUiState(
    val isLoading: Boolean = false,
    val currentTest: MockTest? = null,
    val testResult: TestResult? = null,
    val error: String? = null,
    val userTests: List<MockTest> = emptyList()
)

class MockTestViewModel(
    private val repository: MockTestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MockTestUiState())
    val uiState: StateFlow<MockTestUiState> = _uiState.asStateFlow()
    
    // Hardcoded user ID for now (should come from auth)
    private val userId = "demo_user"
    private var currentUserCategory: String = "B1" // Default category
    
    init {
        loadUserTests()
    }
    
    fun setUserCategory(category: String) {
        Log.d(TAG, "setUserCategory called with: $category")
        currentUserCategory = category
    }
    
    companion object {
        private const val TAG = "MockTestViewModel"
    }
    
    fun loadUserTests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getUserTests(userId)
                .onSuccess { tests ->
                    _uiState.value = _uiState.value.copy(
                        userTests = tests,
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
    
    fun generateNewTest(licenseCategory: String? = null) {
        viewModelScope.launch {
            Log.d(TAG, "generateNewTest called with category: $licenseCategory")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val category = licenseCategory ?: currentUserCategory
            Log.d(TAG, "Using category: $category")
            
            repository.generateTest(
                userId = userId,
                licenseCategory = category,
                questionCount = 50
            )
                .onSuccess { test ->
                    Log.d(TAG, "Test generated successfully with ${test.questions.size} questions")
                    _uiState.value = _uiState.value.copy(
                        currentTest = test,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to generate test", error)
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to generate test",
                        isLoading = false
                    )
                }
        }
    }
    
    fun answerQuestion(questionId: String, selectedOptionIndex: Int) {
        val currentTest = _uiState.value.currentTest ?: return
        
        viewModelScope.launch {
            repository.updateTestAnswer(
                testId = currentTest.id,
                questionId = questionId,
                selectedOptionIndex = selectedOptionIndex
            )
                .onSuccess {
                    // Reload the test to get updated state
                    repository.getTestById(currentTest.id)
                        .onSuccess { updatedTest ->
                            _uiState.value = _uiState.value.copy(currentTest = updatedTest)
                        }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }
    
    fun submitTest() {
        val currentTest = _uiState.value.currentTest ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.submitTest(currentTest.id)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        testResult = result,
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
    
    fun loadTestResult(testId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getTestResult(testId)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        testResult = result,
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
