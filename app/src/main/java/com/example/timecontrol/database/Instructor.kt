package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Instructors")
data class Instructor(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = "name") val firstName: String,

    @ColumnInfo(name = "lastname") val lastName: String,

    @ColumnInfo(name = "nickname") val nickname: String,

    @ColumnInfo(name = "phone_number") val phoneNumber: String,

    @ColumnInfo(name = "is_stationary") val isStationary: Boolean,

    @ColumnInfo(name = "qualification") val qualification: InstructorQualification,

    @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate,

    @ColumnInfo(name = "departure_date") val departureDate: LocalDate,
)

fun Instructor.getFullName(): String {
    return "$firstName $lastName"
}

fun Instructor.prettyQualification(): String {
    return when (qualification) {
        InstructorQualification.ASSISTANT -> "Assistant"
        InstructorQualification.LVL1 -> "Instructor Level 1"
        InstructorQualification.LVL2 -> "Instructor Level 2"
        InstructorQualification.LVL3 -> "Instructor Level 3"
        InstructorQualification.EXAMINER -> "Examiner"
    }
}

enum class InstructorQualification {
    ASSISTANT,
    LVL1,
    LVL2,
    LVL3,
    EXAMINER
}