package com.example.timecontrol.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.LessonWithStudentAndInstructor
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getShortcutName
import com.example.timecontrol.viewModelHelp.schedule.AssignedLesson
import com.example.timecontrol.viewModelHelp.schedule.LoadingState
import com.example.timecontrol.viewModelHelp.schedule.ScheduleEvent
import com.example.timecontrol.viewModelHelp.schedule.ScheduleState
import com.example.timecontrol.viewModelHelp.schedule.SlotDetails
import com.example.timecontrol.viewModelHelp.schedule.SlotStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModel(
    private val databaseViewModel: DatabaseViewModel,
) : ViewModel() {

    private val freeSlotDescription = "Free Slot"

    private val _scheduleDate = MutableStateFlow(LocalDate.now()) //2023-10-25
    val scheduleDate = _scheduleDate.asStateFlow()

    private val _areInstructorsLoading = MutableStateFlow(true)
    private val _areStudentsLoading = MutableStateFlow(true)
    private val _arePreviousLessonsLoading = MutableStateFlow(true)
    private val _loadingState = MutableStateFlow(LoadingState())

    val loadingState = combine(
        _loadingState,
        _areInstructorsLoading,
        _areStudentsLoading,
        _arePreviousLessonsLoading
    ) { loadingState, areInstructorsLoading, areStudentsLoading, arePreviousLessonsLoading ->
        loadingState.copy(
            areInstructorsLoading = areInstructorsLoading,
            areStudentsLoading = areStudentsLoading,
            arePreviousLessonsLoading = arePreviousLessonsLoading,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LoadingState())


    //    private var _instructors = databaseViewModel.getAllCurrentInstructors()
    private val _instructors = _scheduleDate.flatMapLatest {
        databaseViewModel.getAllCurrentInstructors(it).onEach { toggleAreInstructorsLoading(false) }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    private val _students = _scheduleDate.flatMapLatest {
        databaseViewModel.getAllCurrentStudents(it).onEach { toggleAreStudentsLoading(false) }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    private val _previousLessons = _scheduleDate.flatMapLatest {
        databaseViewModel.getAllLessonsFromDate(it)
            .onEach { toggleArePreviousLessonsLoading(false) }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    private val _assignedLessons: MutableStateFlow<List<AssignedLesson>> =
        MutableStateFlow(listOf())

    private val _state = MutableStateFlow(ScheduleState())

    val state = combine(
        _state,
        _students,
        _instructors,
        _assignedLessons,
        _previousLessons,
    ) {
            state, students, instructors, assignedLessons, previousLessons,
        ->
        state.copy(
            instructors = instructors,
            students = students,
            assignedLessons = assignedLessons,
            previousLessons = previousLessons,
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ScheduleState()
    )

    private val slotDetailsList = mutableStateListOf<SlotDetails>()

    private val eventChannel = Channel<Event>()
    val validationEvents = eventChannel.receiveAsFlow()
    fun onEvent(event: ScheduleEvent) {
//        println(event)
        when (event) {
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

            ScheduleEvent.LoadPreviousLessons -> addPreviousLessonsToAssigned()

            ScheduleEvent.OpenSaveBeforeSwitchingDialog -> {
                toggleSaveBeforeSwitchingDialog(true)
            }

            ScheduleEvent.CloseSaveBeforeSwitchingDialog -> closeSaveBeforeSwitchingDialog()


            ScheduleEvent.ToggleEditing -> {
                _state.value = _state.value.copy(isEditingEnabled = !state.value.isEditingEnabled)

            }

            ScheduleEvent.PreventLosingScheduleChanges -> preventLosingScheduleChanges()
        }
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
        removeStudentFromSchedule(student)
        val new = AssignedLesson(
            slotIndex = (instructorIndex to lessonTimeIndex).mapToSlotIndex(),
            studentId = student.student.id
        )
        _assignedLessons.value = _assignedLessons.value + new
    }

    private fun removeStudentFromSchedule(student: StudentWithLessons) {
        freeSlotWithStudent(student)
        _assignedLessons.value =
            _assignedLessons.value.filter { it.studentId != student.student.id }
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
        if (assignedLesson.databaseId != null) databaseViewModel.deleteLessonById(assignedLesson.databaseId)
        _assignedLessons.value = _assignedLessons.value.filter { it != assignedLesson }
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

    private fun areAllAssignedLessonsConfirmed() =
        slotDetailsList.any { it.status != SlotStatus.Confirmed }

    private fun preventLosingScheduleChanges() {
        viewModelScope.launch {
            if (state.value.isEditingEnabled && !areAllAssignedLessonsConfirmed()) eventChannel.send(
                Event.OpenSaveBeforeSwitchingDialog
            )
            else eventChannel.send(Event.OpenDatePicker)
        }
    }

    private fun toggleSaveBeforeSwitchingDialog(isOpen: Boolean) {
        _state.value = _state.value.copy(
            isSaveBeforeSwitchingDialogOpen = isOpen
        )
    }

    private fun changeScheduleDate(date: LocalDate) {
        startLoadings()
        _scheduleDate.value = date
        resetSchedule()

    }

    private fun LessonWithStudentAndInstructor.toAssignedLesson(): AssignedLesson? {
        val i = instructor.getIndex()
        val j = lesson.lessonTime.getIndex()
        return if (i != -1 && j != -1) AssignedLesson(
            (i to j).mapToSlotIndex(), student.id, databaseId = lesson.id
        ) else null
    }

    fun arePreviousLessonNotLoadedAndAvailable() =
        !state.value.previousLessonsLoaded && !loadingState.value.arePreviousLessonsLoading && state.value.previousLessons.isNotEmpty()

    private fun addPreviousLessonsToAssigned() {
//        if (state.value.previousLessons.isNotEmpty()) {
//            //TODO replace with something better one day
//            val previousLessonsFiltered =
//                state.value.previousLessons.filter { it.lesson.date == scheduleDate.value }
//                    .mapNotNull { it.toAssignedLesson() }
//            if (previousLessonsFiltered.isEmpty()) {
//                println(state.value.previousLessons)
//                return
//            }
////            ? - kod majacy na celu poczekanie az z bazy danych zostana wybrane lekcje z odpowiedniego dnia po zamianie daty

        viewModelScope.launch(Dispatchers.IO) {
            _assignedLessons.value =
                _assignedLessons.value + state.value.previousLessons.mapNotNull { it.toAssignedLesson() }
            _assignedLessons.value.forEach {
                setSlotDetails(
                    it.slotIndex,
                    SlotStatus.Confirmed,
                    databaseViewModel.getStudentById(it.studentId)
                )
            }
        }
        _state.value = _state.value.copy(previousLessonsLoaded = true)
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

    fun isDataReadyInitialization(): Boolean {
        println(state.value.initialized.toString())
        println(loadingState.value.areStudentsLoading.toString())
        println("------------")
//        println("$state.value.initialized $loadingState.value.areInstructorsLoading")
        return !state.value.initialized && !loadingState.value.areInstructorsLoading
    }

    private fun initSlotDescriptions() {
        for (i in state.value.instructors.indices) for (j in state.value.lessonTimes.indices) {
            slotDetailsList.add(
                SlotDetails(
                    instructorIndex = i, lessonTimeIndex = j, description = freeSlotDescription
                )
            )
        }
        _state.value = _state.value.copy(initialized = true)
    }

//student functions

    //returns index of student in slotDetails array (-1 if student not found)
    private fun findSlotWithStudent(student: StudentWithLessons): SlotDetails? =
        _assignedLessons.value.firstOrNull { it.studentId == student.student.id }?.getSlot()


    fun isStudentAssigned(student: StudentWithLessons): Boolean =
        _assignedLessons.value.any { it.studentId == student.student.id }


    private fun submitData() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseViewModel.deleteAllLessonsFromDate(scheduleDate.value)
            _assignedLessons.value.forEach { assignedLesson: AssignedLesson ->
                if (assignedLesson.databaseId == null) databaseViewModel.insertLesson(
                    assignedLesson.toLesson()
                )
            }
            resetState()
        }
        viewModelScope.launch(Dispatchers.IO) {
            eventChannel.send(Event.Success)
        }
    }

    private fun closeSaveBeforeSwitchingDialog() {
        toggleSaveBeforeSwitchingDialog(false)
        viewModelScope.launch(Dispatchers.IO) {
            eventChannel.send(Event.OpenDatePicker)
        }
    }

    //resetting methods
    private fun resetSchedule() {
        removeAssignedLessons()
        resetSlots()
        _state.update { ScheduleState() }
    }

    private fun resetState() {
        _state.value = ScheduleState(
            initialized = true
        )
    }

    private fun removeAssignedLessons() {
        _assignedLessons.value = emptyList()
    }

    private fun resetSlots() {
        slotDetailsList.clear()
    }

    private fun startLoadings() {
        toggleAreInstructorsLoading(true)
        toggleAreStudentsLoading(true)
        toggleArePreviousLessonsLoading(true)
    }

    private fun toggleAreInstructorsLoading(value: Boolean) {
        _areInstructorsLoading.value = value
    }

    private fun toggleAreStudentsLoading(value: Boolean) {
        _areStudentsLoading.value = value
    }

    private fun toggleArePreviousLessonsLoading(value: Boolean) {
        _arePreviousLessonsLoading.value = value
    }


    //used for saving to database
    private fun AssignedLesson.toLesson() = Lesson(
        id = 0,
        date = scheduleDate.value,
        lessonTime = state.value.lessonTimes[getSlot().lessonTimeIndex],
        studentId = studentId,
        instructorId = state.value.instructors[getSlot().instructorIndex].instructor.id
    )

    // from linear to 2d
    private fun Int.mapToIndexPair() =
        this / state.value.lessonTimes.size to this % state.value.lessonTimes.size

    // from 2d to linear
    private fun Pair<Int, Int>.mapToSlotIndex() = first * state.value.lessonTimes.size + second

    //getters
    private fun getSlotDescription(i: Int, j: Int): String {
        val lesson = getLessonFromIndices(i, j)
        return if (lesson != null) getStudent(lesson.studentId).student.getShortcutName() else freeSlotDescription
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

    private fun getSlot(slotIndex: Int) = slotDetailsList[slotIndex]
    private fun Pair<Int, Int>.getSlot() = slotDetailsList[mapToSlotIndex()]

    fun getInstructorFromIndex(instructorIndex: Int) = state.value.instructors[instructorIndex]

    private fun Instructor.getIndex() = state.value.instructors.indexOfFirst {
        it.instructor.id == id
    }

    fun getLessonTimeFromIndex(lessonTimeIndex: Int) = state.value.lessonTimes[lessonTimeIndex]
    private fun Pair<String, String>.getIndex() =
        state.value.lessonTimes.indexOfFirst { it == this }

    fun getStudent(studentId: Int) = databaseViewModel.getStudentById(studentId)


    //Deprecated
    @Deprecated("No longer needed")
    fun isLessonConfirmed(i: Int, j: Int): Boolean {
        val lesson = getLessonFromIndices(i, j)
        return if (lesson != null) isLessonConfirmed(lesson) else false
    }

    @Deprecated("No no longer needed")
    private fun removeLessonFromAssignedLessons(i: Int, j: Int) {
        val lesson = getLessonFromIndices(i, j)
        if (lesson != null) removeLessonFromAssignedLessons(lesson) else false
    }


    sealed class Event {
        object Success : Event()
        object OpenSaveBeforeSwitchingDialog : Event()
        object OpenDatePicker : Event()
    }
}
