package com.example.timecontrol.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.validation.AddStudentFormState
import com.example.timecontrol.validation.use_case.ValidateArrivalDate
import com.example.timecontrol.validation.use_case.ValidateBirthDate
import com.example.timecontrol.validation.use_case.ValidateDepartureDate
import com.example.timecontrol.validation.use_case.ValidateFirstName
import com.example.timecontrol.validation.use_case.ValidateLastName
import com.example.timecontrol.validation.use_case.ValidateLevel
import com.example.timecontrol.validation.use_case.ValidatePlaceOfStay
import com.example.timecontrol.validation.use_case.ValidatePhoneNumber
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddStudentViewModel(
    private val validateFirstName: ValidateFirstName = ValidateFirstName(),
    private val validateLastName: ValidateLastName = ValidateLastName(),
    private val validatePhoneNumber: ValidatePhoneNumber = ValidatePhoneNumber(),
    private val validateBirthDate: ValidateBirthDate = ValidateBirthDate(),
    private val validatePlaceOfStay: ValidatePlaceOfStay = ValidatePlaceOfStay(),
    private val validateArrivalDate: ValidateArrivalDate = ValidateArrivalDate(),
    private val validateDepartureDate: ValidateDepartureDate = ValidateDepartureDate(),
    private val validateLevel: ValidateLevel = ValidateLevel()
) : ViewModel() {
    var state by mutableStateOf(AddStudentFormState())
    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()
    fun onEvent(event: AddStudentFormEvent) {
        when (event) {
            is AddStudentFormEvent.FirstNameChanged -> state =
                state.copy(firstName = event.firstName)

            is AddStudentFormEvent.LastNameChanged -> state = state.copy(lastName = event.lastName)

            is AddStudentFormEvent.PhoneNumberChanged -> state = state.copy(phoneNumber = event.telNo)

            is AddStudentFormEvent.BirthDateChanged -> state =
                state.copy(birthDate = event.birthDate)

            is AddStudentFormEvent.PlaceOfStayChanged -> state =
                state.copy(placeOfStay = event.placeOfStay)

            is AddStudentFormEvent.ArrivalDateChanged -> state =
                state.copy(arrivalDate = event.arrivalDate)

            is AddStudentFormEvent.DepartureDateChanged -> state =
                state.copy(departureDate = event.departureDate)

            is AddStudentFormEvent.LevelChanged -> state = state.copy(level = event.level)

            is AddStudentFormEvent.Submit -> {
                submitData()
            }
        }
        println(state)
    }

    private fun submitData() {
        val firstNameResult = validateFirstName.execute(state.firstName)
        val lastNameResult = validateLastName.execute(state.lastName)
        val telNoResult = validatePhoneNumber.execute(state.phoneNumber)
        val birthDateResult = validateBirthDate.execute(state.birthDate)
        val placeOfStayResult = validatePlaceOfStay.execute(state.placeOfStay)
        val arrivalDateResult = validateArrivalDate.execute(state.arrivalDate)
        val departureDateResult = validateDepartureDate.execute(state.departureDate)
        val levelResult = validateLevel.execute(state.level)
        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            telNoResult,
            birthDateResult,
            placeOfStayResult,
            arrivalDateResult,
            departureDateResult,
            levelResult
        ).any { !it.successful }
        if (hasError) {
            state = state.copy(
                firstNameError = firstNameResult.errorMessage,
                lastNameError = lastNameResult.errorMessage,
                phoneNumberError = telNoResult.errorMessage,
                birthDateError = birthDateResult.errorMessage,
                placeOfStayError = placeOfStayResult.errorMessage,
                arrivalDateError = arrivalDateResult.errorMessage,
                departureDateError = departureDateResult.errorMessage,
                levelError = levelResult.errorMessage
            )
            return
        }
        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}