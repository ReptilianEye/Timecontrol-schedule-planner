package com.example.timecontrol.viewModelHelp.schedule

data class LoadingState(
    val areInstructorsLoading: Boolean = true,
    val areStudentsLoading: Boolean = true,
    val arePreviousLessonsLoading: Boolean = true,
)