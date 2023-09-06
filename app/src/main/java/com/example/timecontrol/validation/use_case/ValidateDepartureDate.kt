package com.example.timecontrol.validation.use_case

import java.time.LocalDate

class ValidateDepartureDate {
    fun execute(departureDate: LocalDate): ValidationResult {
        return ValidationResult(successful = true)
    }
}