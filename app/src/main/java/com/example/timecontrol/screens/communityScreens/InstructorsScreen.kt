package com.example.timecontrol.screens.communityScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timecontrol.R
import com.example.timecontrol.database.InstructorQualification
import com.example.timecontrol.database.getFullName
import com.example.timecontrol.instructordetails.InstructorDetails
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.utils.pretty
import com.example.timecontrol.utils.toLocalDate
import com.example.timecontrol.utils.toMillis
import com.example.timecontrol.viewModelFactory.InstructorViewModelFactory
import com.example.timecontrol.viewModelHelp.instructor.AddInstructorEvent
import com.example.timecontrol.viewModelHelp.instructor.AddInstructorState
import com.example.timecontrol.viewModels.DatabaseViewModel
import com.example.timecontrol.viewModels.InstructorViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun InstructorsScreen(
    databaseViewModel: DatabaseViewModel,
    owner: ViewModelStoreOwner,
) {
    val instructorViewModel = ViewModelProvider(
        owner, InstructorViewModelFactory(databaseViewModel)
    )[InstructorViewModel::class.java]
    val state by instructorViewModel.state.collectAsState()
    val onEvent = instructorViewModel::onEvent
    Box(Modifier.fillMaxSize()) {
        val instructors by databaseViewModel.instructors.collectAsStateWithLifecycle(initialValue = emptyList())
        if (state.isAddingInstructor) {
            AddInstructorDialog(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .shadow(2.dp)
                    .fillMaxHeight(0.8f)
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(5.dp)
        ) {
            itemsIndexed(instructors.map { it.instructor }) { i, instructor ->
                InstructorDetails(
                    nickname = instructor.nickname,
                    fullName = instructor.getFullName(),
                    qualification = instructor.qualification.fullName,
                    phoneNumber = instructor.phoneNumber,
                    arrivalDate = instructor.arrivalDate.pretty(),
                    departureDate = instructor.departureDate.pretty(),
                    students = instructorViewModel.getInstructorStudentCount(
                        instructor
                    ).toString(),
                    hours = instructorViewModel.getInstructorHoursTaught(instructor).toString(),
                    background = Blue20
                )
            }
            //made so that the button does not cover the tiles
            item {
                Box(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(75.dp)
                .align(Alignment.BottomEnd),
            onClick = { onEvent(AddInstructorEvent.ShowDialog) },
            containerColor = BlueLogo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Instructor",
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstructorDialog(
    state: AddInstructorState, onEvent: (AddInstructorEvent) -> Unit, modifier: Modifier = Modifier,
) {
    AlertDialog(modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onEvent(AddInstructorEvent.HideDialog) },
        title = {
            Text(
                text = "Add Instructor", fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(label = { Text(text = "Name", fontSize = 14.sp) },
                            value = state.firstName,
                            onValueChange = { onEvent(AddInstructorEvent.FirstNameChanged(it)) })
                        if (state.firstNameError != null) Text(
                            text = state.firstNameError, color = MaterialTheme.colors.error
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(label = {
                            Text(
                                text = "Last Name", fontSize = 14.sp
                            )
                        },
                            value = state.lastName,
                            onValueChange = { onEvent(AddInstructorEvent.LastNameChanged(it)) })
                        if (state.lastNameError != null) Text(
                            text = state.lastNameError, color = MaterialTheme.colors.error
                        )
                    }

                }
                Row {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(label = {
                            Text(
                                text = "Nickname (${state.firstName})", fontSize = 14.sp
                            )
                        },
                            value = state.nickname,
                            onValueChange = { onEvent(AddInstructorEvent.NicknameChanged(it)) })
                        if (state.nicknameError != null) Text(
                            text = state.nicknameError, color = MaterialTheme.colors.error
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            label = { Text(text = "Phone Number", fontSize = 14.sp) },
                            value = state.phoneNumber,
                            onValueChange = { onEvent(AddInstructorEvent.PhoneNumberChanged(it)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        if (state.phoneNumberError != null) Text(
                            text = state.phoneNumberError, color = MaterialTheme.colors.error
                        )
                    }

                }
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Stationary?", fontSize = 14.sp) //TODO - rephrase it
                        Switch(checked = state.isStationary, onCheckedChange = {
                            onEvent(
                                AddInstructorEvent.IsStationaryChanged(
                                    it
                                )
                            )
                        })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        //Qualification
                        var expanded by remember { mutableStateOf(false) }

                        // Up Icon when expanded and down icon when collapsed
                        val icon = if (expanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown

                        Column {
                            OutlinedTextField(label = {
                                Text(
                                    text = "Qualification", fontSize = 14.sp
                                )
                            }, value = state.qualification.fullName, onValueChange = {

                                onEvent(
                                    AddInstructorEvent.QualificationChanged(
                                        InstructorQualification.fromString(it)
                                    )
                                )
                            }, readOnly = true, trailingIcon = {
                                Icon(icon,
                                    "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            })
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                InstructorQualification.values()
                                    .forEach { qualification: InstructorQualification ->
                                        DropdownMenuItem(onClick = {
                                            onEvent(
                                                AddInstructorEvent.QualificationChanged(
                                                    qualification
                                                )
                                            )
                                            expanded = false
                                        }) {
                                            Text(text = qualification.fullName)
                                        }
                                    }
                            }
                        }

                        if (state.qualificationError != null) {
                            Text(
                                text = state.qualificationError, color = MaterialTheme.colors.error
                            )
                        }

                    }

                }

                Row {
                    val stayRangeDialogState = rememberMaterialDialogState()
                    val stayRangePickerState = remember {
                        DateRangePickerState(
                            initialSelectedStartDateMillis = state.arrivalDate.toMillis(),
                            initialDisplayedMonthMillis = null,
                            initialSelectedEndDateMillis = state.departureDate.toMillis(),
                            initialDisplayMode = DisplayMode.Picker,
                            yearRange = (LocalDate.now().year - 1..LocalDate.now().year + 1)
                        )
                    }
                    Column {
                        Text(text = "Stay range:")
                        OutlinedTextField(
                            value = "${
                                state.arrivalDate.pretty()
                            } - ${
                                state.departureDate.pretty()
                            }",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(painterResource(id = R.drawable.calendar_icon),
                                    "Calendar",
                                    modifier = Modifier.clickable { stayRangeDialogState.show() })
                            },
                        )
                    }
                    //RangePicker
                    MaterialDialog(dialogState = stayRangeDialogState,
                        properties = DialogProperties(usePlatformDefaultWidth = false),
                        buttons = {
                            positiveButton(text = "Ok", onClick = {
                                onEvent(
                                    AddInstructorEvent.ArrivalDateChanged(
                                        stayRangePickerState.selectedStartDateMillis!!.toLocalDate()
                                    )
                                )
                                onEvent(
                                    AddInstructorEvent.DepartureDateChanged(
                                        stayRangePickerState.selectedEndDateMillis!!.toLocalDate()
                                    )
                                )
                            })
                            negativeButton(text = "Cancel")
                        }) {

                        DateRangePicker(state = stayRangePickerState)
                    }
                }

            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onEvent(AddInstructorEvent.HideDialog)
                    }, colors = ButtonDefaults.buttonColors(MaterialTheme.colors.onError)
                ) {
                    Text(text = "Cancel", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        onEvent(AddInstructorEvent.SaveInstructor)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueLogo
                    ),
                ) {
                    Text(text = "Save", fontWeight = FontWeight.Bold)
                }
            }
        })
}
