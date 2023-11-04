package com.example.timecontrol.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timecontrol.viewModel.AddStudentViewModel
import com.example.timecontrol.viewModel.DatabaseViewModel

class AddStudentViewModelFactory(private val databaseViewModel: DatabaseViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddStudentViewModel(databaseViewModel = databaseViewModel) as T
    }
}