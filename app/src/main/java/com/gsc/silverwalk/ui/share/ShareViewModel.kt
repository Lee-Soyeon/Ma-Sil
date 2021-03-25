package com.gsc.silverwalk.ui.share

import android.content.Intent
import android.view.View
import android.widget.GridLayout
import androidx.core.view.iterator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gsc.silverwalk.data.share.ShareRepository
import kotlinx.android.synthetic.main.framelayout_history_camera_view.view.*

class ShareViewModel(private val shareRepository: ShareRepository) : ViewModel() {

    private val _shareForm = MutableLiveData<ShareForm>()
    val shareForm: LiveData<ShareForm> = _shareForm

    fun getIntent(intent: Intent){
        _shareForm.value = ShareForm(
            intent.getSerializableExtra("imagePath") as ArrayList<String>
        )

        shareRepository.setLocationData(intent.getStringExtra("location").toString())
    }

    fun postShareData(caption: String, gridLayout: GridLayout, success: () -> Unit) {
        val selectList: MutableList<String> = mutableListOf()
        val iterator = gridLayout.iterator()
        var index = 0
        while(iterator.hasNext()){
            val item = iterator.next()
            if(item.history_camera_check_image.visibility == View.VISIBLE){
                selectList.add(shareForm.value!!.photoPathList[index])
            }
            ++index
        }

        shareRepository.postShareData(caption, selectList, success)
    }
}