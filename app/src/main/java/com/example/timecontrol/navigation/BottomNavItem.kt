package com.example.timecontrol.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val icon: ImageVector,
    val hasNews: Boolean,
    val route: String
)