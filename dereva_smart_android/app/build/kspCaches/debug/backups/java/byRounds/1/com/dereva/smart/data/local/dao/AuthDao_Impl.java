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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dereva.smart.data.local.entity.AuthSessionEntity;
import com.dereva.smart.data.local.entity.UserEntity;
import com.dereva.smart.data.local.entity.VerificationCodeEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AuthDao_Impl implements AuthDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final EntityInsertionAdapter<VerificationCodeEntity> __insertionAdapterOfVerificationCodeEntity;

  private final EntityInsertionAdapter<AuthSessionEntity> __insertionAdapterOfAuthSessionEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastLogin;

  private final SharedSQLiteStatement __preparedStmtOfMarkPhoneVerified;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUser;

  private final SharedSQLiteStatement __preparedStmtOfMarkCodeAsUsed;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExpiredCodes;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUnusedCodesForPhone;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSession;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllUserSessions;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExpiredSessions;

  public AuthDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`id`,`phoneNumber`,`passwordHash`,`name`,`email`,`targetCategory`,`drivingSchoolId`,`subscriptionStatus`,`subscriptionExpiryDate`,`isPhoneVerified`,`isGuestMode`,`createdAt`,`lastActiveAt`,`lastLoginAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPhoneNumber());
        statement.bindString(3, entity.getPasswordHash());
        statement.bindString(4, entity.getName());
        if (entity.getEmail() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getEmail());
        }
        statement.bindString(6, entity.getTargetCategory());
        if (entity.getDrivingSchoolId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDrivingSchoolId());
        }
        statement.bindString(8, entity.getSubscriptionStatus());
        if (entity.getSubscriptionExpiryDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSubscriptionExpiryDate());
        }
        final int _tmp = entity.isPhoneVerified() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isGuestMode() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getLastActiveAt());
        if (entity.getLastLoginAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getLastLoginAt());
        }
      }
    };
    this.__insertionAdapterOfVerificationCodeEntity = new EntityInsertionAdapter<VerificationCodeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `verification_codes` (`code`,`phoneNumber`,`expiresAt`,`isUsed`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VerificationCodeEntity entity) {
        statement.bindString(1, entity.getCode());
        statement.bindString(2, entity.getPhoneNumber());
        statement.bindLong(3, entity.getExpiresAt());
        final int _tmp = entity.isUsed() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfAuthSessionEntity = new EntityInsertionAdapter<AuthSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `auth_sessions` (`token`,`userId`,`createdAt`,`expiresAt`,`deviceId`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AuthSessionEntity entity) {
        statement.bindString(1, entity.getToken());
        statement.bindString(2, entity.getUserId());
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getExpiresAt());
        if (entity.getDeviceId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDeviceId());
        }
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `id` = ?,`phoneNumber` = ?,`passwordHash` = ?,`name` = ?,`email` = ?,`targetCategory` = ?,`drivingSchoolId` = ?,`subscriptionStatus` = ?,`subscriptionExpiryDate` = ?,`isPhoneVerified` = ?,`isGuestMode` = ?,`createdAt` = ?,`lastActiveAt` = ?,`lastLoginAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPhoneNumber());
        statement.bindString(3, entity.getPasswordHash());
        statement.bindString(4, entity.getName());
        if (entity.getEmail() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getEmail());
        }
        statement.bindString(6, entity.getTargetCategory());
        if (entity.getDrivingSchoolId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDrivingSchoolId());
        }
        statement.bindString(8, entity.getSubscriptionStatus());
        if (entity.getSubscriptionExpiryDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSubscriptionExpiryDate());
        }
        final int _tmp = entity.isPhoneVerified() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isGuestMode() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getLastActiveAt());
        if (entity.getLastLoginAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getLastLoginAt());
        }
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateLastLogin = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET lastLoginAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkPhoneVerified = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET isPhoneVerified = 1 WHERE phoneNumber = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkCodeAsUsed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE verification_codes SET isUsed = 1 WHERE code = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteExpiredCodes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM verification_codes WHERE expiresAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteUnusedCodesForPhone = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM verification_codes WHERE phoneNumber = ? AND isUsed = 0";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM auth_sessions WHERE token = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllUserSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM auth_sessions WHERE userId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteExpiredSessions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM auth_sessions WHERE expiresAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserEntity.insert(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertVerificationCode(final VerificationCodeEntity code,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVerificationCodeEntity.insert(code);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSession(final AuthSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAuthSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserEntity.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastLogin(final String userId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastLogin.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
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
          __preparedStmtOfUpdateLastLogin.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markPhoneVerified(final String phoneNumber,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkPhoneVerified.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, phoneNumber);
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
          __preparedStmtOfMarkPhoneVerified.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUser(final String userId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUser.acquire();
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
          __preparedStmtOfDeleteUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markCodeAsUsed(final String code, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkCodeAsUsed.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, code);
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
          __preparedStmtOfMarkCodeAsUsed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExpiredCodes(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExpiredCodes.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteExpiredCodes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUnusedCodesForPhone(final String phoneNumber,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUnusedCodesForPhone.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, phoneNumber);
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
          __preparedStmtOfDeleteUnusedCodesForPhone.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSession(final String token, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSession.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, token);
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
          __preparedStmtOfDeleteSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllUserSessions(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllUserSessions.acquire();
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
          __preparedStmtOfDeleteAllUserSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExpiredSessions(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExpiredSessions.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteExpiredSessions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserById(final String userId,
      final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfTargetCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCategory");
          final int _cursorIndexOfDrivingSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "drivingSchoolId");
          final int _cursorIndexOfSubscriptionStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionStatus");
          final int _cursorIndexOfSubscriptionExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionExpiryDate");
          final int _cursorIndexOfIsPhoneVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isPhoneVerified");
          final int _cursorIndexOfIsGuestMode = CursorUtil.getColumnIndexOrThrow(_cursor, "isGuestMode");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastActiveAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpTargetCategory;
            _tmpTargetCategory = _cursor.getString(_cursorIndexOfTargetCategory);
            final String _tmpDrivingSchoolId;
            if (_cursor.isNull(_cursorIndexOfDrivingSchoolId)) {
              _tmpDrivingSchoolId = null;
            } else {
              _tmpDrivingSchoolId = _cursor.getString(_cursorIndexOfDrivingSchoolId);
            }
            final String _tmpSubscriptionStatus;
            _tmpSubscriptionStatus = _cursor.getString(_cursorIndexOfSubscriptionStatus);
            final Long _tmpSubscriptionExpiryDate;
            if (_cursor.isNull(_cursorIndexOfSubscriptionExpiryDate)) {
              _tmpSubscriptionExpiryDate = null;
            } else {
              _tmpSubscriptionExpiryDate = _cursor.getLong(_cursorIndexOfSubscriptionExpiryDate);
            }
            final boolean _tmpIsPhoneVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPhoneVerified);
            _tmpIsPhoneVerified = _tmp != 0;
            final boolean _tmpIsGuestMode;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsGuestMode);
            _tmpIsGuestMode = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastActiveAt;
            _tmpLastActiveAt = _cursor.getLong(_cursorIndexOfLastActiveAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            _result = new UserEntity(_tmpId,_tmpPhoneNumber,_tmpPasswordHash,_tmpName,_tmpEmail,_tmpTargetCategory,_tmpDrivingSchoolId,_tmpSubscriptionStatus,_tmpSubscriptionExpiryDate,_tmpIsPhoneVerified,_tmpIsGuestMode,_tmpCreatedAt,_tmpLastActiveAt,_tmpLastLoginAt);
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
  public Object getUserByPhone(final String phoneNumber,
      final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE phoneNumber = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phoneNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfTargetCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCategory");
          final int _cursorIndexOfDrivingSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "drivingSchoolId");
          final int _cursorIndexOfSubscriptionStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionStatus");
          final int _cursorIndexOfSubscriptionExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionExpiryDate");
          final int _cursorIndexOfIsPhoneVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isPhoneVerified");
          final int _cursorIndexOfIsGuestMode = CursorUtil.getColumnIndexOrThrow(_cursor, "isGuestMode");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastActiveAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpTargetCategory;
            _tmpTargetCategory = _cursor.getString(_cursorIndexOfTargetCategory);
            final String _tmpDrivingSchoolId;
            if (_cursor.isNull(_cursorIndexOfDrivingSchoolId)) {
              _tmpDrivingSchoolId = null;
            } else {
              _tmpDrivingSchoolId = _cursor.getString(_cursorIndexOfDrivingSchoolId);
            }
            final String _tmpSubscriptionStatus;
            _tmpSubscriptionStatus = _cursor.getString(_cursorIndexOfSubscriptionStatus);
            final Long _tmpSubscriptionExpiryDate;
            if (_cursor.isNull(_cursorIndexOfSubscriptionExpiryDate)) {
              _tmpSubscriptionExpiryDate = null;
            } else {
              _tmpSubscriptionExpiryDate = _cursor.getLong(_cursorIndexOfSubscriptionExpiryDate);
            }
            final boolean _tmpIsPhoneVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPhoneVerified);
            _tmpIsPhoneVerified = _tmp != 0;
            final boolean _tmpIsGuestMode;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsGuestMode);
            _tmpIsGuestMode = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastActiveAt;
            _tmpLastActiveAt = _cursor.getLong(_cursorIndexOfLastActiveAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            _result = new UserEntity(_tmpId,_tmpPhoneNumber,_tmpPasswordHash,_tmpName,_tmpEmail,_tmpTargetCategory,_tmpDrivingSchoolId,_tmpSubscriptionStatus,_tmpSubscriptionExpiryDate,_tmpIsPhoneVerified,_tmpIsGuestMode,_tmpCreatedAt,_tmpLastActiveAt,_tmpLastLoginAt);
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
  public Flow<UserEntity> getUserByPhoneFlow(final String phoneNumber) {
    final String _sql = "SELECT * FROM users WHERE phoneNumber = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phoneNumber);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfTargetCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "targetCategory");
          final int _cursorIndexOfDrivingSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "drivingSchoolId");
          final int _cursorIndexOfSubscriptionStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionStatus");
          final int _cursorIndexOfSubscriptionExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionExpiryDate");
          final int _cursorIndexOfIsPhoneVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isPhoneVerified");
          final int _cursorIndexOfIsGuestMode = CursorUtil.getColumnIndexOrThrow(_cursor, "isGuestMode");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastActiveAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveAt");
          final int _cursorIndexOfLastLoginAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpPasswordHash;
            _tmpPasswordHash = _cursor.getString(_cursorIndexOfPasswordHash);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpTargetCategory;
            _tmpTargetCategory = _cursor.getString(_cursorIndexOfTargetCategory);
            final String _tmpDrivingSchoolId;
            if (_cursor.isNull(_cursorIndexOfDrivingSchoolId)) {
              _tmpDrivingSchoolId = null;
            } else {
              _tmpDrivingSchoolId = _cursor.getString(_cursorIndexOfDrivingSchoolId);
            }
            final String _tmpSubscriptionStatus;
            _tmpSubscriptionStatus = _cursor.getString(_cursorIndexOfSubscriptionStatus);
            final Long _tmpSubscriptionExpiryDate;
            if (_cursor.isNull(_cursorIndexOfSubscriptionExpiryDate)) {
              _tmpSubscriptionExpiryDate = null;
            } else {
              _tmpSubscriptionExpiryDate = _cursor.getLong(_cursorIndexOfSubscriptionExpiryDate);
            }
            final boolean _tmpIsPhoneVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPhoneVerified);
            _tmpIsPhoneVerified = _tmp != 0;
            final boolean _tmpIsGuestMode;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsGuestMode);
            _tmpIsGuestMode = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastActiveAt;
            _tmpLastActiveAt = _cursor.getLong(_cursorIndexOfLastActiveAt);
            final Long _tmpLastLoginAt;
            if (_cursor.isNull(_cursorIndexOfLastLoginAt)) {
              _tmpLastLoginAt = null;
            } else {
              _tmpLastLoginAt = _cursor.getLong(_cursorIndexOfLastLoginAt);
            }
            _result = new UserEntity(_tmpId,_tmpPhoneNumber,_tmpPasswordHash,_tmpName,_tmpEmail,_tmpTargetCategory,_tmpDrivingSchoolId,_tmpSubscriptionStatus,_tmpSubscriptionExpiryDate,_tmpIsPhoneVerified,_tmpIsGuestMode,_tmpCreatedAt,_tmpLastActiveAt,_tmpLastLoginAt);
          } else {
            _result = null;
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
  public Object getVerificationCode(final String code, final String phoneNumber,
      final Continuation<? super VerificationCodeEntity> $completion) {
    final String _sql = "SELECT * FROM verification_codes WHERE code = ? AND phoneNumber = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    _argIndex = 2;
    _statement.bindString(_argIndex, phoneNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VerificationCodeEntity>() {
      @Override
      @Nullable
      public VerificationCodeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfIsUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "isUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final VerificationCodeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final boolean _tmpIsUsed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUsed);
            _tmpIsUsed = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new VerificationCodeEntity(_tmpCode,_tmpPhoneNumber,_tmpExpiresAt,_tmpIsUsed,_tmpCreatedAt);
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
  public Object getSession(final String token,
      final Continuation<? super AuthSessionEntity> $completion) {
    final String _sql = "SELECT * FROM auth_sessions WHERE token = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, token);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AuthSessionEntity>() {
      @Override
      @Nullable
      public AuthSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final AuthSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpDeviceId;
            if (_cursor.isNull(_cursorIndexOfDeviceId)) {
              _tmpDeviceId = null;
            } else {
              _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            }
            _result = new AuthSessionEntity(_tmpToken,_tmpUserId,_tmpCreatedAt,_tmpExpiresAt,_tmpDeviceId);
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
  public Object getLatestSession(final String userId,
      final Continuation<? super AuthSessionEntity> $completion) {
    final String _sql = "SELECT * FROM auth_sessions WHERE userId = ? ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AuthSessionEntity>() {
      @Override
      @Nullable
      public AuthSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final AuthSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            final String _tmpDeviceId;
            if (_cursor.isNull(_cursorIndexOfDeviceId)) {
              _tmpDeviceId = null;
            } else {
              _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            }
            _result = new AuthSessionEntity(_tmpToken,_tmpUserId,_tmpCreatedAt,_tmpExpiresAt,_tmpDeviceId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
