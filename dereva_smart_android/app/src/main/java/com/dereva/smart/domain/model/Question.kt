package com.dereva.smart.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id: String,
    val textEn: String,
    val textSw: String,
    val options: List<QuestionOption>,
    val correctOptionIndex: Int,
    val explanationEn: String,
    val explanationSw: String,
    val imageUrl: String? = null,
    val curriculumTopicId: String,
    val difficultyLevel: DifficultyLevel,
    val licenseCategories: List<LicenseCategory>,
    val isCommonCore: Boolean
) : Parcelable {
    
    fun getText(language: Language): String {
        return when (language) {
            Language.ENGLISH -> textEn
            Language.SWAHILI -> textSw
        }
    }
    
    fun getExplanation(language: Language): String {
        return when (language) {
            Language.ENGLISH -> explanationEn
            Language.SWAHILI -> explanationSw
        }
    }
}

@Parcelize
data class QuestionOption(
    val textEn: String,
    val textSw: String,
    val imageUrl: String? = null
) : Parcelable {
    
    fun getText(language: Language): String {
        return when (language) {
            Language.ENGLISH -> textEn
            Language.SWAHILI -> textSw
        }
    }
}

enum class DifficultyLevel {
    EASY, MEDIUM, HARD
}

enum class Language {
    ENGLISH, SWAHILI
}
