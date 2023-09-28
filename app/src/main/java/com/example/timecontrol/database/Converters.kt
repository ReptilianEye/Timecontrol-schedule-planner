package com.example.timecontrol.database

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(date: String): LocalDate? {
        return LocalDate.parse(date)
    }

    @TypeConverter
    fun fromPair(pair: Pair<String, String>): String {
        return "${pair.first}-${pair.second}"
    }

    @TypeConverter
    fun toPair(pair: String): Pair<String, String> {
        val l = pair.split("-")
        if (l.size != 2)
            throw Error("Date split unsuccessful: $l")
        return Pair(l[0], l[1])
    }
}