package com.dereva.smart.data.repository

import com.dereva.smart.data.local.dao.TutorDao
import com.dereva.smart.data.local.entity.TutorEntity
import com.dereva.smart.data.local.entity.toDomain
import com.dereva.smart.data.remote.GeminiService
import com.dereva.smart.domain.model.AITutor
import com.dereva.smart.domain.model.Language
import com.dereva.smart.domain.repository.AITutorRepository
import com.dereva.smart.domain.repository.StudyRecommendation
import com.dereva.smart.domain.repository.TutorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class AITutorRepositoryImpl(
    private val tutorDao: TutorDao,
    private val geminiService: GeminiService
) : AITutorRepository {
    
    override suspend fun askQuestion(
        userId: String,
        question: String,
        language: Language,
        context: List<String>
    ): Result<TutorResponse> = runCatching {
        // First check cache
        val cached = getCachedResponse(question, language).getOrNull()
        if (cached != null) {
            return@runCatching cached
        }
        
        // Build context from conversation history
        val history = getConversationHistory(userId, limit = 10).getOrDefault(emptyList())
        val historyPairs = history.chunked(2).mapNotNull { chunk ->
            if (chunk.size == 2) Pair(chunk[0].question, chunk[1].response) else null
        }
        
        val tutorResponse = geminiService.askQuestion(
            question = question,
            language = language,
            conversationHistory = historyPairs
        )
        
        val response = TutorResponse(
            id = "tutor_${System.currentTimeMillis()}",
            question = question,
            answer = tutorResponse.answer,
            language = language,
            timestamp = System.currentTimeMillis(),
            isCached = false
        )
        
        // Save to database
        val tutorEntity = TutorEntity(
            id = response.id,
            userId = userId,
            question = question,
            response = tutorResponse.answer,
            language = language.name,
            timestamp = response.timestamp,
            isCached = false
        )
        tutorDao.insertConversation(tutorEntity)
        
        // Cache common questions
        if (isCommonQuestion(question)) {
            cacheResponse(response)
        }
        
        response
    }
    
    override suspend fun getCachedResponse(
        question: String,
        language: Language
    ): Result<TutorResponse?> = runCatching {
        val entity = tutorDao.getCachedResponse(
            questionHash = question.lowercase().hashCode().toString(),
            language = language.name
        )
        
        entity?.let {
            TutorResponse(
                id = it.id,
                question = it.question,
                answer = it.response,
                language = Language.valueOf(it.language),
                timestamp = it.timestamp,
                isCached = true
            )
        }
    }
    
    override suspend fun cacheResponse(response: TutorResponse): Result<Unit> = runCatching {
        val entity = TutorEntity(
            id = "cache_${response.question.hashCode()}",
            userId = "system",
            question = response.question,
            response = response.answer,
            language = response.language.name,
            timestamp = response.timestamp,
            isCached = true
        )
        tutorDao.insertConversation(entity)
    }
    
    override suspend fun getConversationHistory(
        userId: String,
        limit: Int
    ): Result<List<AITutor>> = runCatching {
        tutorDao.getUserConversations(userId, limit).map { it.toDomain() }
    }
    
    override fun getConversationHistoryFlow(userId: String): Flow<List<AITutor>> {
        return tutorDao.getUserConversationsFlow(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun clearConversationHistory(userId: String): Result<Unit> = runCatching {
        tutorDao.clearUserConversations(userId)
    }
    
    override suspend fun getPersonalizedRecommendations(
        userId: String,
        weakAreas: List<String>
    ): Result<List<StudyRecommendation>> = runCatching {
        // Generate recommendations based on weak areas
        weakAreas.mapIndexed { index, topicId ->
            StudyRecommendation(
                topicId = topicId,
                topicName = getTopicName(topicId),
                reason = "You scored below 70% in this topic",
                priority = index + 1,
                estimatedStudyTime = 30
            )
        }
    }
    
    override suspend fun detectLanguage(text: String): Language {
        // Simple language detection based on character patterns
        val swahiliKeywords = listOf(
            "nini", "vipi", "namna", "gani", "je", "kwa", "na", "ya",
            "barabara", "gari", "dereva", "alama", "sheria"
        )
        
        val lowerText = text.lowercase()
        val hasSwahili = swahiliKeywords.any { lowerText.contains(it) }
        
        return if (hasSwahili) Language.SWAHILI else Language.ENGLISH
    }
    
    private fun isCommonQuestion(question: String): Boolean {
        val commonPatterns = listOf(
            "what is", "what are", "how to", "when to", "where to",
            "nini", "vipi", "lini", "wapi", "namna gani"
        )
        val lowerQuestion = question.lowercase()
        return commonPatterns.any { lowerQuestion.contains(it) }
    }
    
    private fun getTopicName(topicId: String): String {
        // This should come from a content repository
        return when (topicId) {
            "road_signs" -> "Road Signs"
            "traffic_rules" -> "Traffic Rules"
            "safe_driving" -> "Safe Driving"
            "vehicle_control" -> "Vehicle Control"
            "parking" -> "Parking Rules"
            else -> "Topic $topicId"
        }
    }
}
