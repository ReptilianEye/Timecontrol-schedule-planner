package com.example.timecontrol.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.Levels
import com.example.timecontrol.viewModel.DatabaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ScheduleScreen(
    databaseViewModel: DatabaseViewModel,
    navController: NavController,
    owner: ViewModelStoreOwner
) {
    val lessons by databaseViewModel.lessons.collectAsStateWithLifecycle(initialValue = emptyList())
    Button(onClick = {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                databaseViewModel.insertLesson(
                    Lesson(
                        id = 0,
                        LocalDate.now(),
                        90,
                        levelAfter = Levels.first().level,
                        studentId = 1,
                        instructorId = 1
                    )
                )
            }
        }
    }) {

    }
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Schedule",
        fontSize = 35.sp,
        textAlign = TextAlign.Center
    )
    lessons.forEach { lesson ->
        Text(text = lesson.toString())
    }

}