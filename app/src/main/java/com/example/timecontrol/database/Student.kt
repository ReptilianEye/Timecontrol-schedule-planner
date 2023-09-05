package com.example.timecontrol.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date
import java.util.Locale

data class Level(val level: String, val skills: List<String>)


@Entity(tableName = "students_data_table")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = "phone_number") val phoneNumber: String,

    @ColumnInfo(name = "name") val name: String,

    @ColumnInfo(name = "lastname") val lastname: String,

    @ColumnInfo(name = "level") val level: String,

    @ColumnInfo(name = "birth_date") val birthDate: LocalDate,

    @ColumnInfo(name = "place_of_stay") val placeOfStay: String,

    @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate,

    @ColumnInfo(name = "departure_date") val departureDate: LocalDate,
)

val Levels = listOf(
    Level(
        "1A", listOf(
            "SEA (Spot, Environment, Activity) assessment",
            "Hold, carry, and secure a kite on land",
            "Kite setup"
        )
    ), Level(
        "1B", listOf(
            "Safety systems use", "Pre-flight check", "Launch and land as an assistant"
        )
    ), Level(
        "1C", listOf(
            "First piloting and explore the wind window's edge",
            "Let go of the bar",
            "Twist and untwist the lines"
        )
    ), Level(
        "1D", listOf(
            "Fly one-handed",
            "Trim introduction",
            "Walk while flying the kite",
            "Launch and land as a pilot",
            "Wind window theory"
        )
    ), Level(
        "1E", listOf(
            "Inflight quick release activation",
            "Self-land",
            "Equipment packing",
        )
    ), Level(
        "2F", listOf(
            "Enter and exit the water while controlling the kite",
            "Water relaunch",
        )
    ), Level(
        "2G", listOf(
            "Body-drag with 2 hands, kite stable",
            "Body-drag with power stroke",
        )
    ), Level(
        "2H", listOf(
            "Body-drag upwind", "Body-drag with the board", "Self-rescue and pack down introduction"
        )
    ), Level(
        "2I", listOf(
            "ROW (Right of Way) rules introduction", "Steady-pull", "Waterstart", "Controlled stop"
        )
    ), Level(
        "3J", listOf(
            "Control of speed by edging"
        )
    ), Level(
        "3J", listOf(
            "Ride upwind"
        )
    ), Level(
        "3L", listOf(
            "Sliding transition"
        )
    ), Level(
        "3M", listOf(
            "Ride toeside", "Jibe"
        )
    ), Level(
        "3N", listOf(
            "Self-launch", "Self-rescue and pack down in deep water"
        )
    ), Level(
        "4O", listOf(
            "Basic jump"
        )
    ), Level(
        "4P", listOf(
            "Power jibe"
        )
    ), Level(
        "4Q", listOf(
            "Jump with grab"
        )
    ), Level(
        "4R", listOf(
            "Jump transition"
        )
    ), Level(
        "4S", listOf(
            "Rider recovery"
        )
    ), Level(
        "4T", listOf(
            "Board recovery"
        )
    ), Level(
        "4U", listOf(
            "International kiteboarding signs"
        )
    ), Level(
        "4V", listOf(
            "ROW (Right of Way) rules"
        )
    ), Level(
        "4W", listOf(
            "Equipment"
        )
    ), Level(
        "4X", listOf(
            "Weather and tides"
        )
    ), Level(
        "4Y", listOf(
            "Aerodynamics"
        )
    )

)
