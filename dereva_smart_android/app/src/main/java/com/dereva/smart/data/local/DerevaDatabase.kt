package com.dereva.smart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dereva.smart.data.local.dao.*
import com.dereva.smart.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        VerificationCodeEntity::class,
        AuthSessionEntity::class,
        QuestionEntity::class,
        MockTestEntity::class,
        TestResultEntity::class,
        StudySessionEntity::class,
        AchievementEntity::class,
        TutorCacheEntity::class,
        TutorEntity::class,
        DrivingSchoolEntity::class,
        ModuleScheduleEntity::class,
        ProgressReportEntity::class,
        SchoolLinkingEntity::class,
        PaymentRequestEntity::class,
        PaymentResultEntity::class,
        UserSubscriptionEntity::class,
        PromoCodeEntity::class,
        ReferralCodeEntity::class,
        SchoolCommissionEntity::class,
        ModuleEntity::class,
        LessonEntity::class,
        MediaAssetEntity::class,
        CurriculumTopicEntity::class,
        LessonProgressEntity::class,
        ModuleDownloadEntity::class,
        ContentSyncMetadataEntity::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DerevaDatabase : RoomDatabase() {
    
    abstract fun authDao(): AuthDao
    abstract fun questionDao(): QuestionDao
    abstract fun mockTestDao(): MockTestDao
    abstract fun progressDao(): ProgressDao
    abstract fun tutorDao(): TutorDao
    abstract fun schoolDao(): SchoolDao
    abstract fun paymentDao(): PaymentDao
    abstract fun contentDao(): ContentDao
    
    companion object {
        const val DATABASE_NAME = "dereva_smart.db"
    }
}
