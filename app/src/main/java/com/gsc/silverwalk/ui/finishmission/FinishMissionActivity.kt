package com.gsc.silverwalk.ui.finishmission

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gsc.silverwalk.R
import com.gsc.silverwalk.ui.share.ShareActivity
import com.gsc.silverwalk.WaitDialog
import kotlinx.android.synthetic.main.activity_finish_mission.*

class FinishMissionActivity : AppCompatActivity() {

    private lateinit var finishMissionViewModel: FinishMissionViewModel

    private val dialogObject: WaitDialog = WaitDialog()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_mission)

        supportActionBar?.hide()

        finishMissionViewModel = ViewModelProvider(this, FinishMissionViewModelFactory())
            .get(FinishMissionViewModel::class.java)

        finishMissionViewModel.getIntent(intent)

        finishMissionViewModel.finishMissionForm.observe(this, Observer {
            val finishMissionForm = it ?: return@Observer

            if (finishMissionForm.level == 0) {
                activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.blue))
                activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
                activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
                activity_finish_mission_easy_text.setTextColor(
                    getColor(R.color.white)
                )
                activity_finish_mission_moderate_text.setTextColor(
                    getColor(R.color.black)
                )
                activity_finish_mission_hard_text.setTextColor(
                    getColor(R.color.black)
                )
                activity_finish_mission_easy_text.typeface = Typeface.DEFAULT_BOLD
                activity_finish_mission_moderate_text.typeface = Typeface.DEFAULT
                activity_finish_mission_hard_text.typeface = Typeface.DEFAULT
            } else if (finishMissionForm.level == 1) {
                activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
                activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.blue))
                activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
                activity_finish_mission_easy_text.setTextColor(getColor(R.color.black))
                activity_finish_mission_moderate_text.setTextColor(getColor(R.color.white))
                activity_finish_mission_hard_text.setTextColor(getColor(R.color.black))
                activity_finish_mission_easy_text.typeface = Typeface.DEFAULT
                activity_finish_mission_moderate_text.typeface = Typeface.DEFAULT_BOLD
                activity_finish_mission_hard_text.typeface = Typeface.DEFAULT
            } else if (finishMissionForm.level == 2) {
                activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
                activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
                activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.blue))
                activity_finish_mission_easy_text.setTextColor(getColor(R.color.black))
                activity_finish_mission_moderate_text.setTextColor(getColor(R.color.black))
                activity_finish_mission_hard_text.setTextColor(getColor(R.color.white))
                activity_finish_mission_easy_text.typeface = Typeface.DEFAULT
                activity_finish_mission_moderate_text.typeface = Typeface.DEFAULT
                activity_finish_mission_hard_text.typeface = Typeface.DEFAULT_BOLD
            }

            activity_finish_mission_button_linearlayout.visibility = View.VISIBLE
        })

        activity_finish_mission_easy_cardview.setOnClickListener(View.OnClickListener {
            finishMissionViewModel.selectMission(0)
        })

        activity_finish_mission_moderate_cardview.setOnClickListener(View.OnClickListener {
            finishMissionViewModel.selectMission(1)
        })

        activity_finish_mission_hard_cardview.setOnClickListener(View.OnClickListener {
            finishMissionViewModel.selectMission(2)
        })

        activity_finish_share_button.setOnClickListener(View.OnClickListener {
            startActivity(finishMissionViewModel.shareMission(this))
        })

        activity_finish_end_button.setOnClickListener(View.OnClickListener {
            dialogObject.show(supportFragmentManager, "wait dialog")
            finishMissionViewModel.postMissionData{
                dialogObject.dismiss()
                finish()
            }
        })
    }
}