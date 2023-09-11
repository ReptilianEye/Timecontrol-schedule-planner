package com.example.timecontrol.viewModelHelp.instructor

import com.example.timecontrol.database.InstructorWithLessons
import java.time.LocalDate

data class AddInstructorState(
    val instructors: List<InstructorWithLessons> = emptyList(),

    val firstName: String = "",
    val firstNameError: String? = null,

    val lastName: String = "",
    val lastNameError: String? = null,

    val nickname: String = "",
    val nicknameError: String? = null,

    val phoneNumber: String = "",
    val phoneNumberError: String? = null,

    val isStationary: Boolean = true,

    val arrivalDate: LocalDate = LocalDate.now(),
    val arrivalDateError: String? = null,

    val departureDate: LocalDate = LocalDate.now().plusWeeks(4),
    val departureDateError: String? = null,

    val sortType: InstructorsSortType = InstructorsSortType.TIME_ADDED,
    val filterType: InstructorsFilterType = InstructorsFilterType.ALL

)
