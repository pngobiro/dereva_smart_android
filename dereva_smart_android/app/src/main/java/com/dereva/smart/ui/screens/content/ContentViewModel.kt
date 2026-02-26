package com.dereva.smart.ui.screens.content

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.ContentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ContentUiState(
    val modules: List<Module> = emptyList(),
    val currentModule: Module? = null,
    val selectedModuleId: String? = null,
    val lessons: List<Lesson> = emptyList(),
    val currentLesson: Lesson? = null,
    val lessonProgress: LessonProgress? = null,
    val activeDownloads: List<ModuleDownload> = emptyList(),
    val contentStats: ContentStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGuestMode: Boolean = true,
    val showSubscriptionRequired: Boolean = false,
    val subscriptionRequiredMessage: String? = null
)

class ContentViewModel(
    private val repository: ContentRepository,
    private val userId: String = "user123" // TODO: Get from auth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ContentUiState())
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()
    
    private var currentUser: User? = null
    
    init {
        // Don't load modules here - wait for setCurrentUser to be called with actual user
        loadActiveDownloads()
        loadContentStats()
    }
    
    fun setCurrentUser(user: User?) {
        Log.d(TAG, "setCurrentUser called with user: ${user?.name}, category: ${user?.targetCategory?.name}, isGuest: ${user?.isGuestMode}")
        currentUser = user
        _uiState.update { it.copy(isGuestMode = user?.isGuestMode ?: true) }
        
        // Only load modules if user is set
        if (user != null) {
            loadModules()
        } else {
            Log.w(TAG, "User is null, skipping module load")
        }
    }
    
    companion object {
        private const val TAG = "ContentViewModel"
    }
    
    fun checkSubscriptionAccess(module: Module): Boolean {
        if (!module.requiresSubscription) return true
        // Guests cannot access premium content
        if (currentUser?.isGuestMode == true) return false
        return currentUser?.isSubscriptionActive == true
    }
    
    fun checkSubscriptionAccess(lesson: Lesson): Boolean {
        if (!lesson.requiresSubscription) return true
        // Guests cannot access premium content
        if (currentUser?.isGuestMode == true) return false
        return currentUser?.isSubscriptionActive == true
    }
    
    private fun loadModules() {
        viewModelScope.launch {
            Log.d(TAG, "loadModules called")
            _uiState.update { it.copy(isLoading = true) }
            
            val category = currentUser?.targetCategory?.name
            Log.d(TAG, "Loading modules for category: $category, isGuest: ${currentUser?.isGuestMode}")
            
            // Load directly from API - guests can browse any category
            try {
                Log.d(TAG, "Calling API: getModules($category)")
                val response = com.dereva.smart.data.remote.ApiClient.apiService.getModules(category)
                
                Log.d(TAG, "API response: isSuccessful=${response.isSuccessful}, code=${response.code()}")
                
                if (response.isSuccessful) {
                    val moduleDtos = response.body()
                    Log.d(TAG, "Received ${moduleDtos?.size ?: 0} modules from API")
                    
                    val modules = moduleDtos?.map { dto ->
                        Log.d(TAG, "Module: id=${dto.id}, title=${dto.title}, category=${dto.category}")
                        Module(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            orderIndex = dto.orderIndex,
                            licenseCategory = LicenseCategory.valueOf(dto.category),
                            thumbnailUrl = dto.iconUrl,
                            estimatedDuration = 0,
                            lessonCount = 0,
                            isDownloaded = false,
                            downloadSize = 0L,
                            status = ModuleStatus.UNLOCKED,
                            completionPercentage = 0,
                            requiresSubscription = dto.requiresSubscription,
                            createdAt = java.util.Date(),
                            updatedAt = java.util.Date()
                        )
                    } ?: emptyList()
                    
                    Log.d(TAG, "Converted to ${modules.size} domain modules")
                    _uiState.update { state -> 
                        val currentMod = state.currentModule ?: modules.find { it.id == state.selectedModuleId }
                        state.copy(
                            modules = modules, 
                            currentModule = currentMod,
                            isLoading = false
                        ) 
                    }
                } else {
                    val errorMsg = "Failed to load modules: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    _uiState.update { it.copy(error = errorMsg, isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading modules", e)
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
    
    fun selectModule(moduleId: String) {
        viewModelScope.launch {
            Log.d(TAG, "selectModule called with moduleId: $moduleId")
            _uiState.update { it.copy(isLoading = true, selectedModuleId = moduleId) }
            
            // Find module from current state
            val module = _uiState.value.modules.find { it.id == moduleId }
            Log.d(TAG, "Found module: ${module?.title}")
            
            if (module != null && !checkSubscriptionAccess(module)) {
                Log.d(TAG, "Module requires subscription, isGuest: ${currentUser?.isGuestMode}")
                val message = if (currentUser?.isGuestMode == true) {
                    "This module requires an account with premium subscription"
                } else {
                    "This module requires a premium subscription"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showSubscriptionRequired = true,
                        subscriptionRequiredMessage = message
                    )
                }
            } else {
                Log.d(TAG, "Module accessible, loading lessons")
                _uiState.update { it.copy(currentModule = module) }
                loadLessons(moduleId)
            }
        }
    }
    
    private fun loadLessons(moduleId: String) {
        viewModelScope.launch {
            Log.d(TAG, "loadLessons called for moduleId: $moduleId")
            // Load lessons directly from API
            try {
                Log.d(TAG, "Calling API: getLessons($moduleId)")
                val response = com.dereva.smart.data.remote.ApiClient.apiService.getLessons(moduleId)
                
                Log.d(TAG, "Lessons API response: isSuccessful=${response.isSuccessful}, code=${response.code()}")
                
                if (response.isSuccessful) {
                    val lessonDtos = response.body()
                    Log.d(TAG, "Received ${lessonDtos?.size ?: 0} lessons from API")
                    
                    val lessons = lessonDtos?.mapNotNull { dto ->
                        try {
                            Log.d(TAG, "Lesson: id=${dto.id}, title=${dto.title}, type=${dto.type}")
                            
                            // Handle null or invalid type - default to TEXT
                            val contentType = try {
                                if (dto.type.isNullOrBlank()) {
                                    ContentType.TEXT
                                } else {
                                    ContentType.valueOf(dto.type.uppercase())
                                }
                            } catch (e: IllegalArgumentException) {
                                Log.w(TAG, "Invalid content type: ${dto.type}, defaulting to TEXT")
                                ContentType.TEXT
                            }
                            
                            Lesson(
                                id = dto.id,
                                moduleId = dto.moduleId,
                                title = dto.title,
                                description = dto.description,
                                orderIndex = dto.orderIndex,
                                contentType = contentType,
                                contentUrl = dto.contentUrl,
                                contentText = dto.contentText,
                                duration = dto.durationMinutes,
                                isDownloaded = false,
                                isCompleted = false,
                                requiresSubscription = dto.requiresSubscription,
                                lastAccessedAt = null,
                                createdAt = java.util.Date()
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing lesson ${dto.id}", e)
                            null
                        }
                    } ?: emptyList()
                    
                    Log.d(TAG, "Converted to ${lessons.size} domain lessons")
                    _uiState.update { 
                        val newState = it.copy(lessons = lessons, isLoading = false)
                        Log.d(TAG, "UI State updated: lessons=${newState.lessons.size}, isLoading=${newState.isLoading}")
                        newState
                    }
                } else {
                    val errorMsg = "Failed to load lessons: ${response.code()} - ${response.message()}"
                    Log.e(TAG, errorMsg)
                    _uiState.update { it.copy(error = errorMsg, isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading lessons", e)
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
    
    fun selectLesson(lessonId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Find lesson from current state
            var lesson = _uiState.value.lessons.find { it.id == lessonId }
            
            if (lesson == null) {
                try {
                    val response = com.dereva.smart.data.remote.ApiClient.apiService.getLesson(lessonId)
                    if (response.isSuccessful) {
                        val dto = response.body()
                        if (dto != null) {
                            val contentType = try {
                                if (dto.type.isNullOrBlank()) {
                                    ContentType.TEXT
                                } else {
                                    ContentType.valueOf(dto.type.uppercase())
                                }
                            } catch (e: IllegalArgumentException) {
                                Log.w(TAG, "Invalid content type: ${dto.type}, defaulting to TEXT")
                                ContentType.TEXT
                            }
                            
                            lesson = Lesson(
                                id = dto.id,
                                moduleId = dto.moduleId,
                                title = dto.title,
                                description = dto.description,
                                orderIndex = dto.orderIndex,
                                contentType = contentType,
                                contentUrl = dto.contentUrl,
                                contentText = dto.contentText,
                                duration = dto.durationMinutes,
                                isDownloaded = false,
                                isCompleted = false,
                                requiresSubscription = dto.requiresSubscription,
                                lastAccessedAt = null,
                                createdAt = java.util.Date()
                            )
                        }
                    } else {
                        Log.e(TAG, "Failed to load lesson: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception loading single lesson", e)
                }
            }
            
            if (lesson != null && !checkSubscriptionAccess(lesson)) {
                val message = if (currentUser?.isGuestMode == true) {
                    "This lesson requires an account with premium subscription"
                } else {
                    "This lesson requires a premium subscription"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showSubscriptionRequired = true,
                        subscriptionRequiredMessage = message
                    )
                }
            } else if (lesson != null) {
                _uiState.update { it.copy(currentLesson = lesson, isLoading = false) }
                startLessonProgress(lesson)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load lesson") }
            }
        }
    }
    
    private fun startLessonProgress(lesson: Lesson) {
        viewModelScope.launch {
            repository.startLesson(userId, lesson.id, lesson.moduleId)
                .onSuccess { progress ->
                    _uiState.update { it.copy(lessonProgress = progress) }
                }
        }
    }
    
    fun updateLessonProgress(timeSpent: Int, position: Int) {
        val progressId = _uiState.value.lessonProgress?.id ?: return
        
        viewModelScope.launch {
            repository.updateLessonProgress(progressId, timeSpent, position)
        }
    }
    
    fun completeLesson() {
        val progressId = _uiState.value.lessonProgress?.id ?: return
        val lessonId = _uiState.value.currentLesson?.id ?: return
        
        viewModelScope.launch {
            repository.completeLessonProgress(progressId)
                .onSuccess {
                    repository.updateLessonCompletion(lessonId, true)
                }
        }
    }
    
    fun downloadModule(moduleId: String) {
        viewModelScope.launch {
            repository.downloadModule(moduleId, userId)
                .onSuccess {
                    loadActiveDownloads()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }
    
    private fun loadActiveDownloads() {
        viewModelScope.launch {
            repository.getActiveDownloads(userId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { downloads ->
                    _uiState.update { it.copy(activeDownloads = downloads) }
                }
        }
    }
    
    fun pauseDownload(downloadId: String) {
        viewModelScope.launch {
            repository.pauseDownload(downloadId)
        }
    }
    
    fun resumeDownload(downloadId: String) {
        viewModelScope.launch {
            repository.resumeDownload(downloadId)
        }
    }
    
    fun cancelDownload(downloadId: String) {
        viewModelScope.launch {
            repository.cancelDownload(downloadId)
        }
    }
    
    private fun loadContentStats() {
        viewModelScope.launch {
            repository.getContentStats(userId)
                .onSuccess { stats ->
                    _uiState.update { it.copy(contentStats = stats) }
                }
        }
    }
    
    fun loadSampleContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.loadSampleContent()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadModules()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun dismissSubscriptionDialog() {
        _uiState.update { 
            it.copy(
                showSubscriptionRequired = false,
                subscriptionRequiredMessage = null
            )
        }
    }
}
