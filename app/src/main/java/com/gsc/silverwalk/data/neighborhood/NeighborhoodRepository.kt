package com.gsc.silverwalk.data.neighborhood

import com.gsc.silverwalk.data.Result
import com.gsc.silverwalk.ui.fragment.neighborhood.NeighborhoodForm

class NeighborhoodRepository(val dataSource: NeighborhoodDataSource) {

    fun findAllNeighborhood(result: (Result<NeighborhoodForm>) -> Unit){
        dataSource.findAllNeighborhood(result)
    }

}