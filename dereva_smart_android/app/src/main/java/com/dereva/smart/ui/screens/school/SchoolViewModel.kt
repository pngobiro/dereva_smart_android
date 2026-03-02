package com.dereva.smart.ui.screens.school

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SchoolUiState(
    val isLoading: Boolean = false,
    val linkedSchool: DrivingSchool? = null,
    val schoolLinking: SchoolLinking? = null,
    val moduleSchedules: List<ModuleSchedule> = emptyList(),
    val latestReport: ProgressReport? = null,
    val schoolStats: SchoolStats? = null,
    val schoolProgress: List<SchoolProgressRecord> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class SchoolViewModel(
    private val repository: SchoolRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SchoolUiState())
    val uiState: StateFlow<SchoolUiState> = _uiState.asStateFlow()
    
    // Hardcoded user ID for now (should come from auth)
    private val userId = "demo_user"
    
    init {
        loadSchoolInfo()
    }
    
    fun loadSchoolInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Get active school linking
            repository.getActiveSchoolLinking(userId)
                .onSuccess { linking ->
                    _uiState.value = _uiState.value.copy(schoolLinking = linking)
                    
                    linking?.let {
                        // Load school details
                        loadSchoolDetails(it.schoolId)
                        
                        // Load module schedules
                        loadModuleSchedules(it.schoolId)
                        
                        // Load latest progress report
                        loadProgressReport(it.schoolId)
                        
                        // Check and unlock modules
                        checkModuleUnlocks(it.schoolId)
                    }
                    
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
        }
    }
    
    fun linkToSchool(schoolCode: String) {
        if (schoolCode.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a school code")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.linkToSchool(userId, schoolCode.trim())
                .onSuccess { linking ->
                    _uiState.value = _uiState.value.copy(
                        schoolLinking = linking,
                        successMessage = "Successfully linked to school!",
                        isLoading = false
                    )
                    loadSchoolInfo()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to link to school",
                        isLoading = false
                    )
                }
        }
    }
    
    fun unlinkFromSchool() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.unlinkFromSchool(userId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        linkedSchool = null,
                        schoolLinking = null,
                        moduleSchedules = emptyList(),
                        latestReport = null,
                        schoolStats = null,
                        successMessage = "Unlinked from school",
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
    
    fun toggleProgressSharing(enabled: Boolean) {
        val linkingId = _uiState.value.schoolLinking?.id ?: return
        
        viewModelScope.launch {
            repository.updateProgressSharing(linkingId, enabled)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        schoolLinking = _uiState.value.schoolLinking?.copy(
                            progressSharingEnabled = enabled
                        )
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }
    
    fun shareProgress() {
        val schoolId = _uiState.value.linkedSchool?.id ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.shareProgressWithSchool(userId, schoolId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Progress shared with school",
                        isLoading = false
                    )
                    loadProgressReport(schoolId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
        }
    }
    
    private suspend fun loadSchoolDetails(schoolId: String) {
        repository.getSchoolById(schoolId)
            .onSuccess { school ->
                _uiState.value = _uiState.value.copy(linkedSchool = school)
            }
            .onFailure { /* ignore */ }
    }
    
    private suspend fun loadModuleSchedules(schoolId: String) {
        repository.getSchoolSchedules(schoolId)
            .onSuccess { schedules ->
                _uiState.value = _uiState.value.copy(moduleSchedules = schedules)
            }
            .onFailure { /* ignore */ }
    }
    
    private suspend fun loadProgressReport(schoolId: String) {
        repository.getLatestProgressReport(userId, schoolId)
            .onSuccess { report ->
                _uiState.value = _uiState.value.copy(latestReport = report)
            }
            .onFailure { /* ignore */ }
    }
    
    fun loadSchoolStats(schoolId: String) {
        viewModelScope.launch {
            repository.getSchoolStats(schoolId)
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(schoolStats = stats)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }
    
    fun loadSchoolProgress(schoolId: String, category: String? = null) {
        viewModelScope.launch {
            repository.getSchoolProgress(schoolId, category)
                .onSuccess { progress ->
                    _uiState.value = _uiState.value.copy(schoolProgress = progress)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }
    
    private suspend fun checkModuleUnlocks(schoolId: String) {
        repository.checkAndUnlockModules(schoolId)
            .onSuccess { unlockedModules ->
                if (unlockedModules.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "${unlockedModules.size} new module(s) unlocked!"
                    )
                    loadModuleSchedules(schoolId)
                }
            }
            .onFailure { /* ignore */ }
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
