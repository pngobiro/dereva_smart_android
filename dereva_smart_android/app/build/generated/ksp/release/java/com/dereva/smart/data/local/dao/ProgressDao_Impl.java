package com.dereva.smart.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dereva.smart.data.local.entity.AchievementEntity;
import com.dereva.smart.data.local.entity.StudySessionEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ProgressDao_Impl implements ProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StudySessionEntity> __insertionAdapterOfStudySessionEntity;

  private final EntityInsertionAdapter<AchievementEntity> __insertionAdapterOfAchievementEntity;

  public ProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStudySessionEntity = new EntityInsertionAdapter<StudySessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `study_sessions` (`id`,`userId`,`lessonId`,`startTime`,`endTime`,`durationMinutes`,`completed`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StudySessionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getLessonId());
        statement.bindLong(4, entity.getStartTime());
        statement.bindLong(5, entity.getEndTime());
        statement.bindLong(6, entity.getDurationMinutes());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__insertionAdapterOfAchievementEntity = new EntityInsertionAdapter<AchievementEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `achievements` (`id`,`userId`,`badgeType`,`earnedAt`,`titleEn`,`titleSw`,`descriptionEn`,`descriptionSw`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AchievementEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getBadgeType());
        statement.bindLong(4, entity.getEarnedAt());
        statement.bindString(5, entity.getTitleEn());
        statement.bindString(6, entity.getTitleSw());
        statement.bindString(7, entity.getDescriptionEn());
        statement.bindString(8, entity.getDescriptionSw());
      }
    };
  }

  @Override
  public Object insertSession(final StudySessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStudySessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAchievement(final AchievementEntity achievement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAchievementEntity.insert(achievement);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserSessions(final String userId,
      final Continuation<? super List<StudySessionEntity>> $completion) {
    final String _sql = "SELECT * FROM study_sessions WHERE userId = ? ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StudySessionEntity>>() {
      @Override
      @NonNull
      public List<StudySessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfLessonId = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySessionEntity> _result = new ArrayList<StudySessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpLessonId;
            _tmpLessonId = _cursor.getString(_cursorIndexOfLessonId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySessionEntity(_tmpId,_tmpUserId,_tmpLessonId,_tmpStartTime,_tmpEndTime,_tmpDurationMinutes,_tmpCompleted);
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
  public Flow<List<StudySessionEntity>> getUserSessionsFlow(final String userId) {
    final String _sql = "SELECT * FROM study_sessions WHERE userId = ? ORDER BY startTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"study_sessions"}, new Callable<List<StudySessionEntity>>() {
      @Override
      @NonNull
      public List<StudySessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfLessonId = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final List<StudySessionEntity> _result = new ArrayList<StudySessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudySessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpLessonId;
            _tmpLessonId = _cursor.getString(_cursorIndexOfLessonId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            _item = new StudySessionEntity(_tmpId,_tmpUserId,_tmpLessonId,_tmpStartTime,_tmpEndTime,_tmpDurationMinutes,_tmpCompleted);
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
  public Object getTotalStudyTime(final String userId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(durationMinutes) FROM study_sessions WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
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
  public Object getCompletedLessonsCount(final String userId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(DISTINCT lessonId) FROM study_sessions WHERE userId = ? AND completed = 1";
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
  public Object getUserAchievements(final String userId,
      final Continuation<? super List<AchievementEntity>> $completion) {
    final String _sql = "SELECT * FROM achievements WHERE userId = ? ORDER BY earnedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfBadgeType = CursorUtil.getColumnIndexOrThrow(_cursor, "badgeType");
          final int _cursorIndexOfEarnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "earnedAt");
          final int _cursorIndexOfTitleEn = CursorUtil.getColumnIndexOrThrow(_cursor, "titleEn");
          final int _cursorIndexOfTitleSw = CursorUtil.getColumnIndexOrThrow(_cursor, "titleSw");
          final int _cursorIndexOfDescriptionEn = CursorUtil.getColumnIndexOrThrow(_cursor, "descriptionEn");
          final int _cursorIndexOfDescriptionSw = CursorUtil.getColumnIndexOrThrow(_cursor, "descriptionSw");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpBadgeType;
            _tmpBadgeType = _cursor.getString(_cursorIndexOfBadgeType);
            final long _tmpEarnedAt;
            _tmpEarnedAt = _cursor.getLong(_cursorIndexOfEarnedAt);
            final String _tmpTitleEn;
            _tmpTitleEn = _cursor.getString(_cursorIndexOfTitleEn);
            final String _tmpTitleSw;
            _tmpTitleSw = _cursor.getString(_cursorIndexOfTitleSw);
            final String _tmpDescriptionEn;
            _tmpDescriptionEn = _cursor.getString(_cursorIndexOfDescriptionEn);
            final String _tmpDescriptionSw;
            _tmpDescriptionSw = _cursor.getString(_cursorIndexOfDescriptionSw);
            _item = new AchievementEntity(_tmpId,_tmpUserId,_tmpBadgeType,_tmpEarnedAt,_tmpTitleEn,_tmpTitleSw,_tmpDescriptionEn,_tmpDescriptionSw);
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
  public Flow<List<AchievementEntity>> getUserAchievementsFlow(final String userId) {
    final String _sql = "SELECT * FROM achievements WHERE userId = ? ORDER BY earnedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfBadgeType = CursorUtil.getColumnIndexOrThrow(_cursor, "badgeType");
          final int _cursorIndexOfEarnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "earnedAt");
          final int _cursorIndexOfTitleEn = CursorUtil.getColumnIndexOrThrow(_cursor, "titleEn");
          final int _cursorIndexOfTitleSw = CursorUtil.getColumnIndexOrThrow(_cursor, "titleSw");
          final int _cursorIndexOfDescriptionEn = CursorUtil.getColumnIndexOrThrow(_cursor, "descriptionEn");
          final int _cursorIndexOfDescriptionSw = CursorUtil.getColumnIndexOrThrow(_cursor, "descriptionSw");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpBadgeType;
            _tmpBadgeType = _cursor.getString(_cursorIndexOfBadgeType);
            final long _tmpEarnedAt;
            _tmpEarnedAt = _cursor.getLong(_cursorIndexOfEarnedAt);
            final String _tmpTitleEn;
            _tmpTitleEn = _cursor.getString(_cursorIndexOfTitleEn);
            final String _tmpTitleSw;
            _tmpTitleSw = _cursor.getString(_cursorIndexOfTitleSw);
            final String _tmpDescriptionEn;
            _tmpDescriptionEn = _cursor.getString(_cursorIndexOfDescriptionEn);
            final String _tmpDescriptionSw;
            _tmpDescriptionSw = _cursor.getString(_cursorIndexOfDescriptionSw);
            _item = new AchievementEntity(_tmpId,_tmpUserId,_tmpBadgeType,_tmpEarnedAt,_tmpTitleEn,_tmpTitleSw,_tmpDescriptionEn,_tmpDescriptionSw);
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
  public Object hasAchievement(final String userId, final String badgeType,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM achievements WHERE userId = ? AND badgeType = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, badgeType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
