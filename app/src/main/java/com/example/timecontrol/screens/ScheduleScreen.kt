package com.example.timecontrol.screens

import android.widget.Toast
import android.widget.ToggleButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.ui.theme.Blue40
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.ScheduleViewModel
import com.example.timecontrol.viewModel.ScheduleViewModelFactory
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState


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
                instructors.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        titleBox(it.instructor.nickname)//instructor name
                        repeat(lessonRanges.size) {
                            dropBox(i, onEvent, getStudentOnIthSlot)
                            i += 1
                        }
                    }
                }

            }
            state.value.assignedLessons.forEach {
                LessonListItem(lesson = it, viewModel = viewModel)
            }
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
fun dropBox(
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
            }
    ) { isInBound, student ->
        if (student != null) {
            Toast.makeText(
                LocalContext.current, student.student.firstName + i.toString(), Toast.LENGTH_LONG
            ).show()
            LaunchedEffect(key1 = student) {
                onEvent(ScheduleEvent.AssignLesson(student, i))
            }
        }
        val cellDescription = getStudentOnIthSlot(i)?.student?.getFullName() ?: "Free Slot"
//        val cellDescription = if (toggler && i % 2 == 0) "red" else "blue"


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
                    text = cellDescription,
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
                    text = cellDescription,
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
            .background(Blue10)
    ) {
        val student = viewModel.databaseViewModel.getStudentById(lesson.studentId)
        val instructor = viewModel.databaseViewModel.getInstructorById(lesson.instructorId)
        Text(
            text = "${lesson.lessonTime.prettyRange()},student: ${student.student.firstName},instructor ${instructor.instructor.nickname}",
            color = Color.Black
        )

    }
}