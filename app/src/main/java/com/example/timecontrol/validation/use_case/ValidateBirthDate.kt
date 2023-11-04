package com.example.timecontrol.validation.use_case

import java.time.LocalDate

class ValidateBirthDate {
    fun execute(birthDate: LocalDate): ValidationResult {
        if (birthDate > LocalDate.now())
            return ValidationResult(
                successful = false,
                errorMessage = "Birth date has to be earlier than today"
            )
        return ValidationResult(successful = true)
    }
}