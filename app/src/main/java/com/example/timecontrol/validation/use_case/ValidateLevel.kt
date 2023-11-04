package com.example.timecontrol.validation.use_case

class ValidateLevel {
    fun execute(level: String): ValidationResult {
        return ValidationResult(successful = true)
    }
}