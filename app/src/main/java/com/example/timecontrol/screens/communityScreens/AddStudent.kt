package com.example.timecontrol.screens.communityScreens

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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.timecontrol.R
import com.example.timecontrol.navigation.CommunityNavItem
import com.example.timecontrol.navigation.MyNavigationViewModel
import com.example.timecontrol.navigation.ScreensRoutes
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.utils.LevelController
import com.example.timecontrol.viewModels.AddStudentViewModel
import com.example.timecontrol.viewModels.DatabaseViewModel
import com.example.timecontrol.viewModelFactory.AddStudentViewModelFactory
import com.example.timecontrol.viewModelHelp.student.AddStudentEvent
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//TODO - make as a Dialog not separate screen
// TODO - add email as an input
@Composable
fun AddStudent(
    databaseViewModel: DatabaseViewModel,
    navController: MyNavigationViewModel,
    owner: ViewModelStoreOwner,
) {
    val addStudentViewModel = ViewModelProvider(
        owner, AddStudentViewModelFactory(databaseViewModel)
    )[AddStudentViewModel::class.java]
    val state by addStudentViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        addStudentViewModel.validationEvents.collect { event ->
            when (event) {
                is AddStudentViewModel.ValidationEvent.Success -> {
                    Toast.makeText(
                        context, "Student has been added successfully!", Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(
                        ScreensRoutes.CommunityScreen(CommunityNavItem.Students).getFullRoute()
                    )
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
    val levelsCheckState = List(LevelController.getLevels().size) {
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
                                addStudentViewModel.onEvent(AddStudentEvent.FirstNameChanged(it))
                            },
                            isError = state.firstNameError != null,
                            label = { Text(text = "Name") },
                        )
                        if (state.firstNameError != null) {
                            Text(text = state.firstNameError!!, color = MaterialTheme.colors.error)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.lastName,
                            onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentEvent.LastNameChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text(text = "Last Name") },
                        )
                        if (state.lastNameError != null) {
                            Text(text = state.lastNameError!!, color = MaterialTheme.colors.error)
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
                                    AddStudentEvent.PhoneNumberChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text(text = "Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        if (state.phoneNumberError != null) {
                            Text(
                                text = state.phoneNumberError!!, color = MaterialTheme.colors.error
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        //Birth Date
                        OutlinedTextField(
                            value = formattedBirthDate,
                            onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentEvent.BirthDateChanged(
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
                            Text(text = state.birthDateError!!, color = MaterialTheme.colors.error)
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

                        Column {
                            OutlinedTextField(value = state.placeOfStay, onValueChange = {
                                addStudentViewModel.onEvent(
                                    AddStudentEvent.PlaceOfStayChanged(
                                        it
                                    )
                                )
                            }, label = { Text(text = "Place of stay") }, trailingIcon = {
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
                                            AddStudentEvent.PlaceOfStayChanged(
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
                            Text(
                                text = state.placeOfStayError!!, color = MaterialTheme.colors.error
                            )
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
                                    AddStudentEvent.DepartureDateChanged(
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
                                    Checkbox(
                                        checked = levelsCheckState[i].value,
                                        onCheckedChange = {
                                            if (it) for (j in 0..i) levelsCheckState[j].value =
                                                true //mark all lower levels and itself
                                            else levelsCheckState[i].value = false //uncheck
                                        })
                                    Text(
                                        text = LevelController.getLevel(i).level,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                        }
                    }
                    if (state.levelError != null) {
                        Text(text = state.levelError!!, color = MaterialTheme.colors.error)
                    }
                }

            }


        }
        FloatingActionButton(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .height(40.dp)
                .width(80.dp)
                .align(Alignment.BottomStart), onClick = {
                addStudentViewModel.onEvent(AddStudentEvent.Cancel)
                navController.navigate(
                    ScreensRoutes.CommunityScreen(CommunityNavItem.Students).getFullRoute()
                )
            }, containerColor = MaterialTheme.colors.onError
        ) {
            Text(text = "Cancel", fontWeight = FontWeight.Bold)
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .height(40.dp)
                .width(80.dp)
                .align(Alignment.BottomEnd), onClick = {
                addStudentViewModel.onEvent(
                    AddStudentEvent.LevelChanged(
                        LevelController.getMaxLevel(
                            levelsCheckState
                        )
                    )
                )
                addStudentViewModel.onEvent(AddStudentEvent.SaveStudent)
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
                AddStudentEvent.BirthDateChanged(
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
                AddStudentEvent.DepartureDateChanged(
                    it
                )
            )
        }
    }


}

