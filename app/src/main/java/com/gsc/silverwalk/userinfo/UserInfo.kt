package com.gsc.silverwalk.userinfo

import com.gsc.silverwalk.location.LocationClient

class UserInfo {
    companion object {
        private val userInfo: UserInfo = UserInfo()

        fun getInstance(): UserInfo {
            return userInfo
        }
    }

    val userName = "Park Misook"
    val userThumbnailPath = "gs://sliverwork.appspot.com/users/X5NztOqVUgGPT84WmSiK/thumbnail.PNG"
    val uid = "X5NztOqVUgGPT84WmSiK"
}