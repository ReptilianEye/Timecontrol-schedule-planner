package com.example.timecontrol.navigation

sealed class NavigationDestinations(val index: Int) { //index of bottom navbar
    object Community : NavigationDestinations(0)
    object Home : NavigationDestinations(1)
    object Schedule : NavigationDestinations(2)
    companion object {
        fun values(): Array<NavigationDestinations> {
            return arrayOf(Community, Home, Schedule)
        }

        fun valueOf(value: String): NavigationDestinations {
            return when (value) {
                "Community" -> Community
                "Home" -> Home
                "Schedule" -> Schedule
                else -> throw IllegalArgumentException("No object com.example.timecontrol.navigation.NavigationDestinations.$value")
            }
        }
    }
}