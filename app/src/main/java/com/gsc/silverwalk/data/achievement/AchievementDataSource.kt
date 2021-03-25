package com.gsc.silverwalk.data.achievement

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.fragment.achievement.AchievementForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryItem
import com.gsc.silverwalk.userinfo.UserInfo

class AchievementDataSource {

    fun statisticsByTimestamp(timestamp: Timestamp, result: (Result<AchievementForm>) -> Unit) {
        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .whereGreaterThan("time", timestamp)
            .get()
            .addOnSuccessListener {
                var totalSteps = 0L
                var totalAveragePace = 0L
                var totalBurnCalories = 0L
                var totalHeartRate = 0L
                var totalDistance = 0.0
                var totalTime = 0L
                var count = 0L

                for (document in it) {
                    totalSteps += document.data.get("steps") as Long
                    totalAveragePace += document.data.get("average_pace") as Long
                    totalBurnCalories += document.data.get("calories") as Long
                    totalHeartRate += document.data.get("heart_rate") as Long
                    totalDistance += document.data.get("distance") as Double
                    totalTime += document.data.get("time_second") as Long
                    ++count
                }

                count = if (count == 0L) 1 else count
                result(
                    Result.Success(
                        AchievementForm(
                            totalSteps,
                            totalAveragePace / count,
                            totalBurnCalories,
                            totalHeartRate / count,
                            totalDistance,
                            totalTime
                        )
                    )
                )
            }
            .addOnFailureListener {
                result(Result.Error(it))
            }
    }

    fun findAllHistory(result: (Result<AchievementHistoryForm>) -> Unit) {
        val histories: MutableList<AchievementHistoryItem> = mutableListOf()

        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    histories.add(
                        AchievementHistoryItem(
                            document.data.get("location").toString(),
                            document.data.get("time") as Timestamp,
                            document.data.get("steps") as Long,
                            document.data.get("average_pace") as Long,
                            document.data.get("distance") as Double,
                            document.data.get("heart_rate") as Long,
                            document.data.get("calories") as Long
                        )
                    )
                }

                result(Result.Success(AchievementHistoryForm(histories)))
            }
            .addOnFailureListener {
                result(Result.Error(it))
            }
    }
}