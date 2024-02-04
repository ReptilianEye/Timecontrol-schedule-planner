package com.example.timecontrol.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecontrol.utils.LevelController

@Composable
fun LevelSelector(levelsCheckState: List<MutableState<Boolean>>) {
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
}