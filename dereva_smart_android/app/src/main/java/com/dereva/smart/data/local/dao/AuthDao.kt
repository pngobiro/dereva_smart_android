package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.AuthSessionEntity
import com.dereva.smart.data.local.entity.UserEntity
import com.dereva.smart.data.local.entity.VerificationCodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    
    // User operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun getUserByPhone(phoneNumber: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    fun getUserByPhoneFlow(phoneNumber: String): Flow<UserEntity?>
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)
    
    @Query("UPDATE users SET isPhoneVerified = 1 WHERE phoneNumber = :phoneNumber")
    suspend fun markPhoneVerified(phoneNumber: String)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
    
    // Verification code operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationCode(code: VerificationCodeEntity)
    
    @Query("SELECT * FROM verification_codes WHERE code = :code AND phoneNumber = :phoneNumber")
    suspend fun getVerificationCode(code: String, phoneNumber: String): VerificationCodeEntity?
    
    @Query("UPDATE verification_codes SET isUsed = 1 WHERE code = :code")
    suspend fun markCodeAsUsed(code: String)
    
    @Query("DELETE FROM verification_codes WHERE expiresAt < :timestamp")
    suspend fun deleteExpiredCodes(timestamp: Long)
    
    @Query("DELETE FROM verification_codes WHERE phoneNumber = :phoneNumber AND isUsed = 0")
    suspend fun deleteUnusedCodesForPhone(phoneNumber: String)
    
    // Session operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AuthSessionEntity)
    
    @Query("SELECT * FROM auth_sessions WHERE token = :token")
    suspend fun getSession(token: String): AuthSessionEntity?
    
    @Query("SELECT * FROM auth_sessions WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestSession(userId: String): AuthSessionEntity?
    
    @Query("DELETE FROM auth_sessions WHERE token = :token")
    suspend fun deleteSession(token: String)
    
    @Query("DELETE FROM auth_sessions WHERE userId = :userId")
    suspend fun deleteAllUserSessions(userId: String)
    
    @Query("DELETE FROM auth_sessions WHERE expiresAt < :timestamp")
    suspend fun deleteExpiredSessions(timestamp: Long)
}
