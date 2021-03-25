package com.gsc.silverwalk.data.finishmission

import com.google.firebase.Timestamp
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryItem

class FinishMissionRepository(private val dataSource: FinishMisionDataSource) {

    private lateinit var achievementHistoryItem: AchievementHistoryItem
    private lateinit var imagePaths: ArrayList<String>

    fun setAchievementHistoryItem(
        location: String,
        time: Timestamp,
        steps: Long,
        averagePace: Long,
        distance: Double,
        heartRate: Long,
        calories: Long,
        walkTime: Long,
        imagePaths: ArrayList<String>
    ) {
        achievementHistoryItem = AchievementHistoryItem(
            location, time, steps, averagePace, distance, heartRate, calories, walkTime
        )
        this.imagePaths = imagePaths
    }

    fun postMissionData(done: () -> Unit) {
        dataSource.postMissionData(achievementHistoryItem){
            done()
        }
    }

    fun getLocation(): String {
        return achievementHistoryItem.location.toString()
    }

    fun getImagePaths(): ArrayList<String> {
        return imagePaths
    }
}