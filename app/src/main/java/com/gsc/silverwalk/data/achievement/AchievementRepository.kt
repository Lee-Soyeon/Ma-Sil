package com.gsc.silverwalk.data.achievement

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.Timestamp
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.fragment.achievement.AchievementForm
import com.gsc.silverwalk.ui.fragment.achievement.AchievementHistoryForm

class AchievementRepository(val dataSource: AchievementDataSource) {

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addDataType(DataType.AGGREGATE_MOVE_MINUTES)
            .build()
    }

    fun statisticsByTimestamp(
        time: Long,
        achievementHistoryForm: AchievementHistoryForm,
        result: (Result<AchievementForm>) -> Unit
    ) {
        dataSource.statisticsByTimestamp(time, achievementHistoryForm, result)
    }

    fun findAllHistory(context: Context, result: (Result<AchievementHistoryForm>) -> Unit) {
        dataSource.findAllHistory(context, fitnessOptions, result)
    }
}