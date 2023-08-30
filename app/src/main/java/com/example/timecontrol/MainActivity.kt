package com.example.timecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timecontrol.ui.theme.TimecontrolTheme
import com.example.timecontrol.uppernavbar.UpperNavbar
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy { getSharedPreferences("Timecontrol", MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimecontrolTheme {
                // A surface container using the 'background' color from the theme
                RootLayout()
            }
        }

    }
}

@Composable
fun RootLayout() {
    Column {
        UpperNavbar(
            modifier = Modifier
                .height(85.dp)
                .fillMaxWidth()
        )
        HomeScreen()// Navhost in final version
    }
}

//TODO - dokonczyc z nawigacja
//@Composable
//fun MyBottomNavigation() {
//    val destinationList = listOf(1, 2, 3)
//    val selectedIndex = rememberSaveable {
//        mutableStateOf(0)
//    }
//    BottomNavigation {
//        destinationList.forEachIndexed { index, destination ->
//            BottomNavigationItem(
//                label = { Text(text = destination.title) },
//                icon = {
//                    Icon(
//                        painter = painterResource(id = destination.icon),
//                        contentDescription = destination.title
//                    )
//                },
//                selected = selectedIndex.value == index,
//                onClick = {
//                    selectedIndex.value = index
//                    navController.navigate(destinationList[index].route) {
//                        popUpTo(Home.route)
//                        launchSingleTop = true
//                    }
//
//                },
//            )
//        }
//    }
//}
