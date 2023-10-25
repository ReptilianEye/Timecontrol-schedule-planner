package com.example.timecontrol.viewModelHelp.schedule

data class SlotDetails(
//    val studentId: Int?,
    val status: SlotStatus = SlotStatus.Unassigned,
    val instructorIndex: Int,
    val lessonTimeIndex: Int,
    val description: String = "Free Slot"
)
