package com.example.timecontrol.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface InstructorDao {
    @Insert
    suspend fun insertInstructor(instructor: Instructor)

    @Update
    suspend fun updateInstructor(instructor: Instructor)

    @Delete
    suspend fun deleteInstructor(instructor: Instructor)

    @Query("DELETE FROM Instructors")
    suspend fun deleteAll()

    @Query("SELECT * FROM Instructors")
    fun getAllInstructors(): Flow<List<InstructorWithLessons>>

    @Query("SELECT * FROM Instructors WHERE :date BETWEEN arrival_date and departure_date")
    fun getAllCurrentInstructors(date: LocalDate = LocalDate.now()): Flow<List<InstructorWithLessons>>

    @Query(
        "SELECT * FROM Instructors WHERE departure_date < DATE()"
    )
    fun getAllPastInstructors(): Flow<List<InstructorWithLessons>>


    @Query("SELECT * FROM Instructors WHERE id = :id")
    suspend fun getInstructorById(id: Int): InstructorWithLessons

}