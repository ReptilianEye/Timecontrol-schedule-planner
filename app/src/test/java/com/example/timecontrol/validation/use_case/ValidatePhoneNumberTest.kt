package com.example.timecontrol.validation.use_case

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ValidatePhoneNumberTest {

    private lateinit var validatePhoneNumber: ValidatePhoneNumber

    @Before
    fun setUp() {
        validatePhoneNumber = ValidatePhoneNumber()
    }

    @Test
    fun `Phone number shorter than 9 characters, returns error`() {
        val result = validatePhoneNumber.execute("12345")
        assertEquals(result.successful, false)
    }

    @Test
    fun `Phone number contains digits and alpha characters, returns error`() {
        val result = validatePhoneNumber.execute("12345avbaw")
        assertEquals(result.successful, false)
    }

    @Test
    fun `Phone number has 9 digit, returns success`() {
        val result = validatePhoneNumber.execute("123456789")
        assertEquals(result.successful, true)
    }
}