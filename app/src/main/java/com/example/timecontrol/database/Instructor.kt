package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "instructors_data_table")
data class Instructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "lastname")
    val lastname: String,

    @ColumnInfo(name = "nickname")
    val nickname: String,

    @ColumnInfo(name = "arrival_date")
    val arrivalDate: LocalDate,

    @ColumnInfo(name = "departure_date")
    val departureDate: LocalDate,
)
