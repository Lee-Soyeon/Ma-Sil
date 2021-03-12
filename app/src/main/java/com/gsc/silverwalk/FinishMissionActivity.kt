package com.gsc.silverwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FinishMissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_mission)

        supportActionBar?.hide()
    }
}