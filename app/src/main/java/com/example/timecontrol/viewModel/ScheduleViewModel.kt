package com.example.timecontrol.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.LessonWithStudentAndInstructor
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getShortcutName
import com.example.timecontrol.viewModelHelp.schedule.AssignedLesson
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import com.example.timecontrol.viewModelHelp.schedule.SlotStatus
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(
    private val databaseViewModel: DatabaseViewModel,
//    var scheduleDate: LocalDate = LocalDate.now()
) : ViewModel() {

    private val freeSlotDescription = "Free Slot"

    private val scheduleDateFlow = MutableStateFlow(LocalDate.now()) //2023-10-25

    //    private var _instructors = databaseViewModel.getAllCurrentInstructors()
    private val _instructors = scheduleDateFlow.flatMapLatest {
        databaseViewModel.getAllCurrentInstructors(it)
    }
    private val _students = scheduleDateFlow.flatMapLatest {
        databaseViewModel.getAllCurrentStudents(it)
    }

    private val _assignedLessons: MutableStateFlow<List<AssignedLesson>> =
        MutableStateFlow(listOf())

    private val _previouslyAdded = scheduleDateFlow.flatMapLatest {
        databaseViewModel.getAllLessonsFromDate(it)
    }

    private val _state = MutableStateFlow(ScheduleState())

    val state = combine(
        _state, _students, _instructors, _assignedLessons, _previouslyAdded
    ) { state, students, instructors, assignedLessons, previouslyAdded ->
        state.copy(
            instructors = instructors,
            students = students,
            assignedLessons = assignedLessons,
            previouslyAdded = previouslyAdded
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), ScheduleState()
    )

    private val slotDetailsList = mutableStateListOf<SlotDetails>()

    private val eventChannel = Channel<Event>()
    val validationEvents = eventChannel.receiveAsFlow()
    fun onEvent(event: ScheduleEvent) = when (event) {
        is ScheduleEvent.AssignLesson -> assignNewLesson(
            event.student, event.instructorIndex, event.lessonTimeIndex
        )

        is ScheduleEvent.RemoveLesson -> {
            removeLessonAndFreeSlot(event.assignedLesson)
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
            handleOnSlotClick(event.i, event.j)
        }

        is ScheduleEvent.ChangeScheduleDate -> changeScheduleDate(event.scheduleDate)

        ScheduleEvent.LoadPreviousLessons -> loadPreviousLessons()

        ScheduleEvent.OpenSaveBeforeSwitchingDialog -> {
            toggleSaveBeforeSwitchingDialog(true)
        }

        ScheduleEvent.CloseSaveBeforeSwitchingDialog -> closeSaveBeforeSwitchingDialog()


        ScheduleEvent.ToggleEditing -> {
            _state.value =
                _state.value.copy(isEditingEnabled = !state.value.isEditingEnabled)

        }

        ScheduleEvent.PreventLosingScheduleChanges -> preventLosingScheduleChanges()
    }


    private fun onDrop(i: Int, j: Int, student: StudentWithLessons) {
        val assignedLesson = getLessonFromIndices(i, j)
        if (assignedLesson != null) removeLessonAndFreeSlot(assignedLesson)
        setSlotDetails(i, j, SlotStatus.Assigned, student)
//        assignStudentToSlot(i, j, student)
    }

    private fun handleOnSlotClick(i: Int, j: Int) {
        val slot = getSlot(i, j)
        when (slot.status) {
            SlotStatus.Assigned -> removeLessonAndFreeSlot(i, j)
            SlotStatus.Confirmed -> setSlotDetails(slot, SlotStatus.Assigned)

            //probably some alert
            SlotStatus.Unassigned -> "Nothing to do"

        }
    }

    //lesson functions
    private fun assignNewLesson(
        student: StudentWithLessons, instructorIndex: Int, lessonTimeIndex: Int,
    ) {
        val new = AssignedLesson(
            slotIndex = (instructorIndex to lessonTimeIndex).mapToSlotIndex(),
            studentId = student.student.id
        )

        _assignedLessons.value = _assignedLessons.value + new
    }

    //removes lesson and fre
    private fun removeLessonAndFreeSlot(assignedLesson: AssignedLesson) {
        freeSlotWithStudent(getStudent(assignedLesson.studentId)) // free last position of a student
        removeLessonFromAssignedLessons(assignedLesson)
    }

    private fun removeLessonAndFreeSlot(i: Int, j: Int) {
        val assignedLesson = getLessonFromIndices(i, j)
        if (assignedLesson != null) {
            freeSlotWithStudent(getStudent(assignedLesson.studentId)) // free last position of a student
            removeLessonFromAssignedLessons(assignedLesson)
        }
    }


    private fun removeLessonFromAssignedLessons(assignedLesson: AssignedLesson) {
        _assignedLessons.value = _assignedLessons.value.filter { it != assignedLesson }
    }

    @Deprecated("No no longer needed")
    private fun removeLessonFromAssignedLessons(i: Int, j: Int) {
        val lesson = getLessonFromIndices(i, j)
        if (lesson != null) removeLessonFromAssignedLessons(lesson) else false
    }

    private fun updateLessonStatus(assignedLesson: AssignedLesson, status: SlotStatus) {
        setSlotDetails(
            assignedLesson.getSlot().instructorIndex,
            assignedLesson.getSlot().lessonTimeIndex,
            status
        )
    }

    fun isLessonConfirmed(assignedLesson: AssignedLesson): Boolean {
        return assignedLesson.getSlot().status == SlotStatus.Confirmed
    }

    @Deprecated("No no longer needed")
    fun isLessonConfirmed(i: Int, j: Int): Boolean {
        val lesson = getLessonFromIndices(i, j)
        return if (lesson != null) isLessonConfirmed(lesson) else false
    }

    private fun getLessonFromIndices(i: Int, j: Int) =
        _assignedLessons.value.firstOrNull { it.getSlot().instructorIndex == i && it.getSlot().lessonTimeIndex == j }

    @JvmName("GetAssignedLessonInner")
    private fun AssignedLesson.getSlot(): SlotDetails {
        return slotDetailsList[slotIndex]
    }

    @JvmName("GetAssignedLessonApi")
    fun getSlot(assignedLesson: AssignedLesson) = assignedLesson.getSlot()

    fun getSlot(instructorIndex: Int, lessonTimeIndex: Int) =
        slotDetailsList[(instructorIndex to lessonTimeIndex).mapToSlotIndex()]

    private fun getSlot(slotIndex: Int) =
        slotDetailsList[slotIndex]

    private fun preventLosingScheduleChanges() {
        if (state.value.isEditingEnabled)
            viewModelScope.launch {
                eventChannel.send(Event.SaveBeforeSwitching)
            }
        else
            viewModelScope.launch {
                eventChannel.send(Event.OpenDatePicker)
            }
    }

    private fun toggleSaveBeforeSwitchingDialog(open: Boolean) {
        _state.value =
            _state.value.copy(
                isSaveBeforeSwitchingDialogOpen = open
            )
    }

    private fun changeScheduleDate(date: LocalDate) {
        scheduleDateFlow.value = date
        resetState()
//        _state.value = _state.value.copy(scheduleDate = scheduleDateFlow.value)
//        removeAssignedLessons()
    }

    private fun Pair<Int, Int>.getSlot() = slotDetailsList[mapToSlotIndex()]

    fun getInstructorFromIndex(instructorIndex: Int) = state.value.instructors[instructorIndex]

    private fun Instructor.getIndex() = state.value.instructors.indexOfFirst {
        it.instructor.id == id
    }

    fun getLessonTimeFromIndex(lessonTimeIndex: Int) = state.value.lessonTimes[lessonTimeIndex]
    private fun Pair<String, String>.getIndex() =
        state.value.lessonTimes.indexOfFirst { it == this }

    fun getStudent(studentId: Int) = databaseViewModel.getStudentById(studentId)

    private fun LessonWithStudentAndInstructor.toAssignedLesson(): AssignedLesson? {
        val i = instructor.getIndex()
        val j = lesson.lessonTime.getIndex()
        return if (i != -1 && j != -1) AssignedLesson(
            (i to j).mapToSlotIndex(),
            student.id
        ) else null
    }

    private fun loadPreviousLessons() {
        _assignedLessons.value =
            _assignedLessons.value + state.value.previouslyAdded.mapNotNull { it.toAssignedLesson() }
        _assignedLessons.value.forEach {
            setSlotDetails(
                it.slotIndex,
                SlotStatus.Confirmed,
                databaseViewModel.getStudentById(it.studentId)
            )
        }
        _state.value = _state.value.copy(loadedPreviousLessons = true);
    }

    //slot functions

    //removes 'student' from slotDetails array
    private fun freeSlotWithStudent(student: StudentWithLessons) {
        val slot = findSlotWithStudent(student)
        //if student was found
        if (slot != null) setSlotDetails(slot, SlotStatus.Unassigned)
    }


    private fun setSlotDetails(
        i: Int, j: Int, status: SlotStatus, student: StudentWithLessons? = null,
    ) {
        val p = (i to j).mapToSlotIndex()
        if (status == SlotStatus.Unassigned) {
            slotDetailsList[p] =
                slotDetailsList[p].copy(status = status, description = freeSlotDescription)
        } else {
            slotDetailsList[p] = slotDetailsList[p].copy(
                status = status,
                description = student?.student?.getShortcutName() ?: getSlotDescription(
                    i, j
                )
            )
        }
    }

    private fun setSlotDetails(
        slotDetails: SlotDetails, status: SlotStatus, student: StudentWithLessons? = null,
    ) = setSlotDetails(
        slotDetails.instructorIndex, slotDetails.lessonTimeIndex, status, student
    )

    private fun setSlotDetails(
        slotIndex: Int, status: SlotStatus, student: StudentWithLessons? = null,
    ) = setSlotDetails(
        getSlot(slotIndex), status, student
    )


    private fun getSlotDescription(i: Int, j: Int): String {
        val lesson = getLessonFromIndices(i, j)
        return if (lesson != null) getStudent(lesson.studentId).student.getShortcutName() else freeSlotDescription
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
    private fun findSlotWithStudent(student: StudentWithLessons): SlotDetails? =
        _assignedLessons.value.firstOrNull { it.studentId == student.student.id }?.getSlot()

    private fun removeStudentFromSchedule(student: StudentWithLessons) {
        _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }
        freeSlotWithStudent(student)
    }

    fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }


    private fun submitData() {
        viewModelScope.launch(Dispatchers.IO) {

            async {
                databaseViewModel.deleteAllLessonsFromDate(scheduleDateFlow.value)
                _assignedLessons.value.forEach { assignedLesson: AssignedLesson ->
                    databaseViewModel.insertLesson(
                        assignedLesson.toLesson()
                    )
                }
            }
            async { eventChannel.send(Event.Success) }
            resetState()
        }
    }

    private fun closeSaveBeforeSwitchingDialog() {
        toggleSaveBeforeSwitchingDialog(false)
        viewModelScope.launch(Dispatchers.IO) {
            eventChannel.send(Event.OpenDatePicker)
        }
    }

    private fun resetState() {
        removeAssignedLessons()
        _state.value = ScheduleState(
            scheduleDate = scheduleDateFlow.value, assignedLessons = emptyList()
        )
    }

    private fun removeAssignedLessons() {
        _assignedLessons.value = emptyList()
    }


    //additional
    private fun AssignedLesson.toLesson() = Lesson(
        id = 0,
        date = state.value.lessonsDay,
        lessonTime = state.value.lessonTimes[getSlot().lessonTimeIndex],
        studentId = studentId,
        instructorId = state.value.instructors[getSlot().instructorIndex].instructor.id
    )

    // from linear to 2d
    private fun Int.mapToIndexPair() =
        this / state.value.lessonTimes.size to this % state.value.lessonTimes.size

    // from 2d to linear
    private fun Pair<Int, Int>.mapToSlotIndex() = first * state.value.lessonTimes.size + second


    sealed class Event {
        object Success : Event()
        object SaveBeforeSwitching : Event()
        object OpenDatePicker : Event()
    }
}
