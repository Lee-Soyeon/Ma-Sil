package com.gsc.silverwalk.data.finishmission

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryItem
import com.gsc.silverwalk.userinfo.UserInfo

class FinishMisionDataSource {

    fun postMissionData(historyItem: AchievementHistoryItem, done: () -> Unit) {
        // History Data
        val data = hashMapOf(
            "average_pace" to historyItem.averagePace,
            "calories" to historyItem.calories,
            "distance" to historyItem.distance,
            "heart_rate" to historyItem.heartRate,
            "location" to historyItem.location,
            "steps" to historyItem.steps,
            "time" to historyItem.time,
            "time_second" to historyItem.walkTime
        )

        // Firebase History Collection Add
        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .add(data)
            .addOnSuccessListener {
                done()
            }
            .addOnFailureListener {
                done()
            }
    }
}