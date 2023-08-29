package com.example.timecontrol.database

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(date: String): LocalDate? {
        return LocalDate.parse(date)
    }
}