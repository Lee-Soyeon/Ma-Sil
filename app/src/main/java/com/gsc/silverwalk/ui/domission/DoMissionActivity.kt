package com.gsc.silverwalk.ui.domission

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.gsc.silverwalk.R
import com.gsc.silverwalk.ui.finishmission.FinishMissionActivity
import com.gsc.silverwalk.ui.map.MapsActivity
import kotlinx.android.synthetic.main.activity_mission.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer
import kotlin.jvm.Throws

class DoMissionActivity : AppCompatActivity() {

    // CAMERA Intent Parameter
    companion object{
        val REQUEST_CAMERA = 100
        const val SIGN_IN_REQUEST_CODE = 1001
    }
    private var cameraPathArray : ArrayList<String> = arrayListOf()
    private var currentImagePath : String = ""

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

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

        supportActionBar?.hide()

        fitSignIn()

        // Get Intent Data
        missionTime =
            if (intent.hasExtra("missionTime"))
                (intent.getLongExtra("missionTime", 0)) else missionTime
        missionLocation =
            if (intent.hasExtra("missionLocation"))
                (intent.getStringExtra("missionLocation")!!) else missionLocation
        missionType =
            if (intent.hasExtra("missionType"))
                (intent.getStringExtra("missionType")!!) else missionType
        missionLevel =
            if (intent.hasExtra("missionLevel"))
                (intent.getLongExtra("missionLevel", 0)) else missionLevel

        // Init Cancle Dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_mission_cancle, null)
        // Cancle Button
        dialogView.findViewById<Button>(R.id.mission_cancle_dialog_cancle_button)
            ?.setOnClickListener(View.OnClickListener {
                dialogObject.cancel()
            })
        // Resume Button
        dialogView.findViewById<Button>(R.id.mission_cancle_dialog_resume_button)
            ?.setOnClickListener(View.OnClickListener {
                pauseTimer()
                finish()
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
            dispatchTakePictureIntent()
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

            finishMissionIntent.putExtra("averagePace", 0L)
            finishMissionIntent.putExtra("calories", 0L)
            finishMissionIntent.putExtra("distance", 0.0)
            finishMissionIntent.putExtra("heartRate", 0L)
            finishMissionIntent.putExtra("location", missionLocation)
            finishMissionIntent.putExtra("steps", 0L)
            finishMissionIntent.putExtra("time", currentTimeSecond)
            finishMissionIntent.putExtra("imagePath", cameraPathArray)

            startActivity(finishMissionIntent)
            finish()
        })
    }

    private fun fitSignIn() {
        if (oAuthPermissionsApproved()) {
            readDailySteps()
        } else {
            GoogleSignIn.requestPermissions(
                this,
                SIGN_IN_REQUEST_CODE,
                getGoogleAccount(),
                fitnessOptions
            )
        }
    }

    private fun oAuthPermissionsApproved() =
        GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    private fun getGoogleAccount(): GoogleSignInAccount =
        GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

        }

        when (resultCode) {
            RESULT_OK -> {
                readDailySteps()
            }
            else -> {

            }
        }
    }

    private fun readDailySteps() {
        Fitness.getHistoryClient(this, getGoogleAccount())
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = when {
                    dataSet.isEmpty -> 0
                    else -> dataSet.dataPoints.first()
                        .getValue(Field.FIELD_STEPS).asInt()
                }

                Log.i("#####", "Total steps: $total")
            }
            .addOnFailureListener { e ->
                Log.w("#####", "There was a problem getting the step count.", e)
            }
    }

    override fun onBackPressed() {
        dialogObject.show()
//        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startTimer(){
        timerTask = timer(initialDelay = 1000, period = 1000) {
            currentTimeSecond += 1

            // UI Thread
            runOnUiThread{
                activity_mission_timer_text.setText(
                        String.format("%02d'  %02d''", currentTimeSecond / 60, currentTimeSecond % 60))
                activity_mission_timer_progressBar.progress =
                    (currentTimeSecond.toFloat() / missionTime.toFloat() * 100.0f).toInt()

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

    // Open Camera
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.gsc.silverwalk",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    // Image To File
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentImagePath = absolutePath
            cameraPathArray.add(currentImagePath)
        }
    }
}