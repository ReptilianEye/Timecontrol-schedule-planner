package com.example.timecontrol.validation.use_case

import java.time.LocalDate

class ValidateArrivalDate {
    fun execute(arrivalDate: LocalDate): ValidationResult {
        return ValidationResult(successful = true)
    }
}