package com.example.timecontrol.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddStudentViewModelFactory(private val databaseViewModel: DatabaseViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddStudentViewModel(databaseViewModel = databaseViewModel) as T
    }
}