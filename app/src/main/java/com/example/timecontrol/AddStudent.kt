package com.example.timecontrol

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecontrol.ui.theme.BlueLogo
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import androidx.compose.material3.FloatingActionButton
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.timecontrol.database.Levels

@Composable
fun AddStudent() {
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var lastname by rememberSaveable {
        mutableStateOf("")
    }
    var tel_no by rememberSaveable {
        mutableStateOf("")
    }
    var birth_date by rememberSaveable {
        mutableStateOf(LocalDate.now())
    }
    val formatted_birth_date by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd.MM.yyyy").format(birth_date)
        }
    }
    val birth_date_dialog_state = rememberMaterialDialogState()

    var place_of_stay by rememberSaveable {
        mutableStateOf("")
    }
    val default_places = listOf("Albatros", "Ekolaguna", "Chałupy 6")
    var departure_date by rememberSaveable {
        mutableStateOf(LocalDate.now().plusWeeks(1))
    }
    val formatted_departure_date by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("dd.MM.yyyy").format(departure_date)
        }
    }
    val departure_date_dialog_state = rememberMaterialDialogState()

    var new_kiter by rememberSaveable {
        mutableStateOf(true)
    }
    var level by rememberSaveable {
        mutableStateOf("")
    }
    val levels_check_state = List(Levels.size) {
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
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Name") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lastname,
                        onValueChange = { lastname = it },
                        label = { Text(text = "Last Name") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(5.dp)
                ) {
                    //Tel no
                    OutlinedTextField(
                        value = tel_no,
                        onValueChange = { tel_no = it },
                        label = { Text(text = "Tel.No.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        isError = tel_no.isEmpty() || tel_no.length != 9,
                        modifier = Modifier.weight(1f)

                    )
                    //Birth Date
                    OutlinedTextField(
                        value = formatted_birth_date,
                        onValueChange = { birth_date = LocalDate.parse(it) },
                        label = { Text(text = "Birth Date") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            Icon(painterResource(id = R.drawable.calendar_icon),
                                "Calendar",
                                modifier = Modifier.clickable { birth_date_dialog_state.show() })
                        },
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(5.dp)
                ) {
                    //Place of stay
                    var expanded by remember { mutableStateOf(false) }

                    // Up Icon when expanded and down icon when collapsed
                    val icon = if (expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown

                    Column(
                        Modifier.weight(1f)
                    ) {
                        // Create an Outlined Text Field
                        // with icon and not expanded
                        OutlinedTextField(value = place_of_stay,
                            onValueChange = { place_of_stay = it },
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
                            default_places.forEach { place ->
                                DropdownMenuItem(onClick = {
                                    place_of_stay = place
                                    expanded = false
                                }) {
                                    Text(text = place)
                                }
                            }
                        }
                    }

                    //Departure Date - probably range in future?
                    OutlinedTextField(
                        value = formatted_departure_date,
                        onValueChange = { departure_date = LocalDate.parse(it) },
                        label = { Text(text = "Departure Date") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            Icon(painterResource(id = R.drawable.calendar_icon),
                                "Calendar",
                                modifier = Modifier.clickable { departure_date_dialog_state.show() })
                        },
                    )

                }
            }
            Column {
                //New Kiter?
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "New to kitesurfing?")
                    Switch(checked = new_kiter, onCheckedChange = { new_kiter = it })
                }


                if (!new_kiter) {
                    //Levels
                    Text(text = "Select level (hold to see level details)")
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 64.dp)) {
                        itemsIndexed(levels_check_state) { i, _ ->
                            Box(
                                modifier = Modifier
                                    .border(0.5.dp, Color.Black)
                                    .padding(1.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(
                                        checked = levels_check_state[i].value,
                                        onCheckedChange = {
                                            if (it)
                                                for (j in 0..i)
                                                    levels_check_state[j].value =
                                                        true //check all lower levels and itself
                                            else
                                                levels_check_state[i].value = false //uncheck
                                        }
                                    )
                                    Text(
                                        text = Levels[i].level,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

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
            onClick = { /*TODO*/ },
            containerColor = BlueLogo
        ) {
            Text(text = "Save", fontWeight = FontWeight.Bold)
        }

    }


    //Birth Date DatePicker
    MaterialDialog(dialogState = birth_date_dialog_state, buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel")
    }) {
        datepicker(initialDate = LocalDate.now(),
            title = "Birth date",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
            ),
            allowedDateValidator = { it < LocalDate.now() }) {
            birth_date = it
        }
    }

    //Departure Date DatePicker
    MaterialDialog(dialogState = departure_date_dialog_state, buttons = {
        positiveButton(text = "Ok")
        negativeButton(text = "Cancel")
    }) {
        datepicker(initialDate = LocalDate.now(),
            title = "Departure date",
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
            ),
            allowedDateValidator = { it > LocalDate.now() }) {
            departure_date = it
        }
    }


}

