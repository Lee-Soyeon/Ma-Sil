package com.gsc.silverwalk.ui.mission

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.gsc.silverwalk.MissionActivity
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.dialog_mission_cancle.*
import kotlinx.android.synthetic.main.fragment_mission.*

class MissionFragment : Fragment() {

    // Current Mission Parameter
    private var currentMissionIndex = 0
    private val missionStringArray : Array<Int> = arrayOf(
        R.string.mission_mission_1, R.string.mission_mission_2,
        R.string.mission_mission_3, R.string.mission_mission_4,
        R.string.mission_mission_5
    )
    private val MissionLevelStringArray : Array<Int> = arrayOf(
        R.string.Easy, R.string.Moderate, R.string.Hard
    )
    private lateinit var MissionLevelCardView : Array<CardView>

    // Dialog Parameter
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialogView : View
    private lateinit var dialogButton : Button
    private lateinit var dialogObject : AlertDialog

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mission, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // MissionLevelCardView Init
        MissionLevelCardView = arrayOf(
            mission_level1_cardview, mission_level2_cardview,
            mission_level3_cardview, mission_level4_cardview,
            mission_level5_cardview
        )

        // Init Dialog Configuration
        dialogBuilder = AlertDialog.Builder(context)
        dialogView = layoutInflater.inflate(R.layout.dialog_mission_start, null)
        dialogBuilder.setView(dialogView)
        dialogButton = dialogView.findViewById<Button>(R.id.mission_cancle_dialog_cnacle_button)
        dialogButton.setOnClickListener(View.OnClickListener {
            // Activity Show
            val nextIntent = Intent(context, MissionActivity::class.java)
            startActivity(nextIntent)
            dialogObject.dismiss()
        })
        dialogObject = dialogBuilder.create()
        dialogObject.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // start Mission Button
        mission_select_start_button.setOnClickListener(View.OnClickListener {
            dialogObject.show()
        })

        // prev Mission Button
        mission_select_left_button.setOnClickListener(View.OnClickListener {
            currentMissionIndex = if (currentMissionIndex == 0) 4 else currentMissionIndex - 1
            setMissionParameterByCurrentMissionIndex()
        })

        // next Mission Button
        mission_select_right_button.setOnClickListener(View.OnClickListener {
            currentMissionIndex = if (currentMissionIndex == 4) 0 else currentMissionIndex + 1
            setMissionParameterByCurrentMissionIndex()
        })
    }

    fun setMissionParameterByCurrentMissionIndex(){

        mission_number_text.setText(missionStringArray[currentMissionIndex])

        /*
        // Mission Explain Text Update
        mission_display_1_text.setText()
        mission_display_2_text.setText()
        mission_display_3_text.setText()

        // Mission Level Update
        mission_level_text.setText(MissionLevelStringArray[MissionLevel])
        for(i in 0..MissionLevel){
            MissionLevelCardView[i].setColor()
        }

        // Background Image Update
        mission_background.setImageURI()
         */
    }
}