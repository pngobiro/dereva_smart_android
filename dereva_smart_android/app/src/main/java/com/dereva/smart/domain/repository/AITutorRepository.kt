package com.dereva.smart.domain.repository

import com.dereva.smart.domain.model.AITutor
import com.dereva.smart.domain.model.Language
import kotlinx.coroutines.flow.Flow

data class TutorResponse(
    val id: String,
    val question: String,
    val answer: String,
    val language: Language,
    val timestamp: Long,
    val isCached: Boolean = false
)

data class StudyRecommendation(
    val topicId: String,
    val topicName: String,
    val reason: String,
    val priority: Int,
    val estimatedStudyTime: Int // minutes
)

interface AITutorRepository {
    
    suspend fun askQuestion(
        userId: String,
        question: String,
        language: Language,
        context: List<String> = emptyList()
    ): Result<TutorResponse>
    
    suspend fun getCachedResponse(question: String, language: Language): Result<TutorResponse?>
    
    suspend fun cacheResponse(response: TutorResponse): Result<Unit>
    
    suspend fun getConversationHistory(userId: String, limit: Int = 10): Result<List<AITutor>>
    
    fun getConversationHistoryFlow(userId: String): Flow<List<AITutor>>
    
    suspend fun clearConversationHistory(userId: String): Result<Unit>
    
    suspend fun getPersonalizedRecommendations(
        userId: String,
        weakAreas: List<String>
    ): Result<List<StudyRecommendation>>
    
    suspend fun detectLanguage(text: String): Language
}
