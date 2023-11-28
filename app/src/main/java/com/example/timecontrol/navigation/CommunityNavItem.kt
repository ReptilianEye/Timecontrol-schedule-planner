package com.example.timecontrol.navigation

sealed class CommunityNavItem(
    val title: String,
    val index: Int,
    ) {
    object Students : CommunityNavItem(title = "Students", index = 0)
    object Instructors : CommunityNavItem(title = "Instructors", index = 1)
    companion object {
        fun values(): Array<CommunityNavItem> {
            return arrayOf(Students, Instructors)
        }

        fun valueOf(value: String): CommunityNavItem {
            return when (value) {
                "Students" -> Students
                "Instructors" -> Instructors
                else -> throw IllegalArgumentException("No object com.example.timecontrol.navigation.CommunityNavItem.$value")
            }
        }
    }

}
