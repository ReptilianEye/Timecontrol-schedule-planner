package com.example.timecontrol.database

import androidx.room.Embedded
import androidx.room.Relation

data class LessonWithStudentAndInstructor(
    @Embedded
    val lesson: Lesson,
    @Relation(
        parentColumn = "student_id",
        entityColumn = "id",
        entity = Student::class
    )
    val student: Student,

    @Relation(
        parentColumn = "instructor_id",
        entityColumn = "id",
        entity = Instructor::class
    )
    val instructor: Instructor
)

data class StudentWithLessons(
    @Embedded
    val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "student_id",
    )
    val lessons: List<Lesson>
)

data class InstructorWithLessons(
    @Embedded
    val instructor: Instructor,
    @Relation(
        parentColumn = "id",
        entityColumn = "instructor_id",
    )
    val lessons: List<Lesson>
)
