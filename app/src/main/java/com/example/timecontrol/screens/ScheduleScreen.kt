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
import com.example.timecontrol.database.getShortcutName
import com.example.timecontrol.instructorcell.InstructorCell
import com.example.timecontrol.lessontimecell.LessonTimeCell
import com.example.timecontrol.prettyTime
import com.example.timecontrol.slot.Slot
import com.example.timecontrol.slot.Variant
import com.example.timecontrol.ui.theme.*
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.ScheduleViewModel
import com.example.timecontrol.viewModel.ScheduleViewModelFactory
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails


@Composable
fun ScheduleScreen(
    databaseViewModel: DatabaseViewModel, navController: NavController, owner: ViewModelStoreOwner
) {
    val viewModel = ViewModelProvider(
        owner, ScheduleViewModelFactory(databaseViewModel)
    )[ScheduleViewModel::class.java]
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val state = viewModel.state.collectAsStateWithLifecycle()
    val students = state.value.students
    val instructors = state.value.instructors
    val lessonTimes = state.value.lessonTimes
    val onEvent = viewModel::onEvent
    val getSlotDescription = viewModel::getSlotDetails
    val isStudentAssigned = viewModel::isStudentAssigned
    if (instructors.size * lessonTimes.size > 0) {
        onEvent(ScheduleEvent.InitSlotDescriptions)
    }



    DraggableScreen(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = viewModel.state.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(students) { student ->
                    DragTarget(
                        dataToDrop = student, viewModel = viewModel
                    ) {
                        if (isStudentAssigned(student))
                            Slot(
                                variant = Variant.Confirmed,
                                studentName = student.student.getShortcutName()
                            )
                        else
                            Slot(
                                variant = Variant.Default,
                                studentName = student.student.getShortcutName()
                            )
//                        Box(
//                            modifier = Modifier
//                                .size(50.dp)
//                                .clip(RoundedCornerShape(15.dp))
//                                .shadow(5.dp, RoundedCornerShape(15.dp))
//                                .background(Blue20, RoundedCornerShape(15.dp)),
//                            contentAlignment = Alignment.Center,
//                        ) {
//                            Text(
//                                text = student.student.getFullName(),
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = Color.White,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                        }
                    }
                }
            }
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
                    titleBox("")//fill
                    lessonTimes.forEach {
//                        titleBox(it.prettyRange())//lesson ranges
                        LessonTimeCell(
                            lessonTime = it.prettyTime(),
                            modifier = Modifier
                                .width((screenWidth / 6f).dp)
                                .padding(3.dp)
                        )
                    }

                }
                for (i in instructors.indices) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InstructorCell(
                            name = instructors[i].instructor.nickname,
                            modifier = Modifier.width((screenWidth / 6f).dp)
                        )
//                        titleBox(instructors[i].instructor.nickname)//instructor name
                        for (j in lessonTimes.indices) {
                            dropBox(onEvent = onEvent,
                                slotInfo = getSlotDescription(i, j),
                                onDrop = { student ->
                                    onEvent(ScheduleEvent.OnDrop(i, j, student))
                                },
                                onClick = { onEvent(ScheduleEvent.FreeSlot(i, j)) })
                        }
                    }
                }

            }
            state.value.assignedLessons.forEach {
                LessonListItem(lesson = it, viewModel = viewModel)
            }
//            Slot(studentName = "student 1")
//            Slot(studentName = "student 2", property1 = Property1.Confirmed)
//            Slot(studentName = "student 1", property1 = Property1.Unassigned)

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
        modifier = Modifier.width((screenWidth / 6f).dp)
    ) {
        Text(text = mess)
    }
}

@Composable
fun dropBox(
    onEvent: (ScheduleEvent) -> Unit,
    slotInfo: SlotDetails,
    onDrop: (StudentWithLessons) -> Unit,
    onClick: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    DropItem<StudentWithLessons>(
        Modifier
            .width((screenWidth / 6f).dp)
            .clickable {
                onClick()
            }) { isInBound, student ->
        if (student != null) {
            Toast.makeText(
                LocalContext.current, "${student.student.firstName} added!", Toast.LENGTH_LONG
            ).show()
            LaunchedEffect(key1 = student) {
                onEvent(
                    ScheduleEvent.AssignLesson(
                        student, slotInfo.instructorIndex, slotInfo.lessonTimeIndex
                    )
                )
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
//            Slot(property1 = Property1.Default, studentName = slotInfo.description)


            if (slotInfo.studentId == null) Slot(variant = Variant.Unassigned)
            else Slot(
                variant = Variant.Default, studentName = slotInfo.description
            )

//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .border(
//                        1.dp, color = Color.White, shape = RoundedCornerShape(15.dp)
//                    )
//                    .background(
//                        Color.Black.copy(0.5f), RoundedCornerShape(15.dp)
//                    ), contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = slotInfo.description,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White
//                )
//            }
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
            text = "${lesson.lessonTime.prettyTime()},student: ${student.student.firstName},instructor ${instructor.instructor.nickname}",
            color = Color.Black
        )
        Button(onClick = { viewModel.onEvent(ScheduleEvent.RemoveLesson(lesson)) }) {
            Text(text = "X")
        }

    }
}

