package com.example.timecontrol.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.DragTarget
import com.example.timecontrol.DraggableScreen
import com.example.timecontrol.DropItem
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getFullName
import com.example.timecontrol.prettyRange
import com.example.timecontrol.ui.theme.*
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.ScheduleViewModel
import com.example.timecontrol.viewModel.ScheduleViewModelFactory
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent

@Composable
fun MyBoxScreen() {
    var selectedBox by remember { mutableStateOf(-1) }
    val desc = remember { mutableStateListOf<String>() }

    // Initialize colors with default colors
    if (desc.isEmpty()) {
        repeat(10) {
            desc.add("Free slot")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Click on a box to change its color to red:")

        Spacer(modifier = Modifier.height(16.dp))

        // Create a column of MyBoxes
        for (i in desc.indices) {
//        var i = -1
//        repeat(desc.size) {
//            i += 1
            MyBox(text = desc[i], onClick = {
                // Update the color of the clicked box to red
                desc[i] = "Occupied"

                // Revert the color of the previously clicked box (if any) to default
                if (selectedBox != -1 && selectedBox != i) {
                    desc[selectedBox] = "Free slot"
                }

                selectedBox = i
            })
        }
    }
}

data class Slot(val i: Int, val j: Int, val description: String = "Free Slot")

@Composable
fun ScheduleScreen(
    databaseViewModel: DatabaseViewModel, navController: NavController, owner: ViewModelStoreOwner
) {
    val viewModel = ViewModelProvider(
        owner, ScheduleViewModelFactory(databaseViewModel)
    )[ScheduleViewModel::class.java]
    val state = viewModel.state.collectAsStateWithLifecycle()
    val students = state.value.students
    val instructors = state.value.instructors
    val lessonRanges = state.value.lessonTimes
    val onEvent = viewModel::onEvent
    val getStudentOnIthSlot = viewModel::getStudentOnIthSlot

    val slotDescriptions = remember {
        mutableStateListOf<Slot>()
    }
    if (instructors.size * lessonRanges.size > 0) {
        for (i in instructors.indices)
            for (j in lessonRanges.indices)
                slotDescriptions.add(Slot(i, j, "Free Slot"))
    }


//    MyBoxScreen()

    DraggableScreen(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val state = viewModel.state.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
                    .background(Blue10),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(students) { student ->
                    DragTarget(
                        dataToDrop = student, viewModel = viewModel
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .shadow(5.dp, RoundedCornerShape(15.dp))
                                .background(Blue20, RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = student.student.getFullName(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            var i = 0
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    titleBox()//fill
                    lessonRanges.forEach {
                        titleBox(it.prettyRange())//lesson ranges
                    }
                }
                for (i in instructors.indices) {
//                instructors.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val it = instructors[i]
                        titleBox(it.instructor.nickname)//instructor name
                        for (j in lessonRanges.indices) {
//                        repeat(lessonRanges.size) {
                            dropBox(
                                onEvent = onEvent,
                                slotInfo = slotDescriptions[i * lessonRanges.size + j],
                                onDrop = { student ->
                                    val prev = slotDescriptions.indexOfFirst {
                                        it.description == student.student.getFullName()
                                    }
                                    if (prev != -1)
                                        slotDescriptions[prev] =
                                            slotDescriptions[prev].copy(description = "Free slot")
                                    slotDescriptions[i * lessonRanges.size + j] =
                                        slotDescriptions[i * lessonRanges.size + j].copy(
                                            description = student.student.getFullName()
                                        )

                                }
                            )
//                            i += 1
                        }
                    }
                }

            }
            state.value.assignedLessons.forEach {
                LessonListItem(lesson = it, viewModel = viewModel)
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .height(40.dp)
                .width(80.dp)
                .align(Alignment.BottomCenter),
            onClick = { onEvent(ScheduleEvent.SaveSchedule) },
            containerColor = BlueLogo
        ) {
            androidx.compose.material.Text(text = "Save", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun titleBox(mess: String = "lesson") {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Box(
        modifier = Modifier
            .width((screenWidth / 6f).dp)
            .background(Blue40)
    ) {

        Text(text = mess)
    }
}

@Composable
fun dropBox1(
    i: Int = -1,
    onEvent: (ScheduleEvent) -> Unit,
    getStudentOnIthSlot: (Int) -> StudentWithLessons?,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    DropItem<StudentWithLessons>(
        Modifier
            .size((screenWidth / 6f).dp)
            .clickable {
                onEvent(ScheduleEvent.ResetSlot(i))
            }) { isInBound, student ->
        if (student != null) {
            Toast.makeText(
                LocalContext.current, "${student.student.firstName} added!", Toast.LENGTH_LONG
            ).show()
            LaunchedEffect(key1 = student) {
                onEvent(ScheduleEvent.AssignLesson(student, i))
            }
        }
        val slotDescription = getStudentOnIthSlot(i)?.student?.getFullName() ?: "Free slot"
        if (isInBound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                    )
                    .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slotDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp, color = Color.White, shape = RoundedCornerShape(15.dp)
                    )
                    .background(
                        Color.Black.copy(0.5f), RoundedCornerShape(15.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slotDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun dropBox(
    onEvent: (ScheduleEvent) -> Unit,
    slotInfo: Slot,
    onDrop: (StudentWithLessons) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    DropItem<StudentWithLessons>(
        Modifier
            .size((screenWidth / 6f).dp)
            .clickable {
//                onEvent(ScheduleEvent.ResetSlot(i))
            }) { isInBound, student ->
        if (student != null) {
            Toast.makeText(
                LocalContext.current, "${student.student.firstName} added!", Toast.LENGTH_LONG
            ).show()
            LaunchedEffect(key1 = student) {
//                onEvent(ScheduleEvent.AssignLesson(student, i))
                onDrop(student)
            }
        }
//        val slotDescription = getStudentOnIthSlot(i)?.student?.getFullName() ?: "Free slot"
        if (isInBound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                    )
                    .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slotInfo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp, color = Color.White, shape = RoundedCornerShape(15.dp)
                    )
                    .background(
                        Color.Black.copy(0.5f), RoundedCornerShape(15.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slotInfo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun LessonListItem(lesson: Lesson, viewModel: ScheduleViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Blue10),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val student = viewModel.databaseViewModel.getStudentById(lesson.studentId)
        val instructor = viewModel.databaseViewModel.getInstructorById(lesson.instructorId)
        Text(
            text = "${lesson.lessonTime.prettyRange()},student: ${student.student.firstName},instructor ${instructor.instructor.nickname}",
            color = Color.Black
        )
        Button(onClick = { viewModel.onEvent(ScheduleEvent.RemoveLesson(lesson)) }) {
            Text(text = "X")
        }

    }
}

@Composable
fun MyBox(
    text: String, onClick: () -> Unit
) {
    Box(modifier = Modifier
        .size(100.dp)
        .clickable { onClick() }) {
        Text(text = text)
    }
}


