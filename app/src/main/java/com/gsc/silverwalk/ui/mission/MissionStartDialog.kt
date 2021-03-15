package com.gsc.silverwalk.ui.mission

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.gsc.silverwalk.MissionActivity
import com.gsc.silverwalk.R
import com.gsc.silverwalk.retrofit.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MissionStartDialog : DialogFragment() {

    lateinit var fusedLocationClient : FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Location Data
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val dialogView = inflater.inflate(R.layout.dialog_mission_start, container, false)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.US)
        val formatted = current.format(formatter)
        dialogView.findViewById<TextView>(R.id.dialog_start_time_textview)
            .setText(formatted)

        dialogView.findViewById<Button>(R.id.mission_start_dialog_start_button)!!
                .setOnClickListener(View.OnClickListener {
                    val missionActivityIntent = Intent(context, MissionActivity::class.java)
                    startActivity(missionActivityIntent)
                    dialog?.dismiss()
                })

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                var location: Location? = task.result
                RetrofitClient.getInstance().getCurrentWeather(
                    location!!.latitude.toString(),
                    location!!.longitude.toString(),
                    { weatherJSON: JSONObject ->
                        val mainJsonArray = weatherJSON.getJSONObject("main")
                        val weatherJsonArray = JSONObject(
                            weatherJSON.getJSONArray("weather")[0].toString())
                        val temp = ((mainJsonArray.getString("temp").toFloat() / 10.0f) *
                                (9.0f / 5.0f) + 32.0f).toInt().toString() + "ºF"
                        dialogView.findViewById<TextView>(R.id.dialog_start_temperature_textview)
                            .setText(temp)
                        dialogView.findViewById<TextView>(R.id.dialog_start_temperature_info_text)
                            .setText(weatherJsonArray.getString("main"))
                    },
                    { call: Call<JsonObject>, t: Throwable ->

                    })
            }
        }

        return dialogView
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}