package com.gsc.silverwalk

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_finish_mission.*

class FinishMissionActivity : AppCompatActivity() {

    var isSelect = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_mission)

        supportActionBar?.hide()

        activity_finish_mission_easy_cardview.setOnClickListener(View.OnClickListener {
            if(!isSelect) {
                activity_finish_mission_easy_cardview.setBackgroundColor(getColor(R.color.green))
                selectLevel(0)
            }
        })

        activity_finish_mission_moderate_cardview.setOnClickListener(View.OnClickListener {
            if(!isSelect) {
                activity_finish_mission_moderate_cardview.setBackgroundColor(getColor(R.color.green))
                selectLevel(1)
            }
        })

        activity_finish_mission_hard_cardview.setOnClickListener(View.OnClickListener {
            if(!isSelect) {
                activity_finish_mission_hard_cardview.setBackgroundColor(getColor(R.color.green))
                selectLevel(2)
            }
        })

        activity_finish_share_button.setOnClickListener(View.OnClickListener {
            val ShareActivityIntent = Intent(this, ShareActivity::class.java)
            startActivity(ShareActivityIntent)
        })

        activity_finish_end_button.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    fun selectLevel(level: Int){
        isSelect = true
        // FireBase Update Data
    }
}