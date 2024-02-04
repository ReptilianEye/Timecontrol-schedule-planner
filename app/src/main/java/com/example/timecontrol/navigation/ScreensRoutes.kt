package com.example.timecontrol.navigation

sealed class ScreensRoutes(val route: String) {
    fun getBaseRoute() = route
    override fun equals(other: Any?) =
        (other is ScreensRoutes) && this.getBaseRoute() == other.getBaseRoute()

    override fun hashCode(): Int {
        return route.hashCode()
    }

    companion object {
        fun values(): List<ScreensRoutes> {
            return listOf<ScreensRoutes>(
                CommunityScreen(), //0
                HomeScreen,         //1
                ScheduleScreen,     //2
                AddStudentScreen,   //3
                StudentDetailsScreen()  //4
            )
        }

    }

    fun mapToInt() = values().indexOfFirst { it == this }
    fun mapToBottomNavIndex(): Int =
        when (this) {
            is CommunityScreen, is StudentDetailsScreen, AddStudentScreen -> 0
            HomeScreen -> 1
            ScheduleScreen -> 2
        }

    object HomeScreen : ScreensRoutes("home_screen")
    class CommunityScreen(private val tab: CommunityNavItem = CommunityNavItem.Students) :
        ScreensRoutes("community_screen") {
        fun getIndex() = tab.index

        fun getFullRoute() = "${route}?tab=${tab}"

        companion object {
            fun parse(route: String): ScreensRoutes {
                var tab: CommunityNavItem = CommunityNavItem.Students
                if (route.contains(ScreensRoutes.CommunityScreen(tab).route)) {
                    with(route) {
                        when {
                            contains(com.example.timecontrol.navigation.CommunityNavItem.Students.title) -> {
                                tab = com.example.timecontrol.navigation.CommunityNavItem.Students
                            }

                            contains(com.example.timecontrol.navigation.CommunityNavItem.Instructors.title) -> {
                                tab =
                                    com.example.timecontrol.navigation.CommunityNavItem.Instructors
                            }

                            else -> {
                            }
                        }
                    }
                }
                return ScreensRoutes.CommunityScreen(tab)
            }
        }
    }

    object ScheduleScreen : ScreensRoutes("schedule_screen")
    object AddStudentScreen : ScreensRoutes("add_student_screen")
    class StudentDetailsScreen(private val studentId: Int = -1) :
        ScreensRoutes("student_details_screen") {
        fun getStudentId() = studentId
        fun getFullRoute() = "$route?id=${studentId}"


        companion object {
            fun parse(route: String): ScreensRoutes {
                val studentId = route.split("/").last().toInt()
                return StudentDetailsScreen(studentId)
            }
        }
    }


    fun withArgs(vararg args: Any): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }


}
//TODO - Think about it
//    fun withOptionalArgs(vararg args: Any): String {
//        return buildString {
//            append(route)
//            args.forEach { arg ->
//                append("?$arg")
//            }
//        }
//    }

//object ScreenRoutesSaver : Saver<ScreensRoutes, Map<String, Any>> {
//    override fun restore(value: Map<String, Any>): ScreensRoutes? {
//        return ScreensRoutes()
//
//    }
//
//    override fun SaverScope.save(value: ScreensRoutes): Map<String, Any> {
//        return mapOf(
//            "tab" to value.mapToBottomNavIndex()
//        )
//    }
//
//}