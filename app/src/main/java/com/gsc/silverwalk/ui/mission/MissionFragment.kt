package com.gsc.silverwalk.ui.mission

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.R

class MissionFragment : Fragment() {

    // Dialog Parameter
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialogView : View
    private lateinit var dialogButton : Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mission, container, false)

        // Init Dialog Configuration
        dialogBuilder = AlertDialog.Builder(context)
        dialogView = layoutInflater.inflate(R.layout.dialog_mission_start, null)
        dialogBuilder.setView(dialogView)
        dialogButton = dialogView.findViewById<Button>(R.id.dialog_start_ok_button)
        dialogButton.setOnClickListener(View.OnClickListener {
            // Activity Show

        })

        // start Mission Button
        val startButton = root.findViewById<Button>(R.id.mission_select_start_button)
        startButton.setOnClickListener(View.OnClickListener {
            dialogBuilder.show().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        })

        return root
    }
}