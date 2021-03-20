package com.gsc.silverwalk

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_finish_mission.*

class FinishMissionActivity : AppCompatActivity() {

    var averagePace = 0L
    var calories = 0L
    var distance = 0.0
    var heartRate = 0L
    var location = ""
    var steps = 0L
    var time = 0L
    var level = 0L
    lateinit var imagePath : ArrayList<String>

    private val dialogObject : WaitDialog = WaitDialog()

    // UID
    private val UID = "X5NztOqVUgGPT84WmSiK"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_mission)

        // Get Intent Data
        averagePace =
                if (intent.hasExtra("averagePace"))
                    (intent.getLongExtra("averagePace",0)) else averagePace
        calories =
                if (intent.hasExtra("calories"))
                    (intent.getLongExtra("calories",0)) else calories
        distance =
                if (intent.hasExtra("distance"))
                    (intent.getDoubleExtra("distance",0.0)) else distance
        heartRate =
                if (intent.hasExtra("heartRate"))
                    (intent.getLongExtra("heartRate",0)) else heartRate
        location =
                if (intent.hasExtra("location"))
                    (intent.getStringExtra("location")!!) else location
        steps =
                if (intent.hasExtra("steps"))
                    (intent.getLongExtra("steps",0)) else steps
        time =
                if (intent.hasExtra("time"))
                    (intent.getLongExtra("time",0)) else time
        imagePath =
                if(intent.hasExtra("imagePath"))
                    (intent.getSerializableExtra("imagePath") as ArrayList<String>)
                else arrayListOf<String>()

        supportActionBar?.hide()

        activity_finish_mission_easy_cardview.setOnClickListener(View.OnClickListener {
            selectLevel(0)
        })

        activity_finish_mission_moderate_cardview.setOnClickListener(View.OnClickListener {
            selectLevel(1)
        })

        activity_finish_mission_hard_cardview.setOnClickListener(View.OnClickListener {
            selectLevel(2)
        })

        activity_finish_share_button.setOnClickListener(View.OnClickListener {
            shareMission()
        })

        activity_finish_end_button.setOnClickListener(View.OnClickListener {
            endMission()
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun selectLevel(level: Long){
        this.level = level

        if(this.level == 0L){
            activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.google_key_color))
            activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_easy_text.setTextColor(
                    getColor(R.color.white))
            activity_finish_mission_moderate_text.setTextColor(
                    getColor(R.color.black))
            activity_finish_mission_hard_text.setTextColor(
                    getColor(R.color.black))
        }else if(this.level == 1L){
            activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.google_key_color))
            activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_easy_text.setTextColor(getColor(R.color.black))
            activity_finish_mission_moderate_text.setTextColor(getColor(R.color.white))
            activity_finish_mission_hard_text.setTextColor(getColor(R.color.black))
        }else if(this.level == 2L){
            activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.google_key_color))
            activity_finish_mission_easy_text.setTextColor(getColor(R.color.black))
            activity_finish_mission_moderate_text.setTextColor(getColor(R.color.black))
            activity_finish_mission_hard_text.setTextColor(getColor(R.color.white))
        }

        activity_finish_mission_button_linearlayout.visibility = View.VISIBLE
    }

    fun shareMission(){
        val shareActivityIntent = Intent(this, ShareActivity::class.java)
        shareActivityIntent.putExtra("imagePath", imagePath)
        shareActivityIntent.putExtra("location",location)
        startActivity(shareActivityIntent)
    }

    fun endMission(){
        // Progress Dialog
        dialogObject.show(supportFragmentManager, "wait dialog")

        // History Data
        val data = hashMapOf(
                "average_pace" to averagePace,
                "calories" to calories,
                "distance" to distance,
                "heart_rate" to heartRate,
                "location" to location,
                "steps" to steps,
                "time" to Timestamp.now(),
                "time_second" to time
        )

        // Firebase History Collection Add
        Firebase.firestore
                .collection("users")
                .document(UID)
                .collection("history")
                .add(data)
                .addOnSuccessListener {
                    dialogObject.dismiss()
                    finish()
                }
                .addOnFailureListener {

                }
    }
}