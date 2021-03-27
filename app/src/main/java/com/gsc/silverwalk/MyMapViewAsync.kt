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
            ?: return

        val address = addressList[0].getAddressLine(0)

        val latitude = addressList[0].latitude
        val longitude = addressList[0].longitude

        val point = LatLng(latitude, longitude)
        val options = MarkerOptions()
        options.title(location)
        options.snippet(address)
        options.position(point)

        googleMap.addMarker(options)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15f))
    }
}