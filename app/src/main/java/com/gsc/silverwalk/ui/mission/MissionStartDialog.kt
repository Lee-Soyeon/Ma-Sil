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
import kotlinx.android.synthetic.main.dialog_mission_start.*
import org.json.JSONObject
import retrofit2.Call
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MissionStartDialog : DialogFragment() {

    private var temperature = ""
    private var temperature_text = ""
    private var time = 0L
    private var location = ""
    private var type = ""
    private var level = 0L

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogView = inflater.inflate(R.layout.dialog_mission_start, container, false)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.US)
        val formatted = current.format(formatter)
        dialogView.findViewById<TextView>(R.id.dialog_start_time_textview)
            .setText(formatted)

        dialogView.findViewById<Button>(R.id.mission_start_dialog_start_button)
            .setOnClickListener(View.OnClickListener {
                val missionActivityIntent = Intent(context, MissionActivity::class.java)
                missionActivityIntent.putExtra("missionTime", time)
                missionActivityIntent.putExtra("missionLocation", location)
                missionActivityIntent.putExtra("missionType", type)
                missionActivityIntent.putExtra("missionLevel",level)
                startActivity(missionActivityIntent)
                dialog?.dismiss()
            })

        dialogView.findViewById<TextView>(R.id.dialog_start_temperature_textview)
            .setText(temperature)
        dialogView.findViewById<TextView>(R.id.dialog_start_temperature_info_text)
            .setText(temperature_text)

        return dialogView
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setDialogInfo(time : Long, location : String, type : String, level : Long,
        temperature : String, temperature_text : String) {
        this.time = time;
        this.location = location;
        this.type = type;
        this.level = level;
        this.temperature = temperature
        this.temperature_text = temperature_text
    }
}