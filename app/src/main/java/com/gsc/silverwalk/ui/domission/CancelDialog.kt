package com.gsc.silverwalk.ui.domission

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.dialog_mission_cancle.view.*

class CancelDialog : DialogFragment() {

    private lateinit var cancelButtonListener: () -> Unit
    private lateinit var resumeButtonListener: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.dialog_mission_cancle, container, false)
        view.mission_cancle_dialog_cancle_button.setOnClickListener { cancelButtonListener() }
        view.mission_cancle_dialog_resume_button.setOnClickListener { resumeButtonListener() }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setCancelButtonClickListener(toDo: () -> Unit) {
        cancelButtonListener = toDo
    }

    fun setResumeButtonClickListener(toDo: () -> Unit) {
        resumeButtonListener = toDo
    }
}