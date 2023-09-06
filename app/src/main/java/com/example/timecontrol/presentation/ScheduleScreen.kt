package com.example.timecontrol.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ScheduleScreen() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Schedule",
        fontSize = 35.sp,
        textAlign = TextAlign.Center
    )
}