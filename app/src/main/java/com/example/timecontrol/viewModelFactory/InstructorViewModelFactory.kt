package com.example.timecontrol.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timecontrol.viewModels.DatabaseViewModel
import com.example.timecontrol.viewModels.InstructorViewModel

class InstructorViewModelFactory(private val databaseViewModel: DatabaseViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InstructorViewModel(databaseViewModel = databaseViewModel) as T
    }
}