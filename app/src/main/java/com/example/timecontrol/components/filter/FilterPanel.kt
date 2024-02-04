package com.example.timecontrol.components.filter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.timecontrol.components.LevelSelector
import com.example.timecontrol.filter.FilterController
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.utils.LevelController
import com.example.timecontrol.utils.toPair

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterPanel(
    filterController: FilterController,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val age by filterController.ageFiltered.collectAsState()
    val levels by filterController.levelsFiltered.collectAsState()
    val available by filterController.available.collectAsState()
    val departed by filterController.departed.collectAsState()
    val incoming by filterController.incoming.collectAsState()
    var sliderPosition by remember { mutableStateOf(age.get().first.toFloat()..age.get().second.toFloat()) }
    val levelsCheckState = List(LevelController.getLevels().size) {
        remember {
            mutableStateOf(
                LevelController.getLevel(it).isBetween(levels.get())
            )
        }
    }
    AlertDialog(modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
        },
        buttons = {
            Button(
                onClick = {
                    age.setFilter(sliderPosition.toPair())
                    levels.setFilter(
                        LevelController.fromString(
                            LevelController.getMinLevel(
                                levelsCheckState
                            )
                        ) to LevelController.fromString(
                            LevelController.getMaxLevel(levelsCheckState)
                        )
                    )
                    onDismiss()
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = BlueLogo, contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text(text = "Save")
            }
        },
        text = {
            Column(
                modifier = Modifier
//                .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                Row {
                    Checkbox(checked = available.isActive(),
                        onCheckedChange = { available.setFilter(it) })
                    Text(text = "Available")
                }
                Row {
                    Checkbox(checked = departed.isActive(),
                        onCheckedChange = { departed.setFilter(it) })
                    Text(text = "Departed")
                }
                Row {
                    Checkbox(checked = incoming.isActive(),
                        onCheckedChange = { incoming.setFilter(it) })
                    Text(text = "Incoming")
                }
                Divider(thickness = Dp.Hairline, color = MaterialTheme.colors.onSurface)
                Text(text = "Age")
                RangeSlider(value = sliderPosition, onValueChange = {
                    sliderPosition = it
                }, steps = 100, onValueChangeFinished = {
                    age.setFilter(sliderPosition.toPair())
                }, valueRange = 0f..100f
                )
                Text(text = "Age: ${age.get().first} - ${age.get().second}")
                Divider(thickness = Dp.Hairline, color = MaterialTheme.colors.onSurface)
                Text(text = "Levels")
                LevelSelector(levelsCheckState = levelsCheckState)
            }
        })
}

@Composable
@Preview(showBackground = true)
fun FilterPanelPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        FilterPanel(
            filterController = FilterController(),
            onDismiss = {},
            modifier = Modifier.fillMaxSize(0.8f)
        )
    }
}
