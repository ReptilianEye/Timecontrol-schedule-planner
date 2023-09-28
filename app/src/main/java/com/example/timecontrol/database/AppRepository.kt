package com.example.timecontrol.database

import java.time.LocalDate

class AppRepository(private val appDatabase: AppDatabase) {
    private val instructorDao = appDatabase.instructorDao()
    private val lessonDao = appDatabase.lessonDao()
    private val studentDao = appDatabase.studentDao()

    val instructors = instructorDao.getAllInstructors()
    val currentInstructors = instructorDao.getAllCurrentInstructors()

    val lessons = lessonDao.getAllLessons()

    val studentsWithLessons = studentDao.getAllStudentsWithLessons()
    val students = studentDao.getAllStudents()
    val currentStudents = studentDao.getAllCurrentStudents()


    //Instructor operations
    suspend fun getInstructorById(id: Int): InstructorWithLessons {
        return instructorDao.getInstructorById(id)
    }

    suspend fun deleteAllInstructors() {
        return instructorDao.deleteAll()
    }

    suspend fun insertInstructor(instructor: Instructor) {
        return instructorDao.insertInstructor(instructor)
    }

    suspend fun updateInstructor(instructor: Instructor) {
        return instructorDao.updateInstructor(instructor)
    }

    suspend fun deleteInstructor(instructor: Instructor) {
        return instructorDao.deleteInstructor(instructor)
    }

    //Lesson operations
    fun getLessonById(id: Int): LessonWithStudentAndInstructor {
        return lessonDao.getLessonById(id)
    }

    fun getAllLessonsOfTheDay(lessonDay: LocalDate) = lessonDao.getLessonsOnDay(lessonDay)

    suspend fun deleteAllLessons() {
        return lessonDao.deleteAll()
    }

    suspend fun insertLesson(lesson: Lesson) {
        return lessonDao.insertLesson(lesson)
    }

    suspend fun updateLesson(lesson: Lesson) {
        return lessonDao.updateLesson(lesson)
    }

    suspend fun deleteLesson(lesson: Lesson) {
        return lessonDao.deleteLesson(lesson)
    }

    //Student operations
    fun getStudentById(id: Int): StudentWithLessons {
        return studentDao.getStudentById(id)
    }

    suspend fun deleteAllStudents() {
        return studentDao.deleteAll()
    }

    suspend fun insertStudent(student: Student) {
        return studentDao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        return studentDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        return studentDao.deleteStudent(student)
    }
}