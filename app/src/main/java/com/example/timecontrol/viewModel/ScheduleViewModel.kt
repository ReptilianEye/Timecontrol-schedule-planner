package com.example.timecontrol.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.timecontrol.database.StudentWithLessons
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat

class ScheduleViewModel(
    private val databaseViewModel: DatabaseViewModel  // nie jestem przekonany
) : ViewModel() {
    var isCurrentlyDragging by mutableStateOf(false)
        private set

    fun startDragging() {
        isCurrentlyDragging = true
    }

    fun stopDragging() {
        isCurrentlyDragging = false
    }
    fun addStudentToLesson(student: StudentWithLessons, i: Int) {

    }
}