package com.gsc.silverwalk.data.mission

import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.fragment.mission.MissionForm
import com.gsc.silverwalk.ui.fragment.mission.MissionImage
import com.gsc.silverwalk.ui.fragment.mission.MissionWeather

class MissionRepository(val dataSource: MissionDataSource) {

    private val missionDataList = mutableListOf<Map<String,Any>>()
    private var currentMissionIndex: Int

    init {
        currentMissionIndex = 0
    }

    fun initializeMission(result: (Result<MissionForm>) -> Unit){
        missionDataList.clear()
        dataSource.initMissionDataList {
            if(it is Result.Success){
                for(document in it.data){
                    missionDataList.add(document.data)
                }
            }

            result(selectMissionByIndex(currentMissionIndex))
        }
    }

    fun getWeatherInfo(result: (Result<MissionWeather>) -> Unit) {
        dataSource.getWeatherInfo { result(it) }
    }

    fun nextMission(): Result<MissionForm> {
        currentMissionIndex =
            if (currentMissionIndex + 1 >= missionDataList.size) 0 else currentMissionIndex + 1

        return selectMissionByIndex(currentMissionIndex)
    }

    fun prevMission(): Result<MissionForm> {
        currentMissionIndex =
            if (currentMissionIndex - 1 < 0) missionDataList.size - 1 else currentMissionIndex - 1

        return selectMissionByIndex(currentMissionIndex)
    }

    fun getMissionLocationInfo(location: String, result: (Result<MissionImage>) -> Unit){
        dataSource.getMissionLocationInfo(location,{result(it)})
    }

    private fun selectMissionByIndex(index: Int): Result<MissionForm> {
        if(index >= 0 && index < missionDataList.size) {
            val missionData = missionDataList[index]
            return Result.Success<MissionForm>(MissionForm(
                missionData.get("time") as Long,
                missionData.get("location").toString(),
                missionData.get("type").toString(),
                missionData.get("level") as Long,
                index,
                true))
        }

        return Result.Error(ArrayIndexOutOfBoundsException())
    }
}