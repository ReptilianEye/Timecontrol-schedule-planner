package com.example.timecontrol.viewModelHelp.schedule

import com.example.timecontrol.database.StudentWithLessons
import java.time.LocalDate

sealed class ScheduleEvent {
    data class AssignLesson(
        val student: StudentWithLessons,
        val instructorIndex: Int,
        val lessonTimeIndex: Int
    ) : ScheduleEvent()

    data class HandleClick(val i: Int, val j: Int) : ScheduleEvent()

    //    data class FreeSlot(val i: Int, val j: Int) : ScheduleEvent()
    data class ConfirmLesson(val assignedLesson: AssignedLesson) : ScheduleEvent()
    data class UnconfirmLesson(val assignedLesson: AssignedLesson) : ScheduleEvent()
    data class RemoveLesson(val assignedLesson: AssignedLesson) : ScheduleEvent()
    data class OnDrop(val i: Int, val j: Int, val student: StudentWithLessons) : ScheduleEvent()
    object ToggleEditing : ScheduleEvent()
    object InitSlotDescriptions : ScheduleEvent()

    object PreventLosingScheduleChanges : ScheduleEvent()

    object OpenSaveBeforeSwitchingDialog : ScheduleEvent()
    object CloseSaveBeforeSwitchingDialog : ScheduleEvent()
    data class ChangeScheduleDate(val scheduleDate: LocalDate) : ScheduleEvent()
    object LoadPreviousLessons : ScheduleEvent()
    object SaveSchedule : ScheduleEvent()
}
