package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Entity(tableName = "students_data_table")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "student_phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "student_name")
    val name: String,

    @ColumnInfo(name = "student_lastname")
    val lastname: String,

    @ColumnInfo(name = "student_level")
    val level: String,

    @ColumnInfo(name = "student_birth_date")
    val birthDate: LocalDate,

    @ColumnInfo(name = "student_arrival_date")
    val arrivalDate: LocalDate,

    @ColumnInfo(name = "student_departure_date")
    val departureDate: LocalDate,
)