package com.gsc.silverwalk.ui.domission

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.gsc.silverwalk.data.domission.DoMissionRepository
import java.util.*
import kotlin.concurrent.timer
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.finishmission.FinishMissionActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class DoMissionViewModel(private val doMissionRepository: DoMissionRepository) : ViewModel() {

    private val _doMissionForm = MutableLiveData<DoMissionForm>()
    val doMissionForm: LiveData<DoMissionForm> = _doMissionForm

    private val _doMissionTimeForm = MutableLiveData<DoMissionTimeForm>()
    val doMissionTimeForm: LiveData<DoMissionTimeForm> = _doMissionTimeForm

    private lateinit var timerTask: Timer

    init {
        _doMissionTimeForm.value = DoMissionTimeForm(0L)
    }

    // Intent
    fun getIntent(intent: Intent) {
        doMissionRepository.setMissionData(
            intent.getLongExtra("missionTime", 0),
            intent.getStringExtra("missionLocation").toString(),
            intent.getStringExtra("missionType").toString(),
            intent.getLongExtra("missionLevel", 0)
        )
    }

    // Repository Interface
    fun getProgressTime(time: Long): Int {
        return doMissionRepository.getProgressTime(time)
    }

    // Timer
    fun pauseTimer() {
        timerTask.cancel()
    }

    fun startTimer(context: Context) {
        timerTask = timer(initialDelay = 1000, period = 1000) {
            doMissionRepository.requestGoogleFitApi(context) {
                if (it is Result.Success) {
                    _doMissionForm.postValue(it.data)
                }
            }

            _doMissionTimeForm.postValue(_doMissionTimeForm.value?.copyAfterTick())
        }
    }

    // Intent
    fun finishMission(context: Context): Intent {
        var i = Intent(context, FinishMissionActivity::class.java)
        i = doMissionRepository.setIntentParameter(i)
        i.putExtra("averagePace", _doMissionForm.value?.averagePace)
        i.putExtra("calories", _doMissionForm.value?.calories)
        i.putExtra("distance", _doMissionForm.value?.distance)
        i.putExtra("heartRate", _doMissionForm.value?.heartRate)
        i.putExtra("steps", _doMissionForm.value?.steps)
        i.putExtra("time", _doMissionTimeForm.value?.time)
        return i
    }

    // Camera
    fun dispatchTakePicture(context: Context): Intent {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = createImageFile()
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.gsc.silverwalk",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    return takePictureIntent
                }
            }
            return takePictureIntent
        }
    }

    // Image To File
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            doMissionRepository.addCameraPath(absolutePath)
        }
    }
}