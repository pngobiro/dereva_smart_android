package com.dereva.smart.data.remote

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import kotlin.random.Random

/**
 * Authentication service for password hashing and SMS verification
 */
class AuthService(private val context: Context) {
    
    private val secureRandom = SecureRandom()
    
    /**
     * Hash password using BCrypt with 12 rounds (as per security requirements)
     */
    suspend fun hashPassword(password: String): String = withContext(Dispatchers.Default) {
        BCrypt.hashpw(password, BCrypt.gensalt(12))
    }
    
    /**
     * Verify password against hash
     */
    suspend fun verifyPassword(password: String, hash: String): Boolean = withContext(Dispatchers.Default) {
        try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Generate 6-digit verification code
     */
    fun generateVerificationCode(): String {
        return String.format("%06d", secureRandom.nextInt(1000000))
    }
    
    /**
     * Generate secure session token
     */
    fun generateSessionToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Send SMS verification code
     * In production, this would integrate with an SMS gateway (e.g., Africa's Talking, Twilio)
     * For now, we'll simulate it
     */
    suspend fun sendSMS(phoneNumber: String, message: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            // TODO: Integrate with SMS gateway
            // For development, just log the code
            android.util.Log.d("AuthService", "SMS to $phoneNumber: $message")
            
            // Simulate network delay
            kotlinx.coroutines.delay(1000)
            
            // In production, make actual API call:
            // val response = smsGateway.sendSMS(phoneNumber, message)
            // if (!response.isSuccessful) throw Exception("SMS sending failed")
        }
    }
    
    /**
     * Format phone number to Kenya format (254...)
     */
    fun formatPhoneNumber(phone: String): String {
        var formatted = phone.replace(Regex("[^0-9]"), "")
        
        return when {
            formatted.startsWith("254") -> formatted
            formatted.startsWith("0") -> "254${formatted.substring(1)}"
            formatted.startsWith("+254") -> formatted.substring(1)
            formatted.length == 9 -> "254$formatted"
            else -> formatted
        }
    }
    
    /**
     * Validate phone number format
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val formatted = formatPhoneNumber(phone)
        return formatted.matches(Regex("^254[17]\\d{8}$"))
    }
    
    /**
     * Validate password strength
     * Requirements:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecial
    }
    
    /**
     * Get password strength message
     */
    fun getPasswordStrengthMessage(password: String): String {
        val issues = mutableListOf<String>()
        
        if (password.length < 8) issues.add("at least 8 characters")
        if (!password.any { it.isUpperCase() }) issues.add("one uppercase letter")
        if (!password.any { it.isLowerCase() }) issues.add("one lowercase letter")
        if (!password.any { it.isDigit() }) issues.add("one digit")
        if (!password.any { !it.isLetterOrDigit() }) issues.add("one special character")
        
        return if (issues.isEmpty()) {
            "Strong password"
        } else {
            "Password must contain: ${issues.joinToString(", ")}"
        }
    }
}
