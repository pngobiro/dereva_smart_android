package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dereva.smart.domain.model.AITutor
import com.dereva.smart.domain.model.Language
import java.util.Date

@Entity(tableName = "tutor_cache")
data class TutorCacheEntity(
    @PrimaryKey val questionHash: String,
    val question: String,
    val answer: String,
    val language: String,
    val recommendationsJson: String,
    val timestamp: Long
)

@Entity(tableName = "tutor_conversations")
data class TutorEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val question: String,
    val response: String,
    val language: String,
    val timestamp: Long,
    val isCached: Boolean = false
)

fun TutorEntity.toDomain(): AITutor {
    return AITutor(
        id = id,
        userId = userId,
        question = question,
        response = response,
        language = Language.valueOf(language),
        timestamp = Date(timestamp)
    )
}

fun AITutor.toEntity(): TutorEntity {
    return TutorEntity(
        id = id,
        userId = userId,
        question = question,
        response = response,
        language = language.name,
        timestamp = timestamp.time,
        isCached = false
    )
}
