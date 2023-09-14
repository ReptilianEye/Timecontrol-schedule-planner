package com.example.timecontrol.viewModelHelp.student

import com.example.timecontrol.database.Student
import java.time.LocalDate

data class AddStudentState(
    val students: List<Student> = emptyList(),

    val firstName: String = "",
    val firstNameError: String? = null,

    val lastName: String = "",
    val lastNameError: String? = null,

    val phoneNumber: String = "",
    val phoneNumberError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val birthDate: LocalDate = LocalDate.now(),
    val birthDateError: String? = null,

    val placeOfStay: String = "",
    val placeOfStayError: String? = null,

    val arrivalDate: LocalDate = LocalDate.now(),
    val arrivalDateError: String? = null,

    val departureDate: LocalDate = LocalDate.now().plusWeeks(1),
    val departureDateError: String? = null,

    val level: String = "",
    val levelError: String? = null,

    val sortType: StudentsSortType = StudentsSortType.TIME_ADDED,
    val filterType: StudentsFilterType = StudentsFilterType.ALL

)
