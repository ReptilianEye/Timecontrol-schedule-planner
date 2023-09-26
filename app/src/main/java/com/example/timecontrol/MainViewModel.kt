package com.example.timecontrol

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var isCurrentlyDragging by mutableStateOf(false)
        private set
    var items by mutableStateOf(emptyList<PersonUiItem>())
        private set
    var addedPersons = mutableStateListOf<PersonUiItem>()
        private set

    init {
        items = listOf(
            PersonUiItem("Michael", "1", Color.Gray),
            PersonUiItem("Larissa", "2", Color.Cyan),
            PersonUiItem("Tomas", "3", Color.Magenta)
        )
    }

    fun startDragging() {
        isCurrentlyDragging = true
    }

    fun stopDragging() {
        isCurrentlyDragging = false
    }

    fun addPerson(personUiItem: PersonUiItem) {
        addedPersons.add(personUiItem)
    }
}