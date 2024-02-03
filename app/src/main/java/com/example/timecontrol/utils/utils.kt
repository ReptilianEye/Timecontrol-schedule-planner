package com.example.timecontrol

import androidx.compose.runtime.MutableState
import com.example.timecontrol.database.LEVELS
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

//get max level from checked for student input
fun getMaxLevel(levelsCheckState: List<MutableState<Boolean>>): String {
    for (i in levelsCheckState.lastIndex downTo 0) if (levelsCheckState[i].value) return LEVELS[i].level
    return "D"
}

fun LocalDate.pretty(pattern: String = "dd.MM"): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.toMillis() = this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

fun Pair<String, String>.prettyTime() = "$first - $second"