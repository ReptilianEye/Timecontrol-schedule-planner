package com.example.timecontrol.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.timecontrol.screens.AddStudent
import com.example.timecontrol.screens.HomeScreen
import com.example.timecontrol.screens.RootLayout
import com.example.timecontrol.screens.ScheduleScreen
import com.example.timecontrol.screens.StudentsScreen
import com.example.timecontrol.preferences.dto.Quote
import com.example.timecontrol.screens.StudentDetailsScreen
import com.example.timecontrol.viewModel.DatabaseViewModel

@Composable
fun Navigation(
    viewModel: DatabaseViewModel, context: Context, quote: Quote, owner: ViewModelStoreOwner
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        //Home Screen
        composable(route = Screen.HomeScreen.route) {
            RootLayout(navController = navController, localization = 1, content = {
                HomeScreen(
                    quote = quote,
                    navController = navController,
                    viewModel = viewModel,
                    context = context
                )
            })
        }

        //Students Screen
        composable(route = Screen.StudentsScreen.route) {
            RootLayout(navController = navController, localization = 0, content = {
                StudentsScreen(viewModel = viewModel, navController = navController)
            })
        }

        //Schedule Screen
        composable(route = Screen.ScheduleScreen.route) {
            RootLayout(navController = navController, localization = 2, content = {
                ScheduleScreen()
            })
        }

        //Add Student Screen
        composable(route = Screen.AddStudentScreen.route) {
            RootLayout(navController = navController, localization = 0, content = {
                AddStudent(databaseViewModel = viewModel, navController = navController, owner)
            })
        }
        composable(
            route = Screen.StudentDetailsScreen.route + "/{index}",
            arguments = listOf(navArgument("index") {
                type = NavType.IntType
                defaultValue = -1
                nullable = false
            })
        ) { entry ->
            RootLayout(navController = navController, localization = 0) {
                StudentDetailsScreen(
                    viewModel = viewModel,
                    navController = navController,
                    entry.arguments?.getInt("index")
                )
            }

        }
    }

}