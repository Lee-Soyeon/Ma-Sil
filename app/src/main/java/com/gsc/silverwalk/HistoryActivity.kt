package com.gsc.silverwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.hide()

        // Find Data From FireBase

        /*
        // Location Display Text Set
        history_display_2_text.setText(
            getString(R.string.history_activity_display_2).replace("%s",""))

        // Route Map Set
        history_routemap_image.setImageURI(0)

        // Statistics Text Set
        history_steps_text.setText("" + getString(R.string.StepsCount))
        history_averagepace_text.setText("" + getString(R.string.Mph))
        history_calories_text.setText("" + getString(R.string.Kcal))
        history_heartrate_text.setText("" + getString(R.string.BPM))
        history_distance_text.setText("" + getString(R.string.Mile))
        history_time_text.setText(String.format("%2d:%2d",0,0))

        // Statistics Mission Text Set
        history_display_3_text.setText(
            getString(R.string.history_activity_display_3).replace("%d",""))
        history_display_6_text.setText(
            getString(R.string.history_activity_display_6).replace("%s",""))

        history_level_easy_count_text.setText("")
        history_level_moderate_count_text.setText("")
        history_level_hard_count_text.setText("")

        history_statistics_gender_men_text.setText("")
        history_statistics_gender_women_text.setText("")

        val entries = listOf<Entry>()
        entries.add(Entry(data.getValueX(),data.getValueY()))

        val dataSet = LineDataSet(entries,"Label")
        dataSet.setColor(...)
        dataSet.setValueTextColors(...)

        history_statistics_chart.data = LineData(dataSet)
        history_statistics_chart.invalidate()
         */
    }
}