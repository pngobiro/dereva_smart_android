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
import com.dereva.smart.data.local.entity.PaymentRequestEntity;
import com.dereva.smart.data.local.entity.PaymentResultEntity;
import com.dereva.smart.data.local.entity.PromoCodeEntity;
import com.dereva.smart.data.local.entity.ReferralCodeEntity;
import com.dereva.smart.data.local.entity.SchoolCommissionEntity;
import com.dereva.smart.data.local.entity.UserSubscriptionEntity;
import java.lang.Class;
import java.lang.Double;
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
public final class PaymentDao_Impl implements PaymentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PaymentRequestEntity> __insertionAdapterOfPaymentRequestEntity;

  private final EntityInsertionAdapter<PaymentResultEntity> __insertionAdapterOfPaymentResultEntity;

  private final EntityInsertionAdapter<UserSubscriptionEntity> __insertionAdapterOfUserSubscriptionEntity;

  private final EntityInsertionAdapter<PromoCodeEntity> __insertionAdapterOfPromoCodeEntity;

  private final EntityInsertionAdapter<ReferralCodeEntity> __insertionAdapterOfReferralCodeEntity;

  private final EntityInsertionAdapter<SchoolCommissionEntity> __insertionAdapterOfSchoolCommissionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllSubscriptions;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAutoRenew;

  private final SharedSQLiteStatement __preparedStmtOfIncrementPromoCodeUsage;

  private final SharedSQLiteStatement __preparedStmtOfIncrementReferralUsage;

  public PaymentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPaymentRequestEntity = new EntityInsertionAdapter<PaymentRequestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payment_requests` (`id`,`userId`,`amount`,`phoneNumber`,`subscriptionTier`,`promoCode`,`referralCode`,`schoolId`,`createdAt`,`expiresAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaymentRequestEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, entity.getPhoneNumber());
        statement.bindString(5, entity.getSubscriptionTier());
        if (entity.getPromoCode() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPromoCode());
        }
        if (entity.getReferralCode() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getReferralCode());
        }
        if (entity.getSchoolId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSchoolId());
        }
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getExpiresAt());
      }
    };
    this.__insertionAdapterOfPaymentResultEntity = new EntityInsertionAdapter<PaymentResultEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payment_results` (`id`,`paymentRequestId`,`userId`,`amount`,`mpesaReceiptNumber`,`transactionId`,`status`,`errorMessage`,`completedAt`,`metadata`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaymentResultEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPaymentRequestId());
        statement.bindString(3, entity.getUserId());
        statement.bindDouble(4, entity.getAmount());
        if (entity.getMpesaReceiptNumber() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMpesaReceiptNumber());
        }
        if (entity.getTransactionId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTransactionId());
        }
        statement.bindString(7, entity.getStatus());
        if (entity.getErrorMessage() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getErrorMessage());
        }
        if (entity.getCompletedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCompletedAt());
        }
        if (entity.getMetadata() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getMetadata());
        }
      }
    };
    this.__insertionAdapterOfUserSubscriptionEntity = new EntityInsertionAdapter<UserSubscriptionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_subscriptions` (`id`,`userId`,`tier`,`startDate`,`endDate`,`isActive`,`autoRenew`,`paymentResultId`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserSubscriptionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getTier());
        statement.bindLong(4, entity.getStartDate());
        if (entity.getEndDate() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getEndDate());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.getAutoRenew() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindString(8, entity.getPaymentResultId());
      }
    };
    this.__insertionAdapterOfPromoCodeEntity = new EntityInsertionAdapter<PromoCodeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `promo_codes` (`code`,`discountPercentage`,`discountAmount`,`validUntil`,`maxUses`,`currentUses`,`isActive`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PromoCodeEntity entity) {
        statement.bindString(1, entity.getCode());
        statement.bindDouble(2, entity.getDiscountPercentage());
        if (entity.getDiscountAmount() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getDiscountAmount());
        }
        statement.bindLong(4, entity.getValidUntil());
        statement.bindLong(5, entity.getMaxUses());
        statement.bindLong(6, entity.getCurrentUses());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__insertionAdapterOfReferralCodeEntity = new EntityInsertionAdapter<ReferralCodeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `referral_codes` (`code`,`referrerId`,`discountPercentage`,`referrerBonusPercentage`,`usageCount`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReferralCodeEntity entity) {
        statement.bindString(1, entity.getCode());
        statement.bindString(2, entity.getReferrerId());
        statement.bindDouble(3, entity.getDiscountPercentage());
        statement.bindDouble(4, entity.getReferrerBonusPercentage());
        statement.bindLong(5, entity.getUsageCount());
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfSchoolCommissionEntity = new EntityInsertionAdapter<SchoolCommissionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `school_commissions` (`id`,`schoolId`,`paymentId`,`amount`,`percentage`,`status`,`createdAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SchoolCommissionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getSchoolId());
        statement.bindString(3, entity.getPaymentId());
        statement.bindDouble(4, entity.getAmount());
        statement.bindDouble(5, entity.getPercentage());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeactivateAllSubscriptions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_subscriptions SET isActive = 0 WHERE userId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAutoRenew = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_subscriptions SET autoRenew = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementPromoCodeUsage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE promo_codes SET currentUses = currentUses + 1 WHERE code = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementReferralUsage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE referral_codes SET usageCount = usageCount + 1 WHERE code = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPaymentRequest(final PaymentRequestEntity request,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPaymentRequestEntity.insert(request);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPaymentResult(final PaymentResultEntity result,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPaymentResultEntity.insert(result);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSubscription(final UserSubscriptionEntity subscription,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserSubscriptionEntity.insert(subscription);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPromoCode(final PromoCodeEntity promoCode,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPromoCodeEntity.insert(promoCode);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertReferralCode(final ReferralCodeEntity referralCode,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReferralCodeEntity.insert(referralCode);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSchoolCommission(final SchoolCommissionEntity commission,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSchoolCommissionEntity.insert(commission);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateAllSubscriptions(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllSubscriptions.acquire();
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
          __preparedStmtOfDeactivateAllSubscriptions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAutoRenew(final String subscriptionId, final boolean autoRenew,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAutoRenew.acquire();
        int _argIndex = 1;
        final int _tmp = autoRenew ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, subscriptionId);
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
          __preparedStmtOfUpdateAutoRenew.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementPromoCodeUsage(final String code,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementPromoCodeUsage.acquire();
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
          __preparedStmtOfIncrementPromoCodeUsage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementReferralUsage(final String code,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementReferralUsage.acquire();
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
          __preparedStmtOfIncrementReferralUsage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPaymentRequest(final String requestId,
      final Continuation<? super PaymentRequestEntity> $completion) {
    final String _sql = "SELECT * FROM payment_requests WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, requestId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PaymentRequestEntity>() {
      @Override
      @Nullable
      public PaymentRequestEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfSubscriptionTier = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionTier");
          final int _cursorIndexOfPromoCode = CursorUtil.getColumnIndexOrThrow(_cursor, "promoCode");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final PaymentRequestEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpSubscriptionTier;
            _tmpSubscriptionTier = _cursor.getString(_cursorIndexOfSubscriptionTier);
            final String _tmpPromoCode;
            if (_cursor.isNull(_cursorIndexOfPromoCode)) {
              _tmpPromoCode = null;
            } else {
              _tmpPromoCode = _cursor.getString(_cursorIndexOfPromoCode);
            }
            final String _tmpReferralCode;
            if (_cursor.isNull(_cursorIndexOfReferralCode)) {
              _tmpReferralCode = null;
            } else {
              _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            }
            final String _tmpSchoolId;
            if (_cursor.isNull(_cursorIndexOfSchoolId)) {
              _tmpSchoolId = null;
            } else {
              _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            _result = new PaymentRequestEntity(_tmpId,_tmpUserId,_tmpAmount,_tmpPhoneNumber,_tmpSubscriptionTier,_tmpPromoCode,_tmpReferralCode,_tmpSchoolId,_tmpCreatedAt,_tmpExpiresAt);
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
  public Object getUserPaymentRequests(final String userId,
      final Continuation<? super List<PaymentRequestEntity>> $completion) {
    final String _sql = "SELECT * FROM payment_requests WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PaymentRequestEntity>>() {
      @Override
      @NonNull
      public List<PaymentRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfSubscriptionTier = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionTier");
          final int _cursorIndexOfPromoCode = CursorUtil.getColumnIndexOrThrow(_cursor, "promoCode");
          final int _cursorIndexOfReferralCode = CursorUtil.getColumnIndexOrThrow(_cursor, "referralCode");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAt");
          final List<PaymentRequestEntity> _result = new ArrayList<PaymentRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentRequestEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpSubscriptionTier;
            _tmpSubscriptionTier = _cursor.getString(_cursorIndexOfSubscriptionTier);
            final String _tmpPromoCode;
            if (_cursor.isNull(_cursorIndexOfPromoCode)) {
              _tmpPromoCode = null;
            } else {
              _tmpPromoCode = _cursor.getString(_cursorIndexOfPromoCode);
            }
            final String _tmpReferralCode;
            if (_cursor.isNull(_cursorIndexOfReferralCode)) {
              _tmpReferralCode = null;
            } else {
              _tmpReferralCode = _cursor.getString(_cursorIndexOfReferralCode);
            }
            final String _tmpSchoolId;
            if (_cursor.isNull(_cursorIndexOfSchoolId)) {
              _tmpSchoolId = null;
            } else {
              _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpExpiresAt;
            _tmpExpiresAt = _cursor.getLong(_cursorIndexOfExpiresAt);
            _item = new PaymentRequestEntity(_tmpId,_tmpUserId,_tmpAmount,_tmpPhoneNumber,_tmpSubscriptionTier,_tmpPromoCode,_tmpReferralCode,_tmpSchoolId,_tmpCreatedAt,_tmpExpiresAt);
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
  public Object getPaymentResult(final String resultId,
      final Continuation<? super PaymentResultEntity> $completion) {
    final String _sql = "SELECT * FROM payment_results WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, resultId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PaymentResultEntity>() {
      @Override
      @Nullable
      public PaymentResultEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPaymentRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfMpesaReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "mpesaReceiptNumber");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final PaymentResultEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPaymentRequestId;
            _tmpPaymentRequestId = _cursor.getString(_cursorIndexOfPaymentRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpMpesaReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfMpesaReceiptNumber)) {
              _tmpMpesaReceiptNumber = null;
            } else {
              _tmpMpesaReceiptNumber = _cursor.getString(_cursorIndexOfMpesaReceiptNumber);
            }
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            _result = new PaymentResultEntity(_tmpId,_tmpPaymentRequestId,_tmpUserId,_tmpAmount,_tmpMpesaReceiptNumber,_tmpTransactionId,_tmpStatus,_tmpErrorMessage,_tmpCompletedAt,_tmpMetadata);
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
  public Object getPaymentResultByRequest(final String requestId,
      final Continuation<? super PaymentResultEntity> $completion) {
    final String _sql = "SELECT * FROM payment_results WHERE paymentRequestId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, requestId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PaymentResultEntity>() {
      @Override
      @Nullable
      public PaymentResultEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPaymentRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfMpesaReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "mpesaReceiptNumber");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final PaymentResultEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPaymentRequestId;
            _tmpPaymentRequestId = _cursor.getString(_cursorIndexOfPaymentRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpMpesaReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfMpesaReceiptNumber)) {
              _tmpMpesaReceiptNumber = null;
            } else {
              _tmpMpesaReceiptNumber = _cursor.getString(_cursorIndexOfMpesaReceiptNumber);
            }
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            _result = new PaymentResultEntity(_tmpId,_tmpPaymentRequestId,_tmpUserId,_tmpAmount,_tmpMpesaReceiptNumber,_tmpTransactionId,_tmpStatus,_tmpErrorMessage,_tmpCompletedAt,_tmpMetadata);
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
  public Object getUserPaymentHistory(final String userId,
      final Continuation<? super List<PaymentResultEntity>> $completion) {
    final String _sql = "SELECT * FROM payment_results WHERE userId = ? ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PaymentResultEntity>>() {
      @Override
      @NonNull
      public List<PaymentResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPaymentRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfMpesaReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "mpesaReceiptNumber");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final List<PaymentResultEntity> _result = new ArrayList<PaymentResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentResultEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPaymentRequestId;
            _tmpPaymentRequestId = _cursor.getString(_cursorIndexOfPaymentRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpMpesaReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfMpesaReceiptNumber)) {
              _tmpMpesaReceiptNumber = null;
            } else {
              _tmpMpesaReceiptNumber = _cursor.getString(_cursorIndexOfMpesaReceiptNumber);
            }
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            _item = new PaymentResultEntity(_tmpId,_tmpPaymentRequestId,_tmpUserId,_tmpAmount,_tmpMpesaReceiptNumber,_tmpTransactionId,_tmpStatus,_tmpErrorMessage,_tmpCompletedAt,_tmpMetadata);
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
  public Flow<List<PaymentResultEntity>> getUserCompletedPaymentsFlow(final String userId) {
    final String _sql = "SELECT * FROM payment_results WHERE userId = ? AND status = 'COMPLETED' ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payment_results"}, new Callable<List<PaymentResultEntity>>() {
      @Override
      @NonNull
      public List<PaymentResultEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPaymentRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfMpesaReceiptNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "mpesaReceiptNumber");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final List<PaymentResultEntity> _result = new ArrayList<PaymentResultEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentResultEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPaymentRequestId;
            _tmpPaymentRequestId = _cursor.getString(_cursorIndexOfPaymentRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpMpesaReceiptNumber;
            if (_cursor.isNull(_cursorIndexOfMpesaReceiptNumber)) {
              _tmpMpesaReceiptNumber = null;
            } else {
              _tmpMpesaReceiptNumber = _cursor.getString(_cursorIndexOfMpesaReceiptNumber);
            }
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            _item = new PaymentResultEntity(_tmpId,_tmpPaymentRequestId,_tmpUserId,_tmpAmount,_tmpMpesaReceiptNumber,_tmpTransactionId,_tmpStatus,_tmpErrorMessage,_tmpCompletedAt,_tmpMetadata);
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
  public Object getActiveSubscription(final String userId,
      final Continuation<? super UserSubscriptionEntity> $completion) {
    final String _sql = "SELECT * FROM user_subscriptions WHERE userId = ? AND isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserSubscriptionEntity>() {
      @Override
      @Nullable
      public UserSubscriptionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfAutoRenew = CursorUtil.getColumnIndexOrThrow(_cursor, "autoRenew");
          final int _cursorIndexOfPaymentResultId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentResultId");
          final UserSubscriptionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final boolean _tmpAutoRenew;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAutoRenew);
            _tmpAutoRenew = _tmp_1 != 0;
            final String _tmpPaymentResultId;
            _tmpPaymentResultId = _cursor.getString(_cursorIndexOfPaymentResultId);
            _result = new UserSubscriptionEntity(_tmpId,_tmpUserId,_tmpTier,_tmpStartDate,_tmpEndDate,_tmpIsActive,_tmpAutoRenew,_tmpPaymentResultId);
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
  public Flow<UserSubscriptionEntity> getActiveSubscriptionFlow(final String userId) {
    final String _sql = "SELECT * FROM user_subscriptions WHERE userId = ? AND isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_subscriptions"}, new Callable<UserSubscriptionEntity>() {
      @Override
      @Nullable
      public UserSubscriptionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfAutoRenew = CursorUtil.getColumnIndexOrThrow(_cursor, "autoRenew");
          final int _cursorIndexOfPaymentResultId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentResultId");
          final UserSubscriptionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final boolean _tmpAutoRenew;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAutoRenew);
            _tmpAutoRenew = _tmp_1 != 0;
            final String _tmpPaymentResultId;
            _tmpPaymentResultId = _cursor.getString(_cursorIndexOfPaymentResultId);
            _result = new UserSubscriptionEntity(_tmpId,_tmpUserId,_tmpTier,_tmpStartDate,_tmpEndDate,_tmpIsActive,_tmpAutoRenew,_tmpPaymentResultId);
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
  public Object getUserSubscriptions(final String userId,
      final Continuation<? super List<UserSubscriptionEntity>> $completion) {
    final String _sql = "SELECT * FROM user_subscriptions WHERE userId = ? ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<UserSubscriptionEntity>>() {
      @Override
      @NonNull
      public List<UserSubscriptionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfAutoRenew = CursorUtil.getColumnIndexOrThrow(_cursor, "autoRenew");
          final int _cursorIndexOfPaymentResultId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentResultId");
          final List<UserSubscriptionEntity> _result = new ArrayList<UserSubscriptionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserSubscriptionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final boolean _tmpAutoRenew;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAutoRenew);
            _tmpAutoRenew = _tmp_1 != 0;
            final String _tmpPaymentResultId;
            _tmpPaymentResultId = _cursor.getString(_cursorIndexOfPaymentResultId);
            _item = new UserSubscriptionEntity(_tmpId,_tmpUserId,_tmpTier,_tmpStartDate,_tmpEndDate,_tmpIsActive,_tmpAutoRenew,_tmpPaymentResultId);
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
  public Object getPromoCode(final String code,
      final Continuation<? super PromoCodeEntity> $completion) {
    final String _sql = "SELECT * FROM promo_codes WHERE code = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PromoCodeEntity>() {
      @Override
      @Nullable
      public PromoCodeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfDiscountPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "discountPercentage");
          final int _cursorIndexOfDiscountAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "discountAmount");
          final int _cursorIndexOfValidUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "validUntil");
          final int _cursorIndexOfMaxUses = CursorUtil.getColumnIndexOrThrow(_cursor, "maxUses");
          final int _cursorIndexOfCurrentUses = CursorUtil.getColumnIndexOrThrow(_cursor, "currentUses");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final PromoCodeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final double _tmpDiscountPercentage;
            _tmpDiscountPercentage = _cursor.getDouble(_cursorIndexOfDiscountPercentage);
            final Double _tmpDiscountAmount;
            if (_cursor.isNull(_cursorIndexOfDiscountAmount)) {
              _tmpDiscountAmount = null;
            } else {
              _tmpDiscountAmount = _cursor.getDouble(_cursorIndexOfDiscountAmount);
            }
            final long _tmpValidUntil;
            _tmpValidUntil = _cursor.getLong(_cursorIndexOfValidUntil);
            final int _tmpMaxUses;
            _tmpMaxUses = _cursor.getInt(_cursorIndexOfMaxUses);
            final int _tmpCurrentUses;
            _tmpCurrentUses = _cursor.getInt(_cursorIndexOfCurrentUses);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _result = new PromoCodeEntity(_tmpCode,_tmpDiscountPercentage,_tmpDiscountAmount,_tmpValidUntil,_tmpMaxUses,_tmpCurrentUses,_tmpIsActive);
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
  public Object getReferralCode(final String code,
      final Continuation<? super ReferralCodeEntity> $completion) {
    final String _sql = "SELECT * FROM referral_codes WHERE code = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReferralCodeEntity>() {
      @Override
      @Nullable
      public ReferralCodeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfReferrerId = CursorUtil.getColumnIndexOrThrow(_cursor, "referrerId");
          final int _cursorIndexOfDiscountPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "discountPercentage");
          final int _cursorIndexOfReferrerBonusPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "referrerBonusPercentage");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ReferralCodeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpReferrerId;
            _tmpReferrerId = _cursor.getString(_cursorIndexOfReferrerId);
            final double _tmpDiscountPercentage;
            _tmpDiscountPercentage = _cursor.getDouble(_cursorIndexOfDiscountPercentage);
            final double _tmpReferrerBonusPercentage;
            _tmpReferrerBonusPercentage = _cursor.getDouble(_cursorIndexOfReferrerBonusPercentage);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ReferralCodeEntity(_tmpCode,_tmpReferrerId,_tmpDiscountPercentage,_tmpReferrerBonusPercentage,_tmpUsageCount,_tmpCreatedAt);
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
  public Object getUserReferralCode(final String userId,
      final Continuation<? super ReferralCodeEntity> $completion) {
    final String _sql = "SELECT * FROM referral_codes WHERE referrerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReferralCodeEntity>() {
      @Override
      @Nullable
      public ReferralCodeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfReferrerId = CursorUtil.getColumnIndexOrThrow(_cursor, "referrerId");
          final int _cursorIndexOfDiscountPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "discountPercentage");
          final int _cursorIndexOfReferrerBonusPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "referrerBonusPercentage");
          final int _cursorIndexOfUsageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "usageCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ReferralCodeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpReferrerId;
            _tmpReferrerId = _cursor.getString(_cursorIndexOfReferrerId);
            final double _tmpDiscountPercentage;
            _tmpDiscountPercentage = _cursor.getDouble(_cursorIndexOfDiscountPercentage);
            final double _tmpReferrerBonusPercentage;
            _tmpReferrerBonusPercentage = _cursor.getDouble(_cursorIndexOfReferrerBonusPercentage);
            final int _tmpUsageCount;
            _tmpUsageCount = _cursor.getInt(_cursorIndexOfUsageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ReferralCodeEntity(_tmpCode,_tmpReferrerId,_tmpDiscountPercentage,_tmpReferrerBonusPercentage,_tmpUsageCount,_tmpCreatedAt);
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
  public Object getSchoolCommissions(final String schoolId,
      final Continuation<? super List<SchoolCommissionEntity>> $completion) {
    final String _sql = "SELECT * FROM school_commissions WHERE schoolId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, schoolId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SchoolCommissionEntity>>() {
      @Override
      @NonNull
      public List<SchoolCommissionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSchoolId = CursorUtil.getColumnIndexOrThrow(_cursor, "schoolId");
          final int _cursorIndexOfPaymentId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "percentage");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SchoolCommissionEntity> _result = new ArrayList<SchoolCommissionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SchoolCommissionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSchoolId;
            _tmpSchoolId = _cursor.getString(_cursorIndexOfSchoolId);
            final String _tmpPaymentId;
            _tmpPaymentId = _cursor.getString(_cursorIndexOfPaymentId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final double _tmpPercentage;
            _tmpPercentage = _cursor.getDouble(_cursorIndexOfPercentage);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SchoolCommissionEntity(_tmpId,_tmpSchoolId,_tmpPaymentId,_tmpAmount,_tmpPercentage,_tmpStatus,_tmpCreatedAt);
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
  public Object getTotalSchoolCommissions(final String schoolId,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(amount) FROM school_commissions WHERE schoolId = ? AND status = 'COMPLETED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, schoolId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
