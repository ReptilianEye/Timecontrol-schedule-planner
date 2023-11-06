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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import com.example.timecontrol.pretty
import com.example.timecontrol.prettyTime
import com.example.timecontrol.slot.Slot
import com.example.timecontrol.slot.Variant
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.ScheduleViewModel
import com.example.timecontrol.viewModelFactory.ScheduleViewModelFactory
import com.example.timecontrol.viewModelHelp.schedule.AssignedLesson
import com.example.timecontrol.viewModelHelp.schedule.LoadingState
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails
import com.example.timecontrol.viewModelHelp.schedule.SlotStatus
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

// TODO: Reset schedule when changing view

@Composable
fun ScheduleScreen(
    databaseViewModel: DatabaseViewModel,
    navController: NavController,
    owner: ViewModelStoreOwner,
) {
    val viewModel = ViewModelProvider(
        owner, ScheduleViewModelFactory(databaseViewModel)
    )[ScheduleViewModel::class.java]
    val state = viewModel.state.collectAsStateWithLifecycle()
    val scheduleDate = viewModel.scheduleDate.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent
    val context = LocalContext.current
    val datePickerState = rememberMaterialDialogState()
    val loadingState = viewModel.loadingState.collectAsState(initial = LoadingState())
//    if (viewModel.isDataReadyInitialization()) {
//        onEvent(ScheduleEvent.InitSlotDescriptions)
//    }
    if (!state.value.initialized && !loadingState.value.areInstructorsLoading){
        onEvent(ScheduleEvent.InitSlotDescriptions)
    }
    if (viewModel.arePreviousLessonNotLoadedAndAvailable())
        onEvent(ScheduleEvent.LoadPreviousLessons)
    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                ScheduleViewModel.Event.Success -> {
                    Toast.makeText(
                        context, "Schedule saved successfully!", Toast.LENGTH_LONG
                    ).show()
                }

                ScheduleViewModel.Event.OpenSaveBeforeSwitchingDialog -> {
                    onEvent(ScheduleEvent.OpenSaveBeforeSwitchingDialog)
                }

                ScheduleViewModel.Event.OpenDatePicker -> {
                    datePickerState.show()
                }
            }
        }
    }
    if (state.value.isSaveBeforeSwitchingDialogOpen) {
        AlertDialog(
            onDismissRequest = { /*TODO nice animation*/ },
            title = { Text(text = "Change schedule without saving?") },
            text = {
                Column {
                    Text(text = "Would you like to save current schedule before switching?")
                }

            },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(ScheduleEvent.CloseSaveBeforeSwitchingDialog)
                    onEvent(ScheduleEvent.SaveSchedule)
                }) {
                    Text("Save!")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onEvent(ScheduleEvent.CloseSaveBeforeSwitchingDialog)
                    },
                ) {
                    Text("No, reset changes.")
                }
            },

            )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Text(
                "Schedule for ${scheduleDate.value.pretty()}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)
            )
            when (state.value.isEditingEnabled) {
                true -> EditScheduleScreen(viewModel = viewModel, state = state)
                false -> ViewScheduleScreen(viewModel = viewModel, state = state, scheduleDate)
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(55.dp)
                .align(Alignment.TopEnd),
            onClick = {
                onEvent(ScheduleEvent.PreventLosingScheduleChanges)
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar_icon),
                contentDescription = "Switch Schedule",
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
    //Birth Date DatePicker
    MaterialDialog(dialogState = datePickerState, buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel")
    }) {
        datepicker(
            initialDate = scheduleDate.value,
            title = "Schedule date",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
            ),
        ) {
            onEvent(
                ScheduleEvent.ChangeScheduleDate(it)
            )
        }
    }

}

@Composable
fun ViewScheduleScreen(
    viewModel: ScheduleViewModel,
    state: State<ScheduleState>,
    scheduleDate: State<LocalDate>,
) {
    val onEvent = viewModel::onEvent
    val getSlot: (Int, Int) -> SlotDetails = viewModel::getSlot
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (state.value.initialized)
                LessonScheduleTable(
                    state = state,
                    onEvent = onEvent,
                    getSlot = getSlot,
                    editable = false
                )

        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(45.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                onEvent(ScheduleEvent.ToggleEditing)
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

@Composable
fun EditScheduleScreen(
    viewModel: ScheduleViewModel, state: State<ScheduleState>,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val students = state.value.students
    val instructors = state.value.instructors
    val lessonTimes = state.value.lessonTimes
    val arePreviousLessonAvailable = state.value.previousLessons.isNotEmpty()
    val onEvent = viewModel::onEvent
    val isStudentAssigned = viewModel::isStudentAssigned
    val isLessonConfirmed: (AssignedLesson) -> Boolean = viewModel::isLessonConfirmed
    val getInstructorFromIndex = viewModel::getInstructorFromIndex
    val getStudent = viewModel::getStudent
    val getLessonTimeFromIndex = viewModel::getLessonTimeFromIndex
    val getSlot: (Int, Int) -> SlotDetails = viewModel::getSlot
    val getSlotFromLesson: (AssignedLesson) -> SlotDetails = viewModel::getSlot

    DraggableScreen(
        modifier = Modifier.fillMaxSize()
    ) {

        Column {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())

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
                    AvailableStudentsList(viewModel = viewModel, state = state)
                }
                if (state.value.initialized)
                    LessonScheduleTable(
                        state = state,
                        onEvent = onEvent,
                        getSlot = getSlot,
                        editable = true
                    )

            }
            Divider(Modifier.padding(15.dp))
            Text(
                text = "Assigned lessons list:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(3.dp)
            )
            LazyColumn {
                items(state.value.assignedLessons) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        LessonsListItem(
                            student = getStudent(it.studentId).student.getShortcutName(),
                            lessonTime = getLessonTimeFromIndex(getSlotFromLesson(it).lessonTimeIndex).prettyTime(),
                            instructor = (getInstructorFromIndex(getSlotFromLesson(it).instructorIndex)).instructor.nickname,
                            backgroundColor = Blue20,
                            modifier = Modifier.weight(4f)
                        )
                        when (isLessonConfirmed(it)) {
                            false -> LessonControls(
                                onConfirm = { onEvent(ScheduleEvent.ConfirmLesson(it)) },
                                onCancel = { onEvent(ScheduleEvent.RemoveLesson(it)) },
                                modifier = Modifier.weight(1f)
                            )

                            true -> LessonControlsLocked(
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
fun AvailableStudentsList(viewModel: ScheduleViewModel, state: State<ScheduleState>) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val students = state.value.students
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
                    if (viewModel.isStudentAssigned(student)) Slot(
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
}


@Composable
fun LessonScheduleTable(
    state: State<ScheduleState>,
    onEvent: (ScheduleEvent) -> Unit,
    getSlot: (Int, Int) -> SlotDetails,
    editable: Boolean,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val instructors = state.value.instructors
    val lessonTimes = state.value.lessonTimes
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
                    val slotDetails = getSlot(i, j)
                    when (editable) {
                        true -> DropBox(onEvent = onEvent,
                            slotDetails = slotDetails,
                            onDrop = { student ->
                                onEvent(ScheduleEvent.OnDrop(i, j, student))
                            },
                            onClick = { onEvent(ScheduleEvent.HandleClick(i, j)) })

                        false -> StudentSlot(slotDetails = slotDetails)
                    }
                }
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
    onClick: () -> Unit,
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
                painter = painterResource(id = R.drawable.cancelicon),
                contentDescription = "Cancel"
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
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Box(
        modifier = Modifier.width(
            (screenWidth / 6f).dp
        )
    ) {
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
}