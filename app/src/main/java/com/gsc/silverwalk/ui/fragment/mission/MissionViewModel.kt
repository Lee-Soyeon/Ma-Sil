package com.gsc.silverwalk.ui.fragment.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.data.mission.MissionRepository

class MissionViewModel(private val missionRepository: MissionRepository) : ViewModel() {

    private val _missionForm = MutableLiveData<MissionForm>()
    val missionForm: LiveData<MissionForm> = _missionForm

    private val _missionImage = MutableLiveData<MissionImage>()
    val missionImage: LiveData<MissionImage> = _missionImage

    private val _missionWeather = MutableLiveData<MissionWeather>()
    val missionWeather: LiveData<MissionWeather> = _missionWeather

    init {
        getWeatherInfo()

        missionRepository.initializeMission {
            if (it is Result.Success) {
                _missionForm.value = it.data
                getMissionLocationInfo()
            }
        }
    }

    fun prevMission() {
        val result = missionRepository.prevMission()
        if (result is Result.Success) {
            _missionForm.value = result.data
            getMissionLocationInfo()
        }
    }

    fun nextMission() {
        val result = missionRepository.nextMission()
        if (result is Result.Success) {
            _missionForm.value = result.data
            getMissionLocationInfo()
        }
    }

    fun getWeatherInfo() {
        missionRepository.getWeatherInfo {
            if (it is Result.Success) {
                _missionWeather.value = it.data
            }
        }
    }

    private fun getMissionLocationInfo(){
        missionRepository.getMissionLocationInfo(
            _missionForm.value!!.location.toString()
        ) {
            if (it is Result.Success) {
                _missionImage.value = it.data
            }
        }
    }
}