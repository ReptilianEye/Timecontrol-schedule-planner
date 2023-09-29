package com.example.timecontrol.viewModelHelp.schedule

import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.StudentWithLessons

sealed class ScheduleEvent {
    data class AssignLesson(val student: StudentWithLessons, val i: Int) : ScheduleEvent()
    data class ResetSlot(val i: Int) : ScheduleEvent()
    data class RemoveLesson(val lesson: Lesson):ScheduleEvent()
    object SaveSchedule : ScheduleEvent()
}
