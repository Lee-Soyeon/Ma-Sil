package com.gsc.silverwalk.data.share

class ShareRepository(private val dataSource: ShareDataSource) {

    var location: String = ""

    fun setLocationData(location: String){
        this.location = location
    }

    fun postShareData(caption: String, photoPathList: List<String>, done: () -> Unit) {
        dataSource.postShareData(caption, location, photoPathList, done)
    }

}