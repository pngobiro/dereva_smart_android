package com.dereva.smart.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dereva.smart.data.local.entity.DrivingSchoolEntity;
import com.dereva.smart.data.local.entity.ModuleScheduleEntity;
import com.dereva.smart.data.local.entity.ProgressReportEntity;
import com.dereva.smart.data.local.entity.SchoolLinkingEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class SchoolDao_Impl implements SchoolDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DrivingSchoolEntity> __insertionAdapterOfDrivingSchoolEntity;

  private final EntityInsertionAdapter<SchoolLinkingEntity> __insertionAdapterOfSchoolLinkingEntity;

  private final EntityInsertionAdapter<ModuleScheduleEntity> __insertionAdapterOfModuleScheduleEntity;

  private final EntityInsertionAdapter<ProgressReportEntity> __insertionAdapterOfProgressReportEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllLinkings;

  private final SharedSQLiteStatement __preparedStmtOfUpdateProgressSharing;

  private final SharedSQLiteStatement __preparedStmtOfUnlockModule;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldReports;

  public SchoolDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDrivingSchoolEntity = new EntityInsertionAdapter<DrivingSchoolEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `driving_schools` (`id`,`name`,`code`,`location`,`phoneNumber`,`email`,`isVerified`,`totalStudents`,`averagePassRate`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DrivingSchoolEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCode());
        statement.bindString(4, entity.getLocation());
        statement.bindString(5, entity.getPhoneNumber());
        statement.bindString(6, entity.getEmail());
        final int _tmp = entity.isVerified() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getTotalStudents());
        statement.bindDouble(9, entity.getAveragePassRate());
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfSchoolLinkingEntity = new EntityInsertionAdapter<SchoolLinkingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `school_linkings` (`id`,`userId`,`schoolId`,`linkedAt`,`isActive`,`progressSharingEnabled`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SchoolLinkingEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getSchoolId());
        statement.bindLong(4, entity.getLinkedAt());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.getProgressSharingEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__insertionAdapterOfModuleScheduleEntity = new EntityInsertionAdapter<ModuleScheduleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `module_schedules` (`id`,`schoolId`,`moduleId`,`moduleName`,`unlockDate`,`dueDate`,`isUnlocked`,`order`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ModuleScheduleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getSchoolId());
        statement.bindString(3, entity.getModuleId());
        statement.bindString(4, entity.getModuleName());
        statement.bindLong(5, entity.getUnlockDate());
        if (entity.getDueDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDueDate());
        }
        final int _tmp = entity.isUnlocked() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getOrder());
      }
    };
    this.__insertionAdapterOfProgressReportEntity = new EntityInsertionAdapter<ProgressReportEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `progress_reports` (`id`,`userId`,`schoolId`,`reportDate`,`completedModules`,`totalModules`,`averageTestScore`,`totalStudyTime`,`currentStreak`,`lastActivityDate`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProgressReportEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getSchoolId());
        statement.bindLong(4, entity.getReportDate());
        statement.bindLong(5, entity.getCompletedModules());
        statement.bindLong(6, entity.getTotalModules());
        statement.bindDouble(7, entity.getAverageTestScore());
        statement.bindLong(8, entity.getTotalStudyTime());
        statement.bindLong(9, entity.getCurrentStreak());
        statement.bindLong(10, entity.getLastActivityDate());
      }
    };
    this.__preparedStmtOfDeactivateAllLinkings = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE school_linkings SET isActive = 0 WHERE userId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateProgressSharing = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE school_linkings SET progressSharingEnabled = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUnlockModule = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE module_schedules SET isUnlocked = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldReports = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM progress_reports WHERE reportDate < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSchool(final DrivingSchoolEntity school,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDrivingSchoolEntity.insert(school);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSchools(final List<DrivingSchoolEntity> schools,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDrivingSchoolEntity.insert(schools);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSchoolLinking(final SchoolLinkingEntity linking,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSchoolLinkingEntity.insert(linking);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSchedule(final ModuleScheduleEntity schedule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfModuleScheduleEntity.insert(schedule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSchedules(final List<ModuleScheduleEntity> schedules,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfModuleScheduleEntity.insert(schedules);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertProgressReport(final ProgressReportEntity report,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProgressReportEntity.insert(report);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateAllLinkings(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllLinkings.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeactivateAllLinkings.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProgressSharing(final String linkingId, final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateProgressSharing.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, linkingId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateProgressSharing.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object unlockModule(final String scheduleId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUnlockModule.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, scheduleId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUnlockModule.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldReports(final long cutoffDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldReports.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffDate);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldReports.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getSchoolById(final String schoolId,
      final Continuation<? super DrivingSchoolEntity> $completion) {
    final String _sql = "SELECT * FROM driving_schools WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, schoolId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DrivingSchoolEntity>() {
      @Override
      @Nullable
      public DrivingSchoolEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfTotalStudents = CursorUtil.getColumnIndexOrThrow(_cursor, "totalStudents");
          final int _cursorIndexOfAveragePassRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averagePassRate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final DrivingSchoolEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final int _tmpTotalStudents;
            _tmpTotalStudents = _cursor.getInt(_cursorIndexOfTotalStudents);
            final double _tmpAveragePassRate;
            _tmpAveragePassRate = _cursor.getDouble(_cursorIndexOfAveragePassRate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new DrivingSchoolEntity(_tmpId,_tmpName,_tmpCode,_tmpLocation,_tmpPhoneNumber,_tmpEmail,_tmpIsVerified,_tmpTotalStudents,_tmpAveragePassRate,_tmpCreatedAt);
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
  public Object getSchoolByCode(final String code,
      final Continuation<? super DrivingSchoolEntity> $completion) {
    final String _sql = "SELECT * FROM driving_schools WHERE code = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DrivingSchoolEntity>() {
      @Override
      @Nullable
      public DrivingSchoolEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfTotalStudents = CursorUtil.getColumnIndexOrThrow(_cursor, "totalStudents");
          final int _cursorIndexOfAveragePassRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averagePassRate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final DrivingSchoolEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final int _tmpTotalStudents;
            _tmpTotalStudents = _cursor.getInt(_cursorIndexOfTotalStudents);
            final double _tmpAveragePassRate;
            _tmpAveragePassRate = _cursor.getDouble(_cursorIndexOfAveragePassRate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new DrivingSchoolEntity(_tmpId,_tmpName,_tmpCode,_tmpLocation,_tmpPhoneNumber,_tmpEmail,_tmpIsVerified,_tmpTotalStudents,_tmpAveragePassRate,_tmpCreatedAt);
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
  public Object getVerifiedSchools(
      final Continuation<? super List<DrivingSchoolEntity>> $completion) {
    final String _sql = "SELECT * FROM driving_schools WHERE isVerified = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DrivingSchoolEntity>>() {
      @Override
      @NonNull
      public List<DrivingSchoolEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfTotalStudents = CursorUtil.getColumnIndexOrThrow(_cursor, "totalStudents");
          final int _cursorIndexOfAveragePassRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averagePassRate");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<DrivingSchoolEntity> _result = new ArrayList<DrivingSchoolEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DrivingSchoolEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final int _tmpTotalStudents;
            _tmpTotalStudents = _cursor.getInt(_cursorIndexOfTotalStudents);
            final double _tmpAveragePassRate;
            _tmpAveragePassRate = _cursor.getDouble(_cursorIndexOfAveragePassRate);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new DrivingSchoolEntity(_tmpId,_tmpName,_tmpCode,_tmpLocation,_tmpPhoneNumber,_tmpEmail,_tmpIsVerified,_tmpTotalStudents,_tmpAveragePassRate,_tmpCreatedAt);
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
  public Object getActiveSchoolLinking(final String userId,
      final Continuation<? super SchoolLinkingEntity> $completion) {
    final String _sql = "SELECT * FROM school_linkings WHERE userId = ? AND isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SchoolLinkingEntity>() {
      @Override
      @Nullable
      public SchoolLinkingEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfLinkedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfProgressSharingEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "progressSharingEnabled");
          final SchoolLinkingEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final long _tmpLinkedAt;
            _tmpLinkedAt = _cursor.getLong(_cursorIndexOfLinkedAt);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final boolean _tmpProgressSharingEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProgressSharingEnabled);
            _tmpProgressSharingEnabled = _tmp_1 != 0;
            _result = new SchoolLinkingEntity(_tmpId,_tmpUserId,_tmpSchoolId,_tmpLinkedAt,_tmpIsActive,_tmpProgressSharingEnabled);
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
  public Flow<List<SchoolLinkingEntity>> getUserSchoolLinkingsFlow(final String userId) {
    final String _sql = "SELECT * FROM school_linkings WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"school_linkings"}, new Callable<List<SchoolLinkingEntity>>() {
      @Override
      @NonNull
      public List<SchoolLinkingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfLinkedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfProgressSharingEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "progressSharingEnabled");
          final List<SchoolLinkingEntity> _result = new ArrayList<SchoolLinkingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SchoolLinkingEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final long _tmpLinkedAt;
            _tmpLinkedAt = _cursor.getLong(_cursorIndexOfLinkedAt);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final boolean _tmpProgressSharingEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfProgressSharingEnabled);
            _tmpProgressSharingEnabled = _tmp_1 != 0;
            _item = new SchoolLinkingEntity(_tmpId,_tmpUserId,_tmpSchoolId,_tmpLinkedAt,_tmpIsActive,_tmpProgressSharingEnabled);
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
  public Object getSchoolSchedules(final String schoolId,
      final Continuation<? super List<ModuleScheduleEntity>> $completion) {
    final String _sql = "SELECT * FROM module_schedules WHERE schoolId = ? ORDER BY `order` ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, schoolId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ModuleScheduleEntity>>() {
      @Override
      @NonNull
      public List<ModuleScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfModuleName = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleName");
          final int _cursorIndexOfUnlockDate = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockDate");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "order");
          final List<ModuleScheduleEntity> _result = new ArrayList<ModuleScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpModuleName;
            _tmpModuleName = _cursor.getString(_cursorIndexOfModuleName);
            final long _tmpUnlockDate;
            _tmpUnlockDate = _cursor.getLong(_cursorIndexOfUnlockDate);
            final Long _tmpDueDate;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmpDueDate = null;
            } else {
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            }
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final int _tmpOrder;
            _tmpOrder = _cursor.getInt(_cursorIndexOfOrder);
            _item = new ModuleScheduleEntity(_tmpId,_tmpSchoolId,_tmpModuleId,_tmpModuleName,_tmpUnlockDate,_tmpDueDate,_tmpIsUnlocked,_tmpOrder);
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
  public Object getUnlockedModules(final String schoolId,
      final Continuation<? super List<ModuleScheduleEntity>> $completion) {
    final String _sql = "SELECT * FROM module_schedules WHERE schoolId = ? AND isUnlocked = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, schoolId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ModuleScheduleEntity>>() {
      @Override
      @NonNull
      public List<ModuleScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfModuleName = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleName");
          final int _cursorIndexOfUnlockDate = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockDate");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "order");
          final List<ModuleScheduleEntity> _result = new ArrayList<ModuleScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpModuleName;
            _tmpModuleName = _cursor.getString(_cursorIndexOfModuleName);
            final long _tmpUnlockDate;
            _tmpUnlockDate = _cursor.getLong(_cursorIndexOfUnlockDate);
            final Long _tmpDueDate;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmpDueDate = null;
            } else {
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            }
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final int _tmpOrder;
            _tmpOrder = _cursor.getInt(_cursorIndexOfOrder);
            _item = new ModuleScheduleEntity(_tmpId,_tmpSchoolId,_tmpModuleId,_tmpModuleName,_tmpUnlockDate,_tmpDueDate,_tmpIsUnlocked,_tmpOrder);
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
  public Flow<List<ModuleScheduleEntity>> getSchoolSchedulesFlow(final String schoolId) {
    final String _sql = "SELECT * FROM module_schedules WHERE schoolId = ? ORDER BY `order` ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, schoolId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"module_schedules"}, new Callable<List<ModuleScheduleEntity>>() {
      @Override
      @NonNull
      public List<ModuleScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfModuleName = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleName");
          final int _cursorIndexOfUnlockDate = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockDate");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "order");
          final List<ModuleScheduleEntity> _result = new ArrayList<ModuleScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpModuleName;
            _tmpModuleName = _cursor.getString(_cursorIndexOfModuleName);
            final long _tmpUnlockDate;
            _tmpUnlockDate = _cursor.getLong(_cursorIndexOfUnlockDate);
            final Long _tmpDueDate;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmpDueDate = null;
            } else {
              _tmpDueDate = _cursor.getLong(_cursorIndexOfDueDate);
            }
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final int _tmpOrder;
            _tmpOrder = _cursor.getInt(_cursorIndexOfOrder);
            _item = new ModuleScheduleEntity(_tmpId,_tmpSchoolId,_tmpModuleId,_tmpModuleName,_tmpUnlockDate,_tmpDueDate,_tmpIsUnlocked,_tmpOrder);
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
  public Object getLatestProgressReport(final String userId, final String schoolId,
      final Continuation<? super ProgressReportEntity> $completion) {
    final String _sql = "SELECT * FROM progress_reports WHERE userId = ? AND schoolId = ? ORDER BY reportDate DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, schoolId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProgressReportEntity>() {
      @Override
      @Nullable
      public ProgressReportEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfReportDate = CursorUtil.getColumnIndexOrThrow(_cursor, "reportDate");
          final int _cursorIndexOfCompletedModules = CursorUtil.getColumnIndexOrThrow(_cursor, "completedModules");
          final int _cursorIndexOfTotalModules = CursorUtil.getColumnIndexOrThrow(_cursor, "totalModules");
          final int _cursorIndexOfAverageTestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "averageTestScore");
          final int _cursorIndexOfTotalStudyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalStudyTime");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfLastActivityDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActivityDate");
          final ProgressReportEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final long _tmpReportDate;
            _tmpReportDate = _cursor.getLong(_cursorIndexOfReportDate);
            final int _tmpCompletedModules;
            _tmpCompletedModules = _cursor.getInt(_cursorIndexOfCompletedModules);
            final int _tmpTotalModules;
            _tmpTotalModules = _cursor.getInt(_cursorIndexOfTotalModules);
            final double _tmpAverageTestScore;
            _tmpAverageTestScore = _cursor.getDouble(_cursorIndexOfAverageTestScore);
            final int _tmpTotalStudyTime;
            _tmpTotalStudyTime = _cursor.getInt(_cursorIndexOfTotalStudyTime);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final long _tmpLastActivityDate;
            _tmpLastActivityDate = _cursor.getLong(_cursorIndexOfLastActivityDate);
            _result = new ProgressReportEntity(_tmpId,_tmpUserId,_tmpSchoolId,_tmpReportDate,_tmpCompletedModules,_tmpTotalModules,_tmpAverageTestScore,_tmpTotalStudyTime,_tmpCurrentStreak,_tmpLastActivityDate);
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
  public Object getUserProgressReports(final String userId,
      final Continuation<? super List<ProgressReportEntity>> $completion) {
    final String _sql = "SELECT * FROM progress_reports WHERE userId = ? ORDER BY reportDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProgressReportEntity>>() {
      @Override
      @NonNull
      public List<ProgressReportEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfReportDate = CursorUtil.getColumnIndexOrThrow(_cursor, "reportDate");
          final int _cursorIndexOfCompletedModules = CursorUtil.getColumnIndexOrThrow(_cursor, "completedModules");
          final int _cursorIndexOfTotalModules = CursorUtil.getColumnIndexOrThrow(_cursor, "totalModules");
          final int _cursorIndexOfAverageTestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "averageTestScore");
          final int _cursorIndexOfTotalStudyTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalStudyTime");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfLastActivityDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActivityDate");
          final List<ProgressReportEntity> _result = new ArrayList<ProgressReportEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProgressReportEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final long _tmpReportDate;
            _tmpReportDate = _cursor.getLong(_cursorIndexOfReportDate);
            final int _tmpCompletedModules;
            _tmpCompletedModules = _cursor.getInt(_cursorIndexOfCompletedModules);
            final int _tmpTotalModules;
            _tmpTotalModules = _cursor.getInt(_cursorIndexOfTotalModules);
            final double _tmpAverageTestScore;
            _tmpAverageTestScore = _cursor.getDouble(_cursorIndexOfAverageTestScore);
            final int _tmpTotalStudyTime;
            _tmpTotalStudyTime = _cursor.getInt(_cursorIndexOfTotalStudyTime);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final long _tmpLastActivityDate;
            _tmpLastActivityDate = _cursor.getLong(_cursorIndexOfLastActivityDate);
            _item = new ProgressReportEntity(_tmpId,_tmpUserId,_tmpSchoolId,_tmpReportDate,_tmpCompletedModules,_tmpTotalModules,_tmpAverageTestScore,_tmpTotalStudyTime,_tmpCurrentStreak,_tmpLastActivityDate);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
