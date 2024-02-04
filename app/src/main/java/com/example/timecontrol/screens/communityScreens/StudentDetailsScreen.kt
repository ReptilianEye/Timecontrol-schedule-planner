package com.example.timecontrol.screens.communityScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecontrol.navigation.MyNavigationViewModel
import com.example.timecontrol.studentsinfo.StudentsInfo
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.White80
import com.example.timecontrol.utils.pretty
import com.example.timecontrol.viewModels.DatabaseViewModel

@Composable
fun StudentDetailsScreen(
    databaseViewModel: DatabaseViewModel, navController: MyNavigationViewModel, studentId: Int?,
) {
    val student = databaseViewModel.getStudentById(studentId!!)
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
                arrivalDate = student.student.arrivalDate.pretty(),
                departureDate = student.student.departureDate.pretty(),
                birthDate = student.student.birthDate.pretty("dd.MM.YY"),
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
                MyTableCell(text = "Date", header = true)
                MyTableCell(text = "Level", header = true)
                MyTableCell(text = "Duration", header = true)
                MyTableCell(
                    text = "Instructor", header = true
                )
            }
        }
        itemsIndexed(student.lessons) { i, lesson ->
            Row(Modifier.background(if (i % 2 == 0) White80 else Blue10)) {
                MyTableCell(text = lesson.date.pretty())
                MyTableCell(text = lesson.levelAfter ?: "?")
                MyTableCell(text = lesson.duration.toString() + "min.")
                MyTableCell(
                    text = databaseViewModel.getInstructorById(lesson.instructorId).instructor.nickname,
                )
            }
        }
    }


}

@Composable
fun RowScope.MyTableCell(
    text: String, header: Boolean = false,
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

