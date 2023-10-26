package com.example.timecontrol.viewModelHelp.schedule

sealed class SlotStatus {
    object Unassigned : SlotStatus()
    object Assigned : SlotStatus()
    object Confirmed : SlotStatus()

}
