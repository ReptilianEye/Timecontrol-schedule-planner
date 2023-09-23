package com.example.timecontrol.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.example.timecontrol.navigation.TabbedNavItems
import com.example.timecontrol.viewModel.DatabaseViewModel

@Composable
fun CommunityScreen(
    databaseViewModel: DatabaseViewModel,
    navController: NavController,
    owner: ViewModelStoreOwner,
    navIndex: Int = 0
) {
    var tabbedNavIndex by remember { mutableStateOf(navIndex) }
    Column {
        TabRow(selectedTabIndex = tabbedNavIndex) {
            TabbedNavItems.values().forEach { item ->
                Tab(selected = tabbedNavIndex == item.index, onClick = {
                    tabbedNavIndex = item.index
                }, text = {
                    Text(text = item.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
                })
            }
        }
        when (tabbedNavIndex) {
            0 -> StudentsScreen(viewModel = databaseViewModel, navController = navController)
            1 -> InstructorsScreen(
                databaseViewModel = databaseViewModel,
                navController = navController,
                owner = owner
            )
        }
    }

}
