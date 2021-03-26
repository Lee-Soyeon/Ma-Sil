package com.gsc.silverwalk.data.domission

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.Timestamp
import com.google.type.Date
import com.gsc.silverwalk.ui.domission.DoMissionForm
import com.gsc.silverwalk.data.Result
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class DoMissionDataSource {

    fun requestGoogleFitApi(
        context: Context,
        fitnessOptions: FitnessOptions,
        time: Long,
        result: (Result<DoMissionForm>) -> Unit
    ) {
        val endTime = Timestamp.now()
        val startTime = time

        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_SPEED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime.seconds, TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(
            context,
            GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        )
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // The aggregate query puts datasets into buckets, so flatten into a single list of datasets
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