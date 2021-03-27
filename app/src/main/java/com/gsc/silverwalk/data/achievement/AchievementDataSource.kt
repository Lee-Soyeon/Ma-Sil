package com.gsc.silverwalk.data.achievement

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.domission.DoMissionForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryItem
import com.gsc.silverwalk.userinfo.UserInfo
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class AchievementDataSource {

    fun statisticsByTimestamp(
        time: Long,
        achievementHistoryForm: AchievementHistoryForm,
        result: (Result<AchievementForm>) -> Unit
    ) {
        var totalSteps = 0L
        var totalAveragePace = 0L
        var totalBurnCalories = 0L
        var totalHeartRate = 0L
        var totalDistance = 0.0
        var totalTime = 0L
        var count = 0L

        val iter = achievementHistoryForm.histories!!.iterator()
        while (iter.hasNext()) {
            val item = iter.next()

            if (item.time!!.toDate().time > time) {
                totalSteps += item.steps!!
                totalAveragePace += item.averagePace!!
                totalBurnCalories += item.calories!!
                totalHeartRate += item.heartRate!!
                totalDistance += item.distance!!
                totalTime += item.walkTime!!
            }
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

    fun findAllHistory(
        context: Context,
        fitnessOptions: FitnessOptions,
        result: (Result<AchievementHistoryForm>) -> Unit
    ) {
        val histories: MutableList<AchievementHistoryItem> = mutableListOf()

        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val stamp = (document.data.get("time") as Timestamp)
                    val walkTime = document.data.get("time_second") as Long
                    requestGoogleFitApi(
                        context, fitnessOptions,
                        stamp.seconds.minus(walkTime), stamp.seconds
                    )
                    {
                        if (it is Result.Success) {
                            histories.add(
                                AchievementHistoryItem(
                                    document.data.get("location").toString(),
                                    document.data.get("time") as Timestamp,
                                    it.data.steps,
                                    it.data.averagePace!!.toLong(),
                                    it.data.distance,
                                    0,
                                    it.data.calories,
                                    walkTime
                                )
                            )

                            if (histories.size == querySnapshot.documents.size) {
                                result(Result.Success(AchievementHistoryForm(histories)))
                            }
                        } else {
                            result(Result.Error(Exception()))
                        }
                    }
                }
            }
            .addOnFailureListener {
                result(Result.Error(it))
            }
    }

    private fun requestGoogleFitApi(
        context: Context,
        fitnessOptions: FitnessOptions,
        startTime: Long,
        endTime: Long,
        result: (Result<DoMissionForm>) -> Unit
    ) {
        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_SPEED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(
            context,
            GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        )
            .readData(readRequest)
            .addOnSuccessListener { response ->
                var distance = 0.0
                var calories = 0
                var typeSpeed = 0.0
                var stepCount = 0

                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    for (dp in dataSet.dataPoints) {
                        for (field in dp.dataType.fields) {
                            if (dp.dataType.name.toString() == "com.google.distance.delta") {
                                distance = dp.getValue(field).toString().toDouble().toInt() / 1000.0
                            } else if (dp.dataType.name.toString() == "com.google.calories.expended") {
                                calories = dp.getValue(field).toString().toDouble().toInt()
                            } else if (dp.dataType.name.toString() == "com.google.speed.summary") {
                                typeSpeed = (dp.getValue(field).toString()
                                    .toDouble() * 100).roundToInt() / 100.0
                            } else if (dp.dataType.name.toString() == "com.google.step_count.delta") {
                                stepCount = dp.getValue(field).toString().toInt()
                            }
                        }
                    }
                }

                result(
                    Result.Success(
                        DoMissionForm(
                            calories.toLong(),
                            distance,
                            typeSpeed,
                            0,
                            stepCount.toLong()
                        )
                    )
                )
            }
            .addOnFailureListener { e ->
                result(Result.Error(e))
            }
    }
}