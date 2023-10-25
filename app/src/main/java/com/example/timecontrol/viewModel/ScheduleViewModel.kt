package com.example.timecontrol.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getShortcutName
import com.example.timecontrol.viewModelHelp.schedule.AssignedLesson
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import com.example.timecontrol.viewModelHelp.schedule.SlotStatus
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(
    private val databaseViewModel: DatabaseViewModel,  // nie jestem przekonany
    var scheduleDate: LocalDate = LocalDate.now().plusDays(1)
) : ViewModel() {

    private val freeSlotDescription = "Free Slot"

    private sealed class SlotProperties {
        data class Description(val description: String) : SlotProperties()
        data class Status(val status: SlotStatus) : SlotProperties()
    }

    private val _instructors = databaseViewModel.getAllCurrentInstructors()
    private val _students = databaseViewModel.currentStudents
    private val _assignedLessons: MutableStateFlow<List<AssignedLesson>> =
        MutableStateFlow(listOf())

    private val _state = MutableStateFlow(ScheduleState())
    private val validationEventChannel = Channel<ValidationEvent>()

    val state = combine(
        _state, _students, _instructors, _assignedLessons
    ) { state, students, instructors, assignedLessons ->
        state.copy(
            instructors = instructors, students = students, assignedLessons = assignedLessons
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), ScheduleState()
    )
    private val slotDetailsList = mutableStateListOf<SlotDetails>()

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.AssignLesson -> assignNewLesson(
                event.student,
                event.instructorIndex,
                event.lessonTimeIndex
            )


            is ScheduleEvent.RemoveLesson -> {
                removeLesson(event.assignedLesson)
            }

            is ScheduleEvent.FreeSlot -> {
                freeSlot(event.i, event.j)
            }

            is ScheduleEvent.ConfirmLesson -> updateLessonStatus(
                event.assignedLesson, SlotStatus.Confirmed
            )

            is ScheduleEvent.UnconfirmLesson -> updateLessonStatus(
                event.assignedLesson, SlotStatus.Assigned
            )

            ScheduleEvent.SaveSchedule -> submitData()

            is ScheduleEvent.OnDrop -> onDrop(event.i, event.j, event.student)

            //should be called only once - on schedule initiation
            ScheduleEvent.InitSlotDescriptions -> initSlotDescriptions()
            is ScheduleEvent.HandleClick -> {
//                if TODO
            }
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
//        val instructor = state.value.instructors[instructorIndex]
//        val lessonTime = state.value.lessonTimes[lessonTimeIndex]

        //remove student from schedule
//        removeStudentFromSchedule(student)
        val new = AssignedLesson(
            slot = (instructorIndex to lessonTimeIndex).getSlot(), studentId = student.student.id
        )

        _assignedLessons.value = _assignedLessons.value + new
    }

    private fun removeLesson(assignedLesson: AssignedLesson) {
        _assignedLessons.value = _assignedLessons.value.filter { it != assignedLesson }
    }

    private fun removeLesson(i: Int, j: Int) {
        removeLesson(getLessonFromIndices(i, j))
    }

    private fun updateLessonStatus(assignedLesson: AssignedLesson, status: SlotStatus) {
        setSlotDetails(
            assignedLesson.getSlot().instructorIndex,
            assignedLesson.getSlot().lessonTimeIndex,
            SlotProperties.Status(status)
        )
    }

    fun isLessonConfirmed(assignedLesson: AssignedLesson): Boolean {
        return assignedLesson.getSlot().status != SlotStatus.Unassigned
    }

    fun isLessonConfirmed(i: Int, j: Int): Boolean {
        return isLessonConfirmed(getLessonFromIndices(i, j))
    }

    private fun getLessonFromIndices(i: Int, j: Int) =
        _assignedLessons.value.first { it.slot.instructorIndex == i && it.slot.lessonTimeIndex == j }

    private fun AssignedLesson.getSlot(): SlotDetails {
        return slot
    }

    fun getInstructorFromIndex(instructorIndex: Int) = state.value.instructors[instructorIndex]

    fun getLessonTimeFromIndex(lessonTimeIndex: Int) = state.value.lessonTimes[lessonTimeIndex]
    fun getStudent(studentId: Int) = databaseViewModel.getStudentById(studentId)

    //slot functions
    private fun freeSlot(instructorIndex: Int, lessonTimeIndex: Int) {
        removeLesson(instructorIndex, lessonTimeIndex)
        setSlotDetails(
            instructorIndex, lessonTimeIndex, SlotProperties.Description(freeSlotDescription)
        )
//        updateSlotDetails(
//            instructorIndex, lessonTimeIndex, SlotProperties.StudentId(null)
//        )
    }

    private fun freeSlot(slot: SlotDetails) {
        freeSlot(slot.instructorIndex, slot.lessonTimeIndex)
    }

    private fun assignStudentToSlot(i: Int, j: Int, student: StudentWithLessons) {
        setSlotDetails(i, j, SlotProperties.Description(student.student.getShortcutName()))
        setSlotDetails(i, j, SlotProperties.Status(SlotStatus.Assigned))
    }

    //removes 'student' from slotDetails array
    private fun freeSlotWithStudent(student: StudentWithLessons) {
        val slot = findSlotOfStudent(student)
        //if student was found
        if (slot != null) {
            freeSlot(slot)
        }

    }

    fun getSlotDetails(instructorIndex: Int, lessonTimeIndex: Int) =
        slotDetailsList[(instructorIndex to lessonTimeIndex).mapToSlotIndex()]


    private fun setSlotDetails(i: Int, j: Int, property: SlotProperties) {
        val p = (i to j).mapToSlotIndex()
        when (property) {
            is SlotProperties.Description -> slotDetailsList[p] =
                slotDetailsList[p].copy(description = property.description)

            is SlotProperties.Status -> slotDetailsList[p] =
                slotDetailsList[p].copy(status = property.status)

        }
    }

    private fun initSlotDescriptions() {
        for (i in state.value.instructors.indices) for (j in state.value.lessonTimes.indices) {
            slotDetailsList.add(
                SlotDetails(
                    instructorIndex = i, lessonTimeIndex = j, description = freeSlotDescription
                )
            )
        }
    }

    //student functions

    //returns index of student in slotDetails array (-1 if student not found)
    private fun findSlotOfStudent(student: StudentWithLessons): SlotDetails? =
        _assignedLessons.value.firstOrNull { it.studentId == student.student.id }?.slot

    private fun removeStudentFromSchedule(student: StudentWithLessons) {
        _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }
        freeSlotWithStudent(student)
    }

    fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }


    private fun submitData() {
        viewModelScope.launch {
            async {
                _assignedLessons.collect { lessons: List<AssignedLesson> ->
                    lessons.forEach { assignedLesson ->
                        databaseViewModel.insertLesson(
                            assignedLesson.toLesson()
                        )
                    }
                }
            }
            async { validationEventChannel.send(ValidationEvent.Success) }
        }
        resetState()
    }


    private fun resetState() {
        _state.update { ScheduleState() }
    }


    //additional
    private fun AssignedLesson.toLesson() = Lesson(
        id = 0,
        date = _state.value.lessonsDay,
        lessonTime = _state.value.lessonTimes[slot.lessonTimeIndex],
        studentId = studentId,
        instructorId = _state.value.instructors[slot.instructorIndex].instructor.id
    )

    // from linear to 2d
    private fun Int.mapToIndexPair() =
        this / state.value.lessonTimes.size to this % state.value.lessonTimes.size

    // from 2d to linear
    private fun Pair<Int, Int>.mapToSlotIndex() = first * state.value.lessonTimes.size + second
    private fun Pair<Int, Int>.getSlot() = slotDetailsList[mapToSlotIndex()]

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}
