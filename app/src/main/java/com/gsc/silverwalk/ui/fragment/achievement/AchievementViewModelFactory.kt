package com.gsc.silverwalk.ui.fragment.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.achievement.AchievementDataSource
import com.gsc.silverwalk.data.achievement.AchievementRepository
import java.lang.IllegalArgumentException

class AchievementViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementViewModel::class.java)) {
            return AchievementViewModel(
                achievementRepository = AchievementRepository(
                    dataSource = AchievementDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}