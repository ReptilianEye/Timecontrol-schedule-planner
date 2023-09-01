package com.example.timecontrol.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timecontrol.database.AppRepository

class DatabaseViewModelFactory (private val repository: AppRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>):T {
        if(modelClass.isAssignableFrom(ViewModel::class.java)) {
            return ViewModel(repository) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel Class")
    }
}