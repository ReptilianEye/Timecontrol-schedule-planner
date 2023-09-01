package com.example.timecontrol

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timecontrol.data.MyPreferences
import com.example.timecontrol.data.QuoteController
import com.example.timecontrol.data.dto.Quote
import com.example.timecontrol.ui.theme.TimecontrolTheme
import com.example.timecontrol.uppernavbar.UpperNavbar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val preferences = remember { MyPreferences(context) }
            val controller = QuoteController.create(preferences)
            val quote = produceState(
                initialValue = Quote(),
                producer = { value = controller.getTodayQuote() })
            TimecontrolTheme {
                // A surface container using the 'background' color from the theme
                RootLayout(quote.value)
            }
        }

    }
}

@Composable
fun RootLayout(quote: Quote) {
    Column {
        UpperNavbar(
            modifier = Modifier
                .height(85.dp)
                .fillMaxWidth()
        )
//        HomeScreen(quote)// Navhost in final version
        StudentsScreen(students = emptyList())
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
