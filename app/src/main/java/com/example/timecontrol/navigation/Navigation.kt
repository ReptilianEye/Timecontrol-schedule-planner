package com.example.timecontrol.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timecontrol.HomeScreen
import com.example.timecontrol.RootLayout
import com.example.timecontrol.ScheduleScreen
import com.example.timecontrol.StudentsScreen
import com.example.timecontrol.data.dto.Quote
import com.example.timecontrol.viewModel.DatabaseViewModel

@Composable
fun Navigation(viewModel: DatabaseViewModel, context: Context, quote: Quote) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        //Home Screen
        composable(route = Screen.HomeScreen.route) {
            RootLayout(navController = navController,
                localization = 1,
                content = {
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
            RootLayout(navController = navController,
                localization = 0,
                content = {
                    StudentsScreen(viewModel = viewModel, navController = navController)
                })
        }

        //Schedule Screen
        composable(route = Screen.ScheduleScreen.route) {
            RootLayout(navController = navController,
                localization = 2,
                content = {
                    ScheduleScreen()
                })
        }
    }

}