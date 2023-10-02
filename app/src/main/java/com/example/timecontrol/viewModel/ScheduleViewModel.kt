package com.example.timecontrol.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getShortcutName
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(
    val databaseViewModel: DatabaseViewModel,  // nie jestem przekonany
    var scheduleDate: LocalDate = LocalDate.now().plusDays(1)
) : ViewModel() {

    private val freeSlotDescription = "Free Slot"

    private sealed class SlotProperties {
        data class Description(val description: String) : SlotProperties()
        data class StudentId(val studentId: Int?) : SlotProperties()
        data class Confirmed(val confirmed: Boolean) : SlotProperties()
    }

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
    private val slotDetails = mutableStateListOf<SlotDetails>()

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.AssignLesson -> {
                viewModelScope.launch {
                    assignNewLesson(event.student, event.instructorIndex, event.lessonTimeIndex)
                }
            }

            is ScheduleEvent.RemoveLesson -> removeLesson(event.lesson)

            is ScheduleEvent.FreeSlot -> {
                freeSlot(event.i, event.j)
            }

            is ScheduleEvent.ConfirmLesson -> updateIfLessonConfirmed(event.lesson, true)
            is ScheduleEvent.UnconfirmLesson -> updateIfLessonConfirmed(event.lesson, false)

            ScheduleEvent.SaveSchedule

            -> {
                submitData()
            }

            is ScheduleEvent.OnDrop -> onDrop(event.i, event.j, event.student)

            //should be called only once - on schedule initiation
            ScheduleEvent.InitSlotDescriptions -> initSlotDescriptions()
        }
    }

    private fun onDrop(i: Int, j: Int, student: StudentWithLessons) {
        freeSlotWithStudent(student) // free last position of a student
        assignStudentToSlot(i, j, student)
    }

    //lesson functions
    private fun assignNewLesson(
        student: StudentWithLessons, instructorIndex: Int, lessonTimeIndex: Int
    ) {
        val instructor = state.value.instructors[instructorIndex]
        val lessonTime = state.value.lessonTimes[lessonTimeIndex]

        //remove student from schedule
        removeStudentFromSchedule(student)

        val new = Lesson(
            id = 0,
            date = _state.value.lessonsDay,
            lessonTime = lessonTime,
            studentId = student.student.id,
            instructorId = instructor.instructor.id
        )

        _assignedLessons.value = _assignedLessons.value + new
    }

    private fun removeLesson(lesson: Lesson) {
        _assignedLessons.value = _assignedLessons.value.filter { it != lesson }
        freeSlotWithStudent(databaseViewModel.getStudentById(lesson.studentId))
    }

    private fun updateIfLessonConfirmed(lesson: Lesson, confirmed: Boolean) {
        val slot = lesson.getLessonSlot()
        if (slot != null)
            updateSlotDetails(
                slot.instructorIndex,
                slot.lessonTimeIndex,
                SlotProperties.Confirmed(confirmed)
            )
    }

    fun isLessonConfirmed(lesson: Lesson): Boolean {
        return lesson.getLessonSlot()?.confirmed ?: false
    }

    private fun Lesson.getLessonSlot(): SlotDetails? {
        val instructorIndex = getInstructorIndex(this.instructorId)
        val lessonTimeIndex = getLessonTimeIndex(this.lessonTime)
        if (instructorIndex == -1 || lessonTimeIndex == -1) return null
        return getSlotDetails(instructorIndex, lessonTimeIndex)
    }

    private fun getInstructorIndex(instructorId: Int) =
        state.value.instructors.indexOfFirst { it.instructor.id == instructorId }

    private fun getLessonTimeIndex(lessonTime: Pair<String, String>) =
        state.value.lessonTimes.indexOfFirst { it == lessonTime }

    //slot functions
    private fun freeSlot(instructorIndex: Int, lessonTimeIndex: Int) {
        _assignedLessons.value =
            _assignedLessons.value.filter { !(it.instructorId == state.value.instructors[instructorIndex].instructor.id && it.lessonTime == state.value.lessonTimes[lessonTimeIndex]) }
        updateSlotDetails(
            instructorIndex, lessonTimeIndex, SlotProperties.Description(freeSlotDescription)
        )
        updateSlotDetails(
            instructorIndex, lessonTimeIndex, SlotProperties.StudentId(null)
        )
    }

    private fun assignStudentToSlot(i: Int, j: Int, student: StudentWithLessons) {
        updateSlotDetails(i, j, SlotProperties.Description(student.student.getShortcutName()))
        updateSlotDetails(i, j, SlotProperties.StudentId(student.student.id))

    }

    //removes 'student' from slotDetails array
    private fun freeSlotWithStudent(student: StudentWithLessons) {
        val studentIndex = findIndexOfStudent(student)
        println(studentIndex)
        //if student was found
        if (studentIndex != -1) {
            val mappedIndex = studentIndex.mapToIndexPair()
            freeSlot(mappedIndex.first, mappedIndex.second)
        }

    }

    fun getSlotDetails(instructorIndex: Int, lessonTimeIndex: Int) =
        slotDetails[(instructorIndex to lessonTimeIndex).mapToIndex()]

    private fun updateSlotDetails(i: Int, j: Int, property: SlotProperties) {
        val p = (i to j).mapToIndex()
        when (property) {
            is SlotProperties.Description -> slotDetails[p] =
                slotDetails[p].copy(description = property.description)

            is SlotProperties.StudentId -> slotDetails[p] =
                slotDetails[p].copy(studentId = property.studentId)

            is SlotProperties.Confirmed -> slotDetails[p] =
                slotDetails[p].copy(confirmed = property.confirmed)

            //rest not needed
        }

    }

    private fun initSlotDescriptions() {
        for (i in state.value.instructors.indices) for (j in state.value.lessonTimes.indices) {
            slotDetails.add(
                SlotDetails(
                    studentId = null,
                    instructorIndex = i,
                    lessonTimeIndex = j,
                    description = freeSlotDescription
                )
            )
        }
    }

    //student functions

    //returns index of student in slotDetails array (-1 if student not found)
    private fun findIndexOfStudent(student: StudentWithLessons): Int =
        slotDetails.indexOfFirst { (it.studentId != null) && (it.studentId == student.student.id) }


    private fun removeStudentFromSchedule(student: StudentWithLessons) {
        _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }
        freeSlotWithStudent(student)
    }

    fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }


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


    //additional
    private fun Int.mapToIndexPair() =
        this / state.value.lessonTimes.size to this % state.value.lessonTimes.size

    private fun Pair<Int, Int>.mapToIndex() = first * state.value.lessonTimes.size + second

}
