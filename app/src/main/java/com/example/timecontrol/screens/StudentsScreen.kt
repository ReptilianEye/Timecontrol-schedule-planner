package com.example.timecontrol.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.R
import com.example.timecontrol.navigation.Screen
import com.example.timecontrol.studentslistitem.StudentsListItem
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.ui.theme.White80
import com.example.timecontrol.viewModel.DatabaseViewModel
import java.time.format.DateTimeFormatter

@Composable
fun StudentsScreen(viewModel: DatabaseViewModel, navController: NavController) {

    val students by viewModel.students.collectAsStateWithLifecycle(initialValue = emptyList())

    Box(modifier = Modifier.fillMaxSize())
    {
        if (students.isEmpty())
            Text(text = "No students have been added yet...")

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            itemsIndexed(students) { index, student ->
                StudentsListItem(
                    level = student.level,
                    name = student.firstName,
                    telNo = student.phoneNumber,
                    placeOfStay = student.placeOfStay,
                    departureDate = student.departureDate.format(DateTimeFormatter.ofPattern("dd.MM"))
                        .toString(),
                    onClick = { navController.navigate(Screen.StudentDetailsScreen.withArgs(student.id.toString())) },
                    background = if (index % 2 == 0) White80 else Blue10,
                    modifier = Modifier
                        .width(390.dp)
                        .height(102.dp)
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(75.dp)
                .align(Alignment.BottomEnd),
            onClick = { navController.navigate(Screen.AddStudentScreen.route) },
            containerColor = BlueLogo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Student", modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}
