package com.gsc.silverwalk.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.gsc.silverwalk.retrofit.RetrofitClient
import org.json.JSONObject

class LocationClient {
    companion object {
        private val LocationClient: LocationClient = LocationClient()

        fun getInstance(): LocationClient {
            return LocationClient
        }
    }

    lateinit var fusedLocationClient : FusedLocationProviderClient
    lateinit var locationCallback : LocationCallback
    lateinit var locationRequest : LocationRequest
    lateinit var context : Context

    fun buildLocationClient(context : Context) {
        this.context = context

        if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.run{
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60000
        }

        locationCallback = object : LocationCallback() {}

        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper())
    }

    fun destoryLocationClient() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun getLastLocation(success: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        success(it)
                    }
                    .addOnFailureListener {

                    }
        }
    }
}