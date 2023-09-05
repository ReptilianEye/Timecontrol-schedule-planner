package com.example.timecontrol

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitesurfing
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitesurfing
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.example.timecontrol.data.MyPreferences
import com.example.timecontrol.data.QuoteController
import com.example.timecontrol.data.dto.Quote
import com.example.timecontrol.database.AppDatabase
import com.example.timecontrol.database.AppRepository
import com.example.timecontrol.database.Instructor
import com.example.timecontrol.navigation.BottomNavigationItem
import com.example.timecontrol.navigation.Navigation
import com.example.timecontrol.navigation.Screen
import com.example.timecontrol.ui.theme.TimecontrolTheme
import com.example.timecontrol.uppernavbar.UpperNavbar
import com.example.timecontrol.viewModel.DatabaseViewModelFactory
import com.example.timecontrol.viewModel.DatabaseViewModel
import java.time.LocalDate

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
                Navigation(viewModel = viewModel, context = this, quote = quote.value)
            }
        }

    }

    companion object {
        val navItems = listOf(
            BottomNavigationItem(
                title = "Students",
                icon = Icons.Filled.Kitesurfing,
                hasNews = false,
                route = Screen.StudentsScreen.route
            ),
            BottomNavigationItem(
                title = "Home",
                icon = Icons.Filled.Home,
                hasNews = false,
                route = Screen.HomeScreen.route
            ),
            BottomNavigationItem(
                title = "Schedule",
                icon = Icons.Filled.CalendarToday,
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
                            label = {
                                Text(text = item.title)
                            },
                            icon = {
                                Box {
                                    Icon(
                                        imageVector = item.icon,
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