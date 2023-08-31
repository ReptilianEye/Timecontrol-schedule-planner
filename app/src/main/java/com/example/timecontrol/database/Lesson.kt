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

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "level_after")
    val levelAfter: String,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "instructor_id")
    val instructorId: Int
)
