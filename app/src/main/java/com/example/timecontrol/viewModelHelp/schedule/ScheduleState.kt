package com.example.timecontrol.viewModelHelp.schedule

import com.example.timecontrol.database.InstructorWithLessons
import com.example.timecontrol.database.LessonWithStudentAndInstructor
import com.example.timecontrol.database.StudentWithLessons
import java.time.LocalDate


data class ScheduleState
    (
    val instructors: List<InstructorWithLessons> = emptyList(),
    val students: List<StudentWithLessons> = emptyList(),
    val assignedLessons: List<AssignedLesson> = emptyList(),
    val previousLessons: List<LessonWithStudentAndInstructor> = emptyList(),
    val initialized: Boolean = false,
    val lessonTimes: List<Pair<String, String>> = listOf(
        Pair("9:00", "11:00"),
        Pair("11:15", "13:15"),
        Pair("13:30", "15:30"),
        Pair("15:45", "17:45"),
        Pair("18:00", "20:00"),
    ),
    val isSaveBeforeSwitchingDialogOpen: Boolean = false,
    val isEditingEnabled: Boolean = false,
//    val loadingState: LoadingState = LoadingState()
)
