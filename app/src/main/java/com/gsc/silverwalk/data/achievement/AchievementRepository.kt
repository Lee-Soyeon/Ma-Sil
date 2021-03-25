package com.gsc.silverwalk.data.achievement

import com.google.firebase.Timestamp
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.fragment.achievement.AchievementForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryForm

class AchievementRepository(val dataSource: AchievementDataSource) {

    fun statisticsByTimestamp(timestamp: Timestamp, result: (Result<AchievementForm>) -> Unit){
        dataSource.statisticsByTimestamp(timestamp, result)
    }

    fun findAllHistory(result: (Result<AchievementHistoryForm>) -> Unit){
        dataSource.findAllHistory(result)
    }
}