package com.gsc.silverwalk.ui.domission

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.gsc.silverwalk.R
import com.gsc.silverwalk.ui.map.MapsActivity
import kotlinx.android.synthetic.main.activity_mission.*
import androidx.lifecycle.Observer
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.concurrent.timer

class DoMissionActivity : AppCompatActivity() {

    private lateinit var doMissionViewModel: DoMissionViewModel

    // Cancle Dialog
    private val dialogObject = CancelDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

        supportActionBar?.hide()

        doMissionViewModel = ViewModelProvider(this, DoMissionViewModelFactory())
            .get(DoMissionViewModel::class.java)

        doMissionViewModel.getIntent(intent)

        doMissionViewModel.doMissionTimeForm.observe(this, Observer {
            val doMissionTimeForm = it ?: return@Observer

            activity_mission_timer_text.setText(doMissionTimeForm.getTimeStringFormat())
            activity_mission_timer_progressBar.progress =
                doMissionViewModel.getProgressTime(doMissionTimeForm.time!!)
        })

        doMissionViewModel.doMissionForm.observe(this, Observer {
            val doMissionForm = it ?: return@Observer

            activity_mission_average_pace_text.setText(doMissionForm.averagePace.toString())
            activity_mission_distance_text.setText(doMissionForm.distance.toString())
            activity_mission_kcal_text.setText(doMissionForm.calories.toString())
        })

        // Init Cancel Dialog
        dialogObject.setCancelButtonClickListener {
            dialogObject.dismiss()
        }
        dialogObject.setResumeButtonClickListener {
            doMissionViewModel.pauseTimer()
            finish()
        }

        // Start Map Activity
        activity_mission_map_button.setOnClickListener(View.OnClickListener {
            val nextIntent = Intent(this, MapsActivity::class.java)
            startActivity(nextIntent)
        })

        // Pause Mission
        activity_mission_pause_button.setOnClickListener(View.OnClickListener {
            activity_mission_playing_linearlayout.visibility = View.INVISIBLE
            activity_mission_stop_linearlayout.visibility = View.VISIBLE
            doMissionViewModel.pauseTimer()
        })

        // Start Camera Activity
        activity_mission_camera_button.setOnClickListener(View.OnClickListener {
            startActivityForResult(doMissionViewModel.dispatchTakePicture(this), 1)
        })

        // Stop Mission
        activity_mission_stop_button.setOnClickListener(View.OnClickListener {
            dialogObject.show(supportFragmentManager, "cancel Dialog")
        })

        // Continue Mission
        activity_mission_continue_button.setOnClickListener(View.OnClickListener {
            activity_mission_playing_linearlayout.visibility = View.VISIBLE
            activity_mission_stop_linearlayout.visibility = View.INVISIBLE
            doMissionViewModel.startTimer(this)
        })

        // Testing Finish Mission
        activity_mission_display_1_text.setOnClickListener(View.OnClickListener {
            startActivity(doMissionViewModel.finishMission(this))
            finish()
        })

        doMissionViewModel.startTimer(this)
    }

    override fun onBackPressed() {
        dialogObject.show(supportFragmentManager, "cancel Dialog")
//        super.onBackPressed()
    }
}