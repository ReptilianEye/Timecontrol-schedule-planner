package com.example.timecontrol.screens.communityScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.ViewModelStoreOwner
import com.example.timecontrol.navigation.CommunityNavItem
import com.example.timecontrol.navigation.MyNavigationViewModel
import com.example.timecontrol.viewModel.DatabaseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommunityScreen(
    navController: MyNavigationViewModel,
    databaseViewModel: DatabaseViewModel,
    owner: ViewModelStoreOwner,
    navIndex: Int=0,
//    optionsOpen: Boolean,
//    toggleOptions: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = navIndex);
    Column {
        val coroutineScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage) {
            CommunityNavItem.values().forEach { item ->
                Tab(selected = pagerState.currentPage == item.index, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(item.index)
                    }
                },
                    text = {
                        Text(text = item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                    owner = owner
                )
            }


        }
    }

}
