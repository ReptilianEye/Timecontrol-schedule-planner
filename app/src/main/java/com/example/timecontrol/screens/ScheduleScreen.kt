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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.DragTarget
import com.example.timecontrol.DraggableScreen
import com.example.timecontrol.DropItem
import com.example.timecontrol.R
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getShortcutName
import com.example.timecontrol.instructorcell.InstructorCell
import com.example.timecontrol.lessonslistitem.LessonsListItem
import com.example.timecontrol.lessontimecell.LessonTimeCell
import com.example.timecontrol.prettyTime
import com.example.timecontrol.slot.Slot
import com.example.timecontrol.slot.Variant
import com.example.timecontrol.ui.theme.*
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.ScheduleViewModel
import com.example.timecontrol.viewModel.ScheduleViewModelFactory
import com.example.timecontrol.viewModelHelp.schedule.AssignedLesson
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails
import com.example.timecontrol.viewModelHelp.schedule.SlotStatus
import java.time.LocalDate


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
    val arePreviousLessonAvailable = state.value.previouslyAdded.isNotEmpty()
    val lessonTimes = state.value.lessonTimes
    val onEvent = viewModel::onEvent
    val isStudentAssigned = viewModel::isStudentAssigned
    val isLessonConfirmed: (AssignedLesson) -> Boolean = viewModel::isLessonConfirmed
    val getInstructorFromIndex = viewModel::getInstructorFromIndex
    val getStudent = viewModel::getStudent
    val getLessonTimeFromIndex = viewModel::getLessonTimeFromIndex
    val getSlot: (Int, Int) -> SlotDetails = viewModel::getSlot
    val getSlotFromLesson: (AssignedLesson) -> SlotDetails = viewModel::getSlot

