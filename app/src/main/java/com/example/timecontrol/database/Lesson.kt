package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = "date") val date: LocalDate,

    @ColumnInfo(name = "lesson_time") val lessonTime: Pair<String, String>,

    @ColumnInfo(name = "duration") val duration: Int? = null,

    @ColumnInfo(name = "level_after") val levelAfter: String? = null,

    @ColumnInfo(name = "took_place") val tookPlace: Boolean = false,

    @ColumnInfo(name = "student_id") val studentId: Int,

    @ColumnInfo(name = "instructor_id") val instructorId: Int
)
