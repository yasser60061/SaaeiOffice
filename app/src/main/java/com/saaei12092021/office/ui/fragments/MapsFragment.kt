package com.saaei12092021.office.ui.fragments

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
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentMapsBinding
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.bitmapDescriptorFromVector
import kotlin.math.round

class MapsFragment : Fragment() , OnMapReadyCallback  {

    private var _binding: FragmentMapsBinding? = null
    val binding get() = _binding!!
    var client: FusedLocationProviderClient? = null
    private var mapFragment: SupportMapFragment? = null
    var adsListFromSocket = ArrayList<Data>()
    var locationIsRequired = false
    var isPermissionRequired = false
    var isEnableGpsRequired = false
    var currentLocation: LatLng = LatLng(0.0, 0.0)
    var midLatLng: LatLng = LatLng(0.0, 0.0)
    var midLocation: Location? = Location("")
    var lastMidLocation: Location? = Location("")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        client = LocationServices.getFusedLocationProviderClient(requireContext())

        try {
            (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility =
                View.VISIBLE
            // (activity as HomeActivity).binding.contentHomeId.mapToolsBar.visibility = View.VISIBLE
            (activity as HomeActivity).binding.contentHomeId.mainBarLinear.visibility =
                View.VISIBLE
            (activity as HomeActivity).binding.contentHomeId.searchResultTv.visibility =
                View.VISIBLE
            (activity as HomeActivity).binding.contentHomeId.allMainCategoryRv.visibility =
                View.VISIBLE
            (activity as HomeActivity).binding.contentHomeId.allSubCategoryRv.visibility =
                View.VISIBLE
            (activity as HomeActivity).binding.notificationRl.visibility = View.VISIBLE
            (activity as HomeActivity).binding.backRl.visibility = View.GONE
            (activity as HomeActivity).binding.homeTitleTv.text = "الرئيسية"

        } catch (e: Exception) {
        }

        adsListFromSocket = ArrayList()
        //    adsListFromSocketNew = ArrayList()
        checkGPSStatus()
        checkLocationPermission()
        // getCurrentLocation()

        binding.mapsOptionCard.setOnClickListener {
            (activity as HomeActivity).displayMapsMenuDialogFragment()
        }

        binding.filterEstateCard.setOnClickListener {
            (activity as HomeActivity).showBottomSheetEstateFilterAndSearch()
        }

        binding.filterEstateCard.setOnClickListener {
            (activity as HomeActivity).showBottomSheetEstateFilterAndSearch()
        }

        binding.myCurrentLocationRl.setOnClickListener {
            getCurrentLocation()
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
            .setMessage("تفعل الموقع ضروري لعرض العقارات , هل تريد تفعيلها بالموبايل")
            .setCancelable(false)
            .setPositiveButton("نعم",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("لا",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
        isEnableGpsRequired = true
    }

    val MY_PERMISSIONS_REQUEST_LOCATION = 99

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
                lastMidLocation = location
                mapFragment?.getMapAsync { googleMap ->
                    if (view != null)
                        (activity as HomeActivity).viewModel.googleMapType.observe(
                            viewLifecycleOwner,
                            Observer {
                                if (it == "satellite")
                                    googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                                else googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                            })
                    //  googleMap.uiSettings.isZoomControlsEnabled = true
                    //  googleMap.uiSettings.isZoomControlsEnabled = true
                    val uiSettings = googleMap.uiSettings
                    uiSettings.isMyLocationButtonEnabled = true
                    //  uiSettings.isMapToolbarEnabled = true
                    val latLng = LatLng(location.latitude, location.longitude)
                    currentLocation = latLng
                    (activity as HomeActivity).currentLat = currentLocation.latitude
                    (activity as HomeActivity).currentLong = currentLocation.longitude
                    googleMap.addMarker(MarkerOptions().position(latLng).title("موقعك الان"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

                    if (view != null) {
                        (activity as HomeActivity).getAdsAccordingLocationAndKm(
                            lat = currentLocation.latitude,
                            long = currentLocation.longitude,
                            km = 20,
                            CategoryId = null ,
                            subCategoryId = null
                        )
                        (activity as HomeActivity).viewModel.getAdsFromSocketResponse.observe(
                            viewLifecycleOwner,
                            Observer {
                                when (it) {
                                    is Resource.Success -> {
                                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                            View.GONE
                                        googleMap.clear()
                                        googleMap.addMarker(
                                            MarkerOptions().position(latLng).title("موقعك الان")
                                        )
                                        adsListFromSocket = it.data?.data as ArrayList<Data>

                                        (activity as HomeActivity).adsListFromSocket =
                                            adsListFromSocket

                                        try {
                                            adsListFromSocket.forEach { adsItem ->
                                                val latLngTemp = LatLng(
                                                    adsItem.location.coordinates[1],
                                                    adsItem.location.coordinates[0]
                                                )
                                                googleMap.addMarker(
                                                    MarkerOptions().position(latLngTemp)
                                                        .title(adsItem.title)
                                                        .icon(getMarkerIcon(adsItem))
                                                )
                                            }
                                            googleMap.setOnMarkerClickListener { thisMarker ->
                                                adsListFromSocket.forEach {
                                                    val thisAdsLocation = LatLng(
                                                        it.location.coordinates[1],
                                                        it.location.coordinates[0]
                                                    )
                                                    if ((thisMarker.title == it.title) and (thisAdsLocation == thisMarker.position)
                                                    ) {
                                                        (activity as HomeActivity).clickedAdsData =
                                                            it
                                                        (activity as HomeActivity).displayAdsInfoFromMap()
                                                    }
                                                }
                                                return@setOnMarkerClickListener (currentLocation != thisMarker.position)
                                            }

                                            (activity as HomeActivity).binding.contentHomeId.searchResultTv.text =
                                                "  تم العثور على  " + adsListFromSocket.size + "   عقار  "

                                            if ((activity as HomeActivity).moveCameraInGoogleMapLatLong.latitude != 0.0) {
                                                googleMap.moveCamera(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                        (activity as HomeActivity).moveCameraInGoogleMapLatLong,
                                                        13f
                                                    )
                                                )
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                    is Resource.Loading -> {
                                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                            View.VISIBLE
                                    }
                                    is Resource.Error -> {
                                        // Toast(context).showCustomToast(err,this)
                                    }
                                }
                            })

                        googleMap.setOnCameraIdleListener { //get latlng at the center by calling
                            midLatLng = googleMap.cameraPosition.target
                            midLocation!!.latitude = midLatLng.latitude
                            midLocation!!.longitude = midLatLng.longitude
                            (activity as HomeActivity).currentLat = midLatLng.latitude
                            (activity as HomeActivity).currentLong = midLatLng.longitude
                            if ((activity as HomeActivity).moveCameraInGoogleMapLatLong.latitude == 0.0) {
                                if (midLocation!!.distanceTo(lastMidLocation) > 500) {
                                    Log.d(
                                        "the distance",
                                        midLocation!!.distanceTo(lastMidLocation).toString()
                                    )
                                    lastMidLocation!!.latitude = midLatLng.latitude
                                    lastMidLocation!!.longitude = midLatLng.longitude

                                    (activity as HomeActivity).getAdsAccordingLocationAndKm(
                                        lat = midLatLng.latitude,
                                        long = midLatLng.longitude,
                                        km = 20,
                                        CategoryId = null ,
                                        subCategoryId = null
                                    )
                                    (activity as HomeActivity).uncheckedAllCategory(false)
                                }
                            } else {
                                (activity as HomeActivity).moveCameraInGoogleMapLatLong =
                                    LatLng(0.0, 0.0)
                            }

                        }

                    }
                }
            }
        }
    }

    private fun getMarkerIcon(ad: Data): BitmapDescriptor? {
        var numberString = ""
        numberString = when {
            Math.abs(ad.price / 1000000) > 1 -> {
                (round((ad.price / 1000000) * 10) / 10).toString() + " مليون "
            }
            Math.abs(ad.price / 1000) > 1 -> {
                (round((ad.price / 1000) * 10) / 10).toString() + " ألف "
            }
            else -> {
                ad.price.toString()
            }
        }

        return if (ad.priceType == Constant.NORMAL_PRICE_TYPE) {
            context?.bitmapDescriptorFromVector(
                R.drawable.ic_price_marker,
                "$numberString  ريال  ",
            )
        } else {
            context?.bitmapDescriptorFromVector(
                R.drawable.ic_highest_marker,
                " ريال $numberString  السوم  ",
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentLocation.latitude == 0.0) {
            if ((!locationIsRequired or isEnableGpsRequired) and
                ((activity as HomeActivity).adsByIdResponse == null)
            )
                getCurrentLocation()
        } else if ((activity as HomeActivity).mustUpdateLocation) {
            (activity as HomeActivity).mustUpdateLocation = false
            (activity as HomeActivity).navController.popBackStack()
            (activity as HomeActivity).clearAllStackFragmentExceptMapFragment()
            (activity as HomeActivity).uncheckedAllCategory(false)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        (activity as HomeActivity).presenter.onMapLoaded()
    }
}


