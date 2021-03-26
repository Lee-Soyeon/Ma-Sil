package com.gsc.silverwalk.data.domission

import android.content.Intent
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.gsc.silverwalk.ui.domission.DoMissionForm
import com.gsc.silverwalk.ui.domission.MissionData
import com.gsc.silverwalk.data.Result

class DoMissionRepository(private val dataSource: DoMissionDataSource) {

    private lateinit var missionData : MissionData

    private val cameraPathList: ArrayList<String> = arrayListOf()

    private var currentTime = 0L

    val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addDataType(DataType.AGGREGATE_MOVE_MINUTES)
            .build()
    }

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

    fun getFitnessOption(): FitnessOptions{
        return fitnessOptions
    }

    fun addCameraPath(path: String) {
        cameraPathList.add(path)
    }

    fun requestGoogleFitApi(result: (Result<DoMissionForm>) -> Unit){
        dataSource.requestGoogleFitApi(result)
    }
}