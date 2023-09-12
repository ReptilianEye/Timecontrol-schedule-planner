package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "Students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = "name") val firstName: String,

    @ColumnInfo(name = "lastname") val lastName: String,

    @ColumnInfo(name = "phone_number") val phoneNumber: String,

    @ColumnInfo(name = "email") val email: String,

    @ColumnInfo(name = "birth_date") val birthDate: LocalDate,

    @ColumnInfo(name = "place_of_stay") val placeOfStay: String,

    @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate,

    @ColumnInfo(name = "departure_date") val departureDate: LocalDate,

    @ColumnInfo(name = "level") val level: String,
)
fun Student.getFullName(): String {
    return "$firstName $lastName"
}
