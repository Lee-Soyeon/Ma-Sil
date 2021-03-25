package com.gsc.silverwalk.ui.fragment.neighborhood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.data.neighborhood.NeighborhoodRepository

class NeighborhoodViewModel(private val neighborhoodRepository: NeighborhoodRepository) :
    ViewModel() {

    private val _neighborhoodForm = MutableLiveData<NeighborhoodForm>()
    val neighborhoodForm: LiveData<NeighborhoodForm> = _neighborhoodForm

    init {
        neighborhoodRepository.findAllNeighborhood {
            if (it is Result.Success) {
                _neighborhoodForm.value = it.data
            }
        }
    }
}