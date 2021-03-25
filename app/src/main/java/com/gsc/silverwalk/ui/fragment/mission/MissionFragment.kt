package com.gsc.silverwalk.ui.fragment.mission

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gsc.silverwalk.R
import kotlinx.android.synthetic.main.fragment_mission.*

class MissionFragment : Fragment() {

    private lateinit var missionViewModel: MissionViewModel

    // Current Mission Parameter
    private val MissionLevelStringArray: Array<Int> = arrayOf(
        R.string.Easy, R.string.Moderate, R.string.Hard
    )

    private val dialogObject: MissionStartDialog = MissionStartDialog()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mission, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        missionViewModel = ViewModelProvider(this, MissionViewModelFactory())
            .get(MissionViewModel::class.java)

        missionViewModel.missionForm.observe(viewLifecycleOwner, Observer {
            val missionForm = it ?: return@Observer

            if (missionForm.isLoaded) {
                mission_progressBar.visibility = View.INVISIBLE
                mission_linearlayout.visibility = View.VISIBLE
            }

            dialogObject.setDialogInfo(
                missionForm.time, missionForm.location, missionForm.type, missionForm.level
            )

            mission_display_1_text.setText(missionForm.getString(this.requireContext()))
            mission_info_index_text.setText(
                (missionForm.currentIndex?.plus(1)).toString() + "/5"
            )
            mission_level_text.setText(
                MissionLevelStringArray[missionForm.level?.toInt()!!]
            )
        })

        missionViewModel.missionImage.observe(viewLifecycleOwner, Observer {
            val missionImage = it ?: return@Observer

            Glide.with(this)
                .load(missionImage.locationImageUrl)
                .listener(requestListener)
                .into(mission_background)
            Glide.with(this)
                .load(missionImage.locationUserThumbnailUrl)
                .into(mission_image_user_info_image)

            mission_image_user_info_text.setText(missionImage.locationUserName)
        })

        missionViewModel.missionWeather.observe(viewLifecycleOwner, Observer {
            val missionWeather = it ?: return@Observer

            dialogObject.setDialogWeatherInfo(
                missionWeather.tempratrue,
                missionWeather.weather_text
            )
        })

        // start Mission Button
        mission_select_start_button.setOnClickListener(View.OnClickListener {
            dialogObject.show(parentFragmentManager, "DialogFragment")
        })

        // prev Mission Button
        mission_select_left_button.setOnClickListener(View.OnClickListener {
            missionViewModel.prevMission()
            missionImageProgressVisiblity(View.VISIBLE, View.INVISIBLE)
        })

        // next Mission Button
        mission_select_right_button.setOnClickListener(View.OnClickListener {
            missionViewModel.nextMission()
            missionImageProgressVisiblity(View.VISIBLE, View.INVISIBLE)
        })
    }

    fun missionImageProgressVisiblity(progressVisiblity: Int, imageVisiblity: Int) {
        mission_image_progressBar.visibility = progressVisiblity
        mission_background.visibility = imageVisiblity
        mission_image_user_info_linearlayout.visibility = imageVisiblity
    }

    val requestListener = (object : RequestListener<Drawable> {
        override fun onLoadFailed(
            p0: GlideException?,
            p1: Any?,
            p2: Target<Drawable>?,
            p3: Boolean
        ): Boolean {
            missionImageProgressVisiblity(View.VISIBLE, View.INVISIBLE)
            return false
        }

        override fun onResourceReady(
            p0: Drawable?,
            p1: Any?,
            p2: Target<Drawable>?,
            p3: DataSource?,
            p4: Boolean
        ): Boolean {
            missionImageProgressVisiblity(View.INVISIBLE, View.VISIBLE)
            return false
        }
    })
}