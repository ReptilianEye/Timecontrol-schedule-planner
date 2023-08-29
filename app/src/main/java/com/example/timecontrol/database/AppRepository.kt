package com.example.timecontrol.database

class AppRepository(private val appDatabase: AppDatabase) {
    private val instructorDao = appDatabase.instructorDao()
    private val lessonDao = appDatabase.lessonDao()
    private val studentDao = appDatabase.studentDao()

    //Instructor operations
    fun getInstructorById(id: Int){
        instructorDao.getInstructorById(id)
    }

    suspend fun insertInstructor(instructor: Instructor) {
        instructorDao.insertInstructor(instructor)
    }

    suspend fun updateInstructor(instructor: Instructor) {
        instructorDao.updateInstructor(instructor)
    }

    suspend fun deleteInstructor(instructor: Instructor) {
        instructorDao.deleteInstructor(instructor)
    }

    //Lesson operations
    fun getLessonById(id: Int){
        lessonDao.getLessonById(id)
    }

    suspend fun insertLesson(lesson: Lesson) {
        lessonDao.insertLesson(lesson)
    }

    suspend fun updateLesson(lesson: Lesson) {
        lessonDao.updateLesson(lesson)
    }

    suspend fun deleteLesson(lesson: Lesson) {
        lessonDao.deleteLesson(lesson)
    }

    //Student operations
    fun getStudentById(id: Int){
        studentDao.getStudentById(id)
    }

    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }
}