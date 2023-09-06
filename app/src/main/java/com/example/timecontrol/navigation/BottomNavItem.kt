package com.example.timecontrol.navigation

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val icon: Int,
    val hasNews: Boolean,
    val route: String
)