package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.MockTestEntity
import com.dereva.smart.data.local.entity.TestResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MockTestDao {
    
    @Query("SELECT * FROM mock_tests WHERE id = :testId")
    suspend fun getTestById(testId: String): MockTestEntity?
    
    @Query("SELECT * FROM mock_tests WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserTests(userId: String): List<MockTestEntity>
    
    @Query("SELECT * FROM mock_tests WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserTestsFlow(userId: String): Flow<List<MockTestEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: MockTestEntity)
    
    @Update
    suspend fun updateTest(test: MockTestEntity)
    
    @Delete
    suspend fun deleteTest(test: MockTestEntity)
    
    // Test Results
    @Query("SELECT * FROM test_results WHERE testId = :testId")
    suspend fun getTestResult(testId: String): TestResultEntity?
    
    @Query("SELECT * FROM test_results WHERE userId = :userId ORDER BY completedAt DESC")
    suspend fun getUserTestResults(userId: String): List<TestResultEntity>
    
    @Query("SELECT * FROM test_results WHERE userId = :userId ORDER BY completedAt DESC")
    fun getUserTestResultsFlow(userId: String): Flow<List<TestResultEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestResult(result: TestResultEntity)
    
    @Query("SELECT AVG(scorePercentage) FROM test_results WHERE userId = :userId")
    suspend fun getAverageScore(userId: String): Double?
    
    @Query("SELECT COUNT(*) FROM test_results WHERE userId = :userId AND passed = 1")
    suspend fun getPassedTestsCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM test_results WHERE userId = :userId")
    suspend fun getTotalTestsCount(userId: String): Int
}
