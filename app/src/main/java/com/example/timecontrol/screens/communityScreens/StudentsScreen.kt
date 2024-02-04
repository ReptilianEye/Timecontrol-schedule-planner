package com.example.timecontrol.screens.communityScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.TextField
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timecontrol.R
import com.example.timecontrol.components.FilterBar
import com.example.timecontrol.filter.FilterController
import com.example.timecontrol.navigation.MyNavigationViewModel
import com.example.timecontrol.navigation.ScreensRoutes
import com.example.timecontrol.studentslistitem.StudentsListItem
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.ui.theme.White80
import com.example.timecontrol.viewModels.DatabaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.format.DateTimeFormatter

@Composable
fun StudentsScreen(
    viewModel: DatabaseViewModel,
    navController: MyNavigationViewModel,
) {
    val filterController = FilterController()
    val students by viewModel.students.combine(filterController.filters) { students, filters ->
        students.filter { student -> filters.all { it.check(student) } }
    }

//    map {
//        it.filter { student -> filterController.filter(student) }
//    }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    Box(modifier = Modifier.fillMaxSize()) {
        Column {

            if (students.isEmpty()) Text(text = "No students fulfill the criteria.")

            FilterBar(filterController)

//            TextField(
//                value = startsWith,
//                onValueChange = { startsWith = it },
//                label = { Text("Search") })


            LazyColumn(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                itemsIndexed(students) { index, student ->
                    StudentsListItem(
                        level = student.level,
                        name = student.firstName,
                        telNo = student.phoneNumber,
                        placeOfStay = student.placeOfStay,
                        departureDate = student.departureDate.format(DateTimeFormatter.ofPattern("dd.MM"))
                            .toString(),
                        onClick = {
                            navController.navigate(
                                ScreensRoutes.StudentDetailsScreen().withArgs(
                                    student.id.toString()
                                )
                            )
                        },
                        background = if (index % 2 == 0) White80 else Blue10,
                        modifier = Modifier
                            .width(390.dp)
                            .height(102.dp)
                    )
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(75.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                navController.navigate(ScreensRoutes.AddStudentScreen.route)
            },
            containerColor = BlueLogo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Student",
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}
