package com.dereva.smart.data.repository

import com.dereva.smart.data.remote.GeminiService
import com.dereva.smart.domain.model.AITutor
import com.dereva.smart.domain.model.Language
import com.dereva.smart.domain.repository.AITutorRepository
import com.dereva.smart.domain.repository.TutorResponse
import com.dereva.smart.domain.repository.StudyRecommendation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class AITutorRepositoryImpl(
    private val geminiService: GeminiService
) : AITutorRepository {
    
    override suspend fun askQuestion(
        userId: String,
        question: String,
        language: Language,
        context: List<String>
    ): Result<TutorResponse> = runCatching {
        // Direct API call without local cache
        // We ignore 'context' for now as GeminiService doesn't support List<String> directly yet
        val response = geminiService.askQuestion(question, language)
        
        TutorResponse(
            id = response.id,
            question = response.question,
            answer = response.answer,
            language = response.language,
            timestamp = response.timestamp.time,
            isCached = false
        )
    }
    
    override suspend fun getCachedResponse(question: String, language: Language): Result<TutorResponse?> = Result.success(null)
    
    override suspend fun cacheResponse(response: TutorResponse): Result<Unit> = Result.success(Unit)
    
    override suspend fun getConversationHistory(userId: String, limit: Int): Result<List<AITutor>> = Result.success(emptyList())
    
    override fun getConversationHistoryFlow(userId: String): Flow<List<AITutor>> = flowOf(emptyList())
    
    override suspend fun clearConversationHistory(userId: String): Result<Unit> = Result.success(Unit)
    
    override suspend fun getPersonalizedRecommendations(
        userId: String,
        weakAreas: List<String>
    ): Result<List<StudyRecommendation>> = Result.success(emptyList())
    
    override suspend fun detectLanguage(text: String): Language = geminiService.detectLanguage(text)
}
