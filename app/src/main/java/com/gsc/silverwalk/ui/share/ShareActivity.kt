package com.gsc.silverwalk.ui.share

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.GridLayout
import com.gsc.silverwalk.R
import com.gsc.silverwalk.WaitDialog
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.framelayout_history_camera_view.view.*
import java.io.File
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class ShareActivity : AppCompatActivity() {

    lateinit var shareViewModel: ShareViewModel

    private val dialogObject: WaitDialog = WaitDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        supportActionBar?.hide()

        shareViewModel = ViewModelProvider(this, ShareViewModelFactory())
            .get(ShareViewModel::class.java)

        shareViewModel.getIntent(intent)

        shareViewModel.shareForm.observe(this, Observer {
            val shareForm = it ?: return@Observer

            val iter = shareForm.photoPathList.iterator()
            while (iter.hasNext()) {
                val newImage = layoutInflater.inflate(
                    R.layout.framelayout_history_camera_view,
                    share_activity_photos_gridlayout, false
                )

                share_activity_photos_gridlayout.addView(newImage)

                newImage.setOnClickListener(View.OnClickListener {
                    if (it.history_camera_check_image.visibility == View.INVISIBLE) {
                        it.history_camera_blur_image.visibility = View.VISIBLE
                        it.history_camera_check_image.visibility = View.VISIBLE
                    } else {
                        it.history_camera_blur_image.visibility = View.INVISIBLE
                        it.history_camera_check_image.visibility = View.INVISIBLE
                    }
                })

                val layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED), GridLayout.spec(GridLayout.UNDEFINED)
                )
                layoutParams.width = convertDpToPixel(120.0f).toInt()
                layoutParams.height = convertDpToPixel(120.0f).toInt()

                newImage.layoutParams = layoutParams
                val file = File(iter.next())
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    newImage.history_camera_main_image.setImageBitmap(bitmap)
                }
            }
        })

        // cancel button
        share_activity_cancel_button.setOnClickListener(View.OnClickListener {
            finish()
        })

        // done button
        share_activity_done_button.setOnClickListener(View.OnClickListener {
            dialogObject.show(supportFragmentManager, "wait dialog")

            shareViewModel.postShareData(
                share_activity_edittext.text.toString(),
                share_activity_photos_gridlayout
            ) {
                dialogObject.dismiss()
                finish()
            }
        })
    }

    fun convertDpToPixel(dp: Float): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float): Float {
        return px / resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
    }
}