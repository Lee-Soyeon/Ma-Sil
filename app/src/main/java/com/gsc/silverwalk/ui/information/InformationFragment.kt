package com.gsc.silverwalk.ui.information

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_information.*

class InformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_information, container, false)

        // Find Data From Firebase

        /*
        information_badge_1_linearlayout.alpha = 0.0f
        information_badge_2_linearlayout.alpha = 0.0f
        information_badge_3_linearlayout.alpha = 0.0f
        information_badge_4_linearlayout.alpha = 0.0f
        information_badge_5_linearlayout.alpha = 0.0f
        information_badge_6_linearlayout.alpha = 0.0f
        information_badge_7_linearlayout.alpha = 0.0f
        information_badge_8_linearlayout.alpha = 0.0f
         */

        return root
    }
}