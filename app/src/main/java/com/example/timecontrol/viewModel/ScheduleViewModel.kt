package com.example.timecontrol.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.InstructorWithLessons
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getFullName
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import com.example.timecontrol.viewModelHelp.schedule.Slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(
    val databaseViewModel: DatabaseViewModel,  // nie jestem przekonany
    var scheduleDate: LocalDate = LocalDate.now().plusDays(1)
) : ViewModel() {

    private val freeSlotDescription = "Free Slot"
    private val _instructors = databaseViewModel.getAllCurrentInstructors()
    private val _students = databaseViewModel.currentStudents
    private val _assignedLessons: MutableStateFlow<List<Lesson>> = MutableStateFlow(listOf())

    private val _state = MutableStateFlow(ScheduleState())

    val state = combine(
        _state, _students, _instructors, _assignedLessons
    ) { state, students, instructors, assignedLessons ->
        state.copy(
            instructors = instructors, students = students, assignedLessons = assignedLessons
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), ScheduleState()
    )
    private val slotDescriptions = mutableStateListOf<Slot>()

    private val occupied = mutableMapOf<Pair<Int, Int>, StudentWithLessons>()
    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.AssignLesson -> {
                viewModelScope.launch {
                    assignNewLesson(event.student, event.instructorIndex, event.lessonTimeIndex)
                }
            }

            is ScheduleEvent.RemoveLesson -> removeLesson(event.lesson)

            is ScheduleEvent.ResetSlot -> {
                freeSlot(event.i, event.j)
            }

            ScheduleEvent.SaveSchedule -> {
                submitData()
            }

            ScheduleEvent.InitSlotDescriptions -> initSlotDescriptions()
            is ScheduleEvent.OnDrop -> onDrop(event.i, event.j, event.student)
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


    private fun assignNewLesson(
        student: StudentWithLessons,
        instructorIndex: Int,
        lessonTimeIndex: Int
    ) {
        val instructor = state.value.instructors[instructorIndex]
        val lessonTime = state.value.lessonTimes[lessonTimeIndex]

        //if slot is already occupied by somebody
        val onSlot = getStudentOnIthSlot(instructorIndex, lessonTimeIndex)
        if (onSlot != null && onSlot != student)
            freeSlot(instructorIndex, lessonTimeIndex)


        //if student is already assigned somewhere
        if (isStudentAssigned(student)) resetStudentFromSchedule(student)

        occupied[Pair(instructorIndex, lessonTimeIndex)] = student

        val new = Lesson(
            id = 0,
            date = _state.value.lessonsDay,
            lessonTime = lessonTime,
            studentId = student.student.id,
            instructorId = instructor.instructor.id
        )

        _assignedLessons.value = _assignedLessons.value + new
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

    private fun onDrop(i: Int, j: Int, student: StudentWithLessons) {

        freePrevSlot(student)
        setSlotDescription(i, j, student.student.getFullName())
    }

    private fun resetState() {
        _state.update { ScheduleState() }
    }

    private fun freeSlot(instructorIndex: Int, lessonTimeIndex: Int) {
        _assignedLessons.value =
            _assignedLessons.value.filter { it.instructorId != state.value.instructors[instructorIndex].instructor.id || it.lessonTime != state.value.lessonTimes[lessonTimeIndex] }
        setSlotDescription(instructorIndex, lessonTimeIndex, "Free Slot")
    }

    //fries last student slot
    private fun freePrevSlot(student: StudentWithLessons) {
        val prev = slotDescriptions.indexOfFirst {
            it.description == student.student.getFullName()
        }
        if (prev != -1)
            slotDescriptions[prev] = slotDescriptions[prev].copy(description = freeSlotDescription)

    }

    private fun removeLesson(lesson: Lesson) {
        _assignedLessons.value = _assignedLessons.value.filter { it != lesson }
    }

    private fun resetStudentFromSchedule(student: StudentWithLessons) {
        _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }
    }

    fun getStudentOnIthSlot(i: Int, j: Int): StudentWithLessons? = occupied[i to j]

    private fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }

    private fun initSlotDescriptions() {
        for (i in state.value.instructors.indices)
            for (j in state.value.lessonTimes.indices)
                slotDescriptions.add(Slot(i, j, freeSlotDescription))

    }

    fun getSlotDescription(i: Int, j: Int) = slotDescriptions[i * state.value.lessonTimes.size + j]
    private fun setSlotDescription(i: Int, j: Int, x: String) {
        slotDescriptions[i * state.value.lessonTimes.size + j] =
            slotDescriptions[i * state.value.lessonTimes.size + j].copy(description = x)
    }

}
