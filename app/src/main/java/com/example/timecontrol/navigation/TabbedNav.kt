package com.example.timecontrol.navigation

enum class TabbedNav {
    Students,
    Instructors
}

fun getTabbedNavIndex(title: TabbedNav): Int {
    return when (title) {
        TabbedNav.Students -> 0
        TabbedNav.Instructors -> 1
    }
}