package com.gsc.silverwalk

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyMapViewAsync(private val location: String, private val context: Context) :
    OnMapReadyCallback {

    override fun onMapReady(googleMap: GoogleMap) {
        val addressList = Geocoder(context).getFromLocationName(
            location,
            10
        )

        val splitStr: List<String> = addressList[0].toString().split(",")
        val address = splitStr[0].substring(
            splitStr[0].indexOf("\"") + 1,
            splitStr[0].length - 2
        )

        val latitude =
            splitStr[10].substring(splitStr[10].indexOf("=") + 1)
        val longitude =
            splitStr[12].substring(splitStr[12].indexOf("=") + 1)

        val point = LatLng(latitude.toDouble(), longitude.toDouble())
        val options = MarkerOptions()
        options.title(location)
        options.snippet(address)
        options.position(point)

        googleMap.addMarker(options)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15f))
    }

}