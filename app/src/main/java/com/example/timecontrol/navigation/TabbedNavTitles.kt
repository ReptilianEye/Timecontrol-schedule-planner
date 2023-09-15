package com.example.timecontrol.navigation

enum class TabbedNavTitles {
    Students,
    Instructors
}

fun getTabbedNavIndex(title: TabbedNavTitles): Int {
    return when (title) {
        TabbedNavTitles.Students -> 0
        TabbedNavTitles.Instructors -> 1
    }
}