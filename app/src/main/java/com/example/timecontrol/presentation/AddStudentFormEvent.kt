package com.example.timecontrol.presentation

import java.time.LocalDate

sealed class AddStudentFormEvent {
    data class FirstNameChanged(val firstName: String) : AddStudentFormEvent()
    data class LastNameChanged(val lastName: String) : AddStudentFormEvent()
    data class PhoneNumberChanged(val telNo: String) : AddStudentFormEvent()
    data class BirthDateChanged(val birthDate: LocalDate) : AddStudentFormEvent()
    data class PlaceOfStayChanged(val placeOfStay: String) : AddStudentFormEvent()
    data class ArrivalDateChanged(val arrivalDate: LocalDate) : AddStudentFormEvent()
    data class DepartureDateChanged(val departureDate: LocalDate) : AddStudentFormEvent()
    data class LevelChanged(val level: String) : AddStudentFormEvent()
    object Submit : AddStudentFormEvent()
}