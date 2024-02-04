package com.example.timecontrol.utils

import androidx.compose.runtime.MutableState

data class LevelController(val level: String, val skills: List<String>) :
    Comparable<LevelController> {
    override fun compareTo(other: LevelController) = level.compareTo(other.level)

    fun isBetween(bounds: Pair<LevelController, LevelController>): Boolean =
        bounds.first <= this && this <= bounds.second

    companion object {
        //get max level from checked for student input
        fun fromString(level: String): LevelController =
            getLevels().find { it.level == level }
                ?: throw IllegalArgumentException("No such level")

        fun getLevel(i: Int): LevelController = getLevels()[i]
        fun getLevels(): List<LevelController> = LEVELS
        fun getMinMaxLevel() = getLevels().first() to getLevels().last()
        fun getMaxLevel(levelsCheckState: List<MutableState<Boolean>>) =
            levelsCheckState.indexOfLast { it.value }
                .let { if (it != -1) getLevels()[it].level else "D" }

        fun getMinLevel(levelsCheckState: List<MutableState<Boolean>>) =
            levelsCheckState.indexOfFirst { it.value }
                .let { if (it != -1) getLevels()[it].level else "D" }

        private val LEVELS = listOf(
            LevelController(
                "1A", listOf(
                    "SEA (Spot, Environment, Activity) assessment",
                    "Hold, carry, and secure a kite on land",
                    "Kite setup"
                )
            ), LevelController(
                "1B", listOf(
                    "Safety systems use", "Pre-flight check", "Launch and land as an assistant"
                )
            ), LevelController(
                "1C", listOf(
                    "First piloting and explore the wind window's edge",
                    "Let go of the bar",
                    "Twist and untwist the lines"
                )
            ), LevelController(
                "1D", listOf(
                    "Fly one-handed",
                    "Trim introduction",
                    "Walk while flying the kite",
                    "Launch and land as a pilot",
                    "Wind window theory"
                )
            ), LevelController(
                "1E", listOf(
                    "Inflight quick release activation",
                    "Self-land",
                    "Equipment packing",
                )
            ), LevelController(
                "2F", listOf(
                    "Enter and exit the water while controlling the kite",
                    "Water relaunch",
                )
            ), LevelController(
                "2G", listOf(
                    "Body-drag with 2 hands, kite stable",
                    "Body-drag with power stroke",
                )
            ), LevelController(
                "2H", listOf(
                    "Body-drag upwind",
                    "Body-drag with the board",
                    "Self-rescue and pack down introduction"
                )
            ), LevelController(
                "2I", listOf(
                    "ROW (Right of Way) rules introduction",
                    "Steady-pull",
                    "Waterstart",
                    "Controlled stop"
                )
            ), LevelController(
                "3J", listOf(
                    "Control of speed by edging"
                )
            ), LevelController(
                "3J", listOf(
                    "Ride upwind"
                )
            ), LevelController(
                "3L", listOf(
                    "Sliding transition"
                )
            ), LevelController(
                "3M", listOf(
                    "Ride toeside", "Jibe"
                )
            ), LevelController(
                "3N", listOf(
                    "Self-launch", "Self-rescue and pack down in deep water"
                )
            ), LevelController(
                "4O", listOf(
                    "Basic jump"
                )
            ), LevelController(
                "4P", listOf(
                    "Power jibe"
                )
            ), LevelController(
                "4Q", listOf(
                    "Jump with grab"
                )
            ), LevelController(
                "4R", listOf(
                    "Jump transition"
                )
            ), LevelController(
                "4S", listOf(
                    "Rider recovery"
                )
            ), LevelController(
                "4T", listOf(
                    "Board recovery"
                )
            ), LevelController(
                "4U", listOf(
                    "International kiteboarding signs"
                )
            ), LevelController(
                "4V", listOf(
                    "ROW (Right of Way) rules"
                )
            ), LevelController(
                "4W", listOf(
                    "Equipment"
                )
            ), LevelController(
                "4X", listOf(
                    "Weather and tides"
                )
            ), LevelController(
                "4Y", listOf(
                    "Aerodynamics"
                )
            )

        )
    }
}