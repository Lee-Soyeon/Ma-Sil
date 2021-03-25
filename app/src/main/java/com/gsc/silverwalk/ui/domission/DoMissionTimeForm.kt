package com.gsc.silverwalk.ui.domission

data class DoMissionTimeForm(
    val time: Long? = null
){
    fun getTimeStringFormat(): String{
        return String.format("%02d'  %02d''", time!! / 60, time!! % 60)
    }

    fun copyAfterTick(): DoMissionTimeForm{
        return DoMissionTimeForm(time!! + 1)
    }

    fun isRequestTime(): Boolean{
        return (time!! % 60L == 0L)
    }
}