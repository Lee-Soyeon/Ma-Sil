package com.gsc.silverwalk.ui.fragment.achievement

import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.cardview_history.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class AchievementHistoryItem(
    val location: String? = null,
    val time: Timestamp? = null,
    val steps: Long? = null,
    val averagePace: Long? = null,
    val distance: Double? = null,
    val heartRate: Long? = null,
    val calories: Long? = null,
    val walkTime: Long? = null
){
    fun timeToStringFormatYYMMDD() : String{
        return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(time!!.toDate())
    }

    fun timeToStringFormatHHMMAA() : String{
        return SimpleDateFormat("h:mm a", Locale.US).format(time!!.toDate())
    }

    fun walkTimeToStringFormatHHMM() : String{
        return String.format("%2d:%2d", walkTime!! / 60L, walkTime!! % 60L)
    }

    fun getDistanceString() : String{
        return ((distance!! * 10).roundToInt() / 10.0).toString()
    }
}