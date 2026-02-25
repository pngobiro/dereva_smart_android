package com.dereva.smart.data.local.entity

import com.dereva.smart.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

// JSON converters
private val gson = Gson()

fun String.toStringIntMap(): Map<String, Int> {
    val type = object : TypeToken<Map<String, Int>>() {}.type
    return gson.fromJson(this, type) ?: emptyMap()
}

fun Map<String, Int>.toJson(): String = gson.toJson(this)

fun String.toStringList(): List<String> {
    val type = object : TypeToken<List<String>>() {}.type
    return gson.fromJson(this, type) ?: emptyList()
}

fun List<String>.toJson(): String = gson.toJson(this)

// MockTest conversions
fun MockTestEntity.toDomain(questions: List<Question>): MockTest {
    return MockTest(
        id = id,
        userId = userId,
        questions = questions,
        userAnswers = userAnswersJson.toStringIntMap(),
        createdAt = Date(createdAt),
        startedAt = startedAt?.let { Date(it) },
        completedAt = completedAt?.let { Date(it) },
        status = TestStatus.valueOf(status),
        durationMinutes = durationMinutes,
        licenseCategory = licenseCategory
    )
}

fun MockTest.toEntity(): MockTestEntity {
    return MockTestEntity(
        id = id,
        userId = userId,
        questionIdsJson = questions.map { it.id }.toJson(),
        userAnswersJson = userAnswers.toJson(),
        createdAt = createdAt.time,
        startedAt = startedAt?.time,
        completedAt = completedAt?.time,
        status = status.name,
        durationMinutes = durationMinutes,
        licenseCategory = licenseCategory
    )
}

// TestResult conversions
fun TestResultEntity.toDomain(): TestResult {
    return TestResult(
        id = id,
        testId = testId,
        userId = userId,
        totalQuestions = totalQuestions,
        correctAnswers = correctAnswers,
        incorrectAnswers = incorrectAnswers,
        unanswered = unanswered,
        scorePercentage = scorePercentage,
        passed = passed,
        topicScores = topicScoresJson.toStringIntMap(),
        topicTotals = topicTotalsJson.toStringIntMap(),
        completedAt = Date(completedAt),
        timeTakenSeconds = timeTakenSeconds
    )
}

fun TestResult.toEntity(): TestResultEntity {
    return TestResultEntity(
        id = id,
        testId = testId,
        userId = userId,
        totalQuestions = totalQuestions,
        correctAnswers = correctAnswers,
        incorrectAnswers = incorrectAnswers,
        unanswered = unanswered,
        scorePercentage = scorePercentage,
        passed = passed,
        topicScoresJson = topicScores.toJson(),
        topicTotalsJson = topicTotals.toJson(),
        completedAt = completedAt.time,
        timeTakenSeconds = timeTakenSeconds
    )
}

// Progress conversions
fun StudySessionEntity.toDomain(): StudySession {
    return StudySession(
        id = id,
        userId = userId,
        lessonId = lessonId,
        startTime = Date(startTime),
        endTime = Date(endTime),
        durationMinutes = durationMinutes,
        completed = completed
    )
}

fun StudySession.toEntity(): StudySessionEntity {
    return StudySessionEntity(
        id = id,
        userId = userId,
        lessonId = lessonId,
        startTime = startTime.time,
        endTime = endTime.time,
        durationMinutes = durationMinutes,
        completed = completed
    )
}

fun AchievementEntity.toDomain(): Achievement {
    return Achievement(
        id = id,
        userId = userId,
        badgeType = BadgeType.valueOf(badgeType),
        earnedAt = Date(earnedAt),
        titleEn = titleEn,
        titleSw = titleSw,
        descriptionEn = descriptionEn,
        descriptionSw = descriptionSw
    )
}

fun Achievement.toEntity(): AchievementEntity {
    return AchievementEntity(
        id = id,
        userId = userId,
        badgeType = badgeType.name,
        earnedAt = earnedAt.time,
        titleEn = titleEn,
        titleSw = titleSw,
        descriptionEn = descriptionEn,
        descriptionSw = descriptionSw
    )
}
