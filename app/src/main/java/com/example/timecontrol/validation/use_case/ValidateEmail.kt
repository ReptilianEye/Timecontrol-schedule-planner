package com.example.timecontrol.validation.use_case

class ValidateEmail {
    fun execute(email: String): ValidationResult {
        return ValidationResult(successful = true) //TODO - make proper validation for email
    }
}