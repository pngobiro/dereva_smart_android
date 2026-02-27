package com.dereva.smart.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showVerificationScreen: Boolean = false,
    val phoneNumber: String = "",
    val verificationCodeSent: Boolean = false,
    val isUserNotFound: Boolean = false,
    val isUserAlreadyExists: Boolean = false,
    val schools: List<DrivingSchool> = emptyList(),
    val isLoadingSchools: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val schoolRepository: com.dereva.smart.domain.repository.SchoolRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        observeAuthState()
        checkAndRestoreSession()
    }
    
    private fun checkAndRestoreSession() {
        viewModelScope.launch {
            // Try to restore existing session first
            val isAuthenticated = authRepository.isAuthenticated()
            if (isAuthenticated) {
                // Try to refresh the session to get user data
                authRepository.refreshSession()
                    .onSuccess { authResult ->
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                                currentUser = authResult.user
                            )
                        }
                    }
                    .onFailure {
                        // Session expired or invalid, check for guest mode
                        checkAndStartGuestMode()
                    }
            } else {
                // No session, check for guest mode
                checkAndStartGuestMode()
            }
        }
    }
    
    private fun checkAndStartGuestMode() {
        viewModelScope.launch {
            val isGuest = authRepository.isGuestMode()
            if (isGuest) {
                // Restore guest user
                val guestUser = User.createGuestUser()
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentUser = guestUser
                    )
                }
            } else {
                // Start new guest mode
                startGuestMode()
            }
        }
    }
    
    fun startGuestMode() {
        viewModelScope.launch {
            authRepository.startGuestMode()
                .onSuccess { guestUser ->
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            currentUser = guestUser
                        )
                    }
                }
        }
    }
    
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthStateFlow()
                .collect { authState ->
                    _uiState.update {
                        it.copy(
                            isAuthenticated = authState.isAuthenticated,
                            currentUser = authState.user
                        )
                    }
                }
        }
    }
    
    fun register(phoneNumber: String, password: String, fullName: String, licenseCategory: LicenseCategory, drivingSchoolId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isUserAlreadyExists = false) }
            
            val request = RegistrationRequest(
                phoneNumber = phoneNumber,
                password = password,
                fullName = fullName,
                licenseCategory = licenseCategory,
                drivingSchoolId = drivingSchoolId
            )
            
            authRepository.register(request)
                .onSuccess { _ ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            phoneNumber = phoneNumber,
                            showVerificationScreen = true,
                            verificationCodeSent = true,
                            successMessage = "Registration successful! Please verify your phone.",
                            isUserAlreadyExists = false
                        )
                    }
                }
                .onFailure { error ->
                    val errorMsg = error.message ?: "Registration failed"
                    val isAlreadyExists = errorMsg.contains("already exists", ignoreCase = true)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg,
                            isUserAlreadyExists = isAlreadyExists
                        )
                    }
                }
        }
    }
    
    fun login(phoneNumber: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isUserNotFound = false) }
            
            val request = LoginRequest(
                phoneNumber = phoneNumber,
                password = password
            )
            
            authRepository.login(request)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            currentUser = result.user,
                            successMessage = "Login successful!",
                            isUserNotFound = false
                        )
                    }
                }
                .onFailure { error ->
                    val errorMsg = error.message ?: "Login failed"
                    val isNotFound = errorMsg.contains("not found", ignoreCase = true) || errorMsg.contains("invalid credentials", ignoreCase = true)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg,
                            isUserNotFound = isNotFound
                        )
                    }
                }
        }
    }
    
    fun verifyPhone(phoneNumber: String, code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            authRepository.verifyPhone(phoneNumber, code)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showVerificationScreen = false,
                            successMessage = "Phone verified successfully!"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Verification failed"
                        )
                    }
                }
        }
    }
    
    fun resendVerificationCode(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            authRepository.sendVerificationCode(phoneNumber)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            verificationCodeSent = true,
                            successMessage = "Verification code sent!"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to send code"
                        )
                    }
                }
        }
    }
    
    fun requestPasswordReset(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            authRepository.requestPasswordReset(phoneNumber)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            phoneNumber = phoneNumber,
                            showVerificationScreen = true,
                            verificationCodeSent = true,
                            successMessage = "Verification code sent to your phone"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to send reset code"
                        )
                    }
                }
        }
    }
    
    fun resetPassword(phoneNumber: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val request = PasswordResetRequest(
                phoneNumber = phoneNumber,
                verificationCode = code,
                newPassword = newPassword
            )
            
            authRepository.resetPassword(request)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showVerificationScreen = false,
                            successMessage = "Password reset successful! Please login."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Password reset failed"
                        )
                    }
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    _uiState.update {
                        AuthUiState(
                            isAuthenticated = false,
                            currentUser = null,
                            successMessage = "Logged out successfully"
                        )
                    }
                }
        }
    }
    
    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
                isUserNotFound = false,
                isUserAlreadyExists = false
            )
        }
    }
    
    fun updateGuestCategory(category: LicenseCategory) {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUser
            if (currentUser != null) {
                if (currentUser.isGuestMode) {
                    // Guest user - update locally
                    authRepository.updateGuestCategory(category)
                        .onSuccess {
                            val updatedUser = currentUser.copy(targetCategory = category)
                            _uiState.update { it.copy(currentUser = updatedUser) }
                        }
                } else if (!currentUser.isSubscriptionActive) {
                    // Registered user without active subscription - update via API
                    authRepository.updateUserCategory(category)
                        .onSuccess {
                            val updatedUser = currentUser.copy(targetCategory = category)
                            _uiState.update { it.copy(currentUser = updatedUser) }
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(errorMessage = error.message) }
                        }
                }
            }
        }
    }
    
    fun validatePassword(password: String): String? {
        return authRepository.validatePassword(password)
            .exceptionOrNull()?.message
    }
    
    fun validatePhoneNumber(phoneNumber: String): String? {
        return authRepository.validatePhoneNumber(phoneNumber)
            .exceptionOrNull()?.message
    }
    
    fun loadSchools() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSchools = true) }
            
            schoolRepository.getVerifiedSchools()
                .onSuccess { schools ->
                    _uiState.update {
                        it.copy(
                            schools = schools,
                            isLoadingSchools = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            schools = emptyList(),
                            isLoadingSchools = false,
                            errorMessage = "Failed to load schools: ${error.message}"
                        )
                    }
                }
        }
    }
    
    fun updateUserSchool(schoolId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val currentUser = _uiState.value.currentUser
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User not found"
                    )
                }
                return@launch
            }
            
            schoolRepository.updateUserSchool(currentUser.id, schoolId)
                .onSuccess {
                    // Refresh user from server to get updated data
                    authRepository.refreshUser()
                        .onSuccess { refreshedUser ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    currentUser = refreshedUser,
                                    successMessage = if (schoolId != null) "School linked successfully" else "School unlinked successfully"
                                )
                            }
                        }
                        .onFailure { error ->
                            // Fallback to local update if refresh fails
                            val updatedUser = currentUser.copy(drivingSchoolId = schoolId)
                            authRepository.updateUser(updatedUser)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    currentUser = updatedUser,
                                    successMessage = if (schoolId != null) "School linked successfully" else "School unlinked successfully"
                                )
                            }
                        }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to update school"
                        )
                    }
                }
        }
    }
    
    suspend fun getAuthToken(): String? {
        return authRepository.getAuthState().token
    }
}
