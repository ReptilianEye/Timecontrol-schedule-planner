package com.example.timecontrol.validation.use_case

open class ValidateName {
    open val fieldName = "This field"
    open fun execute(field: String): ValidationResult {
        if (field.isEmpty()) return ValidationResult(
            successful = false, errorMessage = "$fieldName cannot be empty."
        )
        if (field.any { it.isDigit() }) {
            return ValidationResult(successful = false, "$fieldName cannot contain numbers.")
        }
        return ValidationResult(successful = true)
    }
}

class ValidateFirstName : ValidateName() {
    override val fieldName = "First name"
//    override fun execute(firstName: String): ValidationResult = super.execute(firstName)
}

class ValidateLastName : ValidateName() {
    override val fieldName = "Last name"
//    override fun execute(lastName: String): ValidationResult = super.execute(lastName)
}

class ValidateNickname : ValidateName() {
    override val fieldName = "Nickname"
}

