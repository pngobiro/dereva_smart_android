package com.dereva.smart.data.local.dao

import androidx.room.*
import com.dereva.smart.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    
    // Driving Schools
    @Query("SELECT * FROM driving_schools WHERE id = :schoolId")
    suspend fun getSchoolById(schoolId: String): DrivingSchoolEntity?
    
    @Query("SELECT * FROM driving_schools WHERE code = :code")
    suspend fun getSchoolByCode(code: String): DrivingSchoolEntity?
    
    @Query("SELECT * FROM driving_schools WHERE isVerified = 1")
    suspend fun getVerifiedSchools(): List<DrivingSchoolEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: DrivingSchoolEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<DrivingSchoolEntity>)
    
    // School Linking
    @Query("SELECT * FROM school_linkings WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveSchoolLinking(userId: String): SchoolLinkingEntity?
    
    @Query("SELECT * FROM school_linkings WHERE userId = :userId")
    fun getUserSchoolLinkingsFlow(userId: String): Flow<List<SchoolLinkingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchoolLinking(linking: SchoolLinkingEntity)
    
    @Query("UPDATE school_linkings SET isActive = 0 WHERE userId = :userId")
    suspend fun deactivateAllLinkings(userId: String)
    
    @Query("UPDATE school_linkings SET progressSharingEnabled = :enabled WHERE id = :linkingId")
    suspend fun updateProgressSharing(linkingId: String, enabled: Boolean)
    
    // Module Schedules
    @Query("SELECT * FROM module_schedules WHERE schoolId = :schoolId ORDER BY `order` ASC")
    suspend fun getSchoolSchedules(schoolId: String): List<ModuleScheduleEntity>
    
    @Query("SELECT * FROM module_schedules WHERE schoolId = :schoolId AND isUnlocked = 1")
    suspend fun getUnlockedModules(schoolId: String): List<ModuleScheduleEntity>
    
    @Query("SELECT * FROM module_schedules WHERE schoolId = :schoolId ORDER BY `order` ASC")
    fun getSchoolSchedulesFlow(schoolId: String): Flow<List<ModuleScheduleEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ModuleScheduleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ModuleScheduleEntity>)
    
    @Query("UPDATE module_schedules SET isUnlocked = 1 WHERE id = :scheduleId")
    suspend fun unlockModule(scheduleId: String)
    
    // Progress Reports
    @Query("SELECT * FROM progress_reports WHERE userId = :userId AND schoolId = :schoolId ORDER BY reportDate DESC LIMIT 1")
    suspend fun getLatestProgressReport(userId: String, schoolId: String): ProgressReportEntity?
    
    @Query("SELECT * FROM progress_reports WHERE userId = :userId ORDER BY reportDate DESC")
    suspend fun getUserProgressReports(userId: String): List<ProgressReportEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressReport(report: ProgressReportEntity)
    
    @Query("DELETE FROM progress_reports WHERE reportDate < :cutoffDate")
    suspend fun deleteOldReports(cutoffDate: Long)
}
