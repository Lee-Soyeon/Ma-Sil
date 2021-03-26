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
import com.gsc.silverwalk.ui.domission.DoMissionForm
import com.gsc.silverwalk.data.Result
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class DoMissionDataSource {

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestGoogleFitApi(
        context: Context,
        fitnessOptions: FitnessOptions,
        time: ZonedDateTime,
        result: (Result<DoMissionForm>) -> Unit
    ) {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = time

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