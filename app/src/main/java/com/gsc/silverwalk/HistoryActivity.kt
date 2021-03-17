package com.gsc.silverwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    var steps = 0L
    var averagePace = 0L
    var calories = 0L
    var heartRate = 0L
    var distance = 0L
    var time = 0L
    var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.hide()

        // Find Data From FireBase
        // Get Intent Data
        steps =
            if (intent.hasExtra("steps"))
                (intent.getLongExtra("steps", 0)) else steps
        averagePace =
                if (intent.hasExtra("average_pace"))
                    (intent.getLongExtra("average_pace", 0)) else averagePace
        calories =
                if (intent.hasExtra("calories"))
                    (intent.getLongExtra("calories", 0)) else calories
        heartRate =
                if (intent.hasExtra("heart_rate"))
                    (intent.getLongExtra("heart_rate", 0)) else heartRate
        distance =
                if (intent.hasExtra("distance"))
                    (intent.getLongExtra("distance", 0)) else distance
        time =
                if (intent.hasExtra("time_second"))
                    (intent.getLongExtra("time_second", 0)) else time
        location =
            if (intent.hasExtra("location"))
                (intent.getStringExtra("location")!!) else location

        history_steps_text.setText(steps.toString())
        history_averagepace_text.setText(averagePace.toString())
        history_calories_text.setText(calories.toString())
        history_heartrate_text.setText(heartRate.toString())
        history_distance_text.setText(distance.toString())
        history_time_text.setText(String.format("%2d:%2d", time / 60, time % 60))

        history_title_1_text.setText(
                getString(R.string.history_activity_title_1)
                        .replace("(%location)", location))

        history_top_button.setOnClickListener(View.OnClickListener {
            history_root_scrollview.scrollTo(0,0)
        })

        loadLocationStatistics()
    }

    fun loadLocationStatistics() {
        Firebase.firestore
                .collection("location")
                .whereEqualTo("name",location)
                .get()
                .addOnSuccessListener {
                    if(!it.isEmpty){
                        it.documents[0].reference
                                .collection("done_users")
                                .get()
                                .addOnSuccessListener {
                                    var totalEasyCount = 0
                                    var totalModerateCount = 0
                                    var totalHardCount = 0
                                    var totalMenCount = 0
                                    var totalWomenCount = 0
                                    var total50Count = 0
                                    var total60Count = 0
                                    var total70Count = 0
                                    var count = 0

                                    for(result in it){
                                        val age = result.get("age") as Long
                                        val level = result.get("level") as Long
                                        val gender = result.get("gender").toString()

                                        if(age < 60L){ ++total50Count }
                                        else if(age < 70L){ ++total60Count }
                                        else{ ++total70Count }

                                        if(level == 0L){ ++totalEasyCount }
                                        else if(level == 1L){ ++totalModerateCount }
                                        else{ ++totalHardCount }

                                        if(gender == "men"){ ++totalMenCount }
                                        else{ ++totalWomenCount }

                                        ++count
                                    }

                                    history_level_easy_count_text.setText(
                                            totalEasyCount.toString())
                                    history_level_moderate_count_text.setText(
                                            totalModerateCount.toString())
                                    history_level_hard_count_text.setText(
                                            totalHardCount.toString())

                                    history_statistics_gender_women_button.setText(
                                            (totalWomenCount.toDouble() /
                                                    (totalMenCount + totalWomenCount).toDouble()
                                                    * 100.0).toInt()
                                                    .toString() + "%")
                                    history_statistics_gender_men_button.setText(
                                            (totalMenCount.toDouble() /
                                                    (totalMenCount + totalWomenCount).toDouble()
                                                    * 100.0).toInt()
                                                    .toString() + "%")

                                    var newDisplayString =
                                                    getString(R.string.history_activity_display_1) +
                                                    getString(R.string.history_activity_display_2) +
                                                    getString(R.string.history_activity_display_3) +
                                                    getString(R.string.history_activity_display_4) +
                                                    getString(R.string.history_activity_display_5)
                                    newDisplayString =
                                            newDisplayString
                                                    .replace("(%count)", count.toString())
                                    newDisplayString =
                                            newDisplayString
                                                    .replace("(%location)", location)
                                    val newStringArray =
                                            newDisplayString.split("(%tab)")
                                    history_display_1_text.setText(newStringArray[0])
                                    history_display_2_text.setText(newStringArray[1])
                                    history_display_3_text.setText(newStringArray[2])
                                    history_display_4_text.setText(newStringArray[3])
                                    history_display_5_text.setText(newStringArray[4])
                                }
                                .addOnFailureListener {

                                }
                    }
                }
                .addOnFailureListener {

                }
    }
}