//    val currentlyEditing = rememberSaveable {
//        mutableStateOf(false)
//    }
//    val openDialog = remember {
//        mutableStateOf(
//            false
//        )
//    }
//    val showedDialog = rememberSaveable {
//        mutableStateOf(false)
//    }
    if (instructors.size * lessonTimes.size > 0) {
        onEvent(ScheduleEvent.InitSlotDescriptions)
    }
    if (!state.value.lessonsDialogShowed && state.value.isEditingEnabled && arePreviousLessonAvailable) {
        onEvent(ScheduleEvent.OpenPreviousLessonsDialog)
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                ScheduleViewModel.Event.Success -> {

                    Toast.makeText(
                        context, "Student has been added successfully!", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    DraggableScreen(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = viewModel.state.collectAsState()
        if (state.value.isPreviousLessonsDialogOpen) {
            AlertDialog(
                onDismissRequest = { onEvent(ScheduleEvent.ClosePreviousLessonsDialog) },
                title = { Text(text = "Previous schedule found") },
                text = {
                    Column {
                        Text(text = "Found previously saved schedule. Would you like to load it?")
                    }

                },
                confirmButton = {
                    TextButton(onClick = {
                        onEvent(ScheduleEvent.ClosePreviousLessonsDialog)
                        onEvent(ScheduleEvent.LoadPreviousLessons)
                    }) {
                        Text("Yes, load them!")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onEvent(ScheduleEvent.ClosePreviousLessonsDialog)
                        },
                    ) {
                        Text("No, start from scratch (THEY WOULD BE DELETED!)")
                    }
                },

                )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 50.dp),

//            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (students.isEmpty()) {
                Text(
                    text = "No students available...",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(10.dp)
                )
            } else if (instructors.isEmpty()) {
                Text(
                    text = "No instructors available...",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                Text(text = "Students available:", fontSize = 14.sp, color = Color.Black)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 20.dp, bottom = 20.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(students) { student ->
                        DragTarget(
                            dataToDrop = student, viewModel = viewModel,
                        ) {
                            Box(modifier = Modifier.width((screenWidth / 6f).dp)) {
                                if (isStudentAssigned(student)) Slot(
                                    variant = Variant.Confirmed,
                                    studentName = student.student.getShortcutName(),
                                )
                                else Slot(
                                    variant = Variant.Default,
                                    studentName = student.student.getShortcutName(),
                                )
                            }
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
                        BlankCell()//top left cell
                        lessonTimes.forEach {
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
                            for (j in lessonTimes.indices) {
                                DropBox(onEvent = onEvent,
                                    slotDetails = getSlot(i, j),
                                    onDrop = { student ->
                                        onEvent(ScheduleEvent.OnDrop(i, j, student))
                                    },
                                    onClick = { onEvent(ScheduleEvent.HandleClick(i, j)) })
                            }
                        }
                    }

                }
                state.value.assignedLessons.forEach {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        LessonsListItem(
                            student = getStudent(it.studentId).student.getShortcutName(),
                            lessonTime = getLessonTimeFromIndex(getSlotFromLesson(it).lessonTimeIndex).prettyTime(),
                            instructor = (getInstructorFromIndex(getSlotFromLesson(it).instructorIndex)).instructor.nickname,
                            backgroundColor = Blue20,
                            modifier = Modifier.weight(4f)
                        )
                        if (!isLessonConfirmed(it)) {
                            LessonControls(
                                onConfirm = { onEvent(ScheduleEvent.ConfirmLesson(it)) },
                                onCancel = { onEvent(ScheduleEvent.RemoveLesson(it)) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            LessonControlsLocked(
                                onClick = { onEvent(ScheduleEvent.UnconfirmLesson(it)) },
                                modifier = Modifier.weight(1f)
                            )

                        }
                    }

                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(55.dp)
                .align(Alignment.TopEnd),
            onClick = {
                onEvent(ScheduleEvent.ChangeScheduleDate(LocalDate.of(2023, 10, 25)))
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar_icon),
                contentDescription = "Switch Schedule",
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
//        if (students.isNotEmpty() && instructors.isNotEmpty()) {
        if (state.value.isEditingEnabled) {
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
        } else {
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .size(45.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    onEvent(ScheduleEvent.ToogleEditing)
                },
                containerColor = BlueLogo
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.students_info_edit_icon),
                    contentDescription = "Edit Schedule",
                    modifier = Modifier.fillMaxSize(0.5f)
                )
            }
        }

    }
}

@Composable
fun BlankCell(width: Dp = (LocalConfiguration.current.screenWidthDp / 6f).dp) {
    Box(
        modifier = Modifier.width(width)
    ) {
        Text(text = "")
    }
}

@Composable
fun DropBox(
    onEvent: (ScheduleEvent) -> Unit,
    slotDetails: SlotDetails,
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
                onDrop(student)
                onEvent(
                    ScheduleEvent.AssignLesson(
                        student, slotDetails.instructorIndex, slotDetails.lessonTimeIndex
                    )
                )
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
                    text = slotDetails.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        } else {
            StudentSlot(slotDetails = slotDetails)
        }
    }
}

@Composable
fun LessonControls(onConfirm: () -> Unit = {}, onCancel: () -> Unit = {}, modifier: Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        IconButton(onClick = { onConfirm() }) {
            Icon(
                painter = painterResource(id = R.drawable.approveicon), //? why icons are black
                contentDescription = "Confirm"
            )
        }
        IconButton(onClick = { onCancel() }) {

            Icon(
                painter = painterResource(id = R.drawable.cancelicon), contentDescription = "Cancel"
            )

        }
    }
}

@Composable
fun LessonControlsLocked(onClick: () -> Unit = {}, modifier: Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        IconButton(onClick = { onClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.approvelockedicon),
                contentDescription = "Confirm Locked"
            )
        }
    }
}

@Composable
fun StudentSlot(slotDetails: SlotDetails) {
    when (slotDetails.status) {
        SlotStatus.Unassigned -> Slot(variant = Variant.Unassigned)
        SlotStatus.Assigned -> Slot(
            variant = Variant.Default, studentName = slotDetails.description
        )

        SlotStatus.Confirmed -> Slot(
            variant = Variant.Confirmed, studentName = slotDetails.description
        )
    }
}