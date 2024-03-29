package com.example.timecontrol.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.AppRepository
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.database.InstructorWithLessons
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.LessonWithStudentAndInstructor
import com.example.timecontrol.database.Student
import com.example.timecontrol.database.StudentWithLessons
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class DatabaseViewModel(private val repository: AppRepository) : ViewModel() {
    val instructors = repository.instructors

    val lessons = repository.lessons

    val students = repository.students
    val currentStudents = repository.currentStudents

    //Instructor operations
    fun getInstructorById(id: Int): InstructorWithLessons {
        val result: Deferred<InstructorWithLessons> = viewModelScope.async(Dispatchers.IO) {
            repository.getInstructorById(id)
        }
        val final = runBlocking(Dispatchers.IO) {
            result.await()
        }
        return final
    }

    fun getAllCurrentInstructors(date: LocalDate = LocalDate.now()) =
        repository.getAllCurrentInstructors(date)

    fun getInstructorHoursTaught(id: Int): Float {
        return getInstructorById(id).lessons.sumOf { it.duration ?: 0 }.toFloat() / 60
    }

    fun getInstructorStudentCount(id: Int): Int {
        return getInstructorById(id).lessons.distinctBy { id }.size
    }

    fun deleteAllInstructors() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllInstructors()
        }
    }

    fun insertInstructor(instructor: Instructor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertInstructor(instructor)
        }
    }

    fun updateInstructor(instructor: Instructor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateInstructor(instructor)
        }
    }

    fun deleteInstructor(instructor: Instructor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteInstructor(instructor)
        }
    }

    //Lesson operations
    fun getAllLessonsFromDate(lessonDate: LocalDate) = repository.getAllLessonsFromDate(lessonDate)
    fun areAnyLessonsFromDate(lessonDate: LocalDate) = repository.areAnyLessonsFromDate(lessonDate)
    fun deleteAllLessonsFromDate(lessonDate: LocalDate) {
        repository.deleteAllLessonsFromDate(lessonDate)
    }

    fun getLessonById(id: Int): LessonWithStudentAndInstructor {
        val result: Deferred<LessonWithStudentAndInstructor> =
            viewModelScope.async(Dispatchers.IO) {
                repository.getLessonById(id)
            }
        val final = runBlocking(Dispatchers.IO) {
            result.await()
        }
        return final
    }

    fun deleteAllLessons() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllLessons()
        }
    }

    fun insertLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLesson(lesson)
        }
    }

    fun updateLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLesson(lesson)
        }
    }

    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLesson(lesson)
        }
    }

    fun deleteLessonById(lessonId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLessonById(lessonId)
        }
    }

    //Student operations
    fun getStudentById(id: Int): StudentWithLessons {
        val result: Deferred<StudentWithLessons> = viewModelScope.async(Dispatchers.IO) {
            repository.getStudentById(id)
        }
        val final = runBlocking(Dispatchers.IO) {
            result.await()
        }
        return final
    }

    fun getAllCurrentStudents(date: LocalDate = LocalDate.now()) =
        repository.getAllCurrentStudents(date)

    fun deleteAllStudents() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllStudents()
        }
    }

    fun insertStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStudent(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
        }
    }
}