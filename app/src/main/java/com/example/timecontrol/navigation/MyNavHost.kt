//package com.example.timecontrol.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.example.timecontrol.screens.communityScreens.AddStudent
//import com.example.timecontrol.screens.communityScreens.StudentDetailsScreen
//
//@Composable
//fun MyNavHost(navController) {
//    NavHost(
//        navController = navHostController,
//        startDestination = ScreensRoutes.HomeScreen.route
//    ) {
//        composable(ScreensRoutes.AddStudentScreen.route) {
//            AddStudent(
//                databaseViewModel = databaseViewModel,
//                navController = navController,
//                owner = owner
//            )
//        }
//        composable(
//            route = ScreensRoutes.StudentDetailsScreen.route + "/{index}",
//            arguments = listOf(navArgument("index") {
//                type = NavType.IntType
//                defaultValue = -1
//                nullable = false
//            })
//        ) { entry ->
//            StudentDetailsScreen(
//                databaseViewModel = databaseViewModel,
//                navController = navController,
//                entry.arguments?.getInt("index")
//            )
//
//        }
//    }
//}