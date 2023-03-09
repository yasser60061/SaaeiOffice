package com.saaei12092021.office.util.mapUtil

import androidx.appcompat.app.AppCompatActivity

class MapPresenter(private val activity: AppCompatActivity) {

    private val locationProvider = LocationProvider(activity)
    private val permissionsManager = PermissionsManager(activity, locationProvider)

    fun onMapLoaded() {
        permissionsManager.requestUserLocation()
    }
}