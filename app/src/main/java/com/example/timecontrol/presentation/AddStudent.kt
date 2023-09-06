package com.example.timecontrol.presentation

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.timecontrol.R
import com.example.timecontrol.database.Levels
import com.example.timecontrol.database.Student
import com.example.timecontrol.navigation.Screen
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddStudent(databaseViewModel: DatabaseViewModel, navController: NavController) {

    val addStudentViewModel = viewModel<AddStudentViewModel>()
    val state = addStudentViewModel.state
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        addStudentViewModel.validationEvents.collect { event ->
            when (event) {
                is AddStudentViewModel.ValidationEvent.Success -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        databaseViewModel.insertStudent(
                            Student(
                                id = 0,
                                name = state.firstName,
                                lastname = state.lastName,
                                phoneNumber = state.phoneNumber,
                                birthDate = state.birthDate,
                                placeOfStay = state.placeOfStay,
                                arrivalDate = state.arrivalDate,
                                departureDate = state.departureDate,
                                level = state.level
                            )
                        )
                    }
                    Toast.makeText(
                        context, "Student has been added successfully!", Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.StudentsScreen.route)
                }
            }
        }
    }
    val formattedBirthDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd.MM.yyyy").format(state.birthDate)
        }
    }
    val birthDateDialogState = rememberMaterialDialogState()

    val defaultPlaces = listOf("Albatros", "Ekolaguna", "Chałupy 6")

    val formattedDepartureDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd.MM.yyyy").format(state.departureDate)
        }
    }
    val departureDateDialogState = rememberMaterialDialogState()

    var newKiter by rememberSaveable {
        mutableStateOf(true)
    }
    val levelsCheckState = List(Levels.size) {
        remember {
            mutableStateOf(false)
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(Modifier.fillMaxHeight(0.9f)) {
            Column(
//            Modifier
//                .padding(5.dp)
//                .fillMaxSize()
//            .pointerInput(Unit) {
//            detectTapGestures(onTap = {               //TODO test if keyboard hide when clicked outside
//                localFocusManager.clearFocus()
//            })
//        }

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Add Student", fontSize = 26.sp, fontWeight = FontWeight.Bold)

                //Name and Last Name
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(5.dp)
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.firstName,
                            onValueChange = {
                                addStudentViewModel.onEvent(AddStudentFormEvent.FirstNameChanged(it))
                            },
                            isError = state.firstNameError != null,
                            label = { Text(text = "Name") },
                        )
                        if (state.firstNameError != null) {
                            Text(text = state.firstNameError, color = MaterialTheme.colors.error)
                        }
                    }

                    Column(modifier = Modifier.weight(1f))
                    {
                        OutlinedTextField(
                            value = state.lastName,
                            onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentFormEvent.LastNameChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text(text = "Last Name") },
                        )
                        if (state.lastNameError != null) {
                            Text(text = state.lastNameError, color = MaterialTheme.colors.error)
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(5.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        //Tel no
                        OutlinedTextField(
                            value = state.phoneNumber,
                            onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentFormEvent.PhoneNumberChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text(text = "Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        if (state.phoneNumberError != null) {
                            Text(text = state.phoneNumberError, color = MaterialTheme.colors.error)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        //Birth Date
                        OutlinedTextField(
                            value = formattedBirthDate,
                            onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentFormEvent.BirthDateChanged(
                                        LocalDate.parse(it)
                                    )
                                )
                            },
                            label = { Text(text = "Birth Date") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(painterResource(id = R.drawable.calendar_icon),
                                    "Calendar",
                                    modifier = Modifier.clickable { birthDateDialogState.show() })
                            },
                        )
                        if (state.birthDateError != null) {
                            Text(text = state.birthDateError, color = MaterialTheme.colors.error)
                        }
                    }


                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(5.dp)
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        //Place of stay
                        var expanded by remember { mutableStateOf(false) }

                        // Up Icon when expanded and down icon when collapsed
                        val icon = if (expanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown

                        Column(
//                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(value = state.placeOfStay,
                                onValueChange = {
                                    addStudentViewModel.onEvent(
                                        AddStudentFormEvent.PlaceOfStayChanged(
                                            it
                                        )
                                    )
                                },
                                label = { Text(text = "Place of stay") },
                                trailingIcon = {
                                    Icon(icon,
                                        "contentDescription",
                                        Modifier.clickable { expanded = !expanded })
                                })
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                defaultPlaces.forEach { place ->
                                    DropdownMenuItem(onClick = {
                                        addStudentViewModel.onEvent(
                                            AddStudentFormEvent.PlaceOfStayChanged(
                                                place
                                            )
                                        )
                                        expanded = false
                                    }) {
                                        Text(text = place)
                                    }
                                }
                            }
                        }
                        if (state.placeOfStayError != null) {
                            Text(text = state.placeOfStayError, color = MaterialTheme.colors.error)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        //Departure Date - probably range in future?
                        OutlinedTextField(
                            value = formattedDepartureDate,
                            onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentFormEvent.DepartureDateChanged(
                                        LocalDate.parse(it)
                                    )
                                )
                            },
                            label = { Text(text = "Departure Date") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(painterResource(id = R.drawable.calendar_icon),
                                    "Calendar",
                                    modifier = Modifier.clickable { departureDateDialogState.show() })
                            },
                        )
                    }


                }
            }
            Column {
                //New Kiter?
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "New to kitesurfing?")
                    Switch(checked = newKiter, onCheckedChange = { newKiter = it })
                }


                if (!newKiter) {
                    //Levels
                    Text(text = "Select level (hold to see level details)")
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 64.dp)) {
                        itemsIndexed(levelsCheckState) { i, _ ->
                            Box(
                                modifier = Modifier
                                    .border(0.5.dp, Color.Black)
                                    .padding(1.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(checked = levelsCheckState[i].value,
                                        onCheckedChange = {
                                            if (it) for (j in 0..i) levelsCheckState[j].value =
                                                true //check all lower levels and itself
                                            else levelsCheckState[i].value = false //uncheck
                                        })
                                    Text(
                                        text = Levels[i].level,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                        }
                    }
                    if (state.levelError != null) {
                        Text(text = state.levelError, color = MaterialTheme.colors.error)
                    }
                }

            }

        }
        FloatingActionButton(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .height(40.dp)
                .width(80.dp)
                .align(Alignment.BottomCenter), onClick = {

                addStudentViewModel.onEvent(
                    AddStudentFormEvent.LevelChanged(
                        getMaxLevel(
                            levelsCheckState
                        )
                    )
                )
                addStudentViewModel.onEvent(AddStudentFormEvent.Submit)
            }, containerColor = BlueLogo
        ) {
            Text(text = "Save", fontWeight = FontWeight.Bold)
        }

    }


    //Birth Date DatePicker
    MaterialDialog(dialogState = birthDateDialogState, buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel")
    }) {
        datepicker(initialDate = LocalDate.now(),
            title = "Birth date",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
            ),
            allowedDateValidator = { it < LocalDate.now() }) {
            addStudentViewModel.onEvent(
                AddStudentFormEvent.BirthDateChanged(
                    it
                )
            )
        }
    }

    //Departure Date DatePicker
    MaterialDialog(dialogState = departureDateDialogState, buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel")
    }) {
        datepicker(initialDate = LocalDate.now(),
            title = "Departure date",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
            ),
            allowedDateValidator = { it > LocalDate.now() }) {
            addStudentViewModel.onEvent(
                AddStudentFormEvent.DepartureDateChanged(
                    it
                )
            )
        }
    }


}

fun getMaxLevel(levelsCheckState: List<MutableState<Boolean>>): String {
    val poz = levelsCheckState.lastIndexOf(mutableStateOf(true))
    return if (poz != -1) Levels[poz].level else "D"
}