package com.saaei12092021.office.util.mapUtil

import android.annotation.SuppressLint
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlin.math.roundToInt

@SuppressLint("MissingPermission")
class LocationProvider(private val activity: AppCompatActivity) {

    private val client by lazy { LocationServices.getFusedLocationProviderClient(activity) }

    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->

        }
    }
}