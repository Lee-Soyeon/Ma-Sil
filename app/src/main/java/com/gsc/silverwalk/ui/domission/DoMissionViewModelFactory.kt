package com.gsc.silverwalk.ui.domission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.domission.DoMissionDataSource
import com.gsc.silverwalk.data.domission.DoMissionRepository

class DoMissionViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoMissionViewModel::class.java)) {
            return DoMissionViewModel(
                doMissionRepository = DoMissionRepository(
                    dataSource = DoMissionDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}