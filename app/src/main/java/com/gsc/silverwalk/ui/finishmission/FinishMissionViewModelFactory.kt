package com.gsc.silverwalk.ui.finishmission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.finishmission.FinishMisionDataSource
import com.gsc.silverwalk.data.finishmission.FinishMissionRepository
import java.lang.IllegalArgumentException

class FinishMissionViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinishMissionViewModel::class.java)) {
            return FinishMissionViewModel(
                finishMissionRepository = FinishMissionRepository(
                    dataSource = FinishMisionDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}