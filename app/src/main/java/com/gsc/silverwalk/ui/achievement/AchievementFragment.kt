package com.gsc.silverwalk.ui.achievement

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.gsc.silverwalk.HistoryActivity
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_achievement.*

class AchievementFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_achievement, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        achievement_tablayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Change Tab
                val boolList : MutableList<Int> = mutableListOf(4, 4, 4, 4)
                boolList[tab?.position!!] = View.VISIBLE
                achievement_today_linearlayout.visibility = boolList[0]
                history_statistics_chart.visibility = boolList[1]
                achievement_monthly_chart.visibility = boolList[2]
                achievement_yearly_chart.visibility = boolList[3]
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        addHistory()
        setAchievementData()
    }

    fun setAchievementData() {

        // First, Find Data From Firebase

        // Today Achievement
        /*
        achievement_steps_text.setText("" + getString(R.string.StepsCount))
        achievement_averagepace_text.setText("" + getString(R.string.Mph))
        achievement_calories_text.setText("" + getString(R.string.Kcal))
        achievement_heartrate_text.setText("" + getString(R.string.BPM))
        achievement_distance_text.setText("" + getString(R.string.Mile))
        achievement_time_text.setText("")
         */

        // Weekly, Monthly, Yearly Chart
        /*
        val entries = listOf<Entry>()
        entries.add(Entry(data.getValueX(),data.getValueY()))

        val dataSet = LineDataSet(entries,"Label")
        dataSet.setColor(...)
        dataSet.setValueTextColors(...)

        achievement_weekly_chart.data = LineData(dataSet)
        achievement_weekly_chart.invalidate()
         */
    }

    fun addHistory() {

        // First, Find Data From FireBase

        // while(iterator.hasNext())
        val HistoryLayout = layoutInflater.inflate(
            R.layout.cardview_history, achievement_history_linearlayout, false)

        HistoryLayout.setOnClickListener {
            val historyActivityIntent = Intent(context, HistoryActivity::class.java)
            startActivity(historyActivityIntent)
        }

        // Set History Card Parameter
//        HistoryLayout.findViewById<ImageView>(R.id.history_cardview_image).setImageURI(0)
//        HistoryLayout.findViewById<TextView>(R.id.history_card_view_text_1).setText("")
//        HistoryLayout.findViewById<TextView>(R.id.history_card_view_text_2).setText("")
//        HistoryLayout.findViewById<TextView>(R.id.history_card_view_text_3).setText("")

        achievement_history_linearlayout.addView(HistoryLayout)
    }
}