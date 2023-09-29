package com.example.timecontrol.viewModelHelp.schedule

import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons

sealed class ScheduleEvent {
    data class AssignLesson(
        val student: StudentWithLessons,
        val instructorIndex: Int,
        val lessonTimeIndex: Int
    ) : ScheduleEvent()

    data class ResetSlot(val i: Int, val j: Int) : ScheduleEvent()
    data class RemoveLesson(val lesson: Lesson) : ScheduleEvent()
    data class OnDrop(val i: Int, val j: Int, val student: StudentWithLessons) : ScheduleEvent()
    object InitSlotDescriptions : ScheduleEvent()
    object SaveSchedule : ScheduleEvent()
}
