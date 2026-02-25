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
import com.dereva.smart.data.local.entity.ContentSyncMetadataEntity;
import com.dereva.smart.data.local.entity.LessonEntity;
import com.dereva.smart.data.local.entity.LessonProgressEntity;
import com.dereva.smart.data.local.entity.MediaAssetEntity;
import com.dereva.smart.data.local.entity.ModuleDownloadEntity;
import com.dereva.smart.data.local.entity.ModuleEntity;
import java.lang.Class;
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
public final class ContentDao_Impl implements ContentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ModuleEntity> __insertionAdapterOfModuleEntity;

  private final EntityInsertionAdapter<LessonEntity> __insertionAdapterOfLessonEntity;

  private final EntityInsertionAdapter<MediaAssetEntity> __insertionAdapterOfMediaAssetEntity;

  private final EntityInsertionAdapter<LessonProgressEntity> __insertionAdapterOfLessonProgressEntity;

  private final EntityInsertionAdapter<ModuleDownloadEntity> __insertionAdapterOfModuleDownloadEntity;

  private final EntityInsertionAdapter<ContentSyncMetadataEntity> __insertionAdapterOfContentSyncMetadataEntity;

  private final EntityDeletionOrUpdateAdapter<ModuleEntity> __deletionAdapterOfModuleEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateModuleDownloadStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateModuleDownloadStatus_1;

  private final SharedSQLiteStatement __preparedStmtOfUpdateModuleProgress;

  private final SharedSQLiteStatement __preparedStmtOfDeleteModulesByCategory;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLessonCompletion;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLessonDownloadStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMediaAssetDownload;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMediaAssetProgress;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLessonProgress;

  private final SharedSQLiteStatement __preparedStmtOfMarkLessonCompleted;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDownloadProgress;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDownloadStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  private final SharedSQLiteStatement __preparedStmtOfDeleteModuleDownload;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLessonLocalPath;

  private final SharedSQLiteStatement __preparedStmtOfClearModuleLessonPaths;

  public ContentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfModuleEntity = new EntityInsertionAdapter<ModuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `modules` (`id`,`title`,`description`,`orderIndex`,`licenseCategory`,`thumbnailUrl`,`estimatedDuration`,`lessonCount`,`isDownloaded`,`downloadSize`,`status`,`completionPercentage`,`requiresSubscription`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ModuleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getOrderIndex());
        statement.bindString(5, entity.getLicenseCategory());
        if (entity.getThumbnailUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getThumbnailUrl());
        }
        statement.bindLong(7, entity.getEstimatedDuration());
        statement.bindLong(8, entity.getLessonCount());
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getDownloadSize());
        statement.bindString(11, entity.getStatus());
        statement.bindLong(12, entity.getCompletionPercentage());
        final int _tmp_1 = entity.getRequiresSubscription() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfLessonEntity = new EntityInsertionAdapter<LessonEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lessons` (`id`,`moduleId`,`title`,`description`,`orderIndex`,`contentType`,`contentUrl`,`contentText`,`duration`,`isDownloaded`,`isCompleted`,`requiresSubscription`,`lastAccessedAt`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LessonEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getModuleId());
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        statement.bindLong(5, entity.getOrderIndex());
        statement.bindString(6, entity.getContentType());
        if (entity.getContentUrl() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getContentUrl());
        }
        if (entity.getContentText() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getContentText());
        }
        statement.bindLong(9, entity.getDuration());
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        final int _tmp_2 = entity.getRequiresSubscription() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        if (entity.getLastAccessedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getLastAccessedAt());
        }
        statement.bindLong(14, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfMediaAssetEntity = new EntityInsertionAdapter<MediaAssetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `media_assets` (`id`,`lessonId`,`type`,`url`,`localPath`,`title`,`description`,`fileSize`,`duration`,`thumbnailUrl`,`isDownloaded`,`downloadProgress`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MediaAssetEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLessonId());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getUrl());
        if (entity.getLocalPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLocalPath());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        statement.bindLong(8, entity.getFileSize());
        if (entity.getDuration() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getDuration());
        }
        if (entity.getThumbnailUrl() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getThumbnailUrl());
        }
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getDownloadProgress());
        statement.bindLong(13, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfLessonProgressEntity = new EntityInsertionAdapter<LessonProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lesson_progress` (`id`,`userId`,`lessonId`,`moduleId`,`isCompleted`,`completionPercentage`,`timeSpent`,`lastPosition`,`startedAt`,`completedAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LessonProgressEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getLessonId());
        statement.bindString(4, entity.getModuleId());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getCompletionPercentage());
        statement.bindLong(7, entity.getTimeSpent());
        statement.bindLong(8, entity.getLastPosition());
        statement.bindLong(9, entity.getStartedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCompletedAt());
        }
        statement.bindLong(11, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfModuleDownloadEntity = new EntityInsertionAdapter<ModuleDownloadEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `module_downloads` (`id`,`moduleId`,`userId`,`status`,`progress`,`downloadedSize`,`totalSize`,`startedAt`,`completedAt`,`errorMessage`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ModuleDownloadEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getModuleId());
        statement.bindString(3, entity.getUserId());
        statement.bindString(4, entity.getStatus());
        statement.bindLong(5, entity.getProgress());
        statement.bindLong(6, entity.getDownloadedSize());
        statement.bindLong(7, entity.getTotalSize());
        statement.bindLong(8, entity.getStartedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCompletedAt());
        }
        if (entity.getErrorMessage() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getErrorMessage());
        }
      }
    };
    this.__insertionAdapterOfContentSyncMetadataEntity = new EntityInsertionAdapter<ContentSyncMetadataEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `content_sync_metadata` (`id`,`entityType`,`entityId`,`lastSyncedAt`,`localVersion`,`serverVersion`,`needsSync`,`syncStatus`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContentSyncMetadataEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getEntityType());
        statement.bindString(3, entity.getEntityId());
        statement.bindLong(4, entity.getLastSyncedAt());
        statement.bindLong(5, entity.getLocalVersion());
        statement.bindLong(6, entity.getServerVersion());
        final int _tmp = entity.getNeedsSync() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindString(8, entity.getSyncStatus());
      }
    };
    this.__deletionAdapterOfModuleEntity = new EntityDeletionOrUpdateAdapter<ModuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `modules` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ModuleEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateModuleDownloadStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE modules SET isDownloaded = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateModuleDownloadStatus_1 = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE module_downloads SET status = ?, progress = ?, errorMessage = ? WHERE moduleId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateModuleProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE modules SET status = ?, completionPercentage = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteModulesByCategory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM modules WHERE licenseCategory = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLessonCompletion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lessons SET isCompleted = ?, lastAccessedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLessonDownloadStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lessons SET isDownloaded = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMediaAssetDownload = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_assets SET isDownloaded = ?, localPath = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMediaAssetProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE media_assets SET downloadProgress = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLessonProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lesson_progress SET timeSpent = ?, lastPosition = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkLessonCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lesson_progress SET isCompleted = 1, completionPercentage = 100, completedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDownloadProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE module_downloads SET status = ?, progress = ?, downloadedSize = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDownloadStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE module_downloads SET status = ?, completedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE content_sync_metadata SET needsSync = 0, syncStatus = 'SYNCED', lastSyncedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteModuleDownload = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM module_downloads WHERE moduleId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLessonLocalPath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lessons SET contentUrl = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearModuleLessonPaths = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lessons SET contentUrl = NULL WHERE moduleId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertModule(final ModuleEntity module,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfModuleEntity.insert(module);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertModules(final List<ModuleEntity> modules,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfModuleEntity.insert(modules);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLesson(final LessonEntity lesson,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLessonEntity.insert(lesson);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLessons(final List<LessonEntity> lessons,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLessonEntity.insert(lessons);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMediaAsset(final MediaAssetEntity asset,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaAssetEntity.insert(asset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMediaAssets(final List<MediaAssetEntity> assets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMediaAssetEntity.insert(assets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLessonProgress(final LessonProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLessonProgressEntity.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertModuleDownload(final ModuleDownloadEntity download,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfModuleDownloadEntity.insert(download);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSyncMetadata(final ContentSyncMetadataEntity metadata,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContentSyncMetadataEntity.insert(metadata);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteModule(final ModuleEntity module,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfModuleEntity.handle(module);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateModuleDownloadStatus(final String moduleId, final boolean isDownloaded,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateModuleDownloadStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isDownloaded ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, moduleId);
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
          __preparedStmtOfUpdateModuleDownloadStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateModuleDownloadStatus(final String moduleId, final String status,
      final int progress, final String errorMessage, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateModuleDownloadStatus_1.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, progress);
        _argIndex = 3;
        if (errorMessage == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, errorMessage);
        }
        _argIndex = 4;
        _stmt.bindString(_argIndex, moduleId);
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
          __preparedStmtOfUpdateModuleDownloadStatus_1.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateModuleProgress(final String moduleId, final String status,
      final int percentage, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateModuleProgress.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, percentage);
        _argIndex = 3;
        _stmt.bindString(_argIndex, moduleId);
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
          __preparedStmtOfUpdateModuleProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteModulesByCategory(final String category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteModulesByCategory.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, category);
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
          __preparedStmtOfDeleteModulesByCategory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLessonCompletion(final String lessonId, final boolean isCompleted,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLessonCompletion.acquire();
        int _argIndex = 1;
        final int _tmp = isCompleted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindString(_argIndex, lessonId);
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
          __preparedStmtOfUpdateLessonCompletion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLessonDownloadStatus(final String lessonId, final boolean isDownloaded,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLessonDownloadStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isDownloaded ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, lessonId);
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
          __preparedStmtOfUpdateLessonDownloadStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMediaAssetDownload(final String assetId, final boolean isDownloaded,
      final String localPath, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMediaAssetDownload.acquire();
        int _argIndex = 1;
        final int _tmp = isDownloaded ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (localPath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, localPath);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, assetId);
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
          __preparedStmtOfUpdateMediaAssetDownload.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMediaAssetProgress(final String assetId, final int progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMediaAssetProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, progress);
        _argIndex = 2;
        _stmt.bindString(_argIndex, assetId);
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
          __preparedStmtOfUpdateMediaAssetProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLessonProgress(final String progressId, final int timeSpent,
      final int position, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLessonProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timeSpent);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, position);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        _stmt.bindString(_argIndex, progressId);
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
          __preparedStmtOfUpdateLessonProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markLessonCompleted(final String progressId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkLessonCompleted.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, progressId);
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
          __preparedStmtOfMarkLessonCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDownloadProgress(final String downloadId, final String status,
      final int progress, final long downloadedSize, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDownloadProgress.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, progress);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, downloadedSize);
        _argIndex = 4;
        _stmt.bindString(_argIndex, downloadId);
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
          __preparedStmtOfUpdateDownloadProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDownloadStatus(final String downloadId, final String status,
      final Long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDownloadStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        if (timestamp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, timestamp);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, downloadId);
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
          __preparedStmtOfUpdateDownloadStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markSynced(final String metadataId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, metadataId);
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
          __preparedStmtOfMarkSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteModuleDownload(final String moduleId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteModuleDownload.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, moduleId);
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
          __preparedStmtOfDeleteModuleDownload.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLessonLocalPath(final String lessonId, final String localPath,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLessonLocalPath.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, localPath);
        _argIndex = 2;
        _stmt.bindString(_argIndex, lessonId);
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
          __preparedStmtOfUpdateLessonLocalPath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateModuleDownloadedStatus(final String moduleId, final boolean isDownloaded,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateModuleDownloadStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isDownloaded ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, moduleId);
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
          __preparedStmtOfUpdateModuleDownloadStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearModuleLessonPaths(final String moduleId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearModuleLessonPaths.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, moduleId);
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
          __preparedStmtOfClearModuleLessonPaths.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ModuleEntity>> getAllModulesFlow() {
    final String _sql = "SELECT * FROM modules ORDER BY orderIndex ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"modules"}, new Callable<List<ModuleEntity>>() {
      @Override
      @NonNull
      public List<ModuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfEstimatedDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDuration");
          final int _cursorIndexOfLessonCount = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonCount");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfDownloadSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadSize");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCompletionPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "completionPercentage");
          final int _cursorIndexOfRequiresSubscription = CursorUtil.getColumnIndexOrThrow(_cursor, "requiresSubscription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ModuleEntity> _result = new ArrayList<ModuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final int _tmpEstimatedDuration;
            _tmpEstimatedDuration = _cursor.getInt(_cursorIndexOfEstimatedDuration);
            final int _tmpLessonCount;
            _tmpLessonCount = _cursor.getInt(_cursorIndexOfLessonCount);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final long _tmpDownloadSize;
            _tmpDownloadSize = _cursor.getLong(_cursorIndexOfDownloadSize);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCompletionPercentage;
            _tmpCompletionPercentage = _cursor.getInt(_cursorIndexOfCompletionPercentage);
            final boolean _tmpRequiresSubscription;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfRequiresSubscription);
            _tmpRequiresSubscription = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ModuleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpOrderIndex,_tmpLicenseCategory,_tmpThumbnailUrl,_tmpEstimatedDuration,_tmpLessonCount,_tmpIsDownloaded,_tmpDownloadSize,_tmpStatus,_tmpCompletionPercentage,_tmpRequiresSubscription,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getModuleById(final String moduleId,
      final Continuation<? super ModuleEntity> $completion) {
    final String _sql = "SELECT * FROM modules WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, moduleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ModuleEntity>() {
      @Override
      @Nullable
      public ModuleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfEstimatedDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDuration");
          final int _cursorIndexOfLessonCount = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonCount");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfDownloadSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadSize");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCompletionPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "completionPercentage");
          final int _cursorIndexOfRequiresSubscription = CursorUtil.getColumnIndexOrThrow(_cursor, "requiresSubscription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ModuleEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final int _tmpEstimatedDuration;
            _tmpEstimatedDuration = _cursor.getInt(_cursorIndexOfEstimatedDuration);
            final int _tmpLessonCount;
            _tmpLessonCount = _cursor.getInt(_cursorIndexOfLessonCount);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final long _tmpDownloadSize;
            _tmpDownloadSize = _cursor.getLong(_cursorIndexOfDownloadSize);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCompletionPercentage;
            _tmpCompletionPercentage = _cursor.getInt(_cursorIndexOfCompletionPercentage);
            final boolean _tmpRequiresSubscription;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfRequiresSubscription);
            _tmpRequiresSubscription = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ModuleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpOrderIndex,_tmpLicenseCategory,_tmpThumbnailUrl,_tmpEstimatedDuration,_tmpLessonCount,_tmpIsDownloaded,_tmpDownloadSize,_tmpStatus,_tmpCompletionPercentage,_tmpRequiresSubscription,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<ModuleEntity>> getModulesByCategory(final String category) {
    final String _sql = "SELECT * FROM modules WHERE licenseCategory = ? ORDER BY orderIndex ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"modules"}, new Callable<List<ModuleEntity>>() {
      @Override
      @NonNull
      public List<ModuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfEstimatedDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDuration");
          final int _cursorIndexOfLessonCount = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonCount");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfDownloadSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadSize");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCompletionPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "completionPercentage");
          final int _cursorIndexOfRequiresSubscription = CursorUtil.getColumnIndexOrThrow(_cursor, "requiresSubscription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ModuleEntity> _result = new ArrayList<ModuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final int _tmpEstimatedDuration;
            _tmpEstimatedDuration = _cursor.getInt(_cursorIndexOfEstimatedDuration);
            final int _tmpLessonCount;
            _tmpLessonCount = _cursor.getInt(_cursorIndexOfLessonCount);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final long _tmpDownloadSize;
            _tmpDownloadSize = _cursor.getLong(_cursorIndexOfDownloadSize);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCompletionPercentage;
            _tmpCompletionPercentage = _cursor.getInt(_cursorIndexOfCompletionPercentage);
            final boolean _tmpRequiresSubscription;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfRequiresSubscription);
            _tmpRequiresSubscription = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ModuleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpOrderIndex,_tmpLicenseCategory,_tmpThumbnailUrl,_tmpEstimatedDuration,_tmpLessonCount,_tmpIsDownloaded,_tmpDownloadSize,_tmpStatus,_tmpCompletionPercentage,_tmpRequiresSubscription,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<ModuleEntity>> getDownloadedModules() {
    final String _sql = "SELECT * FROM modules WHERE isDownloaded = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"modules"}, new Callable<List<ModuleEntity>>() {
      @Override
      @NonNull
      public List<ModuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfLicenseCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategory");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfEstimatedDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDuration");
          final int _cursorIndexOfLessonCount = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonCount");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfDownloadSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadSize");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCompletionPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "completionPercentage");
          final int _cursorIndexOfRequiresSubscription = CursorUtil.getColumnIndexOrThrow(_cursor, "requiresSubscription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ModuleEntity> _result = new ArrayList<ModuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpLicenseCategory;
            _tmpLicenseCategory = _cursor.getString(_cursorIndexOfLicenseCategory);
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final int _tmpEstimatedDuration;
            _tmpEstimatedDuration = _cursor.getInt(_cursorIndexOfEstimatedDuration);
            final int _tmpLessonCount;
            _tmpLessonCount = _cursor.getInt(_cursorIndexOfLessonCount);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final long _tmpDownloadSize;
            _tmpDownloadSize = _cursor.getLong(_cursorIndexOfDownloadSize);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCompletionPercentage;
            _tmpCompletionPercentage = _cursor.getInt(_cursorIndexOfCompletionPercentage);
            final boolean _tmpRequiresSubscription;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfRequiresSubscription);
            _tmpRequiresSubscription = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ModuleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpOrderIndex,_tmpLicenseCategory,_tmpThumbnailUrl,_tmpEstimatedDuration,_tmpLessonCount,_tmpIsDownloaded,_tmpDownloadSize,_tmpStatus,_tmpCompletionPercentage,_tmpRequiresSubscription,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<LessonEntity>> getLessonsByModule(final String moduleId) {
    final String _sql = "SELECT * FROM lessons WHERE moduleId = ? ORDER BY orderIndex ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, moduleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lessons"}, new Callable<List<LessonEntity>>() {
      @Override
      @NonNull
      public List<LessonEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
          final int _cursorIndexOfContentUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "contentUrl");
          final int _cursorIndexOfContentText = CursorUtil.getColumnIndexOrThrow(_cursor, "contentText");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfRequiresSubscription = CursorUtil.getColumnIndexOrThrow(_cursor, "requiresSubscription");
          final int _cursorIndexOfLastAccessedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<LessonEntity> _result = new ArrayList<LessonEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LessonEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpContentType;
            _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
            final String _tmpContentUrl;
            if (_cursor.isNull(_cursorIndexOfContentUrl)) {
              _tmpContentUrl = null;
            } else {
              _tmpContentUrl = _cursor.getString(_cursorIndexOfContentUrl);
            }
            final String _tmpContentText;
            if (_cursor.isNull(_cursorIndexOfContentText)) {
              _tmpContentText = null;
            } else {
              _tmpContentText = _cursor.getString(_cursorIndexOfContentText);
            }
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final boolean _tmpRequiresSubscription;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfRequiresSubscription);
            _tmpRequiresSubscription = _tmp_2 != 0;
            final Long _tmpLastAccessedAt;
            if (_cursor.isNull(_cursorIndexOfLastAccessedAt)) {
              _tmpLastAccessedAt = null;
            } else {
              _tmpLastAccessedAt = _cursor.getLong(_cursorIndexOfLastAccessedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new LessonEntity(_tmpId,_tmpModuleId,_tmpTitle,_tmpDescription,_tmpOrderIndex,_tmpContentType,_tmpContentUrl,_tmpContentText,_tmpDuration,_tmpIsDownloaded,_tmpIsCompleted,_tmpRequiresSubscription,_tmpLastAccessedAt,_tmpCreatedAt);
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
  public Object getLessonById(final String lessonId,
      final Continuation<? super LessonEntity> $completion) {
    final String _sql = "SELECT * FROM lessons WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lessonId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LessonEntity>() {
      @Override
      @Nullable
      public LessonEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
          final int _cursorIndexOfContentUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "contentUrl");
          final int _cursorIndexOfContentText = CursorUtil.getColumnIndexOrThrow(_cursor, "contentText");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfRequiresSubscription = CursorUtil.getColumnIndexOrThrow(_cursor, "requiresSubscription");
          final int _cursorIndexOfLastAccessedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final LessonEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpContentType;
            _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
            final String _tmpContentUrl;
            if (_cursor.isNull(_cursorIndexOfContentUrl)) {
              _tmpContentUrl = null;
            } else {
              _tmpContentUrl = _cursor.getString(_cursorIndexOfContentUrl);
            }
            final String _tmpContentText;
            if (_cursor.isNull(_cursorIndexOfContentText)) {
              _tmpContentText = null;
            } else {
              _tmpContentText = _cursor.getString(_cursorIndexOfContentText);
            }
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final boolean _tmpRequiresSubscription;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfRequiresSubscription);
            _tmpRequiresSubscription = _tmp_2 != 0;
            final Long _tmpLastAccessedAt;
            if (_cursor.isNull(_cursorIndexOfLastAccessedAt)) {
              _tmpLastAccessedAt = null;
            } else {
              _tmpLastAccessedAt = _cursor.getLong(_cursorIndexOfLastAccessedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new LessonEntity(_tmpId,_tmpModuleId,_tmpTitle,_tmpDescription,_tmpOrderIndex,_tmpContentType,_tmpContentUrl,_tmpContentText,_tmpDuration,_tmpIsDownloaded,_tmpIsCompleted,_tmpRequiresSubscription,_tmpLastAccessedAt,_tmpCreatedAt);
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
  public Object getCompletedLessonsCount(final String moduleId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM lessons WHERE moduleId = ? AND isCompleted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, moduleId);
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
  public Object getTotalLessonsCount(final String moduleId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM lessons WHERE moduleId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, moduleId);
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
  public Object getTotalLessonsCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM lessons";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object getMediaAssetsByLesson(final String lessonId,
      final Continuation<? super List<MediaAssetEntity>> $completion) {
    final String _sql = "SELECT * FROM media_assets WHERE lessonId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lessonId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MediaAssetEntity>>() {
      @Override
      @NonNull
      public List<MediaAssetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLessonId = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfLocalPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localPath");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfDownloadProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadProgress");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MediaAssetEntity> _result = new ArrayList<MediaAssetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MediaAssetEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLessonId;
            _tmpLessonId = _cursor.getString(_cursorIndexOfLessonId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpLocalPath;
            if (_cursor.isNull(_cursorIndexOfLocalPath)) {
              _tmpLocalPath = null;
            } else {
              _tmpLocalPath = _cursor.getString(_cursorIndexOfLocalPath);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final Integer _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            }
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final int _tmpDownloadProgress;
            _tmpDownloadProgress = _cursor.getInt(_cursorIndexOfDownloadProgress);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new MediaAssetEntity(_tmpId,_tmpLessonId,_tmpType,_tmpUrl,_tmpLocalPath,_tmpTitle,_tmpDescription,_tmpFileSize,_tmpDuration,_tmpThumbnailUrl,_tmpIsDownloaded,_tmpDownloadProgress,_tmpCreatedAt);
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
  public Object getLessonProgress(final String userId, final String lessonId,
      final Continuation<? super LessonProgressEntity> $completion) {
    final String _sql = "SELECT * FROM lesson_progress WHERE userId = ? AND lessonId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, lessonId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LessonProgressEntity>() {
      @Override
      @Nullable
      public LessonProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfLessonId = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonId");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletionPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "completionPercentage");
          final int _cursorIndexOfTimeSpent = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSpent");
          final int _cursorIndexOfLastPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPosition");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final LessonProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpLessonId;
            _tmpLessonId = _cursor.getString(_cursorIndexOfLessonId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpCompletionPercentage;
            _tmpCompletionPercentage = _cursor.getInt(_cursorIndexOfCompletionPercentage);
            final int _tmpTimeSpent;
            _tmpTimeSpent = _cursor.getInt(_cursorIndexOfTimeSpent);
            final int _tmpLastPosition;
            _tmpLastPosition = _cursor.getInt(_cursorIndexOfLastPosition);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new LessonProgressEntity(_tmpId,_tmpUserId,_tmpLessonId,_tmpModuleId,_tmpIsCompleted,_tmpCompletionPercentage,_tmpTimeSpent,_tmpLastPosition,_tmpStartedAt,_tmpCompletedAt,_tmpUpdatedAt);
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
  public Flow<List<LessonProgressEntity>> getModuleLessonProgress(final String userId,
      final String moduleId) {
    final String _sql = "SELECT * FROM lesson_progress WHERE userId = ? AND moduleId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, moduleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lesson_progress"}, new Callable<List<LessonProgressEntity>>() {
      @Override
      @NonNull
      public List<LessonProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfLessonId = CursorUtil.getColumnIndexOrThrow(_cursor, "lessonId");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletionPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "completionPercentage");
          final int _cursorIndexOfTimeSpent = CursorUtil.getColumnIndexOrThrow(_cursor, "timeSpent");
          final int _cursorIndexOfLastPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPosition");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<LessonProgressEntity> _result = new ArrayList<LessonProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LessonProgressEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpLessonId;
            _tmpLessonId = _cursor.getString(_cursorIndexOfLessonId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final int _tmpCompletionPercentage;
            _tmpCompletionPercentage = _cursor.getInt(_cursorIndexOfCompletionPercentage);
            final int _tmpTimeSpent;
            _tmpTimeSpent = _cursor.getInt(_cursorIndexOfTimeSpent);
            final int _tmpLastPosition;
            _tmpLastPosition = _cursor.getInt(_cursorIndexOfLastPosition);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new LessonProgressEntity(_tmpId,_tmpUserId,_tmpLessonId,_tmpModuleId,_tmpIsCompleted,_tmpCompletionPercentage,_tmpTimeSpent,_tmpLastPosition,_tmpStartedAt,_tmpCompletedAt,_tmpUpdatedAt);
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
  public Object getModuleDownload(final String moduleId, final String userId,
      final Continuation<? super ModuleDownloadEntity> $completion) {
    final String _sql = "SELECT * FROM module_downloads WHERE moduleId = ? AND userId = ? ORDER BY startedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, moduleId);
    _argIndex = 2;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ModuleDownloadEntity>() {
      @Override
      @Nullable
      public ModuleDownloadEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfDownloadedSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedSize");
          final int _cursorIndexOfTotalSize = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSize");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final ModuleDownloadEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpProgress;
            _tmpProgress = _cursor.getInt(_cursorIndexOfProgress);
            final long _tmpDownloadedSize;
            _tmpDownloadedSize = _cursor.getLong(_cursorIndexOfDownloadedSize);
            final long _tmpTotalSize;
            _tmpTotalSize = _cursor.getLong(_cursorIndexOfTotalSize);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _result = new ModuleDownloadEntity(_tmpId,_tmpModuleId,_tmpUserId,_tmpStatus,_tmpProgress,_tmpDownloadedSize,_tmpTotalSize,_tmpStartedAt,_tmpCompletedAt,_tmpErrorMessage);
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
  public Flow<List<ModuleDownloadEntity>> getActiveDownloads(final String userId) {
    final String _sql = "SELECT * FROM module_downloads WHERE userId = ? AND status = 'DOWNLOADING'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"module_downloads"}, new Callable<List<ModuleDownloadEntity>>() {
      @Override
      @NonNull
      public List<ModuleDownloadEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfDownloadedSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedSize");
          final int _cursorIndexOfTotalSize = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSize");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final List<ModuleDownloadEntity> _result = new ArrayList<ModuleDownloadEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleDownloadEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpProgress;
            _tmpProgress = _cursor.getInt(_cursorIndexOfProgress);
            final long _tmpDownloadedSize;
            _tmpDownloadedSize = _cursor.getLong(_cursorIndexOfDownloadedSize);
            final long _tmpTotalSize;
            _tmpTotalSize = _cursor.getLong(_cursorIndexOfTotalSize);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new ModuleDownloadEntity(_tmpId,_tmpModuleId,_tmpUserId,_tmpStatus,_tmpProgress,_tmpDownloadedSize,_tmpTotalSize,_tmpStartedAt,_tmpCompletedAt,_tmpErrorMessage);
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
  public Object getSyncMetadata(final String type, final String id,
      final Continuation<? super ContentSyncMetadataEntity> $completion) {
    final String _sql = "SELECT * FROM content_sync_metadata WHERE entityType = ? AND entityId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    _argIndex = 2;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContentSyncMetadataEntity>() {
      @Override
      @Nullable
      public ContentSyncMetadataEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEntityType = CursorUtil.getColumnIndexOrThrow(_cursor, "entityType");
          final int _cursorIndexOfEntityId = CursorUtil.getColumnIndexOrThrow(_cursor, "entityId");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLocalVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "localVersion");
          final int _cursorIndexOfServerVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "serverVersion");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needsSync");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final ContentSyncMetadataEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEntityType;
            _tmpEntityType = _cursor.getString(_cursorIndexOfEntityType);
            final String _tmpEntityId;
            _tmpEntityId = _cursor.getString(_cursorIndexOfEntityId);
            final long _tmpLastSyncedAt;
            _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            final int _tmpLocalVersion;
            _tmpLocalVersion = _cursor.getInt(_cursorIndexOfLocalVersion);
            final int _tmpServerVersion;
            _tmpServerVersion = _cursor.getInt(_cursorIndexOfServerVersion);
            final boolean _tmpNeedsSync;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _result = new ContentSyncMetadataEntity(_tmpId,_tmpEntityType,_tmpEntityId,_tmpLastSyncedAt,_tmpLocalVersion,_tmpServerVersion,_tmpNeedsSync,_tmpSyncStatus);
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
  public Object getPendingSyncItems(
      final Continuation<? super List<ContentSyncMetadataEntity>> $completion) {
    final String _sql = "SELECT * FROM content_sync_metadata WHERE needsSync = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContentSyncMetadataEntity>>() {
      @Override
      @NonNull
      public List<ContentSyncMetadataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEntityType = CursorUtil.getColumnIndexOrThrow(_cursor, "entityType");
          final int _cursorIndexOfEntityId = CursorUtil.getColumnIndexOrThrow(_cursor, "entityId");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final int _cursorIndexOfLocalVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "localVersion");
          final int _cursorIndexOfServerVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "serverVersion");
          final int _cursorIndexOfNeedsSync = CursorUtil.getColumnIndexOrThrow(_cursor, "needsSync");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<ContentSyncMetadataEntity> _result = new ArrayList<ContentSyncMetadataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContentSyncMetadataEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEntityType;
            _tmpEntityType = _cursor.getString(_cursorIndexOfEntityType);
            final String _tmpEntityId;
            _tmpEntityId = _cursor.getString(_cursorIndexOfEntityId);
            final long _tmpLastSyncedAt;
            _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            final int _tmpLocalVersion;
            _tmpLocalVersion = _cursor.getInt(_cursorIndexOfLocalVersion);
            final int _tmpServerVersion;
            _tmpServerVersion = _cursor.getInt(_cursorIndexOfServerVersion);
            final boolean _tmpNeedsSync;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNeedsSync);
            _tmpNeedsSync = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new ContentSyncMetadataEntity(_tmpId,_tmpEntityType,_tmpEntityId,_tmpLastSyncedAt,_tmpLocalVersion,_tmpServerVersion,_tmpNeedsSync,_tmpSyncStatus);
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
  public Object getTotalModulesCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM modules";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object getCompletedModulesCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM modules WHERE status = 'COMPLETED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object getCompletedLessonsCountTotal(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM lessons WHERE isCompleted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object getTotalStudyTime(final String userId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(timeSpent) FROM lesson_progress WHERE userId = ?";
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
  public Object getTotalDownloadedSize(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(downloadSize) FROM modules WHERE isDownloaded = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
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
  public Object getFailedDownloads(
      final Continuation<? super List<ModuleDownloadEntity>> $completion) {
    final String _sql = "SELECT * FROM module_downloads WHERE status = 'FAILED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ModuleDownloadEntity>>() {
      @Override
      @NonNull
      public List<ModuleDownloadEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModuleId = CursorUtil.getColumnIndexOrThrow(_cursor, "moduleId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfDownloadedSize = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedSize");
          final int _cursorIndexOfTotalSize = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSize");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final List<ModuleDownloadEntity> _result = new ArrayList<ModuleDownloadEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ModuleDownloadEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpModuleId;
            _tmpModuleId = _cursor.getString(_cursorIndexOfModuleId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpProgress;
            _tmpProgress = _cursor.getInt(_cursorIndexOfProgress);
            final long _tmpDownloadedSize;
            _tmpDownloadedSize = _cursor.getLong(_cursorIndexOfDownloadedSize);
            final long _tmpTotalSize;
            _tmpTotalSize = _cursor.getLong(_cursorIndexOfTotalSize);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new ModuleDownloadEntity(_tmpId,_tmpModuleId,_tmpUserId,_tmpStatus,_tmpProgress,_tmpDownloadedSize,_tmpTotalSize,_tmpStartedAt,_tmpCompletedAt,_tmpErrorMessage);
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
