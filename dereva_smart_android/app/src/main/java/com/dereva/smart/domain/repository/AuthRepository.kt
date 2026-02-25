package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    // Guest Mode
    suspend fun startGuestMode(): Result<User>
    
    suspend fun isGuestMode(): Boolean
    
    suspend fun updateGuestCategory(category: LicenseCategory): Result<Unit>
    
    // Registration
    suspend fun register(request: RegistrationRequest): Result<AuthResult>
    
    suspend fun sendVerificationCode(phoneNumber: String): Result<Unit>
    
    suspend fun verifyPhone(phoneNumber: String, code: String): Result<Unit>
    
    // Login/Logout
    suspend fun login(request: LoginRequest): Result<AuthResult>
    
    suspend fun logout(): Result<Unit>
    
    suspend fun refreshSession(): Result<AuthResult>
    
    // Password Management
    suspend fun requestPasswordReset(phoneNumber: String): Result<Unit>
    
    suspend fun resetPassword(request: PasswordResetRequest): Result<Unit>
    
    suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Result<Unit>
    
    // User Management
    suspend fun getCurrentUser(): Result<User?>
    
    fun getCurrentUserFlow(): Flow<User?>
    
    suspend fun updateUser(user: User): Result<Unit>
    
    suspend fun deleteAccount(userId: String, password: String): Result<Unit>
    
    // Session Management
    suspend fun isAuthenticated(): Boolean
    
    suspend fun getAuthState(): AuthState
    
    fun getAuthStateFlow(): Flow<AuthState>
    
    // Validation
    fun validatePassword(password: String): Result<Unit>
    
    fun validatePhoneNumber(phoneNumber: String): Result<Unit>
}
