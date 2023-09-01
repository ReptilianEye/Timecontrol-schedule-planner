package com.example.timecontrol.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM students_data_table")
    fun getAllStudents(): Flow<List<StudentWithLessons>>

    @Query("SELECT * FROM students_data_table WHERE id = :id")
    fun getStudentById(id: Int): StudentWithLessons
}