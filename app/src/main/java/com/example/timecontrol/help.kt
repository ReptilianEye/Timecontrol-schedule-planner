package com.example.timecontrol

import androidx.compose.runtime.MutableState
import com.example.timecontrol.database.InstructorQualification
import com.example.timecontrol.database.Levels
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//get max level from checked for student input
fun getMaxLevel(levelsCheckState: List<MutableState<Boolean>>): String {
    for (i in levelsCheckState.lastIndex downTo 0) if (levelsCheckState[i].value) return Levels[i].level
    return "D"
}

fun prettyDate(data: LocalDate, pattern: String = "dd.MM"): String {
    return data.format(DateTimeFormatter.ofPattern(pattern))
}

//fun qualificationToString(qualification: InstructorQualification): String {
//    return when (qualification) {
//        InstructorQualification.ASSISTANT -> "Assistant"
//        InstructorQualification.LVL1 -> "Instructor Level 1"
//        InstructorQualification.LVL2 -> "Instructor Level 2"
//        InstructorQualification.LVL3 -> "Instructor Level 3"
//        InstructorQualification.EXAMINER -> "Examiner"
//    }
//}
object QualificationHelper {
    private val QualificationPairs = listOf(
        Pair(InstructorQualification.ASSISTANT, "Assistant"),
        Pair(InstructorQualification.LVL1, "Instructor Level 1"),
        Pair(InstructorQualification.LVL2, "Instructor Level 2"),
        Pair(InstructorQualification.LVL3, "Instructor Level 3"),
        Pair(InstructorQualification.EXAMINER, "Examiner")
    )

    fun getEnum(qualification: String): InstructorQualification {
        return QualificationPairs.first { it.second == qualification }.first
    }

    fun getString(qualification: InstructorQualification): String {
        return QualificationPairs.first { it.first == qualification }.second
    }
}

//fun getListOfQualificationPairs(): List<Pair<InstructorQualification, String>> {
//    return listOf(
//        Pair(InstructorQualification.ASSISTANT, "Assistant"),
//        Pair(InstructorQualification.LVL1, "Instructor Level 1"),
//        Pair(InstructorQualification.LVL2, "Instructor Level 2"),
//        Pair(InstructorQualification.LVL3, "Instructor Level 3"),
//        Pair(InstructorQualification.EXAMINER, "Examiner")
//    )
//}

