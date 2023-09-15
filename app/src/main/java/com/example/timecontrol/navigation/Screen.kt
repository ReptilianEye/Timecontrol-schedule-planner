package com.example.timecontrol.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object CommunityScreen : Screen("community_screen")
    object StudentsScreen : Screen("students_screen")
    object InstructorsScreen : Screen("instructors_screen")
    object ScheduleScreen : Screen("schedule_screen")
    object AddStudentScreen : Screen("add_student_screen")
    object StudentDetailsScreen : Screen("student_details_screen")

    fun withArgs(vararg args: Any): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
//TODO - Nie wiem jak na razie
//    fun withOptionalArgs(vararg args: Any): String {
//        return buildString {
//            append(route)
//            args.forEach { arg ->
//                append("?$arg")
//            }
//        }
//    }
}