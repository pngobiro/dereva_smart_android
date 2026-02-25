package com.dereva.smart.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dereva.smart.data.local.entity.MockTestEntity;
import com.dereva.smart.data.local.entity.TestResultEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MockTestDao_Impl implements MockTestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MockTestEntity> __insertionAdapterOfMockTestEntity;

  private final EntityInsertionAdapter<TestResultEntity> __insertionAdapterOfTestResultEntity;

  private final EntityDeletionOrUpdateAdapter<MockTestEntity> __deletionAdapterOfMockTestEntity;

  private final EntityDeletionOrUpdateAdapter<MockTestEntity> __updateAdapterOfMockTestEntity;

  public MockTestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMockTestEntity = new EntityInsertionAdapter<MockTestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `mock_tests` (`id`,`userId`,`questionIdsJson`,`userAnswersJson`,`createdAt`,`startedAt`,`completedAt`,`status`,`durationMinutes`,`licenseCategory`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MockTestEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getQuestionIdsJson());
        statement.bindString(4, entity.getUserAnswersJson());
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getStartedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getStartedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCompletedAt());
        }
        statement.bindString(8, entity.getStatus());
        statement.bindLong(9, entity.getDurationMinutes());
        statement.bindString(10, entity.getLicenseCategory());
      }
    };
    this.__insertionAdapterOfTestResultEntity = new EntityInsertionAdapter<TestResultEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `test_results` (`id`,`testId`,`userId`,`totalQuestions`,`correctAnswers`,`incorrectAnswers`,`unanswered`,`scorePercentage`,`passed`,`topicScoresJson`,`topicTotalsJson`,`completedAt`,`timeTakenSeconds`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TestResultEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTestId());
        statement.bindString(3, entity.getUserId());
        statement.bindLong(4, entity.getTotalQuestions());
        statement.bindLong(5, entity.getCorrectAnswers());
        statement.bindLong(6, entity.getIncorrectAnswers());
        statement.bindLong(7, entity.getUnanswered());
        statement.bindDouble(8, entity.getScorePercentage());
        final int _tmp = entity.getPassed() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindString(10, entity.getTopicScoresJson());
        statement.bindString(11, entity.getTopicTotalsJson());
        statement.bindLong(12, entity.getCompletedAt());
        statement.bindLong(13, entity.getTimeTakenSeconds());
      }
    };
    this.__deletionAdapterOfMockTestEntity = new EntityDeletionOrUpdateAdapter<MockTestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `mock_tests` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MockTestEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfMockTestEntity = new EntityDeletionOrUpdateAdapter<MockTestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `mock_tests` SET `id` = ?,`userId` = ?,`questionIdsJson` = ?,`userAnswersJson` = ?,`createdAt` = ?,`startedAt` = ?,`completedAt` = ?,`status` = ?,`durationMinutes` = ?,`licenseCategory` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MockTestEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getQuestionIdsJson());
        statement.bindString(4, entity.getUserAnswersJson());
        statement.bindLong(5, entity.getCreatedAt());
        if (entity.getStartedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getStartedAt());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCompletedAt());
        }
        statement.bindString(8, entity.getStatus());
        statement.bindLong(9, entity.getDurationMinutes());
        statement.bindString(10, entity.getLicenseCategory());
        statement.bindString(11, entity.getId());
      }
    };
  }

  @Override
  public Object insertTest(final MockTestEntity test,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMockTestEntity.insert(test);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTestResult(final TestResultEntity result,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTestResultEntity.insert(result);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTest(final MockTestEntity test,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMockTestEntity.handle(test);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTest(final MockTestEntity test,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMockTestEntity.handle(test);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTestById(final String testId,
      final Continuation<? super MockTestEntity> $completion) {
    final String _sql = "SELECT * FROM mock_tests WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, testId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MockTestEntity>() {
      @Override
      @Nullable
      public MockTestEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfQuestionIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "questionIdsJson");
          final int _cursorIndexOfUserAnswersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "userAnswersJson");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final MockTestEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpQuestionIdsJson;
            _tmpQuestionIdsJson = _cursor.getString(_cursorIndexOfQuestionIdsJson);
            final String _tmpUserAnswersJson;
            _tmpUserAnswersJson = _cursor.getString(_cursorIndexOfUserAnswersJson);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            _result = new MockTestEntity(_tmpId,_tmpUserId,_tmpQuestionIdsJson,_tmpUserAnswersJson,_tmpCreatedAt,_tmpStartedAt,_tmpCompletedAt,_tmpStatus,_tmpDurationMinutes,_tmpLicenseCategory);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserTests(final String userId,
      final Continuation<? super List<MockTestEntity>> $completion) {
    final String _sql = "SELECT * FROM mock_tests WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MockTestEntity>>() {
      @Override
      @NonNull
      public List<MockTestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfQuestionIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "questionIdsJson");
          final int _cursorIndexOfUserAnswersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "userAnswersJson");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final List<MockTestEntity> _result = new ArrayList<MockTestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MockTestEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpQuestionIdsJson;
            _tmpQuestionIdsJson = _cursor.getString(_cursorIndexOfQuestionIdsJson);
            final String _tmpUserAnswersJson;
            _tmpUserAnswersJson = _cursor.getString(_cursorIndexOfUserAnswersJson);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            _item = new MockTestEntity(_tmpId,_tmpUserId,_tmpQuestionIdsJson,_tmpUserAnswersJson,_tmpCreatedAt,_tmpStartedAt,_tmpCompletedAt,_tmpStatus,_tmpDurationMinutes,_tmpLicenseCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MockTestEntity>> getUserTestsFlow(final String userId) {
    final String _sql = "SELECT * FROM mock_tests WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"mock_tests"}, new Callable<List<MockTestEntity>>() {
      @Override
      @NonNull
      public List<MockTestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfQuestionIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "questionIdsJson");
          final int _cursorIndexOfUserAnswersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "userAnswersJson");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final List<MockTestEntity> _result = new ArrayList<MockTestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MockTestEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpQuestionIdsJson;
            _tmpQuestionIdsJson = _cursor.getString(_cursorIndexOfQuestionIdsJson);
            final String _tmpUserAnswersJson;
            _tmpUserAnswersJson = _cursor.getString(_cursorIndexOfUserAnswersJson);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpStartedAt;
            if (_cursor.isNull(_cursorIndexOfStartedAt)) {
              _tmpStartedAt = null;
            } else {
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            _item = new MockTestEntity(_tmpId,_tmpUserId,_tmpQuestionIdsJson,_tmpUserAnswersJson,_tmpCreatedAt,_tmpStartedAt,_tmpCompletedAt,_tmpStatus,_tmpDurationMinutes,_tmpLicenseCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTestResult(final String testId,
      final Continuation<? super TestResultEntity> $completion) {
    final String _sql = "SELECT * FROM test_results WHERE testId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, testId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TestResultEntity>() {
      @Override
      @Nullable
      public TestResultEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTestId = CursorUtil.getColumnIndexOrThrow(_cursor, "testId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
          final int _cursorIndexOfIncorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAnswers");
          final int _cursorIndexOfUnanswered = CursorUtil.getColumnIndexOrThrow(_cursor, "unanswered");
          final int _cursorIndexOfScorePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercentage");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfTopicScoresJson = CursorUtil.getColumnIndexOrThrow(_cursor, "topicScoresJson");
          final int _cursorIndexOfTopicTotalsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "topicTotalsJson");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTimeTakenSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTakenSeconds");
          final TestResultEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTestId;
            _tmpTestId = _cursor.getString(_cursorIndexOfTestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpCorrectAnswers;
            _tmpCorrectAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
            final int _tmpIncorrectAnswers;
            _tmpIncorrectAnswers = _cursor.getInt(_cursorIndexOfIncorrectAnswers);
            final int _tmpUnanswered;
            _tmpUnanswered = _cursor.getInt(_cursorIndexOfUnanswered);
            final double _tmpScorePercentage;
            _tmpScorePercentage = _cursor.getDouble(_cursorIndexOfScorePercentage);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final String _tmpTopicScoresJson;
            _tmpTopicScoresJson = _cursor.getString(_cursorIndexOfTopicScoresJson);
            final String _tmpTopicTotalsJson;
            _tmpTopicTotalsJson = _cursor.getString(_cursorIndexOfTopicTotalsJson);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final int _tmpTimeTakenSeconds;
            _tmpTimeTakenSeconds = _cursor.getInt(_cursorIndexOfTimeTakenSeconds);
            _result = new TestResultEntity(_tmpId,_tmpTestId,_tmpUserId,_tmpTotalQuestions,_tmpCorrectAnswers,_tmpIncorrectAnswers,_tmpUnanswered,_tmpScorePercentage,_tmpPassed,_tmpTopicScoresJson,_tmpTopicTotalsJson,_tmpCompletedAt,_tmpTimeTakenSeconds);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserTestResults(final String userId,
      final Continuation<? super List<TestResultEntity>> $completion) {
    final String _sql = "SELECT * FROM test_results WHERE userId = ? ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TestResultEntity>>() {
      @Override
      @NonNull
      public List<TestResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTestId = CursorUtil.getColumnIndexOrThrow(_cursor, "testId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
          final int _cursorIndexOfIncorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAnswers");
          final int _cursorIndexOfUnanswered = CursorUtil.getColumnIndexOrThrow(_cursor, "unanswered");
          final int _cursorIndexOfScorePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercentage");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfTopicScoresJson = CursorUtil.getColumnIndexOrThrow(_cursor, "topicScoresJson");
          final int _cursorIndexOfTopicTotalsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "topicTotalsJson");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTimeTakenSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTakenSeconds");
          final List<TestResultEntity> _result = new ArrayList<TestResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestResultEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTestId;
            _tmpTestId = _cursor.getString(_cursorIndexOfTestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpCorrectAnswers;
            _tmpCorrectAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
            final int _tmpIncorrectAnswers;
            _tmpIncorrectAnswers = _cursor.getInt(_cursorIndexOfIncorrectAnswers);
            final int _tmpUnanswered;
            _tmpUnanswered = _cursor.getInt(_cursorIndexOfUnanswered);
            final double _tmpScorePercentage;
            _tmpScorePercentage = _cursor.getDouble(_cursorIndexOfScorePercentage);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final String _tmpTopicScoresJson;
            _tmpTopicScoresJson = _cursor.getString(_cursorIndexOfTopicScoresJson);
            final String _tmpTopicTotalsJson;
            _tmpTopicTotalsJson = _cursor.getString(_cursorIndexOfTopicTotalsJson);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final int _tmpTimeTakenSeconds;
            _tmpTimeTakenSeconds = _cursor.getInt(_cursorIndexOfTimeTakenSeconds);
            _item = new TestResultEntity(_tmpId,_tmpTestId,_tmpUserId,_tmpTotalQuestions,_tmpCorrectAnswers,_tmpIncorrectAnswers,_tmpUnanswered,_tmpScorePercentage,_tmpPassed,_tmpTopicScoresJson,_tmpTopicTotalsJson,_tmpCompletedAt,_tmpTimeTakenSeconds);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TestResultEntity>> getUserTestResultsFlow(final String userId) {
    final String _sql = "SELECT * FROM test_results WHERE userId = ? ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"test_results"}, new Callable<List<TestResultEntity>>() {
      @Override
      @NonNull
      public List<TestResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTestId = CursorUtil.getColumnIndexOrThrow(_cursor, "testId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
          final int _cursorIndexOfIncorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "incorrectAnswers");
          final int _cursorIndexOfUnanswered = CursorUtil.getColumnIndexOrThrow(_cursor, "unanswered");
          final int _cursorIndexOfScorePercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercentage");
          final int _cursorIndexOfPassed = CursorUtil.getColumnIndexOrThrow(_cursor, "passed");
          final int _cursorIndexOfTopicScoresJson = CursorUtil.getColumnIndexOrThrow(_cursor, "topicScoresJson");
          final int _cursorIndexOfTopicTotalsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "topicTotalsJson");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTimeTakenSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTakenSeconds");
          final List<TestResultEntity> _result = new ArrayList<TestResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TestResultEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTestId;
            _tmpTestId = _cursor.getString(_cursorIndexOfTestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpCorrectAnswers;
            _tmpCorrectAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
            final int _tmpIncorrectAnswers;
            _tmpIncorrectAnswers = _cursor.getInt(_cursorIndexOfIncorrectAnswers);
            final int _tmpUnanswered;
            _tmpUnanswered = _cursor.getInt(_cursorIndexOfUnanswered);
            final double _tmpScorePercentage;
            _tmpScorePercentage = _cursor.getDouble(_cursorIndexOfScorePercentage);
            final boolean _tmpPassed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPassed);
            _tmpPassed = _tmp != 0;
            final String _tmpTopicScoresJson;
            _tmpTopicScoresJson = _cursor.getString(_cursorIndexOfTopicScoresJson);
            final String _tmpTopicTotalsJson;
            _tmpTopicTotalsJson = _cursor.getString(_cursorIndexOfTopicTotalsJson);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final int _tmpTimeTakenSeconds;
            _tmpTimeTakenSeconds = _cursor.getInt(_cursorIndexOfTimeTakenSeconds);
            _item = new TestResultEntity(_tmpId,_tmpTestId,_tmpUserId,_tmpTotalQuestions,_tmpCorrectAnswers,_tmpIncorrectAnswers,_tmpUnanswered,_tmpScorePercentage,_tmpPassed,_tmpTopicScoresJson,_tmpTopicTotalsJson,_tmpCompletedAt,_tmpTimeTakenSeconds);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAverageScore(final String userId,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT AVG(scorePercentage) FROM test_results WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPassedTestsCount(final String userId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM test_results WHERE userId = ? AND passed = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalTestsCount(final String userId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM test_results WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
