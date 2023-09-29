package com.example.timecontrol.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM Students")
    suspend fun deleteAll()

    @Query("SELECT * FROM Students")
    fun getAllStudentsWithLessons(): Flow<List<StudentWithLessons>>

    @Query("SELECT * FROM Students")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM Students WHERE arrival_date <= :date <= departure_date")
    fun getAllCurrentStudents(date: LocalDate = LocalDate.now()): Flow<List<StudentWithLessons>>

    @Query("SELECT * FROM Students WHERE id = :id")
    fun getStudentById(id: Int): StudentWithLessons
}