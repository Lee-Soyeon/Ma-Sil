package com.gsc.silverwalk.ui.achievement

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.gsc.silverwalk.HistoryActivity
import com.gsc.silverwalk.R
import com.gsc.silverwalk.userinfo.UserInfo
import kotlinx.android.synthetic.main.cardview_history.view.*
import kotlinx.android.synthetic.main.fragment_achievement.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

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
        val date = Date()
        date.hours = 0
        date.minutes = 0
        date.seconds = 0
        val timestamp = Timestamp(date)

        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .whereGreaterThan("time", timestamp)
            .get()
            .addOnSuccessListener {
                var totalSteps = 0L
                var totalAveragePace = 0L
                var totalBurnCalories = 0L
                var totalHeartRate = 0L
                var totalDistance = 0.0
                var totalTime = 0L
                var count = 0L

                for(result in it){
                    totalSteps += result.data.get("steps") as Long
                    totalAveragePace += result.data.get("average_pace") as Long
                    totalBurnCalories += result.data.get("calories") as Long
                    totalHeartRate += result.data.get("heart_rate") as Long
                    totalDistance += result.data.get("distance") as Double
                    totalTime += result.data.get("time_second") as Long
                    ++count
                }

                achievement_today_steps_text.setText(totalSteps.toString())
                achievement_today_averagepace_text.setText((totalAveragePace / count).toString())
                achievement_today_calories_text.setText(totalBurnCalories.toString())
                achievement_today_heartrate_text.setText((totalHeartRate / count).toString())
                achievement_today_distance_text.setText(
                    ((totalDistance * 10).roundToInt() / 10.0).toString())
                achievement_today_time_text.setText(
                    String.format("%2d:%2d", totalTime / 60,totalTime % 60))
            }
            .addOnFailureListener {
                Log.d("TAG","Fail FireStore")
            }

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
        Firebase.firestore
            .collection("users")
            .document(UserInfo.getInstance().uid)
            .collection("history")
            .get()
            .addOnSuccessListener {
                for(result in it){
                    val historyLayout = layoutInflater.inflate(
                        R.layout.cardview_history,
                        achievement_history_linearlayout, false)

                    historyLayout.setOnClickListener {
                        val historyActivityIntent = Intent(context, HistoryActivity::class.java)
                        startActivity(historyActivityIntent)
                    }
                    historyLayout.history_card_view_text_1.setText(
                        result.data.get("location").toString())

                    achievement_history_linearlayout.addView(historyLayout)
                }
            }
            .addOnFailureListener {

            }
    }
}