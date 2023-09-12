package com.example.timecontrol.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.QualificationHelper
import com.example.timecontrol.R
import com.example.timecontrol.database.InstructorQualification
import com.example.timecontrol.database.getFullName
import com.example.timecontrol.instructordetails.InstructorDetails
import com.example.timecontrol.prettyDate
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.InstructorViewModel
import com.example.timecontrol.viewModel.InstructorViewModelFactory
import com.example.timecontrol.viewModelHelp.instructor.AddInstructorEvent
import com.example.timecontrol.viewModelHelp.instructor.AddInstructorState

@Composable
fun InstructorsScreen(
    databaseViewModel: DatabaseViewModel, navController: NavController, owner: ViewModelStoreOwner
) {
    val instructorViewModel = ViewModelProvider(
        owner, InstructorViewModelFactory(databaseViewModel)
    )[InstructorViewModel::class.java]
    val state by instructorViewModel.state.collectAsState()
    val context = LocalContext.current
    val onEvent = instructorViewModel::onEvent
    Box(Modifier.fillMaxSize()) {
        val instructors by databaseViewModel.instructors.collectAsStateWithLifecycle(initialValue = emptyList())
        if (state.isAddingInstructor) {
            AddInstructorDialog(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .background(Blue10)
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
                    qualification = QualificationHelper.getString(instructor.qualification),
                    phoneNumber = instructor.phoneNumber,
                    arrivalDate = prettyDate(instructor.arrivalDate),
                    departureDate = prettyDate(instructor.departureDate),
                    students = "10", //TODO - implement that
                    hours = "30",
                    background = Blue20
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
                contentDescription = "Add Student",
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}

@Composable
fun AddInstructorDialog(
    state: AddInstructorState, onEvent: (AddInstructorEvent) -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(modifier = modifier,
        onDismissRequest = { onEvent(AddInstructorEvent.HideDialog) },
        title = { Text(text = "Add Instructor", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
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
                        OutlinedTextField(label = { Text(text = "Last Name", fontSize = 14.sp) },
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
                                text = "Nickname (optional)", fontSize = 14.sp
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
                            onValueChange = { onEvent(AddInstructorEvent.LastNameChanged(it)) },
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
                        Switch(checked = state.isStationary,
                            onCheckedChange = { onEvent(AddInstructorEvent.IsStationaryChanged(it)) })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        //Place of stay
                        var expanded by remember { mutableStateOf(false) }

                        // Up Icon when expanded and down icon when collapsed
                        val icon = if (expanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown

                        Column {
                            OutlinedTextField(label = {
                                Text(
                                    text = "Qualification", fontSize = 14.sp
                                )
                            },
                                value = QualificationHelper.getString(state.qualification),
                                onValueChange = {

                                    onEvent(
                                        AddInstructorEvent.QualificationChanged(
                                            QualificationHelper.getEnum(it)
                                        )
                                    )
                                },
                                readOnly = true,
                                trailingIcon = {
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
                                            Text(text = QualificationHelper.getString(qualification))
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
                    Row {
                        //TODO add range for stay time
                    }

                }

            }
        },
        buttons = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(onClick = { onEvent(AddInstructorEvent.SaveInstructor) }) {
                    Text(text = "Save")
                }
            }
        })
}

//
//fun insert(viewModel: DatabaseViewModel) {
//    CoroutineScope(Dispatchers.IO).launch {
//        async {
//            viewModel.insertInstructor(
//                Instructor(
//                    0, "Piotrek", "Rzadk", "Piter", "123456789", true, InstructorQualification.LVL1,
//                    LocalDate.now(),
//                    LocalDate.now().plusMonths(1)
//                )
//            )
//        }
//        async {
//            viewModel.insertInstructor(
//                Instructor(
//                    0,
//                    "Tomek",
//                    "Tomek",
//                    "Tomek",
//                    "123456789",
//                    true,
//                    InstructorQualification.ASSISTANT,
//                    LocalDate.now(),
//                    LocalDate.now().plusMonths(2)
//                )
//            )
//        }
//        async {
//            viewModel.insertInstructor(
//                Instructor(
//                    0, "Ala", "Ala", "Ala", "123456789", true, InstructorQualification.EXAMINER,
//                    LocalDate.now().minusDays(3),
//                    LocalDate.now().plusMonths(1)
//                )
//            )
//        }
//        async {
//            viewModel.insertInstructor(
//                Instructor(
//                    0, "Kuba", "Kuba", "Kuba", "123456789", false, InstructorQualification.LVL3,
//                    LocalDate.now().minusWeeks(2),
//                    LocalDate.now().plusMonths(3)
//                )
//            )
//        }
//
//    }
//}