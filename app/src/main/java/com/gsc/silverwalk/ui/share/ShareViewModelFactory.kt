package com.gsc.silverwalk.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.share.ShareDataSource
import com.gsc.silverwalk.data.share.ShareRepository
import java.lang.IllegalArgumentException

class ShareViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            return ShareViewModel(
                shareRepository = ShareRepository(
                    dataSource = ShareDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}