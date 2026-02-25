package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.dereva.smart.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val textEn: String,
    val textSw: String,
    val optionsJson: String,
    val correctOptionIndex: Int,
    val explanationEn: String,
    val explanationSw: String,
    val imageUrl: String?,
    val curriculumTopicId: String,
    val difficultyLevel: String,
    val licenseCategoriesJson: String,
    val isCommonCore: Boolean
)

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromQuestionOptionList(value: List<QuestionOption>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toQuestionOptionList(value: String): List<QuestionOption> {
        val type = object : TypeToken<List<QuestionOption>>() {}.type
        return gson.fromJson(value, type)
    }
    
    @TypeConverter
    fun fromLicenseCategoryList(value: List<LicenseCategory>): String {
        return gson.toJson(value.map { it.name })
    }
    
    @TypeConverter
    fun toLicenseCategoryList(value: String): List<LicenseCategory> {
        val type = object : TypeToken<List<String>>() {}.type
        val names: List<String> = gson.fromJson(value, type)
        return names.map { LicenseCategory.valueOf(it) }
    }
    
    @TypeConverter
    fun fromMap(value: Map<String, Int>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toMap(value: String): Map<String, Int> {
        val type = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(value, type)
    }
    
    @TypeConverter
    fun fromDate(value: java.util.Date?): Long? {
        return value?.time
    }
    
    @TypeConverter
    fun toDate(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }
}

fun QuestionEntity.toDomain(): Question {
    val gson = Gson()
    val optionsType = object : TypeToken<List<QuestionOption>>() {}.type
    val options: List<QuestionOption> = gson.fromJson(optionsJson, optionsType)
    
    val categoriesType = object : TypeToken<List<String>>() {}.type
    val categoryNames: List<String> = gson.fromJson(licenseCategoriesJson, categoriesType)
    val categories = categoryNames.map { LicenseCategory.valueOf(it) }
    
    return Question(
        id = id,
        textEn = textEn,
        textSw = textSw,
        options = options,
        correctOptionIndex = correctOptionIndex,
        explanationEn = explanationEn,
        explanationSw = explanationSw,
        imageUrl = imageUrl,
        curriculumTopicId = curriculumTopicId,
        difficultyLevel = DifficultyLevel.valueOf(difficultyLevel),
        licenseCategories = categories,
        isCommonCore = isCommonCore
    )
}

fun Question.toEntity(): QuestionEntity {
    val gson = Gson()
    return QuestionEntity(
        id = id,
        textEn = textEn,
        textSw = textSw,
        optionsJson = gson.toJson(options),
        correctOptionIndex = correctOptionIndex,
        explanationEn = explanationEn,
        explanationSw = explanationSw,
        imageUrl = imageUrl,
        curriculumTopicId = curriculumTopicId,
        difficultyLevel = difficultyLevel.name,
        licenseCategoriesJson = gson.toJson(licenseCategories.map { it.name }),
        isCommonCore = isCommonCore
    )
}
