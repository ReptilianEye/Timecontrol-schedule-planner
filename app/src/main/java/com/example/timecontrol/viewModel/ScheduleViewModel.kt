package com.example.timecontrol.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.InstructorWithLessons
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleViewModel(
    val databaseViewModel: DatabaseViewModel  // nie jestem przekonany
) : ViewModel() {

    private val _instructors = databaseViewModel.currentInstructors
    private val _students = databaseViewModel.currentStudents
    private val _assignedLessons: MutableStateFlow<List<Lesson>> = MutableStateFlow(listOf())

    private val _state = MutableStateFlow(ScheduleState())

    val state = combine(
        _state, _students, _instructors, _assignedLessons,
    ) { state, students, instructors, assignedLessons ->
        state.copy(
            instructors = instructors, students = students, assignedLessons = assignedLessons
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), ScheduleState()
    )
    private val occupied = mutableMapOf<Int, StudentWithLessons>()

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.AssignLesson -> viewModelScope.launch {
                assignNewLesson(event.student, event.i)
            }

            ScheduleEvent.SaveSchedule -> submitData()

        }
    }

    var isCurrentlyDragging by mutableStateOf(false)
        private set

    fun startDragging() {
        isCurrentlyDragging = true
    }

    fun stopDragging() {
        isCurrentlyDragging = false
    }


    private suspend fun assignNewLesson(student: StudentWithLessons, i: Int) {
        if (isOccupied(i, student)) _assignedLessons.value =
            _assignedLessons.value.filter { it.mapToIndex() != i }

        if (isStudentAssigned(student)) _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }

        occupied[i] = student
        val (instructor, lessonTime) = mapFromIndex(i)
        val new = Lesson(
            id = 0,
            date = _state.value.date,
            lessonTime = lessonTime,
            studentId = student.student.id,
            instructorId = instructor.instructor.id
        )
        _assignedLessons.value = _assignedLessons.value + new

    }

    private suspend fun mapFromIndex(i: Int): Pair<InstructorWithLessons, Pair<String, String>> {
        val ranges = _state.value.lessonTimes.size
        val lessonTime = _state.value.lessonTimes[i % ranges]
        val instructor: InstructorWithLessons = _instructors.first()[i / ranges]
        return Pair(instructor, lessonTime)
    }

    private suspend fun Lesson.mapToIndex(
    ): Int {
        val ranges = _state.value.lessonTimes.size
        return _instructors.first()
            .indexOfFirst { it.instructor.id == instructorId } * ranges + _state.value.lessonTimes.indexOf(
            lessonTime
        )
    }

    private fun submitData() {
        viewModelScope.launch {
            _assignedLessons.collect { lessons: List<Lesson> ->
                lessons.forEach { lesson ->
                    databaseViewModel.insertLesson(lesson)
                }
            }
        }
        resetState()
    }

    private fun resetState() {
        _state.update { ScheduleState() }
    }

    private fun isOccupied(i: Int, student: StudentWithLessons): Boolean =
        occupied[i] != null && occupied[i] != student

    private fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }

}
