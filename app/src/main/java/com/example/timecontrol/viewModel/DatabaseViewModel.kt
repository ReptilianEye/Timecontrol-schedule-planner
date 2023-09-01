package com.example.timecontrol.viewModel

import androidx.lifecycle.ViewModel
import com.example.timecontrol.database.AppRepository
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.database.InstructorWithLessons
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.LessonWithStudentAndInstructor
import com.example.timecontrol.database.Student
import com.example.timecontrol.database.StudentWithLessons

class DatabaseViewModel(private val repository: AppRepository): ViewModel() {
    val instructors = repository.instructors
    val lessons = repository.lessons
    val students = repository.students

    //Instructor operations
    fun getInstructorById(id: Int): InstructorWithLessons {
        return repository.getInstructorById(id)
    }

    suspend fun deleteAllInstructors() {
        return repository.deleteAllInstructors()
    }

    suspend fun insertInstructor(instructor: Instructor) {
        return repository.insertInstructor(instructor)
    }

    suspend fun updateInstructor(instructor: Instructor) {
        return repository.updateInstructor(instructor)
    }

    suspend fun deleteInstructor(instructor: Instructor) {
        return repository.deleteInstructor(instructor)
    }

    //Lesson operations
    fun getLessonById(id: Int): LessonWithStudentAndInstructor {
        return repository.getLessonById(id)
    }

    suspend fun deleteAllLessons() {
        return repository.deleteAllLessons()
    }

    suspend fun insertLesson(lesson: Lesson) {
        return repository.insertLesson(lesson)
    }

    suspend fun updateLesson(lesson: Lesson) {
        return repository.updateLesson(lesson)
    }

    suspend fun deleteLesson(lesson: Lesson) {
        return repository.deleteLesson(lesson)
    }

    //Student operations
    fun getStudentById(id: Int): StudentWithLessons {
        return repository.getStudentById(id)
    }

    suspend fun deleteAllStudents() {
        return repository.deleteAllStudents()
    }

    suspend fun insertStudent(student: Student) {
        return repository.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        return repository.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        return repository.deleteStudent(student)
    }
}