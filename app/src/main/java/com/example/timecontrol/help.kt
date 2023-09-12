package com.example.timecontrol

import androidx.compose.runtime.MutableState
import com.example.timecontrol.database.Levels
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//get max level from checked for student input
fun getMaxLevel(levelsCheckState: List<MutableState<Boolean>>): String {
    for (i in levelsCheckState.lastIndex downTo 0)
        if (levelsCheckState[i].value)
            return Levels[i].level
    return "D"
}

fun prettyDate(data: LocalDate, pattern: String = "dd.MM"): String {
    return data.format(DateTimeFormatter.ofPattern(pattern))
}