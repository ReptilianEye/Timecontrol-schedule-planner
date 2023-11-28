package com.example.timecontrol.navigation

sealed class BottomNavBarDestinations(val index: Int) { //index of bottom navbar
    object Community : BottomNavBarDestinations(0)
    object Home : BottomNavBarDestinations(1)
    object Schedule : BottomNavBarDestinations(2)
//    object AddStudent : BottomNavBarDestinations(3)
//    companion object {
//
//        fun valueOf(value: String): NavigationDestinations {
//            return when (value) {
//                "Community" -> Community
//                "Home" -> Home
//                "Schedule" -> Schedule
//                else -> throw IllegalArgumentException("No object com.example.timecontrol.navigation.NavigationDestinations.$value")
//            }
//        }
//    }
}