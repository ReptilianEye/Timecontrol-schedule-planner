package com.example.timecontrol.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InstructorDao {
    @Insert
    suspend fun insertInstructor(instructor: Instructor)

    @Update
    suspend fun updateInstructor(instructor: Instructor)

    @Delete
    suspend fun deleteInstructor(instructor: Instructor)

    @Query("DELETE FROM instructors_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM instructors_data_table")
    fun getAllInstructors(): Flow<List<Instructor>>
}