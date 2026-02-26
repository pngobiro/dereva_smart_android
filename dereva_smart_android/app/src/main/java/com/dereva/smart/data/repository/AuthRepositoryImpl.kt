package com.dereva.smart.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dereva.smart.data.local.entity.toDomain
import com.dereva.smart.data.local.entity.toEntity
import com.dereva.smart.data.remote.ApiClient
import com.dereva.smart.data.remote.AuthService
import com.dereva.smart.domain.model.AuthResult
import com.dereva.smart.domain.model.AuthState
import com.dereva.smart.domain.model.LicenseCategory
import com.dereva.smart.domain.model.LoginRequest
import com.dereva.smart.domain.model.PasswordResetRequest
import com.dereva.smart.domain.model.RegistrationRequest
import com.dereva.smart.domain.model.SubscriptionStatus
import com.dereva.smart.domain.model.User
import com.dereva.smart.domain.model.VerificationCode
import com.dereva.smart.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import com.dereva.smart.data.remote.dto.LoginRequest as ApiLoginRequest
import com.dereva.smart.data.remote.dto.RegisterRequest as ApiRegisterRequest

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val context: Context
) : AuthRepository {
    
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val GUEST_MODE_KEY = stringPreferencesKey("guest_mode")
    private val GUEST_CATEGORY_KEY = stringPreferencesKey("guest_category")

    // In-memory cache for the current session
    private val _currentUser = MutableStateFlow<User?>(null)
    
    override suspend fun startGuestMode(): Result<User> = runCatching {
        val guestUser = User.createGuestUser()
        
        context.dataStore.edit { prefs ->
            prefs[GUEST_MODE_KEY] = "true"
            prefs[USER_ID_KEY] = guestUser.id
            prefs[GUEST_CATEGORY_KEY] = guestUser.targetCategory.name
        }
        
        _currentUser.value = guestUser
        guestUser
    }
    
    override suspend fun isGuestMode(): Boolean {
        return context.dataStore.data.first()[GUEST_MODE_KEY] == "true"
    }
    
    override suspend fun updateGuestCategory(category: LicenseCategory): Result<Unit> = runCatching {
        context.dataStore.edit { prefs ->
            prefs[GUEST_CATEGORY_KEY] = category.name
        }
        _currentUser.update { it?.copy(targetCategory = category) }
    }
    
    override suspend fun register(request: RegistrationRequest): Result<AuthResult> = runCatching {
        validatePhoneNumber(request.phoneNumber).getOrThrow()
        validatePassword(request.password).getOrThrow()
        
        val formattedPhone = authService.formatPhoneNumber(request.phoneNumber)
        
        val apiRequest = ApiRegisterRequest(
            phone = formattedPhone,
            password = request.password,
            name = request.fullName,
            category = request.licenseCategory.name
        )
        
        val response = ApiClient.apiService.register(apiRequest)
        
        if (!response.isSuccessful || response.body()?.success != true) {
            throw Exception("Registration failed")
        }
        
        val authResponse = response.body()!!
        val userDto = authResponse.user ?: throw Exception("No user data returned")
        
        val user = User(
            id = userDto.id,
            phoneNumber = userDto.phone,
            name = userDto.name,
            email = null,
            targetCategory = try { LicenseCategory.valueOf(userDto.category) } catch(e: Exception) { LicenseCategory.B1 },
            subscriptionStatus = SubscriptionStatus.FREE,
            subscriptionExpiryDate = null,
            isPhoneVerified = false,
            createdAt = Date(),
            lastActiveAt = Date(),
            lastLoginAt = null
        )
        
        authResponse.token?.let { token ->
            saveSession(token, user.id)
        }
        
        _currentUser.value = user
        AuthResult(success = true, user = user, token = authResponse.token ?: "", errorMessage = null)
    }
    
    override suspend fun sendVerificationCode(phoneNumber: String): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(phoneNumber)
        val apiRequest = com.dereva.smart.data.remote.dto.ResendCodeRequest(phone = formattedPhone)
        ApiClient.apiService.resendCode(apiRequest)
    }
    
    override suspend fun verifyPhone(phoneNumber: String, code: String): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(phoneNumber)
        val apiRequest = com.dereva.smart.data.remote.dto.VerifyRequest(phone = formattedPhone, code = code)
        ApiClient.apiService.verifyCode(apiRequest)
        refreshUser()
    }
    
    override suspend fun login(request: LoginRequest): Result<AuthResult> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(request.phoneNumber)
        val apiRequest = ApiLoginRequest(phone = formattedPhone, password = request.password)
        
        val response = ApiClient.apiService.login(apiRequest)
        
        if (!response.isSuccessful || response.body()?.success != true) {
            throw Exception("Login failed: Invalid credentials")
        }
        
        val authResponse = response.body()!!
        val userDto = authResponse.user ?: throw Exception("Failed to load user")
        
        val user = User(
            id = userDto.id,
            phoneNumber = userDto.phone,
            name = userDto.name,
            email = null,
            targetCategory = try { LicenseCategory.valueOf(userDto.category) } catch(e: Exception) { LicenseCategory.B1 },
            subscriptionStatus = when(userDto.subscriptionStatus.uppercase()) {
                "PREMIUM_MONTHLY" -> SubscriptionStatus.PREMIUM_MONTHLY
                else -> SubscriptionStatus.FREE
            },
            subscriptionExpiryDate = userDto.subscriptionExpiryDate?.let { Date(it) },
            isPhoneVerified = true,
            createdAt = userDto.createdAt?.let { Date(it) } ?: Date(),
            lastActiveAt = Date(),
            lastLoginAt = Date()
        )
        
        val token = authResponse.token ?: throw Exception("No token received")
        saveSession(token, user.id)
        
        context.dataStore.edit { prefs ->
            prefs.remove(GUEST_MODE_KEY)
            prefs.remove(GUEST_CATEGORY_KEY)
        }
        
        _currentUser.value = user
        AuthResult(success = true, user = user, token = token, errorMessage = null)
    }
    
    override suspend fun logout(): Result<Unit> = runCatching {
        val token = getStoredToken()
        if (token != null) {
            try { ApiClient.apiService.logout("Bearer $token") } catch(e: Exception) {}
        }
        clearSession()
        _currentUser.value = null
        
        context.dataStore.edit { prefs ->
            prefs.remove(GUEST_MODE_KEY)
            prefs.remove(GUEST_CATEGORY_KEY)
        }
    }
    
    override suspend fun refreshSession(): Result<AuthResult> = runCatching {
        val token = getStoredToken() ?: throw Exception("No active session")
        val user = refreshUser().getOrThrow()
        AuthResult(success = true, user = user, token = token, errorMessage = null)
    }
    
    override suspend fun requestPasswordReset(phoneNumber: String): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(phoneNumber)
        ApiClient.apiService.forgotPassword(com.dereva.smart.data.remote.dto.ForgotPasswordRequest(phone = formattedPhone))
    }
    
    override suspend fun resetPassword(request: PasswordResetRequest): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(request.phoneNumber)
        ApiClient.apiService.resetPassword(com.dereva.smart.data.remote.dto.ResetPasswordRequest(
            phone = formattedPhone, code = request.verificationCode, newPassword = request.newPassword
        ))
    }
    
    override suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Result<Unit> = Result.failure(Exception("Not implemented"))
    
    override suspend fun getCurrentUser(): Result<User?> = runCatching {
        val user = _currentUser.value
        if (user == null) {
            val token = getStoredToken()
            if (token != null) {
                return refreshUser().map { it }
            }
        }
        user
    }

    override suspend fun refreshUser(): Result<User> = runCatching {
        val userId = getStoredUserId() ?: throw Exception("No user ID found")
        val token = getStoredToken() ?: throw Exception("No auth token found")
        
        val response = ApiClient.apiService.getUser(userId, "Bearer $token")
        if (!response.isSuccessful || response.body() == null) throw Exception("Failed to refresh profile")
        
        val userDto = response.body()!!
        val updatedUser = User(
            id = userDto.id,
            phoneNumber = userDto.phoneNumber,
            name = userDto.name,
            email = userDto.email,
            targetCategory = try { LicenseCategory.valueOf(userDto.targetCategory) } catch(e: Exception) { LicenseCategory.B1 },
            drivingSchoolId = userDto.drivingSchoolId,
            subscriptionStatus = when(userDto.subscriptionStatus.uppercase()) {
                "PREMIUM_MONTHLY" -> SubscriptionStatus.PREMIUM_MONTHLY
                else -> SubscriptionStatus.FREE
            },
            subscriptionExpiryDate = userDto.subscriptionExpiryDate?.let { Date(it) },
            isPhoneVerified = userDto.isPhoneVerified,
            createdAt = Date(userDto.createdAt),
            lastActiveAt = Date(),
            lastLoginAt = null
        )
        
        _currentUser.value = updatedUser
        updatedUser
    }
    
    override fun getCurrentUserFlow(): Flow<User?> = _currentUser.asStateFlow()
    
    override suspend fun updateUser(user: User): Result<Unit> = runCatching {
        _currentUser.value = user
    }
    
    override suspend fun deleteAccount(userId: String, password: String): Result<Unit> = runCatching {
        logout()
    }
    
    override suspend fun isAuthenticated(): Boolean {
        return getStoredToken() != null
    }
    
    override suspend fun getAuthState(): AuthState {
        val token = getStoredToken()
        val userId = getStoredUserId()
        if (token == null || userId == null) return AuthState(false, null, null)
        
        val userResult = getCurrentUser()
        val user = userResult.getOrNull()
        return AuthState(isAuthenticated = user != null, user = user, token = token)
    }
    
    override fun getAuthStateFlow(): Flow<AuthState> {
        return combine(context.dataStore.data, _currentUser) { prefs, user ->
            val token = prefs[TOKEN_KEY]
            val userId = prefs[USER_ID_KEY]
            val isGuest = prefs[GUEST_MODE_KEY] == "true"
            val guestCategory = prefs[GUEST_CATEGORY_KEY]?.let { 
                try { LicenseCategory.valueOf(it) } catch (e: Exception) { LicenseCategory.B1 }
            } ?: LicenseCategory.B1
            
            if (isGuest && userId != null) {
                AuthState(true, User.createGuestUser().copy(id = userId, targetCategory = guestCategory), null)
            } else if (token == null || userId == null) {
                AuthState(false, null, null)
            } else {
                // If we have a token but no user object yet, isAuthenticated remains false
                // until the user object is fetched (refreshUser will be called by ViewModels)
                AuthState(isAuthenticated = user != null, user = user, token = token)
            }
        }
    }
    
    override fun validatePassword(password: String): Result<Unit> = runCatching {
        if (password.length < 6) throw Exception("Password too short")
    }
    
    override fun validatePhoneNumber(phoneNumber: String): Result<Unit> = runCatching {
        if (phoneNumber.length < 10) throw Exception("Invalid phone number")
    }
    
    private suspend fun saveSession(token: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
        }
    }
    
    private suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }
    
    private suspend fun getStoredToken(): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }
    
    private suspend fun getStoredUserId(): String? {
        return context.dataStore.data.first()[USER_ID_KEY]
    }
}
