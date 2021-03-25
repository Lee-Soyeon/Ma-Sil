package com.gsc.silverwalk.data.history

import com.gsc.silverwalk.ui.history.HistoryForm
import com.gsc.silverwalk.data.Result

class HistoryRepository(private val dataSource: HistoryDataSource) {

    fun getLocationStatistics(location: String, result: (Result<HistoryForm>) -> Unit) {
        dataSource.getLocationStatistics(location, result)
    }

}