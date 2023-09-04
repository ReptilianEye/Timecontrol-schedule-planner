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

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "lastname")
    val lastname: String,

    @ColumnInfo(name = "level")
    val level: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: LocalDate,

//    @ColumnInfo(name = "place_of_stay") - WAITS FOR DATABASE MIGRATION
//    val placeOfStay: String,

    @ColumnInfo(name = "arrival_date")
    val arrivalDate: LocalDate,

    @ColumnInfo(name = "departure_date")
    val departureDate: LocalDate,
)