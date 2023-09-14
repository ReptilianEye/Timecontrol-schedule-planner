package com.example.timecontrol.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Insert
    suspend fun insertLesson(lesson: Lesson)

    @Update
    suspend fun updateLesson(lesson: Lesson)

    @Delete
    suspend fun deleteLesson(lesson: Lesson)

    @Query("DELETE FROM Lessons")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM Lessons")
    fun getAllLessons(): Flow<List<LessonWithStudentAndInstructor>>

    @Query("SELECT * FROM Lessons WHERE id = :id")
    fun getLessonById(id: Int): LessonWithStudentAndInstructor
}