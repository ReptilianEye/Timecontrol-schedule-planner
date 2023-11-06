package com.example.timecontrol.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.example.timecontrol.navigation.TabbedNavItems
import com.example.timecontrol.viewModel.DatabaseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommunityScreen(
    databaseViewModel: DatabaseViewModel,
    navController: NavController,
    owner: ViewModelStoreOwner,
    navIndex: Int,
    optionsOpen: Boolean,
    toggleOptions: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = navIndex);
    Column {
        val coroutineScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage) {
            TabbedNavItems.values().forEach { item ->
                Tab(selected = pagerState.currentPage == item.index, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(item.index)
                    }
                },
                    text = {
                        Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    })
            }
        }
        HorizontalPager(pageCount = 2, state = pagerState) { index ->
            when (index) {
                0 -> StudentsScreen(
                    viewModel = databaseViewModel,
                    navController = navController
                )

                1 -> InstructorsScreen(
                    databaseViewModel = databaseViewModel,
                    navController = navController,
                    owner = owner
                )
            }


        }
    }

}
