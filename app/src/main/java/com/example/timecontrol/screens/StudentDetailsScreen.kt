package com.example.timecontrol.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.Levels
import com.example.timecontrol.studentsinfo.StudentsInfo
import com.example.timecontrol.studentslistitem.Level
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.White80
import com.example.timecontrol.viewModel.DatabaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Random

@Composable
fun StudentDetailsScreen(
    viewModel: DatabaseViewModel, navController: NavController, studentId: Int?
) {
    val student = viewModel.getStudentById(studentId!!)
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(2.dp)
    ) {
        item {
            StudentsInfo(level = student.student.level,
                fullName = "${student.student.firstName} ${student.student.lastName}",
                phoneNumber = student.student.phoneNumber,
                email = student.student.email,
                arrivalDate = prettyDate(student.student.arrivalDate),
                departureDate = prettyDate(student.student.departureDate),
                birthDate = prettyDate(student.student.birthDate, "dd.MM.YY"),
                placeOfStay = student.student.placeOfStay,
                onEditClicked = {/* TODO - add edit functionality */ })
        }
        item {
            Text(
                text = "Lessons:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)
            )
        }
        item {
            Row(Modifier.background(Blue10)) {
                TableCell(text = "Date", header = true)
                TableCell(text = "Level", header = true)
                TableCell(text = "Duration", header = true)
                TableCell(
                    text = "Instructor", header = true
                )
            }
        }
        itemsIndexed(student.lessons) { i, lesson ->
            Row(Modifier.background(if (i % 2 == 0) White80 else Blue10)) {
                TableCell(text = prettyDate(lesson.date))
                TableCell(text = lesson.levelAfter)
                TableCell(text = lesson.duration.toString() + "min.")
                TableCell(
                    text = viewModel.getInstructorById(lesson.instructorId).instructor.nickname,
                )
            }
        }
    }


}

@Composable
fun RowScope.TableCell(
    text: String, header: Boolean = false
) {
    Text(
        text = text,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .border(1.dp, Color.Black)
            .padding(8.dp)
            .weight(1f)
    )
}

fun prettyDate(data: LocalDate, pattern: String = "dd.MM"): String {
    return data.format(DateTimeFormatter.ofPattern(pattern))
}