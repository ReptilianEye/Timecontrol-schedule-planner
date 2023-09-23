package com.example.timecontrol.navigation

enum class TabbedNavItems(
    val title: String,
    val index: Int,

    // ! not working because of difference in argument count in StudentScreen and InstructorScreen
//    val content: @Composable () -> Unit
) {
    Students(title = "Students", index = 0),
    Instructors(title = "Instructors", index = 1)

}
