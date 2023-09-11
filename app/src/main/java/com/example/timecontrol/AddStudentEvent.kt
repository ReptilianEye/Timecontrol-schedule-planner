package com.example.timecontrol

import java.time.LocalDate

sealed class AddStudentEvent {
    data class FirstNameChanged(val firstName: String) : AddStudentEvent()
    data class LastNameChanged(val lastName: String) : AddStudentEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : AddStudentEvent()
    data class EmailChanged(val email: String) : AddStudentEvent()
    data class BirthDateChanged(val birthDate: LocalDate) : AddStudentEvent()
    data class PlaceOfStayChanged(val placeOfStay: String) : AddStudentEvent()
    data class ArrivalDateChanged(val arrivalDate: LocalDate) : AddStudentEvent()
    data class DepartureDateChanged(val departureDate: LocalDate) : AddStudentEvent()
    data class LevelChanged(val level: String) : AddStudentEvent()
    object SaveStudent : AddStudentEvent()
    data class SortStudents(val sortType: StudentsSortType) : AddStudentEvent()
    data class FilterStudents(val filterType: StudentsFilterType) : AddStudentEvent()
}