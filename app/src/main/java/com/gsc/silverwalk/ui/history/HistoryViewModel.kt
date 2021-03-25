package com.gsc.silverwalk.ui.history

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.data.history.HistoryRepository
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryItem

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    private val _achievementHistoryItem = MutableLiveData<AchievementHistoryItem>()
    val achievementHistoryItem: LiveData<AchievementHistoryItem> = _achievementHistoryItem

    private val _historyForm = MutableLiveData<HistoryForm>()
    val historyForm: LiveData<HistoryForm> = _historyForm

    fun getIntent(intent: Intent){
        _achievementHistoryItem.value = AchievementHistoryItem(
            intent.getStringExtra("location"),
            intent.getParcelableExtra("time"),
            intent.getLongExtra("steps", 0),
            intent.getLongExtra("averagePace", 0),
            intent.getDoubleExtra("distance",0.0),
            intent.getLongExtra("heartRate", 0),
            intent.getLongExtra("calories", 0),
            intent.getLongExtra("walkTime", 0)
        )
    }

    fun getLocationStatistics(location: String){
        historyRepository.getLocationStatistics(location) {
            if(it is Result.Success){
                _historyForm.value = it.data
            }
        }
    }
}