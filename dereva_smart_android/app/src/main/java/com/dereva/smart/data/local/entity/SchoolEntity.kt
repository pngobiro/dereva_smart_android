package com.dereva.smart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dereva.smart.domain.model.DrivingSchool
import com.dereva.smart.domain.model.ModuleSchedule
import com.dereva.smart.domain.model.ProgressReport
import com.dereva.smart.domain.model.SchoolLinking
import java.util.Date

@Entity(tableName = "driving_schools")
data class DrivingSchoolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val location: String,
    val phoneNumber: String,
    val email: String,
    val isVerified: Boolean,
    val totalStudents: Int,
    val averagePassRate: Double,
    val createdAt: Long
)

@Entity(tableName = "module_schedules")
data class ModuleScheduleEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val moduleId: String,
    val moduleName: String,
    val unlockDate: Long,
    val dueDate: Long?,
    val isUnlocked: Boolean,
    val order: Int
)

@Entity(tableName = "progress_reports")
data class ProgressReportEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val schoolId: String,
    val reportDate: Long,
    val completedModules: Int,
    val totalModules: Int,
    val averageTestScore: Double,
    val totalStudyTime: Int,
    val currentStreak: Int,
    val lastActivityDate: Long
)

@Entity(tableName = "school_linkings")
data class SchoolLinkingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val schoolId: String,
    val linkedAt: Long,
    val isActive: Boolean,
    val progressSharingEnabled: Boolean
)

// Converters
fun DrivingSchoolEntity.toDomain(): DrivingSchool {
    return DrivingSchool(
        id = id,
        name = name,
        code = code,
        location = location,
        phoneNumber = phoneNumber,
        email = email,
        isVerified = isVerified,
        totalStudents = totalStudents,
        averagePassRate = averagePassRate,
        createdAt = Date(createdAt)
    )
}

fun DrivingSchool.toEntity(): DrivingSchoolEntity {
    return DrivingSchoolEntity(
        id = id,
        name = name,
        code = code,
        location = location,
        phoneNumber = phoneNumber,
        email = email,
        isVerified = isVerified,
        totalStudents = totalStudents,
        averagePassRate = averagePassRate,
        createdAt = createdAt.time
    )
}

fun ModuleScheduleEntity.toDomain(): ModuleSchedule {
    return ModuleSchedule(
        id = id,
        schoolId = schoolId,
        moduleId = moduleId,
        moduleName = moduleName,
        unlockDate = Date(unlockDate),
        dueDate = dueDate?.let { Date(it) },
        isUnlocked = isUnlocked,
        order = order
    )
}

fun ModuleSchedule.toEntity(): ModuleScheduleEntity {
    return ModuleScheduleEntity(
        id = id,
        schoolId = schoolId,
        moduleId = moduleId,
        moduleName = moduleName,
        unlockDate = unlockDate.time,
        dueDate = dueDate?.time,
        isUnlocked = isUnlocked,
        order = order
    )
}

fun ProgressReportEntity.toDomain(): ProgressReport {
    return ProgressReport(
        id = id,
        userId = userId,
        schoolId = schoolId,
        reportDate = Date(reportDate),
        completedModules = completedModules,
        totalModules = totalModules,
        averageTestScore = averageTestScore,
        totalStudyTime = totalStudyTime,
        currentStreak = currentStreak,
        lastActivityDate = Date(lastActivityDate)
    )
}

fun ProgressReport.toEntity(): ProgressReportEntity {
    return ProgressReportEntity(
        id = id,
        userId = userId,
        schoolId = schoolId,
        reportDate = reportDate.time,
        completedModules = completedModules,
        totalModules = totalModules,
        averageTestScore = averageTestScore,
        totalStudyTime = totalStudyTime,
        currentStreak = currentStreak,
        lastActivityDate = lastActivityDate.time
    )
}

fun SchoolLinkingEntity.toDomain(): SchoolLinking {
    return SchoolLinking(
        id = id,
        userId = userId,
        schoolId = schoolId,
        linkedAt = Date(linkedAt),
        isActive = isActive,
        progressSharingEnabled = progressSharingEnabled
    )
}

fun SchoolLinking.toEntity(): SchoolLinkingEntity {
    return SchoolLinkingEntity(
        id = id,
        userId = userId,
        schoolId = schoolId,
        linkedAt = linkedAt.time,
        isActive = isActive,
        progressSharingEnabled = progressSharingEnabled
    )
}
