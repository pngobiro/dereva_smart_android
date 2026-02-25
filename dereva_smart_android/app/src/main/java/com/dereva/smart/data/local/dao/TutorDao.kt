package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.TutorCacheEntity
import com.dereva.smart.data.local.entity.TutorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorDao {
    
    // Cache operations
    @Query("SELECT * FROM tutor_cache WHERE questionHash = :hash")
    suspend fun getCachedResponseByHash(hash: String): TutorCacheEntity?
    
    @Query("SELECT * FROM tutor_conversations WHERE question = :questionHash AND language = :language AND isCached = 1 LIMIT 1")
    suspend fun getCachedResponse(questionHash: String, language: String): TutorEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheResponse(cache: TutorCacheEntity)
    
    @Query("DELETE FROM tutor_cache WHERE timestamp < :cutoffTime")
    suspend fun deleteOldCache(cutoffTime: Long)
    
    @Query("SELECT COUNT(*) FROM tutor_cache")
    suspend fun getCacheCount(): Int
    
    // Conversation operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: TutorEntity)
    
    @Query("SELECT * FROM tutor_conversations WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUserConversations(userId: String, limit: Int): List<TutorEntity>
    
    @Query("SELECT * FROM tutor_conversations WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserConversationsFlow(userId: String): Flow<List<TutorEntity>>
    
    @Query("DELETE FROM tutor_conversations WHERE userId = :userId")
    suspend fun clearUserConversations(userId: String)
    
    @Query("SELECT COUNT(*) FROM tutor_conversations WHERE userId = :userId")
    suspend fun getConversationCount(userId: String): Int
}

