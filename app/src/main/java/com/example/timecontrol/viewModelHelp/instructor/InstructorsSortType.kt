package com.example.timecontrol.viewModelHelp.instructor

enum class InstructorsSortType(val parameterName: String, val desc: Boolean = false) {
    TIME_ADDED(""),
    NICKNAME("nickname"),
    DEPARTURE_DATE("departure_date"),
    DEPARTURE_DATE_DESC("departure_date", true),
    HOURS_TAUGHT("HOURS_TAUGHT")
}