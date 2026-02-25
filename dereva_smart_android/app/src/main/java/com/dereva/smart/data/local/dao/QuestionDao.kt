package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    
    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: String): QuestionEntity?
    
    @Query("SELECT * FROM questions WHERE id IN (:questionIds)")
    suspend fun getQuestionsByIds(questionIds: List<String>): List<QuestionEntity>
    
    @Query("""
        SELECT * FROM questions 
        WHERE isCommonCore = 1 
        OR licenseCategoriesJson LIKE '%' || :licenseCategory || '%'
    """)
    suspend fun getQuestionsByCategory(licenseCategory: String): List<QuestionEntity>
    
    @Query("SELECT * FROM questions WHERE curriculumTopicId = :topicId")
    suspend fun getQuestionsByTopic(topicId: String): List<QuestionEntity>
    
    @Query("SELECT * FROM questions")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
    
    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)
    
    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
    
    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}
