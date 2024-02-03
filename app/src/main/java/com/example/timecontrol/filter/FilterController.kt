package com.example.timecontrol.database.filter

import com.example.timecontrol.database.Level

data class filterController(
    var ageFiltered: IntRange? = null,
    var levelsFiltered: Pair<Level, Level>? = null,
    var areAvailable: Boolean = false,
    var departed: Boolean = false,
)