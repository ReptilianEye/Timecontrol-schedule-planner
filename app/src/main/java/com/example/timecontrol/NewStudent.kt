package com.example.timecontrol

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecontrol.ui.theme.BlueLogo
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NewStudent() {
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
            DateTimeFormatter
                .ofPattern("dd.MM.yyyy")
                .format(birth_date)
        }
    }
    var place_of_stay by rememberSaveable {
        mutableStateOf("")
    }
    var departure_date by rememberSaveable {
        mutableStateOf(LocalDate.now().plusWeeks(1))
    }
    val formatted_departure_date by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd.MM.yyyy")
                .format(departure_date)
        }
    }
    val dateDialogState = rememberMaterialDialogState()
    val context = LocalContext.current

    var new_kiter by rememberSaveable {
        mutableStateOf(true)
    }
    var level by rememberSaveable {
        mutableStateOf("")
    }
    Column(
        Modifier.fillMaxSize()
//            .pointerInput(Unit) {
//            detectTapGestures(onTap = {               //TODO test if keyboard hide when clicked outside
//                localFocusManager.clearFocus()
//            })
//        }
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("New Student", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        //Name and Last Name
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(5.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lastname, onValueChange = { lastname = it },
                label = { Text(text = "Last Name") },
                modifier = Modifier.weight(1f)
            )
        }
        //Tel no
        OutlinedTextField(
            value = tel_no,
            onValueChange = { tel_no = it },
            label = { Text(text = "Tel.No.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = tel_no.isEmpty() || tel_no.length != 9

        )
        //Birth Date
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "Birth Date: $formatted_birth_date ", fontSize = 16.sp)
            Button(
                onClick = { dateDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = BlueLogo)
            ) {
                Text(text = "Pick date", color = Color.White)
                Icon(
                    painter = painterResource(id = R.drawable.calendar_icon),
                    contentDescription = "Calendar"
                )
            }
            MaterialDialog(dialogState = dateDialogState, buttons = {
                positiveButton(text = "Ok")
                negativeButton(text = "Cancel")
            }) {
                datepicker(
                    initialDate = LocalDate.now(),
                    title = "Birth date",
                    colors = DatePickerDefaults.colors(
                        headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
                    ),
                ) {
                    birth_date = it
                }
            }

        }

        // TODO Place of stay as dropdown with custom option


        //Departure Date - probably range in future?
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "Departure Date: $formatted_departure_date ", fontSize = 16.sp)
            Button(
                onClick = { dateDialogState.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = BlueLogo)
            ) {
                Text(text = "Pick date", color = Color.White)
                Icon(
                    painter = painterResource(id = R.drawable.calendar_icon),
                    contentDescription = "Calendar"
                )
            }
            MaterialDialog(dialogState = dateDialogState, buttons = {
                positiveButton(text = "Ok")
                negativeButton(text = "Cancel")
            }) {
                datepicker(initialDate = LocalDate.now(),
                    title = "Departure date",
                    colors = DatePickerDefaults.colors(
                        headerBackgroundColor = BlueLogo, dateActiveBackgroundColor = BlueLogo
                    ),
                    allowedDateValidator = {
                        it >= LocalDate.now()
                    }) {
                    departure_date = it
                }
            }

        }
        //TODO switch - Have you kitesurf before?
        // Select - levels
    }
}

