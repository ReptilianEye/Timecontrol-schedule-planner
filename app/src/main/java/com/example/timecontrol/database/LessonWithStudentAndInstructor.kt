package com.example.timecontrol.database

import androidx.room.Embedded
import androidx.room.Relation

data class LessonWithStudentAndInstructor(
    @Embedded
    val lesson: Lesson,
    @Relation(
        parentColumn = "lesson_student_id",
        entityColumn = "id",
        entity = Student::class
    )
    val student: Student,

    @Relation(
        parentColumn = "id",
        entityColumn = "instructor_id",
        entity = Instructor::class
    )
    val instructor: Instructor
)