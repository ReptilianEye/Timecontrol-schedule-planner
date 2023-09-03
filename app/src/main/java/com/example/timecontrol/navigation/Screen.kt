package com.example.timecontrol.navigation

sealed class Screen(val route: String){
    object HomeScreen: Screen("home_screen")
    object StudentsScreen: Screen("students_screen")
    object ScheduleScreen: Screen("schedule_screen")

    fun withArgs(vararg args: Any): String{
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}