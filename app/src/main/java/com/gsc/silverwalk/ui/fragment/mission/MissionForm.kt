package com.gsc.silverwalk.ui.fragment.mission

import android.content.Context
import com.gsc.silverwalk.R

data class MissionForm (
    val time : Long? = null,
    val location : String? = null,
    val type : String? = null,
    val level : Long? = null,
    val currentIndex : Int? = null,
    val isLoaded : Boolean = false
){
    fun getString(context: Context): String {
        var mission_string =
            context.getString(R.string.mission_display_1);
        mission_string = mission_string.replace("(%location)", location!!)
        mission_string = mission_string.replace("(%time)", (time!! / 60).toString())
        mission_string = mission_string.replace("(%type)", type!!)
        return mission_string
    }
}