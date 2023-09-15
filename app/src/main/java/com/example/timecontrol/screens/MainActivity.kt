package com.example.timecontrol.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.timecontrol.R
import com.example.timecontrol.database.AppDatabase
import com.example.timecontrol.database.AppRepository
import com.example.timecontrol.navigation.BottomNavigationItem
import com.example.timecontrol.navigation.Navigation
import com.example.timecontrol.navigation.Screen
import com.example.timecontrol.preferences.MyPreferences
import com.example.timecontrol.preferences.QuoteController
import com.example.timecontrol.preferences.dto.Quote
import com.example.timecontrol.ui.theme.TimecontrolTheme
import com.example.timecontrol.uppernavbar.UpperNavbar
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.DatabaseViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DatabaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getInstance(application)
        val repository = AppRepository(dao)
        val factory = DatabaseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DatabaseViewModel::class.java]

        setContent {
            val preferences = remember { MyPreferences(this) }
            val controller = QuoteController.create(preferences)
            val quote = produceState(
                initialValue = Quote(),
                producer = { value = controller.getTodayQuote() })
            TimecontrolTheme {
                // A surface container using the 'background' color from the theme
                Navigation(viewModel = viewModel, context = this, quote = quote.value, owner = this)
            }
        }

    }

    companion object {
        val navItems = listOf(
            BottomNavigationItem(
                title = "Community",
                icon = R.drawable.community_icon,
                hasNews = false,
                route = Screen.CommunityScreen.route
            ),
            BottomNavigationItem(
                title = "Home",
                icon = R.drawable.home_icon,
                hasNews = false,
                route = Screen.HomeScreen.route
            ),
            BottomNavigationItem(
                title = "Schedule",
                icon = R.drawable.schedule_icon,
                hasNews = false,
                route = Screen.ScheduleScreen.route
            )
        )
    }

}

@Composable
fun RootLayout(navController: NavController, localization: Int, content: @Composable () -> Unit) {
    Column {
        UpperNavbar(
            modifier = Modifier
                .height(85.dp)
                .fillMaxWidth()
        )
        Box(modifier = Modifier.fillMaxHeight(0.88f)) {
            content()
        }
        BotNavBar(navController = navController, localization = localization)
    }
}

@Composable
fun BotNavBar(navController: NavController, localization: Int = 1) {
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(localization)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Scaffold(
            bottomBar = ({
                NavigationBar {
                    MainActivity.navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.route)
                            },
                            icon = {
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