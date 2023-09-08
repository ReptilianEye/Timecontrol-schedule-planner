package com.example.timecontrol.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timecontrol.studentsinfo.StudentsInfo
import com.example.timecontrol.viewModel.DatabaseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StudentDetailsScreen(
    viewModel: DatabaseViewModel,
    navController: NavController,
    studentId: Int?
) {
    val student = viewModel.getStudentById(studentId!!)
    Column(
        modifier = Modifier
            .fillMaxHeight(.8f)
            .padding(2.dp)
            .verticalScroll(rememberScrollState())
    ) {
        StudentsInfo(
            level = student.student.level,
            fullName = "${student.student.firstName} ${student.student.lastName}",
            phoneNumber = student.student.phoneNumber,
            email = student.student.email,
            arrivalDate = prettyDate(student.student.arrivalDate),
            departureDate = prettyDate(student.student.departureDate),
            birthDate = prettyDate(student.student.birthDate, "dd.MM.YY"),
            placeOfStay = student.student.placeOfStay,
        )
    }


}

fun prettyDate(data: LocalDate, pattern: String = "dd.MM"): String {
    return data.format(DateTimeFormatter.ofPattern(pattern))
}