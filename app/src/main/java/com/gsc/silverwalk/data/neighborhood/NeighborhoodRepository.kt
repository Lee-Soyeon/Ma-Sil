package com.gsc.silverwalk.data.neighborhood

class NeighborhoodRepository(val dataSource: NeighborhoodDataSource) {

    fun findAllNeighborhood(){
        dataSource.findAllNeighborhood()
    }

}