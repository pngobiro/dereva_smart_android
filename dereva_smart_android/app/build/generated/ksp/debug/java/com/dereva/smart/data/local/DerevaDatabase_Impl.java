package com.dereva.smart.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.dereva.smart.data.local.dao.AuthDao;
import com.dereva.smart.data.local.dao.AuthDao_Impl;
import com.dereva.smart.data.local.dao.ContentDao;
import com.dereva.smart.data.local.dao.ContentDao_Impl;
import com.dereva.smart.data.local.dao.MockTestDao;
import com.dereva.smart.data.local.dao.MockTestDao_Impl;
import com.dereva.smart.data.local.dao.PaymentDao;
import com.dereva.smart.data.local.dao.PaymentDao_Impl;
import com.dereva.smart.data.local.dao.ProgressDao;
import com.dereva.smart.data.local.dao.ProgressDao_Impl;
import com.dereva.smart.data.local.dao.QuestionDao;
import com.dereva.smart.data.local.dao.QuestionDao_Impl;
import com.dereva.smart.data.local.dao.SchoolDao;
import com.dereva.smart.data.local.dao.SchoolDao_Impl;
import com.dereva.smart.data.local.dao.TutorDao;
import com.dereva.smart.data.local.dao.TutorDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DerevaDatabase_Impl extends DerevaDatabase {
  private volatile AuthDao _authDao;

  private volatile QuestionDao _questionDao;

  private volatile MockTestDao _mockTestDao;

  private volatile ProgressDao _progressDao;

  private volatile TutorDao _tutorDao;

  private volatile SchoolDao _schoolDao;

  private volatile PaymentDao _paymentDao;

  private volatile ContentDao _contentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(8) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `name` TEXT NOT NULL, `email` TEXT, `targetCategory` TEXT NOT NULL, `drivingSchoolId` TEXT, `subscriptionStatus` TEXT NOT NULL, `subscriptionExpiryDate` INTEGER, `isPhoneVerified` INTEGER NOT NULL, `isGuestMode` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastActiveAt` INTEGER NOT NULL, `lastLoginAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `verification_codes` (`code` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `expiresAt` INTEGER NOT NULL, `isUsed` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`code`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `auth_sessions` (`token` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL, `deviceId` TEXT, PRIMARY KEY(`token`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` TEXT NOT NULL, `textEn` TEXT NOT NULL, `textSw` TEXT NOT NULL, `optionsJson` TEXT NOT NULL, `correctOptionIndex` INTEGER NOT NULL, `explanationEn` TEXT NOT NULL, `explanationSw` TEXT NOT NULL, `imageUrl` TEXT, `curriculumTopicId` TEXT NOT NULL, `difficultyLevel` TEXT NOT NULL, `licenseCategoriesJson` TEXT NOT NULL, `isCommonCore` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `mock_tests` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `questionIdsJson` TEXT NOT NULL, `userAnswersJson` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `startedAt` INTEGER, `completedAt` INTEGER, `status` TEXT NOT NULL, `durationMinutes` INTEGER NOT NULL, `licenseCategory` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_results` (`id` TEXT NOT NULL, `testId` TEXT NOT NULL, `userId` TEXT NOT NULL, `totalQuestions` INTEGER NOT NULL, `correctAnswers` INTEGER NOT NULL, `incorrectAnswers` INTEGER NOT NULL, `unanswered` INTEGER NOT NULL, `scorePercentage` REAL NOT NULL, `passed` INTEGER NOT NULL, `topicScoresJson` TEXT NOT NULL, `topicTotalsJson` TEXT NOT NULL, `completedAt` INTEGER NOT NULL, `timeTakenSeconds` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `study_sessions` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `lessonId` TEXT NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `completed` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `achievements` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `badgeType` TEXT NOT NULL, `earnedAt` INTEGER NOT NULL, `titleEn` TEXT NOT NULL, `titleSw` TEXT NOT NULL, `descriptionEn` TEXT NOT NULL, `descriptionSw` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tutor_cache` (`questionHash` TEXT NOT NULL, `question` TEXT NOT NULL, `answer` TEXT NOT NULL, `language` TEXT NOT NULL, `recommendationsJson` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`questionHash`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tutor_conversations` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `question` TEXT NOT NULL, `response` TEXT NOT NULL, `language` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `isCached` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `driving_schools` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `code` TEXT NOT NULL, `location` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `email` TEXT NOT NULL, `isVerified` INTEGER NOT NULL, `totalStudents` INTEGER NOT NULL, `averagePassRate` REAL NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `module_schedules` (`id` TEXT NOT NULL, `schoolId` TEXT NOT NULL, `moduleId` TEXT NOT NULL, `moduleName` TEXT NOT NULL, `unlockDate` INTEGER NOT NULL, `dueDate` INTEGER, `isUnlocked` INTEGER NOT NULL, `order` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `progress_reports` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `schoolId` TEXT NOT NULL, `reportDate` INTEGER NOT NULL, `completedModules` INTEGER NOT NULL, `totalModules` INTEGER NOT NULL, `averageTestScore` REAL NOT NULL, `totalStudyTime` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, `lastActivityDate` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `school_linkings` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `schoolId` TEXT NOT NULL, `linkedAt` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, `progressSharingEnabled` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payment_requests` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `phoneNumber` TEXT NOT NULL, `subscriptionTier` TEXT NOT NULL, `promoCode` TEXT, `referralCode` TEXT, `schoolId` TEXT, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payment_results` (`id` TEXT NOT NULL, `paymentRequestId` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `mpesaReceiptNumber` TEXT, `transactionId` TEXT, `status` TEXT NOT NULL, `errorMessage` TEXT, `completedAt` INTEGER, `metadata` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_subscriptions` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `tier` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER, `isActive` INTEGER NOT NULL, `autoRenew` INTEGER NOT NULL, `paymentResultId` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `promo_codes` (`code` TEXT NOT NULL, `discountPercentage` REAL NOT NULL, `discountAmount` REAL, `validUntil` INTEGER NOT NULL, `maxUses` INTEGER NOT NULL, `currentUses` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`code`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `referral_codes` (`code` TEXT NOT NULL, `referrerId` TEXT NOT NULL, `discountPercentage` REAL NOT NULL, `referrerBonusPercentage` REAL NOT NULL, `usageCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`code`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `school_commissions` (`id` TEXT NOT NULL, `schoolId` TEXT NOT NULL, `paymentId` TEXT NOT NULL, `amount` REAL NOT NULL, `percentage` REAL NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `modules` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `licenseCategory` TEXT NOT NULL, `thumbnailUrl` TEXT, `estimatedDuration` INTEGER NOT NULL, `lessonCount` INTEGER NOT NULL, `isDownloaded` INTEGER NOT NULL, `downloadSize` INTEGER NOT NULL, `status` TEXT NOT NULL, `completionPercentage` INTEGER NOT NULL, `requiresSubscription` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lessons` (`id` TEXT NOT NULL, `moduleId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `contentType` TEXT NOT NULL, `contentUrl` TEXT, `contentText` TEXT, `duration` INTEGER NOT NULL, `isDownloaded` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `requiresSubscription` INTEGER NOT NULL, `lastAccessedAt` INTEGER, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `media_assets` (`id` TEXT NOT NULL, `lessonId` TEXT NOT NULL, `type` TEXT NOT NULL, `url` TEXT NOT NULL, `localPath` TEXT, `title` TEXT, `description` TEXT, `fileSize` INTEGER NOT NULL, `duration` INTEGER, `thumbnailUrl` TEXT, `isDownloaded` INTEGER NOT NULL, `downloadProgress` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `curriculum_topics` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `moduleId` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `keyPoints` TEXT NOT NULL, `relatedLessons` TEXT NOT NULL, `relatedQuestions` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lesson_progress` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `lessonId` TEXT NOT NULL, `moduleId` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `completionPercentage` INTEGER NOT NULL, `timeSpent` INTEGER NOT NULL, `lastPosition` INTEGER NOT NULL, `startedAt` INTEGER NOT NULL, `completedAt` INTEGER, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `module_downloads` (`id` TEXT NOT NULL, `moduleId` TEXT NOT NULL, `userId` TEXT NOT NULL, `status` TEXT NOT NULL, `progress` INTEGER NOT NULL, `downloadedSize` INTEGER NOT NULL, `totalSize` INTEGER NOT NULL, `startedAt` INTEGER NOT NULL, `completedAt` INTEGER, `errorMessage` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `content_sync_metadata` (`id` TEXT NOT NULL, `entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `lastSyncedAt` INTEGER NOT NULL, `localVersion` INTEGER NOT NULL, `serverVersion` INTEGER NOT NULL, `needsSync` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '57653fe216548987b87e42b9ff8b1101')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `verification_codes`");
        db.execSQL("DROP TABLE IF EXISTS `auth_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `mock_tests`");
        db.execSQL("DROP TABLE IF EXISTS `test_results`");
        db.execSQL("DROP TABLE IF EXISTS `study_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `achievements`");
        db.execSQL("DROP TABLE IF EXISTS `tutor_cache`");
        db.execSQL("DROP TABLE IF EXISTS `tutor_conversations`");
        db.execSQL("DROP TABLE IF EXISTS `driving_schools`");
        db.execSQL("DROP TABLE IF EXISTS `module_schedules`");
        db.execSQL("DROP TABLE IF EXISTS `progress_reports`");
        db.execSQL("DROP TABLE IF EXISTS `school_linkings`");
        db.execSQL("DROP TABLE IF EXISTS `payment_requests`");
        db.execSQL("DROP TABLE IF EXISTS `payment_results`");
        db.execSQL("DROP TABLE IF EXISTS `user_subscriptions`");
        db.execSQL("DROP TABLE IF EXISTS `promo_codes`");
        db.execSQL("DROP TABLE IF EXISTS `referral_codes`");
        db.execSQL("DROP TABLE IF EXISTS `school_commissions`");
        db.execSQL("DROP TABLE IF EXISTS `modules`");
        db.execSQL("DROP TABLE IF EXISTS `lessons`");
        db.execSQL("DROP TABLE IF EXISTS `media_assets`");
        db.execSQL("DROP TABLE IF EXISTS `curriculum_topics`");
        db.execSQL("DROP TABLE IF EXISTS `lesson_progress`");
        db.execSQL("DROP TABLE IF EXISTS `module_downloads`");
        db.execSQL("DROP TABLE IF EXISTS `content_sync_metadata`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(14);
        _columnsUsers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("passwordHash", new TableInfo.Column("passwordHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("targetCategory", new TableInfo.Column("targetCategory", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("drivingSchoolId", new TableInfo.Column("drivingSchoolId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("subscriptionStatus", new TableInfo.Column("subscriptionStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("subscriptionExpiryDate", new TableInfo.Column("subscriptionExpiryDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("isPhoneVerified", new TableInfo.Column("isPhoneVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("isGuestMode", new TableInfo.Column("isGuestMode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("lastActiveAt", new TableInfo.Column("lastActiveAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("lastLoginAt", new TableInfo.Column("lastLoginAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.dereva.smart.data.local.entity.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsVerificationCodes = new HashMap<String, TableInfo.Column>(5);
        _columnsVerificationCodes.put("code", new TableInfo.Column("code", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerificationCodes.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerificationCodes.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerificationCodes.put("isUsed", new TableInfo.Column("isUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVerificationCodes.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVerificationCodes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVerificationCodes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVerificationCodes = new TableInfo("verification_codes", _columnsVerificationCodes, _foreignKeysVerificationCodes, _indicesVerificationCodes);
        final TableInfo _existingVerificationCodes = TableInfo.read(db, "verification_codes");
        if (!_infoVerificationCodes.equals(_existingVerificationCodes)) {
          return new RoomOpenHelper.ValidationResult(false, "verification_codes(com.dereva.smart.data.local.entity.VerificationCodeEntity).\n"
                  + " Expected:\n" + _infoVerificationCodes + "\n"
                  + " Found:\n" + _existingVerificationCodes);
        }
        final HashMap<String, TableInfo.Column> _columnsAuthSessions = new HashMap<String, TableInfo.Column>(5);
        _columnsAuthSessions.put("token", new TableInfo.Column("token", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuthSessions.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuthSessions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuthSessions.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuthSessions.put("deviceId", new TableInfo.Column("deviceId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAuthSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAuthSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAuthSessions = new TableInfo("auth_sessions", _columnsAuthSessions, _foreignKeysAuthSessions, _indicesAuthSessions);
        final TableInfo _existingAuthSessions = TableInfo.read(db, "auth_sessions");
        if (!_infoAuthSessions.equals(_existingAuthSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "auth_sessions(com.dereva.smart.data.local.entity.AuthSessionEntity).\n"
                  + " Expected:\n" + _infoAuthSessions + "\n"
                  + " Found:\n" + _existingAuthSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(12);
        _columnsQuestions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("textEn", new TableInfo.Column("textEn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("textSw", new TableInfo.Column("textSw", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("optionsJson", new TableInfo.Column("optionsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("correctOptionIndex", new TableInfo.Column("correctOptionIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("explanationEn", new TableInfo.Column("explanationEn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("explanationSw", new TableInfo.Column("explanationSw", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("curriculumTopicId", new TableInfo.Column("curriculumTopicId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("difficultyLevel", new TableInfo.Column("difficultyLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("licenseCategoriesJson", new TableInfo.Column("licenseCategoriesJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("isCommonCore", new TableInfo.Column("isCommonCore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.dereva.smart.data.local.entity.QuestionEntity).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsMockTests = new HashMap<String, TableInfo.Column>(10);
        _columnsMockTests.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("questionIdsJson", new TableInfo.Column("questionIdsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("userAnswersJson", new TableInfo.Column("userAnswersJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMockTests.put("licenseCategory", new TableInfo.Column("licenseCategory", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMockTests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMockTests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMockTests = new TableInfo("mock_tests", _columnsMockTests, _foreignKeysMockTests, _indicesMockTests);
        final TableInfo _existingMockTests = TableInfo.read(db, "mock_tests");
        if (!_infoMockTests.equals(_existingMockTests)) {
          return new RoomOpenHelper.ValidationResult(false, "mock_tests(com.dereva.smart.data.local.entity.MockTestEntity).\n"
                  + " Expected:\n" + _infoMockTests + "\n"
                  + " Found:\n" + _existingMockTests);
        }
        final HashMap<String, TableInfo.Column> _columnsTestResults = new HashMap<String, TableInfo.Column>(13);
        _columnsTestResults.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("testId", new TableInfo.Column("testId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("totalQuestions", new TableInfo.Column("totalQuestions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("correctAnswers", new TableInfo.Column("correctAnswers", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("incorrectAnswers", new TableInfo.Column("incorrectAnswers", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("unanswered", new TableInfo.Column("unanswered", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("scorePercentage", new TableInfo.Column("scorePercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("passed", new TableInfo.Column("passed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("topicScoresJson", new TableInfo.Column("topicScoresJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("topicTotalsJson", new TableInfo.Column("topicTotalsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTestResults.put("timeTakenSeconds", new TableInfo.Column("timeTakenSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTestResults = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTestResults = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTestResults = new TableInfo("test_results", _columnsTestResults, _foreignKeysTestResults, _indicesTestResults);
        final TableInfo _existingTestResults = TableInfo.read(db, "test_results");
        if (!_infoTestResults.equals(_existingTestResults)) {
          return new RoomOpenHelper.ValidationResult(false, "test_results(com.dereva.smart.data.local.entity.TestResultEntity).\n"
                  + " Expected:\n" + _infoTestResults + "\n"
                  + " Found:\n" + _existingTestResults);
        }
        final HashMap<String, TableInfo.Column> _columnsStudySessions = new HashMap<String, TableInfo.Column>(7);
        _columnsStudySessions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("lessonId", new TableInfo.Column("lessonId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("endTime", new TableInfo.Column("endTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStudySessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStudySessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStudySessions = new TableInfo("study_sessions", _columnsStudySessions, _foreignKeysStudySessions, _indicesStudySessions);
        final TableInfo _existingStudySessions = TableInfo.read(db, "study_sessions");
        if (!_infoStudySessions.equals(_existingStudySessions)) {
          return new RoomOpenHelper.ValidationResult(false, "study_sessions(com.dereva.smart.data.local.entity.StudySessionEntity).\n"
                  + " Expected:\n" + _infoStudySessions + "\n"
                  + " Found:\n" + _existingStudySessions);
        }
        final HashMap<String, TableInfo.Column> _columnsAchievements = new HashMap<String, TableInfo.Column>(8);
        _columnsAchievements.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("badgeType", new TableInfo.Column("badgeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("earnedAt", new TableInfo.Column("earnedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("titleEn", new TableInfo.Column("titleEn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("titleSw", new TableInfo.Column("titleSw", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("descriptionEn", new TableInfo.Column("descriptionEn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("descriptionSw", new TableInfo.Column("descriptionSw", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAchievements = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAchievements = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAchievements = new TableInfo("achievements", _columnsAchievements, _foreignKeysAchievements, _indicesAchievements);
        final TableInfo _existingAchievements = TableInfo.read(db, "achievements");
        if (!_infoAchievements.equals(_existingAchievements)) {
          return new RoomOpenHelper.ValidationResult(false, "achievements(com.dereva.smart.data.local.entity.AchievementEntity).\n"
                  + " Expected:\n" + _infoAchievements + "\n"
                  + " Found:\n" + _existingAchievements);
        }
        final HashMap<String, TableInfo.Column> _columnsTutorCache = new HashMap<String, TableInfo.Column>(6);
        _columnsTutorCache.put("questionHash", new TableInfo.Column("questionHash", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorCache.put("question", new TableInfo.Column("question", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorCache.put("answer", new TableInfo.Column("answer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorCache.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorCache.put("recommendationsJson", new TableInfo.Column("recommendationsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorCache.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTutorCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTutorCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTutorCache = new TableInfo("tutor_cache", _columnsTutorCache, _foreignKeysTutorCache, _indicesTutorCache);
        final TableInfo _existingTutorCache = TableInfo.read(db, "tutor_cache");
        if (!_infoTutorCache.equals(_existingTutorCache)) {
          return new RoomOpenHelper.ValidationResult(false, "tutor_cache(com.dereva.smart.data.local.entity.TutorCacheEntity).\n"
                  + " Expected:\n" + _infoTutorCache + "\n"
                  + " Found:\n" + _existingTutorCache);
        }
        final HashMap<String, TableInfo.Column> _columnsTutorConversations = new HashMap<String, TableInfo.Column>(7);
        _columnsTutorConversations.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorConversations.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorConversations.put("question", new TableInfo.Column("question", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorConversations.put("response", new TableInfo.Column("response", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorConversations.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorConversations.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTutorConversations.put("isCached", new TableInfo.Column("isCached", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTutorConversations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTutorConversations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTutorConversations = new TableInfo("tutor_conversations", _columnsTutorConversations, _foreignKeysTutorConversations, _indicesTutorConversations);
        final TableInfo _existingTutorConversations = TableInfo.read(db, "tutor_conversations");
        if (!_infoTutorConversations.equals(_existingTutorConversations)) {
          return new RoomOpenHelper.ValidationResult(false, "tutor_conversations(com.dereva.smart.data.local.entity.TutorEntity).\n"
                  + " Expected:\n" + _infoTutorConversations + "\n"
                  + " Found:\n" + _existingTutorConversations);
        }
        final HashMap<String, TableInfo.Column> _columnsDrivingSchools = new HashMap<String, TableInfo.Column>(10);
        _columnsDrivingSchools.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("code", new TableInfo.Column("code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("isVerified", new TableInfo.Column("isVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("totalStudents", new TableInfo.Column("totalStudents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("averagePassRate", new TableInfo.Column("averagePassRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDrivingSchools.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDrivingSchools = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDrivingSchools = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDrivingSchools = new TableInfo("driving_schools", _columnsDrivingSchools, _foreignKeysDrivingSchools, _indicesDrivingSchools);
        final TableInfo _existingDrivingSchools = TableInfo.read(db, "driving_schools");
        if (!_infoDrivingSchools.equals(_existingDrivingSchools)) {
          return new RoomOpenHelper.ValidationResult(false, "driving_schools(com.dereva.smart.data.local.entity.DrivingSchoolEntity).\n"
                  + " Expected:\n" + _infoDrivingSchools + "\n"
                  + " Found:\n" + _existingDrivingSchools);
        }
        final HashMap<String, TableInfo.Column> _columnsModuleSchedules = new HashMap<String, TableInfo.Column>(8);
        _columnsModuleSchedules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("schoolId", new TableInfo.Column("schoolId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("moduleId", new TableInfo.Column("moduleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("moduleName", new TableInfo.Column("moduleName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("unlockDate", new TableInfo.Column("unlockDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("dueDate", new TableInfo.Column("dueDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("isUnlocked", new TableInfo.Column("isUnlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleSchedules.put("order", new TableInfo.Column("order", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysModuleSchedules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesModuleSchedules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoModuleSchedules = new TableInfo("module_schedules", _columnsModuleSchedules, _foreignKeysModuleSchedules, _indicesModuleSchedules);
        final TableInfo _existingModuleSchedules = TableInfo.read(db, "module_schedules");
        if (!_infoModuleSchedules.equals(_existingModuleSchedules)) {
          return new RoomOpenHelper.ValidationResult(false, "module_schedules(com.dereva.smart.data.local.entity.ModuleScheduleEntity).\n"
                  + " Expected:\n" + _infoModuleSchedules + "\n"
                  + " Found:\n" + _existingModuleSchedules);
        }
        final HashMap<String, TableInfo.Column> _columnsProgressReports = new HashMap<String, TableInfo.Column>(10);
        _columnsProgressReports.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("schoolId", new TableInfo.Column("schoolId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("reportDate", new TableInfo.Column("reportDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("completedModules", new TableInfo.Column("completedModules", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("totalModules", new TableInfo.Column("totalModules", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("averageTestScore", new TableInfo.Column("averageTestScore", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("totalStudyTime", new TableInfo.Column("totalStudyTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("currentStreak", new TableInfo.Column("currentStreak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProgressReports.put("lastActivityDate", new TableInfo.Column("lastActivityDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProgressReports = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProgressReports = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProgressReports = new TableInfo("progress_reports", _columnsProgressReports, _foreignKeysProgressReports, _indicesProgressReports);
        final TableInfo _existingProgressReports = TableInfo.read(db, "progress_reports");
        if (!_infoProgressReports.equals(_existingProgressReports)) {
          return new RoomOpenHelper.ValidationResult(false, "progress_reports(com.dereva.smart.data.local.entity.ProgressReportEntity).\n"
                  + " Expected:\n" + _infoProgressReports + "\n"
                  + " Found:\n" + _existingProgressReports);
        }
        final HashMap<String, TableInfo.Column> _columnsSchoolLinkings = new HashMap<String, TableInfo.Column>(6);
        _columnsSchoolLinkings.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolLinkings.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolLinkings.put("schoolId", new TableInfo.Column("schoolId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolLinkings.put("linkedAt", new TableInfo.Column("linkedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolLinkings.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolLinkings.put("progressSharingEnabled", new TableInfo.Column("progressSharingEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSchoolLinkings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSchoolLinkings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSchoolLinkings = new TableInfo("school_linkings", _columnsSchoolLinkings, _foreignKeysSchoolLinkings, _indicesSchoolLinkings);
        final TableInfo _existingSchoolLinkings = TableInfo.read(db, "school_linkings");
        if (!_infoSchoolLinkings.equals(_existingSchoolLinkings)) {
          return new RoomOpenHelper.ValidationResult(false, "school_linkings(com.dereva.smart.data.local.entity.SchoolLinkingEntity).\n"
                  + " Expected:\n" + _infoSchoolLinkings + "\n"
                  + " Found:\n" + _existingSchoolLinkings);
        }
        final HashMap<String, TableInfo.Column> _columnsPaymentRequests = new HashMap<String, TableInfo.Column>(10);
        _columnsPaymentRequests.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("subscriptionTier", new TableInfo.Column("subscriptionTier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("promoCode", new TableInfo.Column("promoCode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("referralCode", new TableInfo.Column("referralCode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("schoolId", new TableInfo.Column("schoolId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRequests.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPaymentRequests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPaymentRequests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPaymentRequests = new TableInfo("payment_requests", _columnsPaymentRequests, _foreignKeysPaymentRequests, _indicesPaymentRequests);
        final TableInfo _existingPaymentRequests = TableInfo.read(db, "payment_requests");
        if (!_infoPaymentRequests.equals(_existingPaymentRequests)) {
          return new RoomOpenHelper.ValidationResult(false, "payment_requests(com.dereva.smart.data.local.entity.PaymentRequestEntity).\n"
                  + " Expected:\n" + _infoPaymentRequests + "\n"
                  + " Found:\n" + _existingPaymentRequests);
        }
        final HashMap<String, TableInfo.Column> _columnsPaymentResults = new HashMap<String, TableInfo.Column>(10);
        _columnsPaymentResults.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("paymentRequestId", new TableInfo.Column("paymentRequestId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("mpesaReceiptNumber", new TableInfo.Column("mpesaReceiptNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("transactionId", new TableInfo.Column("transactionId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentResults.put("metadata", new TableInfo.Column("metadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPaymentResults = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPaymentResults = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPaymentResults = new TableInfo("payment_results", _columnsPaymentResults, _foreignKeysPaymentResults, _indicesPaymentResults);
        final TableInfo _existingPaymentResults = TableInfo.read(db, "payment_results");
        if (!_infoPaymentResults.equals(_existingPaymentResults)) {
          return new RoomOpenHelper.ValidationResult(false, "payment_results(com.dereva.smart.data.local.entity.PaymentResultEntity).\n"
                  + " Expected:\n" + _infoPaymentResults + "\n"
                  + " Found:\n" + _existingPaymentResults);
        }
        final HashMap<String, TableInfo.Column> _columnsUserSubscriptions = new HashMap<String, TableInfo.Column>(8);
        _columnsUserSubscriptions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("tier", new TableInfo.Column("tier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("endDate", new TableInfo.Column("endDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("autoRenew", new TableInfo.Column("autoRenew", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserSubscriptions.put("paymentResultId", new TableInfo.Column("paymentResultId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserSubscriptions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserSubscriptions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserSubscriptions = new TableInfo("user_subscriptions", _columnsUserSubscriptions, _foreignKeysUserSubscriptions, _indicesUserSubscriptions);
        final TableInfo _existingUserSubscriptions = TableInfo.read(db, "user_subscriptions");
        if (!_infoUserSubscriptions.equals(_existingUserSubscriptions)) {
          return new RoomOpenHelper.ValidationResult(false, "user_subscriptions(com.dereva.smart.data.local.entity.UserSubscriptionEntity).\n"
                  + " Expected:\n" + _infoUserSubscriptions + "\n"
                  + " Found:\n" + _existingUserSubscriptions);
        }
        final HashMap<String, TableInfo.Column> _columnsPromoCodes = new HashMap<String, TableInfo.Column>(7);
        _columnsPromoCodes.put("code", new TableInfo.Column("code", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPromoCodes.put("discountPercentage", new TableInfo.Column("discountPercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPromoCodes.put("discountAmount", new TableInfo.Column("discountAmount", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPromoCodes.put("validUntil", new TableInfo.Column("validUntil", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPromoCodes.put("maxUses", new TableInfo.Column("maxUses", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPromoCodes.put("currentUses", new TableInfo.Column("currentUses", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPromoCodes.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPromoCodes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPromoCodes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPromoCodes = new TableInfo("promo_codes", _columnsPromoCodes, _foreignKeysPromoCodes, _indicesPromoCodes);
        final TableInfo _existingPromoCodes = TableInfo.read(db, "promo_codes");
        if (!_infoPromoCodes.equals(_existingPromoCodes)) {
          return new RoomOpenHelper.ValidationResult(false, "promo_codes(com.dereva.smart.data.local.entity.PromoCodeEntity).\n"
                  + " Expected:\n" + _infoPromoCodes + "\n"
                  + " Found:\n" + _existingPromoCodes);
        }
        final HashMap<String, TableInfo.Column> _columnsReferralCodes = new HashMap<String, TableInfo.Column>(6);
        _columnsReferralCodes.put("code", new TableInfo.Column("code", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferralCodes.put("referrerId", new TableInfo.Column("referrerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferralCodes.put("discountPercentage", new TableInfo.Column("discountPercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferralCodes.put("referrerBonusPercentage", new TableInfo.Column("referrerBonusPercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferralCodes.put("usageCount", new TableInfo.Column("usageCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReferralCodes.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReferralCodes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReferralCodes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReferralCodes = new TableInfo("referral_codes", _columnsReferralCodes, _foreignKeysReferralCodes, _indicesReferralCodes);
        final TableInfo _existingReferralCodes = TableInfo.read(db, "referral_codes");
        if (!_infoReferralCodes.equals(_existingReferralCodes)) {
          return new RoomOpenHelper.ValidationResult(false, "referral_codes(com.dereva.smart.data.local.entity.ReferralCodeEntity).\n"
                  + " Expected:\n" + _infoReferralCodes + "\n"
                  + " Found:\n" + _existingReferralCodes);
        }
        final HashMap<String, TableInfo.Column> _columnsSchoolCommissions = new HashMap<String, TableInfo.Column>(7);
        _columnsSchoolCommissions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolCommissions.put("schoolId", new TableInfo.Column("schoolId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolCommissions.put("paymentId", new TableInfo.Column("paymentId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolCommissions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolCommissions.put("percentage", new TableInfo.Column("percentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolCommissions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSchoolCommissions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSchoolCommissions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSchoolCommissions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSchoolCommissions = new TableInfo("school_commissions", _columnsSchoolCommissions, _foreignKeysSchoolCommissions, _indicesSchoolCommissions);
        final TableInfo _existingSchoolCommissions = TableInfo.read(db, "school_commissions");
        if (!_infoSchoolCommissions.equals(_existingSchoolCommissions)) {
          return new RoomOpenHelper.ValidationResult(false, "school_commissions(com.dereva.smart.data.local.entity.SchoolCommissionEntity).\n"
                  + " Expected:\n" + _infoSchoolCommissions + "\n"
                  + " Found:\n" + _existingSchoolCommissions);
        }
        final HashMap<String, TableInfo.Column> _columnsModules = new HashMap<String, TableInfo.Column>(15);
        _columnsModules.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("licenseCategory", new TableInfo.Column("licenseCategory", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("thumbnailUrl", new TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("estimatedDuration", new TableInfo.Column("estimatedDuration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("lessonCount", new TableInfo.Column("lessonCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("downloadSize", new TableInfo.Column("downloadSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("completionPercentage", new TableInfo.Column("completionPercentage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("requiresSubscription", new TableInfo.Column("requiresSubscription", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModules.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysModules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesModules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoModules = new TableInfo("modules", _columnsModules, _foreignKeysModules, _indicesModules);
        final TableInfo _existingModules = TableInfo.read(db, "modules");
        if (!_infoModules.equals(_existingModules)) {
          return new RoomOpenHelper.ValidationResult(false, "modules(com.dereva.smart.data.local.entity.ModuleEntity).\n"
                  + " Expected:\n" + _infoModules + "\n"
                  + " Found:\n" + _existingModules);
        }
        final HashMap<String, TableInfo.Column> _columnsLessons = new HashMap<String, TableInfo.Column>(14);
        _columnsLessons.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("moduleId", new TableInfo.Column("moduleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("contentType", new TableInfo.Column("contentType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("contentUrl", new TableInfo.Column("contentUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("contentText", new TableInfo.Column("contentText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("requiresSubscription", new TableInfo.Column("requiresSubscription", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("lastAccessedAt", new TableInfo.Column("lastAccessedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessons.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLessons = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLessons = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLessons = new TableInfo("lessons", _columnsLessons, _foreignKeysLessons, _indicesLessons);
        final TableInfo _existingLessons = TableInfo.read(db, "lessons");
        if (!_infoLessons.equals(_existingLessons)) {
          return new RoomOpenHelper.ValidationResult(false, "lessons(com.dereva.smart.data.local.entity.LessonEntity).\n"
                  + " Expected:\n" + _infoLessons + "\n"
                  + " Found:\n" + _existingLessons);
        }
        final HashMap<String, TableInfo.Column> _columnsMediaAssets = new HashMap<String, TableInfo.Column>(13);
        _columnsMediaAssets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("lessonId", new TableInfo.Column("lessonId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("url", new TableInfo.Column("url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("localPath", new TableInfo.Column("localPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("fileSize", new TableInfo.Column("fileSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("duration", new TableInfo.Column("duration", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("thumbnailUrl", new TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("downloadProgress", new TableInfo.Column("downloadProgress", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMediaAssets.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMediaAssets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMediaAssets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMediaAssets = new TableInfo("media_assets", _columnsMediaAssets, _foreignKeysMediaAssets, _indicesMediaAssets);
        final TableInfo _existingMediaAssets = TableInfo.read(db, "media_assets");
        if (!_infoMediaAssets.equals(_existingMediaAssets)) {
          return new RoomOpenHelper.ValidationResult(false, "media_assets(com.dereva.smart.data.local.entity.MediaAssetEntity).\n"
                  + " Expected:\n" + _infoMediaAssets + "\n"
                  + " Found:\n" + _existingMediaAssets);
        }
        final HashMap<String, TableInfo.Column> _columnsCurriculumTopics = new HashMap<String, TableInfo.Column>(8);
        _columnsCurriculumTopics.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("moduleId", new TableInfo.Column("moduleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("keyPoints", new TableInfo.Column("keyPoints", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("relatedLessons", new TableInfo.Column("relatedLessons", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCurriculumTopics.put("relatedQuestions", new TableInfo.Column("relatedQuestions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCurriculumTopics = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCurriculumTopics = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCurriculumTopics = new TableInfo("curriculum_topics", _columnsCurriculumTopics, _foreignKeysCurriculumTopics, _indicesCurriculumTopics);
        final TableInfo _existingCurriculumTopics = TableInfo.read(db, "curriculum_topics");
        if (!_infoCurriculumTopics.equals(_existingCurriculumTopics)) {
          return new RoomOpenHelper.ValidationResult(false, "curriculum_topics(com.dereva.smart.data.local.entity.CurriculumTopicEntity).\n"
                  + " Expected:\n" + _infoCurriculumTopics + "\n"
                  + " Found:\n" + _existingCurriculumTopics);
        }
        final HashMap<String, TableInfo.Column> _columnsLessonProgress = new HashMap<String, TableInfo.Column>(11);
        _columnsLessonProgress.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("lessonId", new TableInfo.Column("lessonId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("moduleId", new TableInfo.Column("moduleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("completionPercentage", new TableInfo.Column("completionPercentage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("timeSpent", new TableInfo.Column("timeSpent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("lastPosition", new TableInfo.Column("lastPosition", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLessonProgress.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLessonProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLessonProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLessonProgress = new TableInfo("lesson_progress", _columnsLessonProgress, _foreignKeysLessonProgress, _indicesLessonProgress);
        final TableInfo _existingLessonProgress = TableInfo.read(db, "lesson_progress");
        if (!_infoLessonProgress.equals(_existingLessonProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "lesson_progress(com.dereva.smart.data.local.entity.LessonProgressEntity).\n"
                  + " Expected:\n" + _infoLessonProgress + "\n"
                  + " Found:\n" + _existingLessonProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsModuleDownloads = new HashMap<String, TableInfo.Column>(10);
        _columnsModuleDownloads.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("moduleId", new TableInfo.Column("moduleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("progress", new TableInfo.Column("progress", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("downloadedSize", new TableInfo.Column("downloadedSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("totalSize", new TableInfo.Column("totalSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsModuleDownloads.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysModuleDownloads = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesModuleDownloads = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoModuleDownloads = new TableInfo("module_downloads", _columnsModuleDownloads, _foreignKeysModuleDownloads, _indicesModuleDownloads);
        final TableInfo _existingModuleDownloads = TableInfo.read(db, "module_downloads");
        if (!_infoModuleDownloads.equals(_existingModuleDownloads)) {
          return new RoomOpenHelper.ValidationResult(false, "module_downloads(com.dereva.smart.data.local.entity.ModuleDownloadEntity).\n"
                  + " Expected:\n" + _infoModuleDownloads + "\n"
                  + " Found:\n" + _existingModuleDownloads);
        }
        final HashMap<String, TableInfo.Column> _columnsContentSyncMetadata = new HashMap<String, TableInfo.Column>(8);
        _columnsContentSyncMetadata.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("entityType", new TableInfo.Column("entityType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("lastSyncedAt", new TableInfo.Column("lastSyncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("localVersion", new TableInfo.Column("localVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("serverVersion", new TableInfo.Column("serverVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("needsSync", new TableInfo.Column("needsSync", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContentSyncMetadata.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContentSyncMetadata = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContentSyncMetadata = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContentSyncMetadata = new TableInfo("content_sync_metadata", _columnsContentSyncMetadata, _foreignKeysContentSyncMetadata, _indicesContentSyncMetadata);
        final TableInfo _existingContentSyncMetadata = TableInfo.read(db, "content_sync_metadata");
        if (!_infoContentSyncMetadata.equals(_existingContentSyncMetadata)) {
          return new RoomOpenHelper.ValidationResult(false, "content_sync_metadata(com.dereva.smart.data.local.entity.ContentSyncMetadataEntity).\n"
                  + " Expected:\n" + _infoContentSyncMetadata + "\n"
                  + " Found:\n" + _existingContentSyncMetadata);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "57653fe216548987b87e42b9ff8b1101", "cc8bc4621cf2d63edc8e31cf0535be5e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","verification_codes","auth_sessions","questions","mock_tests","test_results","study_sessions","achievements","tutor_cache","tutor_conversations","driving_schools","module_schedules","progress_reports","school_linkings","payment_requests","payment_results","user_subscriptions","promo_codes","referral_codes","school_commissions","modules","lessons","media_assets","curriculum_topics","lesson_progress","module_downloads","content_sync_metadata");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `verification_codes`");
      _db.execSQL("DELETE FROM `auth_sessions`");
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `mock_tests`");
      _db.execSQL("DELETE FROM `test_results`");
      _db.execSQL("DELETE FROM `study_sessions`");
      _db.execSQL("DELETE FROM `achievements`");
      _db.execSQL("DELETE FROM `tutor_cache`");
      _db.execSQL("DELETE FROM `tutor_conversations`");
      _db.execSQL("DELETE FROM `driving_schools`");
      _db.execSQL("DELETE FROM `module_schedules`");
      _db.execSQL("DELETE FROM `progress_reports`");
      _db.execSQL("DELETE FROM `school_linkings`");
      _db.execSQL("DELETE FROM `payment_requests`");
      _db.execSQL("DELETE FROM `payment_results`");
      _db.execSQL("DELETE FROM `user_subscriptions`");
      _db.execSQL("DELETE FROM `promo_codes`");
      _db.execSQL("DELETE FROM `referral_codes`");
      _db.execSQL("DELETE FROM `school_commissions`");
      _db.execSQL("DELETE FROM `modules`");
      _db.execSQL("DELETE FROM `lessons`");
      _db.execSQL("DELETE FROM `media_assets`");
      _db.execSQL("DELETE FROM `curriculum_topics`");
      _db.execSQL("DELETE FROM `lesson_progress`");
      _db.execSQL("DELETE FROM `module_downloads`");
      _db.execSQL("DELETE FROM `content_sync_metadata`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AuthDao.class, AuthDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QuestionDao.class, QuestionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MockTestDao.class, MockTestDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProgressDao.class, ProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TutorDao.class, TutorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SchoolDao.class, SchoolDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PaymentDao.class, PaymentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContentDao.class, ContentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AuthDao authDao() {
    if (_authDao != null) {
      return _authDao;
    } else {
      synchronized(this) {
        if(_authDao == null) {
          _authDao = new AuthDao_Impl(this);
        }
        return _authDao;
      }
    }
  }

  @Override
  public QuestionDao questionDao() {
    if (_questionDao != null) {
      return _questionDao;
    } else {
      synchronized(this) {
        if(_questionDao == null) {
          _questionDao = new QuestionDao_Impl(this);
        }
        return _questionDao;
      }
    }
  }

  @Override
  public MockTestDao mockTestDao() {
    if (_mockTestDao != null) {
      return _mockTestDao;
    } else {
      synchronized(this) {
        if(_mockTestDao == null) {
          _mockTestDao = new MockTestDao_Impl(this);
        }
        return _mockTestDao;
      }
    }
  }

  @Override
  public ProgressDao progressDao() {
    if (_progressDao != null) {
      return _progressDao;
    } else {
      synchronized(this) {
        if(_progressDao == null) {
          _progressDao = new ProgressDao_Impl(this);
        }
        return _progressDao;
      }
    }
  }

  @Override
  public TutorDao tutorDao() {
    if (_tutorDao != null) {
      return _tutorDao;
    } else {
      synchronized(this) {
        if(_tutorDao == null) {
          _tutorDao = new TutorDao_Impl(this);
        }
        return _tutorDao;
      }
    }
  }

  @Override
  public SchoolDao schoolDao() {
    if (_schoolDao != null) {
      return _schoolDao;
    } else {
      synchronized(this) {
        if(_schoolDao == null) {
          _schoolDao = new SchoolDao_Impl(this);
        }
        return _schoolDao;
      }
    }
  }

  @Override
  public PaymentDao paymentDao() {
    if (_paymentDao != null) {
      return _paymentDao;
    } else {
      synchronized(this) {
        if(_paymentDao == null) {
          _paymentDao = new PaymentDao_Impl(this);
        }
        return _paymentDao;
      }
    }
  }

  @Override
  public ContentDao contentDao() {
    if (_contentDao != null) {
      return _contentDao;
    } else {
      synchronized(this) {
        if(_contentDao == null) {
          _contentDao = new ContentDao_Impl(this);
        }
        return _contentDao;
      }
    }
  }
}
