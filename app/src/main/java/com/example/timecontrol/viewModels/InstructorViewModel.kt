package com.example.timecontrol.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.validation.use_case.ValidateArrivalDate
import com.example.timecontrol.validation.use_case.ValidateDepartureDate
import com.example.timecontrol.validation.use_case.ValidateFirstName
import com.example.timecontrol.validation.use_case.ValidateLastName
import com.example.timecontrol.validation.use_case.ValidateNickname
import com.example.timecontrol.validation.use_case.ValidatePhoneNumber
import com.example.timecontrol.validation.use_case.ValidateQualification
import com.example.timecontrol.viewModelHelp.instructor.AddInstructorEvent
import com.example.timecontrol.viewModelHelp.instructor.AddInstructorState
import com.example.timecontrol.viewModelHelp.instructor.InstructorsFilterType
import com.example.timecontrol.viewModelHelp.instructor.InstructorsSortType
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InstructorViewModel(
    private val validateFirstName: ValidateFirstName = ValidateFirstName(),
    private val validateLastName: ValidateLastName = ValidateLastName(),
    private val validateNickname: ValidateNickname = ValidateNickname(),
    private val validatePhoneNumber: ValidatePhoneNumber = ValidatePhoneNumber(),
    private val validateQualification: ValidateQualification = ValidateQualification(),
    private val validateArrivalDate: ValidateArrivalDate = ValidateArrivalDate(),
    private val validateDepartureDate: ValidateDepartureDate = ValidateDepartureDate(),
    private val databaseViewModel: DatabaseViewModel  // nie jestem przekonany
) : ViewModel() {
    private val _sortType = MutableStateFlow(InstructorsSortType.TIME_ADDED)
    private val _filterType = MutableStateFlow(InstructorsFilterType.ALL)

    private val _instructors = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            //implement in future
            InstructorsSortType.TIME_ADDED -> databaseViewModel.instructors
            InstructorsSortType.NICKNAME -> databaseViewModel.instructors
            InstructorsSortType.DEPARTURE_DATE -> databaseViewModel.instructors
            InstructorsSortType.DEPARTURE_DATE_DESC -> databaseViewModel.instructors
            InstructorsSortType.HOURS_TAUGHT -> databaseViewModel.instructors
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )
    private val _state = MutableStateFlow(AddInstructorState())
    val state = combine(
        _state, _sortType, _filterType, _instructors
    ) { state, sortType, filterType, instructors ->
        state.copy(
            instructors = instructors, sortType = sortType, filterType = filterType
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AddInstructorState()
    )

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    fun onEvent(event: AddInstructorEvent) {
        when (event) {
            is AddInstructorEvent.FirstNameChanged -> _state.update { it.copy(firstName = event.firstName) }
            is AddInstructorEvent.LastNameChanged -> _state.update { it.copy(lastName = event.lastName) }
            is AddInstructorEvent.NicknameChanged -> _state.update { it.copy(nickname = event.nickname) }
            is AddInstructorEvent.IsStationaryChanged -> _state.update { it.copy(isStationary = event.isStationary) }
            is AddInstructorEvent.QualificationChanged -> _state.update { it.copy(qualification = event.qualification) }
            is AddInstructorEvent.PhoneNumberChanged -> _state.update { it.copy(phoneNumber = event.phoneNumber) }
            is AddInstructorEvent.ArrivalDateChanged -> _state.update { it.copy(arrivalDate = event.arrivalDate) }
            is AddInstructorEvent.DepartureDateChanged -> _state.update { it.copy(departureDate = event.departureDate) }

            AddInstructorEvent.SaveInstructor -> submitData()

            is AddInstructorEvent.SortInstructors -> _sortType.value = event.sortType
            is AddInstructorEvent.FilterInstructors -> _filterType.value = event.filterType
            AddInstructorEvent.HideDialog -> resetState()
            AddInstructorEvent.ShowDialog -> _state.update { it.copy(isAddingInstructor = true) }
        }
    }

    private fun submitData() {
        val firstNameResult = validateFirstName.execute(state.value.firstName)
        val lastNameResult = validateLastName.execute(state.value.lastName)
        val nicknameResult = validateNickname.execute(state.value.nickname)
        val qualificationResult = validateQualification.execute(state.value.qualification)
        val phoneNumberResult = validatePhoneNumber.execute(state.value.phoneNumber)
        val arrivalDateResult = validateArrivalDate.execute(state.value.arrivalDate)
        val departureDateResult = validateDepartureDate.execute(state.value.departureDate)

        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            nicknameResult,
            qualificationResult,
            phoneNumberResult,
            arrivalDateResult,
            departureDateResult,
        ).any { !it.successful }

        _state.update {
            it.copy(
                firstNameError = firstNameResult.errorMessage,
                lastNameError = lastNameResult.errorMessage,
                nicknameError = nicknameResult.errorMessage,
                qualificationError = qualificationResult.errorMessage,
                phoneNumberError = phoneNumberResult.errorMessage,
                arrivalDateError = arrivalDateResult.errorMessage,
                departureDateError = departureDateResult.errorMessage,
            )

        }
        if (hasError) return

        val new = Instructor(
            id = 0,
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            nickname = if (state.value.nickname != "") state.value.nickname else state.value.firstName,
            isStationary = state.value.isStationary,
            qualification = state.value.qualification,
            phoneNumber = state.value.phoneNumber,
            arrivalDate = state.value.arrivalDate,
            departureDate = state.value.departureDate,
        )
        viewModelScope.launch {
            async { databaseViewModel.insertInstructor(new) }
            async { validationEventChannel.send(ValidationEvent.Success) }
        }
        resetState()
//        reset(state)  - https://www.youtube.com/watch?v=bOd3wO0uFr8 29:00
    }

    private fun resetState() {
        _state.update { AddInstructorState() }
    }


    fun getInstructorHoursTaught(instructor: Instructor): Float {
        return databaseViewModel.getInstructorHoursTaught(instructor.id)
    }

    fun getInstructorStudentCount(instructor: Instructor): Int {
        return databaseViewModel.getInstructorStudentCount(instructor.id)
    }


    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}