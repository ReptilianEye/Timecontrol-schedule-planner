package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "instructors_data_table")
data class Instructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "instructor_phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "instructor_name")
    val name: String,

    @ColumnInfo(name = "instructor_lastname")
    val lastname: String,

    @ColumnInfo(name = "instructor_nickname")
    val nickname: String,

    @ColumnInfo(name = "instructor_arrival_date")
    val arrivalDate: LocalDate,

    @ColumnInfo(name = "instructor_departure_date")
    val departureDate: LocalDate,
)
