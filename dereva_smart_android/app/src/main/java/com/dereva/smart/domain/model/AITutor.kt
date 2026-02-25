package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class AITutor(
    val id: String,
    val userId: String,
    val question: String,
    val response: String,
    val language: Language,
    val timestamp: Date
) : Parcelable

@Parcelize
data class TutorResponse(
    val id: String,
    val question: String,
    val answer: String,
    val language: Language,
    val timestamp: Date,
    val recommendations: List<StudyRecommendation>
) : Parcelable

@Parcelize
data class StudyRecommendation(
    val topicId: String,
    val topicNameEn: String,
    val topicNameSw: String,
    val reason: String,
    val priority: Priority
) : Parcelable {
    
    fun getTopicName(language: Language): String {
        return when (language) {
            Language.ENGLISH -> topicNameEn
            Language.SWAHILI -> topicNameSw
        }
    }
}

enum class Priority {
    HIGH, MEDIUM, LOW
}

@Parcelize
data class Conversation(
    val id: String,
    val userId: String,
    val messages: List<ConversationMessage>,
    val createdAt: Date,
    val updatedAt: Date
) : Parcelable

@Parcelize
data class ConversationMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Date
) : Parcelable

enum class MessageRole {
    USER, ASSISTANT
}
