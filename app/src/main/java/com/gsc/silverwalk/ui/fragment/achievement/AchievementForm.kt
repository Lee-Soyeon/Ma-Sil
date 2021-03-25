package com.gsc.silverwalk.ui.fragment.achievement

import kotlin.math.roundToInt

data class AchievementForm(
    val totalSteps: Long? = null,
    val totalAveragePace: Long? = null,
    val totalBurnCalories: Long? = null,
    val totalHeartrate: Long? = null,
    val totalDistance: Double? = null,
    val totalTime: Long? = null
){
    fun totalTimeStringFormat() : String {
        return String.format(
            "%2d:%2d", totalTime!! / 60, totalTime!! % 60)
    }

    fun totalDistanceStringFormat() : String {
        return ((totalDistance!! * 10).roundToInt() / 10.0).toString()
    }
}