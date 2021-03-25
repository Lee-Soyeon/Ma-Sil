package com.gsc.silverwalk.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.history.HistoryDataSource
import com.gsc.silverwalk.data.history.HistoryRepository
import java.lang.IllegalArgumentException

class HistoryViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                historyRepository = HistoryRepository(
                    dataSource = HistoryDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}