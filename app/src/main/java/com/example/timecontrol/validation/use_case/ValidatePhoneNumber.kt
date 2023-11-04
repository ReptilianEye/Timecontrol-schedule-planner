package com.example.timecontrol.validation.use_case

class ValidatePhoneNumber {
    fun execute(number: String): ValidationResult {
        if (number.length < 9)
            return ValidationResult(
                successful = false,
                errorMessage = "Tel number has to be at least 9 digits long"
            )
        if (number.any { !it.isDigit() })
            return ValidationResult(
                successful = false,
                errorMessage = "Tel numbers should contain only digits"
            )
        return ValidationResult(successful = true)
    }
}