package com.example.timecontrol.validation

import java.time.LocalDate

data class AddStudentFormState(
    val firstName: String = "",
    val firstNameError: String? = null,

    val lastName: String = "",
    val lastNameError: String? = null,

    val phoneNumber: String = "",
    val phoneNumberError: String? = null,

    val birthDate: LocalDate = LocalDate.now(),
    val birthDateError: String? = null,

    val placeOfStay: String = "",
    val placeOfStayError: String? = null,

    val arrivalDate: LocalDate = LocalDate.now(),
    val arrivalDateError: String? = null,

    val departureDate: LocalDate = LocalDate.now().plusWeeks(1),
    val departureDateError: String? = null,

    val level: String = "",
    val levelError: String? = null
)
