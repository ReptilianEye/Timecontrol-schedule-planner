package com.example.timecontrol.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.InstructorWithLessons
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections

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
    val vis = MutableList<StudentWithLessons?>(15) { null }
    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.AssignLesson -> {
                viewModelScope.launch {
                    assignNewLesson(event.student, event.i)
                }
            }

            is ScheduleEvent.ResetSlot -> {
                viewModelScope.launch(Dispatchers.IO) {
                    freeIthSlot(event.i)
                }
            }

            ScheduleEvent.SaveSchedule -> {
                submitData()
            }

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
        //if slot is already occupied by somebody
        val studentOnIthSlot = getStudentOnIthSlot(i)
        if (studentOnIthSlot != null && studentOnIthSlot != student) viewModelScope.launch(
            Dispatchers.IO
        ) {
            freeIthSlot(i)
        }

        //if student is already assigned somewhere
        if (isStudentAssigned(student)) resetStudentFromSchedule(student)

        occupied[i] = student
        vis[i] = student
        val (instructor, lessonTime) = mapFromIndex(i)
        val new = Lesson(
            id = 0,
            date = _state.value.lessonsDay,
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

    private suspend fun freeIthSlot(i: Int) {
        _assignedLessons.value = _assignedLessons.value.filter { it.mapToIndex() != i }
        vis[i] = null
    }

    private fun resetStudentFromSchedule(student: StudentWithLessons) {
        _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }
    }

    fun getStudentOnIthSlot(i: Int): StudentWithLessons? = occupied[i]

    private fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }

}
