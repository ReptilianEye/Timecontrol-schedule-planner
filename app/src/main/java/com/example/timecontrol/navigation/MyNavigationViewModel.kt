package com.example.timecontrol.navigation

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyNavigationViewModel(/*val navigationController: NavHostController*/) : ViewModel() {

    private val _localizationFlow =
        MutableStateFlow<ScreensRoutes>(ScreensRoutes.ScheduleScreen)
    val localization = _localizationFlow.asStateFlow()

    //    fun setLocalization(newLocalization: Int) {
//        _localizationFlow.value = newLocalization
//    }
    fun navigate(destination: ScreensRoutes) {
        _localizationFlow.update { destination }
    }

    fun navigate(route: String) {
        val destination = parseScreenRoute(route)
        navigate(destination)
    }


    fun parseScreenRoute(route: String): ScreensRoutes {
        return when (route) {
            ScreensRoutes.HomeScreen.getBaseRoute() -> ScreensRoutes.HomeScreen
//        ScreensRoutes.StudentsScreen.route -> ScreensRoutes.StudentsScreen
//        ScreensRoutes.InstructorsScreen.route -> ScreensRoutes.InstructorsScreen
            ScreensRoutes.ScheduleScreen.getBaseRoute() -> ScreensRoutes.ScheduleScreen
            ScreensRoutes.AddStudentScreen.getBaseRoute() -> ScreensRoutes.AddStudentScreen
            else -> {
                with(route) {
                    when {
                        contains(
                            ScreensRoutes.StudentDetailsScreen().getBaseRoute()
                        ) -> ScreensRoutes.StudentDetailsScreen.parse(
                            route
                        )

                        contains(
                            ScreensRoutes.CommunityScreen().getBaseRoute()
                        ) -> ScreensRoutes.CommunityScreen.parse(route)

                        else -> {
                            throw IllegalArgumentException("No screen associated with route $route")
                        }
                    }
                }
            }
        }
    }
}