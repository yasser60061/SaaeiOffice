package com.saaei12092021.office.util.mapUtil

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PermissionsManager(activity: AppCompatActivity,
    private val locationProvider: LocationProvider) {

  private val locationPermissionProvider = activity.registerForActivityResult(
      ActivityResultContracts.RequestPermission()) { granted ->
    if (granted) {
      locationProvider.getUserLocation()
    }
  }

  fun requestUserLocation() {
    locationPermissionProvider.launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }
}