package com.gsc.silverwalk.data.domission

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.Timestamp
import com.gsc.silverwalk.ui.domission.DoMissionForm
import com.gsc.silverwalk.ui.domission.MissionData
import com.gsc.silverwalk.data.Result
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DoMissionRepository(private val dataSource: DoMissionDataSource) {

    private lateinit var missionData : MissionData

    private val cameraPathList: ArrayList<String> = arrayListOf()

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addDataType(DataType.AGGREGATE_MOVE_MINUTES)
            .build()
    }

    val startTime: Long = (Timestamp.now().seconds)

    fun setMissionData(
        missionTime: Long,
        missionLocation: String,
        missionType: String,
        missionLevel: Long
    ){
        missionData = MissionData(missionTime, missionLocation, missionType, missionLevel)
    }

    fun setIntentParameter(intent: Intent) : Intent{
        intent.putExtra("location", missionData.missionLocation)
        intent.putExtra("imagePath", cameraPathList)
        return intent
    }

    fun getProgressTime(time: Long): Int {
        return (time.toFloat() / missionData.missionTime!!.toFloat() * 100.0f).toInt()
    }

    fun addCameraPath(path: String) {
        cameraPathList.add(path)
    }

    fun requestGoogleFitApi(context: Context, result: (Result<DoMissionForm>) -> Unit){
        dataSource.requestGoogleFitApi(context, fitnessOptions, startTime, result)
    }
}