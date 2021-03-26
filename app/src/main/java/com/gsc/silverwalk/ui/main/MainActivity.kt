package com.gsc.silverwalk.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.gsc.silverwalk.R
import com.gsc.silverwalk.location.LocationClient

class MainActivity : AppCompatActivity() {

    private val multiplePermissionsCode = 100

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
            .addDataType(DataType.AGGREGATE_MOVE_MINUTES)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Google Map Options
        GoogleMapOptions().liteMode(true)

        // Location Client Build
        LocationClient.getInstance().buildLocationClient(this)

        // Main Activity View
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_mission,
                R.id.navigation_achievement,
                R.id.navigation_neighborhood,
                R.id.navigation_information
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()

        checkPermissionsAndRun()
    }

    private fun checkPermissions() {
        val rejectedPermissionList = ArrayList<String>()

        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                rejectedPermissionList.add(permission)
            }
        }

        if (rejectedPermissionList.isNotEmpty()) {
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                multiplePermissionsCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if (grantResults.isNotEmpty()) {
                    checkPermissionsAndRun()
                }
            }
        }
    }

    private fun checkPermissionsAndRun() {
        if (permissionApproved()) {
            if (oAuthPermissionsApproved()) {
                subscribe()
            } else {
                GoogleSignIn.requestPermissions(this, 0, getGoogleAccount(), fitnessOptions)
            }
        } else {
            checkPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            subscribe()
        }
    }

    private fun oAuthPermissionsApproved() =
        GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    private fun subscribe() {
        Fitness.getRecordingClient(this, getGoogleAccount())
            .listSubscriptions()
            .addOnSuccessListener { subscriptions ->
                val checkList : MutableList<Boolean> = mutableListOf(false, false, false, false)
                for (sc in subscriptions) {
                    if(sc.dataType == DataType.TYPE_STEP_COUNT_DELTA){
                        checkList[0] = true
                    }else if(sc.dataType == DataType.TYPE_DISTANCE_DELTA){
                        checkList[1] = true
                    }else if(sc.dataType == DataType.TYPE_CALORIES_EXPENDED){
                        checkList[2] = true
                    }else if(sc.dataType == DataType.TYPE_MOVE_MINUTES){
                        checkList[3] = true
                    }
                }

                if(!checkList[0]) {
                    Fitness.getRecordingClient(this, getGoogleAccount())
                        .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                }
                if(!checkList[1]) {
                    Fitness.getRecordingClient(this, getGoogleAccount())
                        .subscribe(DataType.TYPE_DISTANCE_DELTA)
                }
                if(!checkList[2]) {
                    Fitness.getRecordingClient(this, getGoogleAccount())
                        .subscribe(DataType.TYPE_CALORIES_EXPENDED)
                }
                if(!checkList[3]) {
                    Fitness.getRecordingClient(this, getGoogleAccount())
                        .subscribe(DataType.TYPE_MOVE_MINUTES)
                }
            }
    }

    private fun permissionApproved(): Boolean {
        return (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
                )) &&
                (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) &&
                (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BODY_SENSORS
                )) &&
                (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                )) &&
                (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )) &&
                (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                )) &&
                (PackageManager.PERMISSION_GRANTED
                        == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
    }
}