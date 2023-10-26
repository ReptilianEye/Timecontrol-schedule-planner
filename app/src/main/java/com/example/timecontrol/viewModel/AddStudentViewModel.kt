package com.example.timecontrol.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.viewModelHelp.student.AddStudentEvent
import com.example.timecontrol.viewModelHelp.student.AddStudentState
import com.example.timecontrol.viewModelHelp.student.StudentsFilterType
import com.example.timecontrol.viewModelHelp.student.StudentsSortType
import com.example.timecontrol.database.Student
import com.example.timecontrol.validation.use_case.ValidateArrivalDate
import com.example.timecontrol.validation.use_case.ValidateBirthDate
import com.example.timecontrol.validation.use_case.ValidateDepartureDate
import com.example.timecontrol.validation.use_case.ValidateEmail
import com.example.timecontrol.validation.use_case.ValidateFirstName
import com.example.timecontrol.validation.use_case.ValidateLastName
import com.example.timecontrol.validation.use_case.ValidateLevel
import com.example.timecontrol.validation.use_case.ValidatePlaceOfStay
import com.example.timecontrol.validation.use_case.ValidatePhoneNumber
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

class AddStudentViewModel(
    private val validateFirstName: ValidateFirstName = ValidateFirstName(),
    private val validateLastName: ValidateLastName = ValidateLastName(),
    private val validatePhoneNumber: ValidatePhoneNumber = ValidatePhoneNumber(),
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validateBirthDate: ValidateBirthDate = ValidateBirthDate(),
    private val validatePlaceOfStay: ValidatePlaceOfStay = ValidatePlaceOfStay(),
    private val validateArrivalDate: ValidateArrivalDate = ValidateArrivalDate(),
    private val validateDepartureDate: ValidateDepartureDate = ValidateDepartureDate(),
    private val validateLevel: ValidateLevel = ValidateLevel(),
    private val databaseViewModel: DatabaseViewModel  // nie jestem przekonany
) : ViewModel() {
    private val _sortType = MutableStateFlow(StudentsSortType.TIME_ADDED)
    private val _filterType = MutableStateFlow(StudentsFilterType.ALL)

    private val _students = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            //implement in future
            StudentsSortType.TIME_ADDED -> databaseViewModel.students
            StudentsSortType.FIRST_NAME -> databaseViewModel.students
            StudentsSortType.LAST_NAME -> databaseViewModel.students
            StudentsSortType.DEPARTURE_DATE -> databaseViewModel.students
            StudentsSortType.DEPARTURE_DATE_DESC -> databaseViewModel.students
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    private val _state = MutableStateFlow(AddStudentState())
    val state = combine(
        _state, _sortType, _filterType, _students
    ) { state, sortType, filterType, students ->
        state.copy(
            students = students, sortType = sortType, filterType = filterType
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AddStudentState()
    )

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    fun onEvent(event: AddStudentEvent) {
        when (event) {
            is AddStudentEvent.FirstNameChanged -> _state.update { it.copy(firstName = event.firstName) }

            is AddStudentEvent.LastNameChanged -> _state.update { it.copy(lastName = event.lastName) }

            is AddStudentEvent.PhoneNumberChanged -> _state.update { it.copy(phoneNumber = event.phoneNumber) }

            is AddStudentEvent.EmailChanged -> _state.update { it.copy(email = event.email) }

            is AddStudentEvent.BirthDateChanged -> _state.update { it.copy(birthDate = event.birthDate) }

            is AddStudentEvent.PlaceOfStayChanged -> _state.update { it.copy(placeOfStay = event.placeOfStay) }

            is AddStudentEvent.ArrivalDateChanged -> _state.update { it.copy(arrivalDate = event.arrivalDate) }

            is AddStudentEvent.DepartureDateChanged -> _state.update { it.copy(departureDate = event.departureDate) }


            is AddStudentEvent.LevelChanged -> _state.update { it.copy(level = event.level) }

            is AddStudentEvent.SaveStudent -> submitData()

            is AddStudentEvent.Cancel -> resetState()

            is AddStudentEvent.SortStudents -> _sortType.value = event.sortType
            is AddStudentEvent.FilterStudents -> _filterType.value = event.filterType

        }
    }

    private fun submitData() {
        val firstNameResult = validateFirstName.execute(state.value.firstName)
        val lastNameResult = validateLastName.execute(state.value.lastName)
        val phoneNumberResult = validatePhoneNumber.execute(state.value.phoneNumber)
        val emailResult = validateEmail.execute(state.value.email)
        val birthDateResult = validateBirthDate.execute(state.value.birthDate)
        val placeOfStayResult = validatePlaceOfStay.execute(state.value.placeOfStay)
        val arrivalDateResult = validateArrivalDate.execute(state.value.arrivalDate)
        val departureDateResult = validateDepartureDate.execute(state.value.departureDate)
        val levelResult = validateLevel.execute(state.value.level)

        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            phoneNumberResult,
            emailResult,
            birthDateResult,
            placeOfStayResult,
            arrivalDateResult,
            departureDateResult,
            levelResult
        ).any { !it.successful }

        _state.update {
            it.copy(
                firstNameError = firstNameResult.errorMessage,
                lastNameError = lastNameResult.errorMessage,
                phoneNumberError = phoneNumberResult.errorMessage,
                emailError = emailResult.errorMessage,
                birthDateError = birthDateResult.errorMessage,
                placeOfStayError = placeOfStayResult.errorMessage,
                arrivalDateError = arrivalDateResult.errorMessage,
                departureDateError = departureDateResult.errorMessage,
                levelError = levelResult.errorMessage
            )

        }
        if (hasError) return

        val new = Student(
            id = 0,
            firstName = state.value.firstName,
            lastName = state.value.lastName,
            phoneNumber = state.value.phoneNumber,
            email = state.value.email,
            birthDate = state.value.birthDate,
            placeOfStay = state.value.placeOfStay,
            arrivalDate = state.value.arrivalDate,
            departureDate = state.value.departureDate,
            level = state.value.level
        )
        viewModelScope.launch {
            async { databaseViewModel.insertStudent(new) }
            async { validationEventChannel.send(ValidationEvent.Success) }
        }
        resetState()
    }

    private fun resetState() {
        _state.update { AddStudentState() }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}