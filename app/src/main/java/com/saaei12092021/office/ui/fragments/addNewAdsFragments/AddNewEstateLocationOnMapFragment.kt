package com.saaei12092021.office.ui.fragments.addNewAdsFragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentAddNewEstateLocationOnMapBinding
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.bitmapDescriptorFromVector

class AddNewEstateLocationOnMapFragment(private val listener: OnLocationOfEstateSubmit) : DialogFragment() {

    private var _binding: FragmentAddNewEstateLocationOnMapBinding? = null
    val binding get() = _binding!!
    var client: FusedLocationProviderClient? = null
    private var mapFragment: SupportMapFragment? = null
    var adsListFromSocket = ArrayList<Data>()
    var locationIsRequired = false
    var isPermissionRequired = false
    var isEnableGpsRequired = false
    var currentLocation: LatLng = LatLng(0.0, 0.0)
    override fun getTheme(): Int = R.style.DialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewEstateLocationOnMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment =
            childFragmentManager.findFragmentById(R.id.map_for_estate_location) as SupportMapFragment?
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        adsListFromSocket = ArrayList()

        setUpLanguageViewAndDirection()
        checkGPSStatus()
        checkLocationPermission()

        binding.submitCurrentLocationBtn.setOnClickListener {
            val location: ArrayList<Double> = ArrayList()
            location.clear()
            location.add(0,currentLocation.longitude)
            location.add(currentLocation.latitude)
            listener.submitLocationOfEstate(location)
            dismiss()
        }

        binding.backRl.setOnClickListener {
            dismiss()
        }

    }

    private fun setUpLanguageViewAndDirection() {
        if ((activity as HomeActivity).deviceLanguage == "العربية") {
            binding.headerRelative.layoutDirection = LAYOUT_DIRECTION_RTL
            binding.backIv.rotation = 180F
        }
    }

    private fun checkGPSStatus() {
        val manager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!manager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.enable_gps))
            .setMessage("تفعل الموقع ضروري لتحديد موع العقار , هل تريد تفعيلها بالهاتف")
            .setCancelable(false)
            .setPositiveButton("نعم",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("لا",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
        isEnableGpsRequired = true
    }

    private val MY_PERMISSIONS_REQUEST_LOCATION = 101

    private fun checkLocationPermission() {
        if (!isPermissionRequired)
            ActivityCompat.requestPermissions(
                requireContext() as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        isPermissionRequired = true
    }

    private fun getCurrentLocation() {
        checkLocationPermission()
        val task: Task<Location>? = client?.lastLocation
        task?.addOnSuccessListener { location ->
            if (location != null) {
                locationIsRequired = true
                mapFragment?.getMapAsync { googleMap ->
                    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    val uiSettings = googleMap.uiSettings
                    uiSettings.isMyLocationButtonEnabled = true
                    uiSettings.isMapToolbarEnabled = true
                    val latLng = LatLng(location.latitude, location.longitude)

                    Log.d("yasser_location", currentLocation.toString())

                    if ((activity as HomeActivity).moveCameraGoogleMapToEstateLocationLatLong.latitude != 0.0){
                        currentLocation = (activity as HomeActivity).moveCameraGoogleMapToEstateLocationLatLong
                        googleMap.addMarker(MarkerOptions().position(currentLocation).title("موقع افتراضي حسب المدينة او الحي"))

                    } else {
                        currentLocation = latLng
                        googleMap.addMarker(MarkerOptions().position(currentLocation).title("موقعك الان"))
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11f))

                    googleMap.setOnMapClickListener {
                        googleMap.clear()
                        currentLocation = it
                        Log.d("yasser_location", currentLocation.toString())
                        googleMap.addMarker(MarkerOptions().position(currentLocation).title("الموقع المختار للعقار"))
                    }
                }
            }
        }
    }

    private fun getMarkerIcon(): BitmapDescriptor? {
        return context?.bitmapDescriptorFromVector(
            R.drawable.ic_price_marker,
            "--" + " --",
        )
    }

    override fun onResume() {
        super.onResume()
        if (!locationIsRequired or isEnableGpsRequired or (currentLocation.latitude > 0.0))
            getCurrentLocation()
    }

    interface OnLocationOfEstateSubmit {
        fun submitLocationOfEstate(
            location: ArrayList<Double>
        )
    }

}
