package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "lessons_data_table")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "lesson_date")
    val date: LocalDate,

    @ColumnInfo(name = "lesson_duration")
    val duration: Int,

    @ColumnInfo(name = "lesson_level_after")
    val levelAfter: String,

    @ColumnInfo(name = "lesson_student_id")
    val studentId: Int,

    @ColumnInfo(name = "lesson_instructor_id")
    val instructorId: Int,

    @Embedded
    val student: Student
)
