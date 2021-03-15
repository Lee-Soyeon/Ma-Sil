package com.gsc.silverwalk

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import kotlinx.android.synthetic.main.activity_mission.*
import kotlinx.android.synthetic.main.dialog_mission_cancle.*
import org.jetbrains.annotations.NotNull
import java.lang.Double.min
import java.util.*
import kotlin.concurrent.timer
import kotlin.properties.Delegates

class MissionActivity : AppCompatActivity() {

    // CAMERA Intent Parameter
    companion object{
        val REQUEST_CAMERA = 100
    }

    // Cancle Dialog
    private lateinit var dialogObject : AlertDialog

    // CurrentPos Update Paramter
    private var currentTimeSecond = 0
    private var timerTask: Timer? = null

    // Mission Data
    private var missionTime = 1800L
    private var missionLocation = ""
    private var missionType = ""
    private var missionLevel = 0L

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

        supportActionBar?.hide()

        // Get Intent Data
        missionTime =
            if (intent.hasExtra("missionTime"))
                (intent.getLongExtra("missionTime",0)) else missionTime
        missionLocation =
            if (intent.hasExtra("missionLocation"))
                (intent.getStringExtra("missionLocation")!!) else missionLocation
        missionType =
            if (intent.hasExtra("missionType"))
                (intent.getStringExtra("missionType")!!) else missionType
        missionLevel =
            if (intent.hasExtra("missionLevel"))
                (intent.getLongExtra("missionLevel",0)) else missionLevel

        // Init Cancle Dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_mission_cancle, null)
        // Cancle Button
        dialogView.findViewById<Button>(R.id.mission_cancle_dialog_cancle_button)
            ?.setOnClickListener(View.OnClickListener {
                finish()
            })
        // Resume Button
        dialogView.findViewById<Button>(R.id.mission_cancle_dialog_resume_button)
            ?.setOnClickListener(View.OnClickListener {
                dialogObject.cancel()
            })
        dialogObject = AlertDialog.Builder(this).setView(dialogView).create()
        dialogObject.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Start Map Activity
        activity_mission_map_button.setOnClickListener(View.OnClickListener {
            val nextIntent = Intent(this, MapsActivity::class.java)
            startActivity(nextIntent)
        })

        // Pause Mission
        activity_mission_pause_button.setOnClickListener(View.OnClickListener {
            activity_mission_playing_linearlayout.visibility = View.INVISIBLE
            activity_mission_stop_linearlayout.visibility = View.VISIBLE
            pauseTimer()
        })

        // Start Camera Activity
        activity_mission_camera_button.setOnClickListener(View.OnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        })

        // Stop Mission
        activity_mission_stop_button.setOnClickListener(View.OnClickListener {
            dialogObject.show()
        })

        // Continue Mission
        activity_mission_continue_button.setOnClickListener(View.OnClickListener {
            activity_mission_playing_linearlayout.visibility = View.VISIBLE
            activity_mission_stop_linearlayout.visibility = View.INVISIBLE
            startTimer()
        })

        // Set Timer
        startTimer()

        // Testing Finish Mission
        activity_mission_display_1_text.setOnClickListener(View.OnClickListener {
            val finishMissionIntent =
                Intent(this, FinishMissionActivity::class.java)
            startActivity(finishMissionIntent)
            finish()
        })
    }

    override fun onBackPressed() {
        dialogObject.show()
//        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startTimer(){
        timerTask = timer(period = 1000) {
            currentTimeSecond += 1

            // UI Thread
            runOnUiThread{
                activity_mission_timer_text.setText(
                    String.format("%02d'  %02d''",currentTimeSecond / 60, currentTimeSecond % 60))
                activity_mission_timer_progressBar.progress =
                    (currentTimeSecond / missionTime * 100).toInt()

                // Google Fit Data Request
                if(currentTimeSecond % 60 == 0) {
                    activity_mission_average_pace_text.setText("0")
                    activity_mission_distance_text.setText("0")
                    activity_mission_kcal_text.setText("0")
                }
            }
        }
    }

    private fun pauseTimer(){
        timerTask?.cancel()
    }
}