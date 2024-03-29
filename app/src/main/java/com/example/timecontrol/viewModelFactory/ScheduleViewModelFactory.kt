package com.example.timecontrol.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timecontrol.viewModels.DatabaseViewModel
import com.example.timecontrol.viewModels.ScheduleViewModel

class ScheduleViewModelFactory(private val databaseViewModel: DatabaseViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScheduleViewModel(databaseViewModel = databaseViewModel) as T
    }
}