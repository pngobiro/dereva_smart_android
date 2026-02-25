package com.dereva.smart.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dereva.smart.data.local.dao.AuthDao
import com.dereva.smart.data.local.entity.AuthSessionEntity
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import com.dereva.smart.data.remote.dto.LoginRequest as ApiLoginRequest
import com.dereva.smart.data.remote.dto.RegisterRequest as ApiRegisterRequest

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepositoryImpl(
    private val authDao: AuthDao,
    private val authService: AuthService,
    private val context: Context
) : AuthRepository {
    
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val GUEST_MODE_KEY = stringPreferencesKey("guest_mode")
    private val GUEST_CATEGORY_KEY = stringPreferencesKey("guest_category")
    
    override suspend fun startGuestMode(): Result<User> = runCatching {
        val guestUser = User.createGuestUser()
        
        // Save guest mode flag
        context.dataStore.edit { prefs ->
            prefs[GUEST_MODE_KEY] = "true"
            prefs[USER_ID_KEY] = guestUser.id
            prefs[GUEST_CATEGORY_KEY] = guestUser.targetCategory.name
        }
        
        guestUser
    }
    
    override suspend fun isGuestMode(): Boolean {
        return context.dataStore.data.first()[GUEST_MODE_KEY] == "true"
    }
    
    override suspend fun updateGuestCategory(category: LicenseCategory): Result<Unit> = runCatching {
        context.dataStore.edit { prefs ->
            prefs[GUEST_CATEGORY_KEY] = category.name
        }
    }
    
    override suspend fun register(request: RegistrationRequest): Result<AuthResult> = runCatching {
        // Validate inputs
        validatePhoneNumber(request.phoneNumber).getOrThrow()
        validatePassword(request.password).getOrThrow()
        
        val formattedPhone = authService.formatPhoneNumber(request.phoneNumber)
        
        // Call Cloudflare API
        val apiRequest = ApiRegisterRequest(
            phone = formattedPhone,
            password = request.password,
            name = request.fullName,
            category = request.licenseCategory.name
        )
        
        val response = ApiClient.apiService.register(apiRequest)
        
        if (!response.isSuccessful || response.body()?.success != true) {
            val errorBodyString = response.errorBody()?.string()
            val errorMessage = if (errorBodyString != null && errorBodyString.contains("Validation error", ignoreCase = true)) {
                "Registration failed: Please check your details and try again."
            } else if (errorBodyString != null && errorBodyString.contains("already exists", ignoreCase = true)) {
                "User already exists. Please login instead."
            } else if (errorBodyString != null) {
                // Try to extract a simple message if possible, or fallback
                "Registration failed. Please try again."
            } else {
                response.body()?.message ?: "Registration failed"
            }
            throw Exception(errorMessage)
        }
        
        val authResponse = response.body()!!
        
        // Create local user object
        val user = User(
            id = authResponse.user?.id ?: UUID.randomUUID().toString(),
            phoneNumber = formattedPhone,
            name = request.fullName,
            email = null,
            targetCategory = request.licenseCategory,
            drivingSchoolId = null,
            subscriptionStatus = SubscriptionStatus.FREE,
            subscriptionExpiryDate = null,
            isPhoneVerified = false,
            createdAt = Date(),
            lastActiveAt = Date(),
            lastLoginAt = null
        )
        
        // Save to local database
        val passwordHash = authService.hashPassword(request.password)
        authDao.insertUser(user.toEntity(passwordHash))
        
        // Save session if token provided
        authResponse.token?.let { token ->
            saveSession(token, user.id)
        }
        
        AuthResult(
            success = true,
            user = user,
            token = authResponse.token ?: "",
            errorMessage = null
        )
    }
    
    override suspend fun sendVerificationCode(phoneNumber: String): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(phoneNumber)
        
        // Delete old unused codes
        authDao.deleteUnusedCodesForPhone(formattedPhone)
        
        // Generate new code
        val code = authService.generateVerificationCode()
        val verificationCode = VerificationCode(
            code = code,
            phoneNumber = formattedPhone,
            expiresAt = Date(System.currentTimeMillis() + 10 * 60 * 1000), // 10 minutes
            isUsed = false
        )
        
        // Save to database
        authDao.insertVerificationCode(verificationCode.toEntity())
        
        // Send SMS
        val message = "Your Dereva Smart verification code is: $code. Valid for 10 minutes."
        authService.sendSMS(formattedPhone, message).getOrThrow()
    }
    
    override suspend fun verifyPhone(phoneNumber: String, code: String): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(phoneNumber)
        
        val verificationCode = authDao.getVerificationCode(code, formattedPhone)
            ?: throw Exception("Invalid verification code")
        
        val domainCode = verificationCode.toDomain()
        
        if (!domainCode.isValid) {
            throw Exception(
                if (domainCode.isExpired) "Verification code has expired"
                else "Verification code has already been used"
            )
        }
        
        // Mark code as used
        authDao.markCodeAsUsed(code)
        
        // Mark phone as verified
        authDao.markPhoneVerified(formattedPhone)
    }
    
    override suspend fun login(request: LoginRequest): Result<AuthResult> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(request.phoneNumber)
        
        // Call Cloudflare API
        val apiRequest = ApiLoginRequest(
            phone = formattedPhone,
            password = request.password
        )
        
        val response = ApiClient.apiService.login(apiRequest)
        
        if (!response.isSuccessful || response.body()?.success != true) {
            val errorBodyString = response.errorBody()?.string()
            val errorMessage = if (errorBodyString != null && errorBodyString.contains("Invalid credentials", ignoreCase = true)) {
                "Account not found or invalid credentials."
            } else {
                response.body()?.message ?: "Login failed"
            }
            throw Exception(errorMessage)
        }
        
        val authResponse = response.body()!!
        
        // Get or create local user
        var userEntity = authDao.getUserByPhone(formattedPhone)
        
        if (userEntity == null && authResponse.user != null) {
            // Create local user from API response
            val user = User(
                id = authResponse.user.id,
                phoneNumber = formattedPhone,
                name = authResponse.user.name,
                email = null,
                targetCategory = LicenseCategory.valueOf(authResponse.user.category),
                drivingSchoolId = null,
                subscriptionStatus = when(authResponse.user.subscriptionStatus.uppercase()) {
                    "PREMIUM_ONE_TIME" -> SubscriptionStatus.PREMIUM_ONE_TIME
                    "PREMIUM_MONTHLY" -> SubscriptionStatus.PREMIUM_MONTHLY
                    "EXPIRED" -> SubscriptionStatus.EXPIRED
                    else -> SubscriptionStatus.FREE
                },
                subscriptionExpiryDate = null,
                isPhoneVerified = true,
                createdAt = Date(),
                lastActiveAt = Date(),
                lastLoginAt = Date()
            )
            
            val passwordHash = authService.hashPassword(request.password)
            authDao.insertUser(user.toEntity(passwordHash))
            userEntity = authDao.getUserByPhone(formattedPhone)
        }
        
        val user = userEntity?.toDomain() ?: throw Exception("Failed to load user")
        
        // Update last login
        authDao.updateLastLogin(user.id, System.currentTimeMillis())
        
        // Save session
        val token = authResponse.token ?: authService.generateSessionToken()
        saveSession(token, user.id)
        
        // Clear guest mode
        context.dataStore.edit { prefs ->
            prefs.remove(GUEST_MODE_KEY)
            prefs.remove(GUEST_CATEGORY_KEY)
        }
        
        AuthResult(
            success = true,
            user = user.copy(lastLoginAt = Date()),
            token = token,
            errorMessage = null
        )
    }
    
    override suspend fun logout(): Result<Unit> = runCatching {
        val token = getStoredToken()
        if (token != null) {
            authDao.deleteSession(token)
        }
        clearSession()
        
        // Clear guest mode flag and category
        context.dataStore.edit { prefs ->
            prefs.remove(GUEST_MODE_KEY)
            prefs.remove(GUEST_CATEGORY_KEY)
        }
    }
    
    override suspend fun refreshSession(): Result<AuthResult> = runCatching {
        val token = getStoredToken() ?: throw Exception("No active session")
        val userId = getStoredUserId() ?: throw Exception("No user ID")
        
        val session = authDao.getSession(token)
        if (session == null || session.expiresAt < System.currentTimeMillis()) {
            clearSession()
            throw Exception("Session expired")
        }
        
        val userEntity = authDao.getUserById(userId)
            ?: throw Exception("User not found")
        
        AuthResult(
            success = true,
            user = userEntity.toDomain(),
            token = token,
            errorMessage = null
        )
    }
    
    override suspend fun requestPasswordReset(phoneNumber: String): Result<Unit> = runCatching {
        val formattedPhone = authService.formatPhoneNumber(phoneNumber)
        
        val request = com.dereva.smart.data.remote.dto.ForgotPasswordRequest(phone = formattedPhone)
        val response = ApiClient.apiService.forgotPassword(request)
        
        if (!response.isSuccessful || response.body()?.success != true) {
            val errorBodyString = response.errorBody()?.string()
            val errorMessage = if (errorBodyString != null && errorBodyString.contains("Validation error", ignoreCase = true)) {
                "Invalid phone number format."
            } else {
                response.body()?.message ?: "Failed to send reset code. Please try again."
            }
            throw Exception(errorMessage)
        }
    }
    
    override suspend fun resetPassword(request: PasswordResetRequest): Result<Unit> = runCatching {
        // Validate new password locally
        validatePassword(request.newPassword).getOrThrow()
        
        val formattedPhone = authService.formatPhoneNumber(request.phoneNumber)
        
        val apiRequest = com.dereva.smart.data.remote.dto.ResetPasswordRequest(
            phone = formattedPhone,
            code = request.verificationCode,
            newPassword = request.newPassword
        )
        
        val response = ApiClient.apiService.resetPassword(apiRequest)
        
        if (!response.isSuccessful || response.body()?.success != true) {
            val errorBodyString = response.errorBody()?.string()
            val errorMessage = if (errorBodyString != null) {
                if (errorBodyString.contains("Invalid verification code", ignoreCase = true)) {
                    "Invalid verification code."
                } else if (errorBodyString.contains("Verification code expired", ignoreCase = true)) {
                    "Verification code expired."
                } else {
                    "Password reset failed. Please try again."
                }
            } else {
                response.body()?.message ?: "Password reset failed."
            }
            throw Exception(errorMessage)
        }
        
        val userEntity = authDao.getUserByPhone(formattedPhone)
        if (userEntity != null) {
            // Hash new password for local database
            val newPasswordHash = authService.hashPassword(request.newPassword)
            
            // Update user
            authDao.updateUser(userEntity.copy(passwordHash = newPasswordHash))
            
            // Delete all sessions for security
            authDao.deleteAllUserSessions(userEntity.id)
            
            // Also clear the current device's session if this is the user resetting password
            val currentUserId = getStoredUserId()
            if (currentUserId == userEntity.id) {
                clearSession()
            }
        }
    }
    
    override suspend fun changePassword(
        userId: String,
        oldPassword: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        val userEntity = authDao.getUserById(userId)
            ?: throw Exception("User not found")
        
        // Verify old password
        val passwordValid = authService.verifyPassword(oldPassword, userEntity.passwordHash)
        if (!passwordValid) {
            throw Exception("Current password is incorrect")
        }
        
        // Validate new password
        validatePassword(newPassword).getOrThrow()
        
        // Hash new password
        val newPasswordHash = authService.hashPassword(newPassword)
        
        // Update user
        authDao.updateUser(userEntity.copy(passwordHash = newPasswordHash))
    }
    
    override suspend fun getCurrentUser(): Result<User?> = runCatching {
        val userId = getStoredUserId() ?: return@runCatching null
        authDao.getUserById(userId)?.toDomain()
    }
    
    override fun getCurrentUserFlow(): Flow<User?> {
        return context.dataStore.data.map { prefs ->
            val userId = prefs[USER_ID_KEY]
            userId?.let { authDao.getUserById(it)?.toDomain() }
        }
    }
    
    override suspend fun updateUser(user: User): Result<Unit> = runCatching {
        val userEntity = authDao.getUserById(user.id)
            ?: throw Exception("User not found")
        
        authDao.updateUser(user.toEntity(userEntity.passwordHash))
    }
    
    override suspend fun deleteAccount(userId: String, password: String): Result<Unit> = runCatching {
        val userEntity = authDao.getUserById(userId)
            ?: throw Exception("User not found")
        
        // Verify password
        val passwordValid = authService.verifyPassword(password, userEntity.passwordHash)
        if (!passwordValid) {
            throw Exception("Invalid password")
        }
        
        // Delete user and all sessions
        authDao.deleteUser(userId)
        authDao.deleteAllUserSessions(userId)
        clearSession()
    }
    
    override suspend fun isAuthenticated(): Boolean {
        val token = getStoredToken() ?: return false
        val session = authDao.getSession(token) ?: return false
        return session.expiresAt > System.currentTimeMillis()
    }
    
    override suspend fun getAuthState(): AuthState {
        val token = getStoredToken()
        val userId = getStoredUserId()
        
        if (token == null || userId == null) {
            return AuthState(isAuthenticated = false, user = null, token = null)
        }
        
        val user = authDao.getUserById(userId)?.toDomain()
        val isAuth = isAuthenticated()
        
        return AuthState(
            isAuthenticated = isAuth,
            user = user,
            token = if (isAuth) token else null
        )
    }
    
    override fun getAuthStateFlow(): Flow<AuthState> {
        return context.dataStore.data.map { prefs ->
            val token = prefs[TOKEN_KEY]
            val userId = prefs[USER_ID_KEY]
            val isGuest = prefs[GUEST_MODE_KEY] == "true"
            val guestCategory = prefs[GUEST_CATEGORY_KEY]?.let { 
                try { LicenseCategory.valueOf(it) } catch (e: Exception) { LicenseCategory.B1 }
            } ?: LicenseCategory.B1
            
            if (isGuest && userId != null) {
                // Guest mode
                AuthState(
                    isAuthenticated = true,
                    user = User.createGuestUser().copy(
                        id = userId,
                        targetCategory = guestCategory
                    ),
                    token = null
                )
            } else if (token == null || userId == null) {
                AuthState(isAuthenticated = false, user = null, token = null)
            } else {
                val user = authDao.getUserById(userId)?.toDomain()
                val session = authDao.getSession(token)
                val isAuth = session != null && session.expiresAt > System.currentTimeMillis()
                
                AuthState(
                    isAuthenticated = isAuth,
                    user = user,
                    token = if (isAuth) token else null
                )
            }
        }
    }
    
    override fun validatePassword(password: String): Result<Unit> = runCatching {
        if (!authService.isValidPassword(password)) {
            throw Exception(authService.getPasswordStrengthMessage(password))
        }
    }
    
    override fun validatePhoneNumber(phoneNumber: String): Result<Unit> = runCatching {
        if (!authService.isValidPhoneNumber(phoneNumber)) {
            throw Exception("Invalid phone number format. Use format: 0712345678 or 254712345678")
        }
    }
    
    private suspend fun saveSession(token: String, userId: String) {
        val session = AuthSessionEntity(
            token = token,
            userId = userId,
            createdAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000, // 30 days
            deviceId = null
        )
        authDao.insertSession(session)
        
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
