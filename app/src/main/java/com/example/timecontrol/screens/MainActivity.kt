package com.example.timecontrol.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timecontrol.R
import com.example.timecontrol.components.TitleBar
import com.example.timecontrol.database.AppDatabase
import com.example.timecontrol.database.AppRepository
import com.example.timecontrol.navigation.BottomNavigationItem
import com.example.timecontrol.navigation.MyNavigationViewModel
import com.example.timecontrol.navigation.ScreensRoutes
import com.example.timecontrol.screens.communityScreens.AddStudent
import com.example.timecontrol.screens.communityScreens.CommunityScreen
import com.example.timecontrol.screens.communityScreens.StudentDetailsScreen
import com.example.timecontrol.ui.theme.TimecontrolTheme
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModelFactory.DatabaseViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var databaseViewModel: DatabaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getInstance(application)
        val repository = AppRepository(dao)
        val factory = DatabaseViewModelFactory(repository)
        databaseViewModel = ViewModelProvider(this, factory)[DatabaseViewModel::class.java]
        setContent {
            //TODO Think about rememberSavable solution
//            val myNavigationViewModel = rememberSaveable {
//                    MyNavigationViewModel()
//                }
            val myNavigationViewModel = MyNavigationViewModel()
            TimecontrolTheme {

                // A surface container using the 'background' color from the theme
                RootLayout(
                    myNavigationViewModel = myNavigationViewModel,
                    databaseViewModel = databaseViewModel,
                    owner = this
                )
                //                Navigation(viewModel = viewModel, context = this, quote = quote.value, owner = this)
            }
        }

    }

    companion object {
        val navItems = listOf(
            BottomNavigationItem(
                title = "Community",
                icon = R.drawable.community_icon,
                hasNews = false,
                route = ScreensRoutes.CommunityScreen().getBaseRoute()
            ), BottomNavigationItem(
                title = "Home",
                icon = R.drawable.home_icon,
                hasNews = false,
                route = ScreensRoutes.HomeScreen.getBaseRoute()
            ), BottomNavigationItem(
                title = "Schedule",
                icon = R.drawable.schedule_icon,
                hasNews = false,
                route = ScreensRoutes.ScheduleScreen.getBaseRoute()
            )
        )
    }

}

@Composable
fun RootLayout(
    myNavigationViewModel: MyNavigationViewModel,
    databaseViewModel: DatabaseViewModel,
    owner: ViewModelStoreOwner,
) {
//    var optionsOpen by remember {
//        mutableStateOf(false)
//    }
//    val toggleOptions = { optionsOpen = !optionsOpen }

    val localization = myNavigationViewModel.localization.collectAsStateWithLifecycle()

    Column {
        if (localization.value != ScreensRoutes.ScheduleScreen) {  //because drag and drop does not work with it
            TitleBar(
                onLogoClick = { myNavigationViewModel.navigate(ScreensRoutes.HomeScreen) }
            )
        }
        Box(modifier = Modifier.fillMaxHeight(0.88f)) {

            when (localization.value) {
                is ScreensRoutes.CommunityScreen -> CommunityScreen(
                    myNavigationViewModel,
                    databaseViewModel,
                    owner,
                    (localization.value as ScreensRoutes.CommunityScreen).getIndex(),
                )

                ScreensRoutes.HomeScreen -> HomeScreen(
                    viewModel = databaseViewModel, myNavigationViewModel = myNavigationViewModel
                )

                ScreensRoutes.ScheduleScreen -> ScheduleScreen(
                    databaseViewModel = databaseViewModel, owner = owner
                )

                ScreensRoutes.AddStudentScreen -> AddStudent(
                    databaseViewModel = databaseViewModel,
                    navController = myNavigationViewModel,
                    owner = owner
                )

                is ScreensRoutes.StudentDetailsScreen -> StudentDetailsScreen(
                    databaseViewModel = databaseViewModel,
                    navController = myNavigationViewModel,
                    studentId = (localization.value as ScreensRoutes.StudentDetailsScreen).getStudentId()
                )
            }
        }
        BotNavBar(navController = myNavigationViewModel, localization = localization.value)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BotNavBar(navController: MyNavigationViewModel, localization: ScreensRoutes) {
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(localization.mapToBottomNavIndex())
    }
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Transparent
    ) {
        Scaffold(
            bottomBar = ({
                NavigationBar {
                    MainActivity.navItems.forEachIndexed { index, item ->
                        NavigationBarItem(selected = selectedItemIndex == index, onClick = {
                            val destination = navController.parseScreenRoute(item.route)
                            selectedItemIndex = index
                            navController.navigate(destination)
                        }, icon = {
                            Box {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.title
                                )
                            }
                        })
                    }
                }
            })
        ) {}
    }
}