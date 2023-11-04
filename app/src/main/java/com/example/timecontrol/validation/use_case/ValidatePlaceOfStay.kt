package com.example.timecontrol.validation.use_case

import java.time.LocalDate

class ValidatePlaceOfStay {
    fun execute(placeOfStay: String): ValidationResult {
        return ValidationResult(successful = true)
    }
}