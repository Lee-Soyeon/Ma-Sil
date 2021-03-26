package com.gsc.silverwalk.ui.fragment.mission

import android.content.Context
import com.gsc.silverwalk.R

data class MissionWeather(
    val temperature: String? = null,
    val weather_text: String? = null
) {
    fun getTemperatureComment(context: Context): String {
        val temp = temperature!!.replace("ºF","").toInt()
        if (temp < 20) {
            return context.getString(R.string.weather_1)
        } else if (temp < 30) {
            return context.getString(R.string.weather_2)
        } else if (temp < 40) {
            return context.getString(R.string.weather_3)
        } else if (temp < 50) {
            return context.getString(R.string.weather_4)
        } else if (temp < 60) {
            return context.getString(R.string.weather_5)
        } else if (temp < 70) {
            return context.getString(R.string.weather_6)
        } else if (temp < 80) {
            return context.getString(R.string.weather_7)
        }
        return context.getString(R.string.weather_8)
    }

    fun getWeatherIconId(): Int{
        val data = hashMapOf(
            "Thunderstorm" to R.drawable.ic_thunderstorm,
            "Drizzle" to R.drawable.ic_drizzle,
            "Rain" to R.drawable.ic_rain,
            "Snow" to R.drawable.ic_snow,
            "Clear" to R.drawable.ic_clear,
            "Clouds" to R.drawable.ic_cloud
        )

        val iconId = data.get(weather_text)
        return if (iconId != null) iconId else R.drawable.ic_mist
    }
}