package com.example.timecontrol.viewModelHelp.instructor

import com.example.timecontrol.viewModelHelp.student.StudentsFilterType
import com.example.timecontrol.viewModelHelp.student.StudentsSortType
import java.time.LocalDate

sealed class AddInstructorEvent {
    data class FirstNameChanged(val firstName: String) : AddInstructorEvent()
    data class LastNameChanged(val lastName: String) : AddInstructorEvent()
    data class NicknameChanged(val nickname: String) : AddInstructorEvent()
    data class IsStationaryChanged(val isStationary: Boolean) : AddInstructorEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : AddInstructorEvent()
    data class ArrivalDateChanged(val arrivalDate: LocalDate) : AddInstructorEvent()
    data class DepartureDateChanged(val departureDate: LocalDate) : AddInstructorEvent()
    object SaveInstructor : AddInstructorEvent()
    data class SortInstructors(val sortType: InstructorsSortType) : AddInstructorEvent()
    data class FilterInstructors(val filterType: InstructorsFilterType) : AddInstructorEvent()
}