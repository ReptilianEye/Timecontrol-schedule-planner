package com.example.timecontrol.validation.use_case

import com.example.timecontrol.database.InstructorQualification

class ValidateQualification {
    fun execute(qualification: InstructorQualification): ValidationResult {
        return ValidationResult(successful = true)
    }
}