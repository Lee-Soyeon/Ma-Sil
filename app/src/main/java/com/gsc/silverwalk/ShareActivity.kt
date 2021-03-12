package com.gsc.silverwalk

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.iterator
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        supportActionBar?.hide()

        // cancel button
        share_activity_cancel_button.setOnClickListener(View.OnClickListener {
            finish()
        })

        // done button
        share_activity_done_button.setOnClickListener(View.OnClickListener {
            val selectList: List<Int> = listOf()
            val iterator = share_activity_photos_gridlayout.iterator()
            while(iterator.hasNext()){
                val item = iterator.next()
                /*
                if(item...){
                    selectList.plus()
                }
                 */
            }
            postShareData(share_activity_edittext.text.toString(), selectList)
        })

        // set ui data
        setRouteImage()
        addMyPhotos()
    }

    fun setRouteImage() {
        // Something To Do
    }

    fun addMyPhotos(){
        // Something To Do
    }

    fun postShareData(caption: String, selectList: List<Int>){
        // Progress Bar Setting
        // Need To Change Custome Dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Upload...")
        progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar_Horizontal)

        // Firebase Update
        CoroutineScope(Main).launch {
            progressDialog.show()
            delay(1000)
            progressDialog.cancel()
            finish()
        }
    }
}