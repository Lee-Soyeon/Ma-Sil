package com.gsc.silverwalk.ui.fragment.neighborhood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gsc.silverwalk.data.neighborhood.NeighborhoodDataSource
import com.gsc.silverwalk.data.neighborhood.NeighborhoodRepository
import java.lang.IllegalArgumentException

class NeighborhoodViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NeighborhoodViewModel::class.java)) {
            return NeighborhoodViewModel(
                neighborhoodRepository = NeighborhoodRepository(
                    dataSource = NeighborhoodDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}