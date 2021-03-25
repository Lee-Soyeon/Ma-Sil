package com.gsc.silverwalk.ui.finishmission

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.gsc.silverwalk.data.finishmission.FinishMissionRepository
import com.gsc.silverwalk.ui.share.ShareActivity
import java.util.*

class FinishMissionViewModel(private val finishMissionRepository: FinishMissionRepository)
    : ViewModel() {

    private val _finishMissionForm = MutableLiveData<FinishMissionForm>()
    val finishMissionForm: LiveData<FinishMissionForm> = _finishMissionForm

    fun getIntent(intent: Intent){
        finishMissionRepository.setAchievementHistoryItem(
            intent.getStringExtra("location").toString(),
            Timestamp(Date()),
            intent.getLongExtra("steps",0),
            intent.getLongExtra("averagePace",0),
            intent.getDoubleExtra("distance",0.0),
            intent.getLongExtra("heartRate",0),
            intent.getLongExtra("calories",0),
            intent.getLongExtra("time",0),
            intent.getSerializableExtra("imagePath") as ArrayList<String>
        )
    }

    fun selectMission(index: Int){
        _finishMissionForm.value = FinishMissionForm(index)
    }

    fun postMissionData(done: () -> Unit){
        finishMissionRepository.postMissionData{
            done()
        }
    }

    fun shareMission(context: Context) : Intent{
        val i = Intent(context, ShareActivity::class.java)
        i.putExtra("imagePath",finishMissionRepository.getImagePaths())
        i.putExtra("location",finishMissionRepository.getLocation())
        return i
    }
}