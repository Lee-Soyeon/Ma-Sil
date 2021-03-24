package com.gsc.silverwalk.ui.fragment.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.mission.MissionDataSource
import com.gsc.silverwalk.data.mission.MissionRepository
import java.lang.IllegalArgumentException

class MissionViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionViewModel::class.java)) {
            return MissionViewModel(
                missionRepository = MissionRepository(
                    dataSource = MissionDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}