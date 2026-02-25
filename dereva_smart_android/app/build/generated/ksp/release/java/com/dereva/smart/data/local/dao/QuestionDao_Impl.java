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
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dereva.smart.data.local.entity.QuestionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QuestionEntity> __insertionAdapterOfQuestionEntity;

  private final EntityDeletionOrUpdateAdapter<QuestionEntity> __deletionAdapterOfQuestionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllQuestions;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestionEntity = new EntityInsertionAdapter<QuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`textEn`,`textSw`,`optionsJson`,`correctOptionIndex`,`explanationEn`,`explanationSw`,`imageUrl`,`curriculumTopicId`,`difficultyLevel`,`licenseCategoriesJson`,`isCommonCore`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuestionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTextEn());
        statement.bindString(3, entity.getTextSw());
        statement.bindString(4, entity.getOptionsJson());
        statement.bindLong(5, entity.getCorrectOptionIndex());
        statement.bindString(6, entity.getExplanationEn());
        statement.bindString(7, entity.getExplanationSw());
        if (entity.getImageUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getImageUrl());
        }
        statement.bindString(9, entity.getCurriculumTopicId());
        statement.bindString(10, entity.getDifficultyLevel());
        statement.bindString(11, entity.getLicenseCategoriesJson());
        final int _tmp = entity.isCommonCore() ? 1 : 0;
        statement.bindLong(12, _tmp);
      }
    };
    this.__deletionAdapterOfQuestionEntity = new EntityDeletionOrUpdateAdapter<QuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `questions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuestionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllQuestions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM questions";
        return _query;
      }
    };
  }

  @Override
  public Object insertQuestion(final QuestionEntity question,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestionEntity.insert(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuestions(final List<QuestionEntity> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestionEntity.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQuestion(final QuestionEntity question,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQuestionEntity.handle(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllQuestions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllQuestions.acquire();
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
          __preparedStmtOfDeleteAllQuestions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getQuestionById(final String questionId,
      final Continuation<? super QuestionEntity> $completion) {
    final String _sql = "SELECT * FROM questions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, questionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QuestionEntity>() {
      @Override
      @Nullable
      public QuestionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTextEn = CursorUtil.getColumnIndexOrThrow(_cursor, "textEn");
          final int _cursorIndexOfTextSw = CursorUtil.getColumnIndexOrThrow(_cursor, "textSw");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfExplanationEn = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationEn");
          final int _cursorIndexOfExplanationSw = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationSw");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfCurriculumTopicId = CursorUtil.getColumnIndexOrThrow(_cursor, "curriculumTopicId");
          final int _cursorIndexOfDifficultyLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "difficultyLevel");
          final int _cursorIndexOfLicenseCategoriesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategoriesJson");
          final int _cursorIndexOfIsCommonCore = CursorUtil.getColumnIndexOrThrow(_cursor, "isCommonCore");
          final QuestionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTextEn;
            _tmpTextEn = _cursor.getString(_cursorIndexOfTextEn);
            final String _tmpTextSw;
            _tmpTextSw = _cursor.getString(_cursorIndexOfTextSw);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final int _tmpCorrectOptionIndex;
            _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            final String _tmpExplanationEn;
            _tmpExplanationEn = _cursor.getString(_cursorIndexOfExplanationEn);
            final String _tmpExplanationSw;
            _tmpExplanationSw = _cursor.getString(_cursorIndexOfExplanationSw);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final String _tmpCurriculumTopicId;
            _tmpCurriculumTopicId = _cursor.getString(_cursorIndexOfCurriculumTopicId);
            final String _tmpDifficultyLevel;
            _tmpDifficultyLevel = _cursor.getString(_cursorIndexOfDifficultyLevel);
            final String _tmpLicenseCategoriesJson;
            _tmpLicenseCategoriesJson = _cursor.getString(_cursorIndexOfLicenseCategoriesJson);
            final boolean _tmpIsCommonCore;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCommonCore);
            _tmpIsCommonCore = _tmp != 0;
            _result = new QuestionEntity(_tmpId,_tmpTextEn,_tmpTextSw,_tmpOptionsJson,_tmpCorrectOptionIndex,_tmpExplanationEn,_tmpExplanationSw,_tmpImageUrl,_tmpCurriculumTopicId,_tmpDifficultyLevel,_tmpLicenseCategoriesJson,_tmpIsCommonCore);
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
  public Object getQuestionsByIds(final List<String> questionIds,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM questions WHERE id IN (");
    final int _inputSize = questionIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : questionIds) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTextEn = CursorUtil.getColumnIndexOrThrow(_cursor, "textEn");
          final int _cursorIndexOfTextSw = CursorUtil.getColumnIndexOrThrow(_cursor, "textSw");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfExplanationEn = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationEn");
          final int _cursorIndexOfExplanationSw = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationSw");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfCurriculumTopicId = CursorUtil.getColumnIndexOrThrow(_cursor, "curriculumTopicId");
          final int _cursorIndexOfDifficultyLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "difficultyLevel");
          final int _cursorIndexOfLicenseCategoriesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategoriesJson");
          final int _cursorIndexOfIsCommonCore = CursorUtil.getColumnIndexOrThrow(_cursor, "isCommonCore");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTextEn;
            _tmpTextEn = _cursor.getString(_cursorIndexOfTextEn);
            final String _tmpTextSw;
            _tmpTextSw = _cursor.getString(_cursorIndexOfTextSw);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final int _tmpCorrectOptionIndex;
            _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            final String _tmpExplanationEn;
            _tmpExplanationEn = _cursor.getString(_cursorIndexOfExplanationEn);
            final String _tmpExplanationSw;
            _tmpExplanationSw = _cursor.getString(_cursorIndexOfExplanationSw);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final String _tmpCurriculumTopicId;
            _tmpCurriculumTopicId = _cursor.getString(_cursorIndexOfCurriculumTopicId);
            final String _tmpDifficultyLevel;
            _tmpDifficultyLevel = _cursor.getString(_cursorIndexOfDifficultyLevel);
            final String _tmpLicenseCategoriesJson;
            _tmpLicenseCategoriesJson = _cursor.getString(_cursorIndexOfLicenseCategoriesJson);
            final boolean _tmpIsCommonCore;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCommonCore);
            _tmpIsCommonCore = _tmp != 0;
            _item_1 = new QuestionEntity(_tmpId,_tmpTextEn,_tmpTextSw,_tmpOptionsJson,_tmpCorrectOptionIndex,_tmpExplanationEn,_tmpExplanationSw,_tmpImageUrl,_tmpCurriculumTopicId,_tmpDifficultyLevel,_tmpLicenseCategoriesJson,_tmpIsCommonCore);
            _result.add(_item_1);
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
  public Object getQuestionsByCategory(final String licenseCategory,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM questions \n"
            + "        WHERE isCommonCore = 1 \n"
            + "        OR licenseCategoriesJson LIKE '%' || ? || '%'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, licenseCategory);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTextEn = CursorUtil.getColumnIndexOrThrow(_cursor, "textEn");
          final int _cursorIndexOfTextSw = CursorUtil.getColumnIndexOrThrow(_cursor, "textSw");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfExplanationEn = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationEn");
          final int _cursorIndexOfExplanationSw = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationSw");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfCurriculumTopicId = CursorUtil.getColumnIndexOrThrow(_cursor, "curriculumTopicId");
          final int _cursorIndexOfDifficultyLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "difficultyLevel");
          final int _cursorIndexOfLicenseCategoriesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategoriesJson");
          final int _cursorIndexOfIsCommonCore = CursorUtil.getColumnIndexOrThrow(_cursor, "isCommonCore");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTextEn;
            _tmpTextEn = _cursor.getString(_cursorIndexOfTextEn);
            final String _tmpTextSw;
            _tmpTextSw = _cursor.getString(_cursorIndexOfTextSw);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final int _tmpCorrectOptionIndex;
            _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            final String _tmpExplanationEn;
            _tmpExplanationEn = _cursor.getString(_cursorIndexOfExplanationEn);
            final String _tmpExplanationSw;
            _tmpExplanationSw = _cursor.getString(_cursorIndexOfExplanationSw);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final String _tmpCurriculumTopicId;
            _tmpCurriculumTopicId = _cursor.getString(_cursorIndexOfCurriculumTopicId);
            final String _tmpDifficultyLevel;
            _tmpDifficultyLevel = _cursor.getString(_cursorIndexOfDifficultyLevel);
            final String _tmpLicenseCategoriesJson;
            _tmpLicenseCategoriesJson = _cursor.getString(_cursorIndexOfLicenseCategoriesJson);
            final boolean _tmpIsCommonCore;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCommonCore);
            _tmpIsCommonCore = _tmp != 0;
            _item = new QuestionEntity(_tmpId,_tmpTextEn,_tmpTextSw,_tmpOptionsJson,_tmpCorrectOptionIndex,_tmpExplanationEn,_tmpExplanationSw,_tmpImageUrl,_tmpCurriculumTopicId,_tmpDifficultyLevel,_tmpLicenseCategoriesJson,_tmpIsCommonCore);
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
  public Object getQuestionsByTopic(final String topicId,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE curriculumTopicId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, topicId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTextEn = CursorUtil.getColumnIndexOrThrow(_cursor, "textEn");
          final int _cursorIndexOfTextSw = CursorUtil.getColumnIndexOrThrow(_cursor, "textSw");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfExplanationEn = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationEn");
          final int _cursorIndexOfExplanationSw = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationSw");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfCurriculumTopicId = CursorUtil.getColumnIndexOrThrow(_cursor, "curriculumTopicId");
          final int _cursorIndexOfDifficultyLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "difficultyLevel");
          final int _cursorIndexOfLicenseCategoriesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategoriesJson");
          final int _cursorIndexOfIsCommonCore = CursorUtil.getColumnIndexOrThrow(_cursor, "isCommonCore");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTextEn;
            _tmpTextEn = _cursor.getString(_cursorIndexOfTextEn);
            final String _tmpTextSw;
            _tmpTextSw = _cursor.getString(_cursorIndexOfTextSw);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final int _tmpCorrectOptionIndex;
            _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            final String _tmpExplanationEn;
            _tmpExplanationEn = _cursor.getString(_cursorIndexOfExplanationEn);
            final String _tmpExplanationSw;
            _tmpExplanationSw = _cursor.getString(_cursorIndexOfExplanationSw);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final String _tmpCurriculumTopicId;
            _tmpCurriculumTopicId = _cursor.getString(_cursorIndexOfCurriculumTopicId);
            final String _tmpDifficultyLevel;
            _tmpDifficultyLevel = _cursor.getString(_cursorIndexOfDifficultyLevel);
            final String _tmpLicenseCategoriesJson;
            _tmpLicenseCategoriesJson = _cursor.getString(_cursorIndexOfLicenseCategoriesJson);
            final boolean _tmpIsCommonCore;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCommonCore);
            _tmpIsCommonCore = _tmp != 0;
            _item = new QuestionEntity(_tmpId,_tmpTextEn,_tmpTextSw,_tmpOptionsJson,_tmpCorrectOptionIndex,_tmpExplanationEn,_tmpExplanationSw,_tmpImageUrl,_tmpCurriculumTopicId,_tmpDifficultyLevel,_tmpLicenseCategoriesJson,_tmpIsCommonCore);
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
  public Flow<List<QuestionEntity>> getAllQuestionsFlow() {
    final String _sql = "SELECT * FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTextEn = CursorUtil.getColumnIndexOrThrow(_cursor, "textEn");
          final int _cursorIndexOfTextSw = CursorUtil.getColumnIndexOrThrow(_cursor, "textSw");
          final int _cursorIndexOfOptionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "optionsJson");
          final int _cursorIndexOfCorrectOptionIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctOptionIndex");
          final int _cursorIndexOfExplanationEn = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationEn");
          final int _cursorIndexOfExplanationSw = CursorUtil.getColumnIndexOrThrow(_cursor, "explanationSw");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfCurriculumTopicId = CursorUtil.getColumnIndexOrThrow(_cursor, "curriculumTopicId");
          final int _cursorIndexOfDifficultyLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "difficultyLevel");
          final int _cursorIndexOfLicenseCategoriesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "licenseCategoriesJson");
          final int _cursorIndexOfIsCommonCore = CursorUtil.getColumnIndexOrThrow(_cursor, "isCommonCore");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTextEn;
            _tmpTextEn = _cursor.getString(_cursorIndexOfTextEn);
            final String _tmpTextSw;
            _tmpTextSw = _cursor.getString(_cursorIndexOfTextSw);
            final String _tmpOptionsJson;
            _tmpOptionsJson = _cursor.getString(_cursorIndexOfOptionsJson);
            final int _tmpCorrectOptionIndex;
            _tmpCorrectOptionIndex = _cursor.getInt(_cursorIndexOfCorrectOptionIndex);
            final String _tmpExplanationEn;
            _tmpExplanationEn = _cursor.getString(_cursorIndexOfExplanationEn);
            final String _tmpExplanationSw;
            _tmpExplanationSw = _cursor.getString(_cursorIndexOfExplanationSw);
            final String _tmpImageUrl;
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _tmpImageUrl = null;
            } else {
              _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            final String _tmpCurriculumTopicId;
            _tmpCurriculumTopicId = _cursor.getString(_cursorIndexOfCurriculumTopicId);
            final String _tmpDifficultyLevel;
            _tmpDifficultyLevel = _cursor.getString(_cursorIndexOfDifficultyLevel);
            final String _tmpLicenseCategoriesJson;
            _tmpLicenseCategoriesJson = _cursor.getString(_cursorIndexOfLicenseCategoriesJson);
            final boolean _tmpIsCommonCore;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCommonCore);
            _tmpIsCommonCore = _tmp != 0;
            _item = new QuestionEntity(_tmpId,_tmpTextEn,_tmpTextSw,_tmpOptionsJson,_tmpCorrectOptionIndex,_tmpExplanationEn,_tmpExplanationSw,_tmpImageUrl,_tmpCurriculumTopicId,_tmpDifficultyLevel,_tmpLicenseCategoriesJson,_tmpIsCommonCore);
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
  public Object getQuestionCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM questions";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
