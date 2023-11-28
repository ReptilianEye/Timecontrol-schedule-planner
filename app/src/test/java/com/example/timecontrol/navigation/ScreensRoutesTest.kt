package com.example.timecontrol.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreensRoutesTest {

    @Test
    fun mapToInt() {
        val indices = listOf(0..4)
        val case1 = ScreensRoutes.ScheduleScreen
        val case2 = ScreensRoutes.CommunityScreen(CommunityNavItem.Students)
        val case3 = ScreensRoutes.StudentDetailsScreen(5)
        assertEquals(2,case1.mapToInt())
        assertEquals(0,case2.mapToInt() )//{ "Wrong index when mapping from Community Screen " + case2.mapToInt() }
        assertEquals(4,case3.mapToInt() )//{ "Wrong index when mapping from Student Details Screen, got:  " + case3.mapToInt() }
    }
}