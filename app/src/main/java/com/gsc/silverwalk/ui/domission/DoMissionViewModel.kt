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
import kotlin.coroutines.coroutineContext

class DoMissionViewModel(private val doMissionRepository: DoMissionRepository) : ViewModel() {

    private val _doMissionForm = MutableLiveData<DoMissionForm>()
    val doMissionForm: LiveData<DoMissionForm> = _doMissionForm

    private val _doMissionTimeForm = MutableLiveData<DoMissionTimeForm>()
    val doMissionTimeForm: LiveData<DoMissionTimeForm> = _doMissionTimeForm

    private lateinit var timerTask: Timer

    init {
        _doMissionTimeForm.value = DoMissionTimeForm(0L)
        startTimer()
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

    fun startTimer() {
        timerTask = timer(initialDelay = 1000, period = 1000) {
            if(_doMissionTimeForm.value!!.isRequestTime()){
                doMissionRepository.requestGoogleFitApi {
                    if(it is Result.Success){
                        _doMissionForm.postValue(it.data)
                    }
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

    // Google Fit
    @RequiresApi(Build.VERSION_CODES.O)
    fun fitSignIn(context: Activity) {
        if (oAuthPermissionsApproved(context)) {
            //readFitnessData(context)
        } else {
            GoogleSignIn.requestPermissions(
                context,
                DoMissionActivity.SIGN_IN_REQUEST_CODE,
                getGoogleAccount(context),
                doMissionRepository.getFitnessOption()
            )
        }
    }

    fun readDailySteps(context: Context) {
        Fitness.getHistoryClient(context, getGoogleAccount(context))
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun readFitnessData(context: Context, time: ZonedDateTime) {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = time
        Log.i(TAG, "Range Start: $startTime")
        Log.i(TAG, "Range End: $endTime")

        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.AGGREGATE_MOVE_MINUTES)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, doMissionRepository.fitnessOptions))
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // The aggregate query puts datasets into buckets, so flatten into a single list of datasets
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                        dumpDataSet(dataSet)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG,"There was an error reading data from Google Fit", e)
            }
    }

    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i(TAG,"Data point:")
            Log.i(TAG,"\tType: ${dp.dataType.name}")
            for (field in dp.dataType.fields) {
                Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
            }
        }
    }

    private fun oAuthPermissionsApproved(context: Context) =
        GoogleSignIn.hasPermissions(
            getGoogleAccount(context),
            doMissionRepository.getFitnessOption()
        )

    private fun getGoogleAccount(context: Context): GoogleSignInAccount =
        GoogleSignIn.getAccountForExtension(context, doMissionRepository.getFitnessOption())
}