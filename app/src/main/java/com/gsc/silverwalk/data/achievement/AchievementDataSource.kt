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
import java.util.concurrent.TimeUnit

class AchievementDataSource {

    fun statisticsByTimestamp(
        timestamp: Timestamp,
        achievementHistoryForm: AchievementHistoryForm,
        result: (Result<AchievementForm>) -> Unit
    ) {
        val iter = achievementHistoryForm.histories!!.iterator()
        while(iter.hasNext()){
            val item = iter.next()

            var totalSteps = 0L
            var totalAveragePace = 0L
            var totalBurnCalories = 0L
            var totalHeartRate = 0L
            var totalDistance = 0.0
            var totalTime = 0L
            var count = 0L

            if(item.time!! > timestamp){
                totalSteps += item.steps!!
                totalAveragePace += item.averagePace!!
                totalBurnCalories += item.calories!!
                totalHeartRate += item.heartRate!!
                totalDistance += item.distance!!
                totalTime += item.walkTime!!
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    val stamp = (document.data.get("time") as Timestamp).toDate()
                    val walkTime = document.data.get("time_second") as Long
                    val endTime = ZonedDateTime.of(
                        stamp.year,
                        stamp.month,
                        stamp.day,
                        stamp.hours,
                        stamp.minutes,
                        stamp.seconds,
                        0,
                        ZoneId.systemDefault()
                    )
                    requestGoogleFitApi(
                        context, fitnessOptions,
                        endTime.minusSeconds(walkTime), endTime
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestGoogleFitApi(
        context: Context,
        fitnessOptions: FitnessOptions,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        result: (Result<DoMissionForm>) -> Unit
    ) {
        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_SPEED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(
            context,
            GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        )
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // The aggregate query puts datasets into buckets, so flatten into a single list of datasets
                for (dataSet in response.buckets.flatMap { it.dataSets }) {

                }

                result(
                    Result.Success(
                        DoMissionForm(0, 0.0, 0.0, 0, 0)
                    )
                )
            }
            .addOnFailureListener { e ->
                result(Result.Error(e))
            }
    }
}