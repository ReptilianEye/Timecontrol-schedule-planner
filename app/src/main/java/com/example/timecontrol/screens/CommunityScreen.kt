package com.example.timecontrol.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.database.Lesson
import com.example.timecontrol.database.Levels
import com.example.timecontrol.viewModel.DatabaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CommunityScreen(
    databaseViewModel: DatabaseViewModel,
    navController: NavController,
    owner: ViewModelStoreOwner,
    navIndex: Int = 0
) {
    var state by remember { mutableStateOf(navIndex) }
    val titles = listOf(
        "Students", "Instructors"
    )
    Column {
        TabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(selected = state == index, onClick = {
                    state = index
                }, text = {
                    Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                })
            }
        }
        when (state) {
            0 -> StudentsScreen(viewModel = databaseViewModel, navController = navController)
            1 -> InstructorsScreen(
                databaseViewModel = databaseViewModel,
                navController = navController,
                owner = owner
            )
        }
    }

}
