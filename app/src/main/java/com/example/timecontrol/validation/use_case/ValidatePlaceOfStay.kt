package com.example.timecontrol.validation.use_case

class ValidatePlaceOfStay {
    fun execute(placeOfStay: String): ValidationResult {
        return ValidationResult(successful = true)
    }
}