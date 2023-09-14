package com.example.timecontrol.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InstructorViewModelFactory(private val databaseViewModel: DatabaseViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InstructorViewModel(databaseViewModel = databaseViewModel) as T
    }
}