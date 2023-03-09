package com.saaei12092021.office.ui.activities.estateLocationOnMapActivity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResult
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.ServiceListOnMapAdapter
import com.saaei12092021.office.databinding.ActivityEstateLocationOnMapBinding
import com.saaei12092021.office.model.requestModel.ServiceListOnMapModel
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.adsById.Advertisement
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.bitmapDescriptorFromVector
import io.socket.client.Socket
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.round


class EstateLocationOnMapActivity : AppCompatActivity(), OnMapReadyCallback,
    ServiceListOnMapAdapter.OnItemClickListener {

    lateinit var binding: ActivityEstateLocationOnMapBinding
    lateinit var viewModel: EstateLocationOnMapViewModel
    private lateinit var mMap: GoogleMap
    lateinit var adsResponse: AdsByIdResponse
    var serviceList = ArrayList<ServiceListOnMapModel>()
    private var nearByPlacesList: List<PlacesSearchResult>? = listOf()
    lateinit var serviceListOnMapAdapter: ServiceListOnMapAdapter
    var estateLocationOnTheMap = LatLng(0.0, 0.0)
    var lastComparisonPriceSelected = "all"
    lateinit var mSocket: Socket
    var adsListFromSocket = ArrayList<Data>()
    lateinit var adsByCategoryMapUpdated: HashMap<String, Any>
    lateinit var myLang: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstateLocationOnMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.estate_on_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        adsResponse = intent.getSerializableExtra("adsResponse") as AdsByIdResponse
        serviceList = ArrayList()
        serviceListOnMapAdapter = ServiceListOnMapAdapter(this)
        displayServiceList()
        myLang = Constant.getMyLanguage(this)
        viewModel = ViewModelProvider(this).get(EstateLocationOnMapViewModel::class.java)

        mSocket = GeneralFunctions.createSocket(this)
        viewModel.mSocket = mSocket
        adsListFromSocket = ArrayList()
        viewModel.mSocket.connect()

        setUpLanguageViewAndDirection()

        binding.backRl.setOnClickListener {
            finish()
        }

        binding.theLocationLinear.setOnClickListener {
            binding.serviceListOnMapRv.visibility = View.GONE
            binding.comparisonPriceLinear.visibility = View.GONE
            binding.theLocationLinear.setBackgroundResource(R.drawable.rounded_button)
            binding.theServiceLinear.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.comparisonLinear.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.theLocationTv.setTextColor(Color.parseColor("#FFFFFF"))
            binding.comparisonTv.setTextColor(Color.parseColor("#000000"))
            binding.theServiceTv.setTextColor(Color.parseColor("#000000"))
            mMap.clear()
            setMarkerForThisAdsAndZoom()
        }

        binding.theServiceLinear.setOnClickListener {
            binding.theServiceLinear.setBackgroundResource(R.drawable.rounded_button)
            binding.theLocationLinear.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.comparisonLinear.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.theServiceTv.setTextColor(Color.parseColor("#FFFFFF"))
            binding.theLocationTv.setTextColor(Color.parseColor("#000000"))
            binding.comparisonTv.setTextColor(Color.parseColor("#000000"))
            serviceList.clear()
            serviceList.add(ServiceListOnMapModel("الكل", true, null))
            // serviceList.add(ServiceListOnMapModel("الخدمات المجاورة",false, ""))
            serviceList.add(
                ServiceListOnMapModel(
                    "المستشفيات",
                    false,
                    PlaceType.HOSPITAL
                )
            )
            serviceList.add(ServiceListOnMapModel("المساجد", false, PlaceType.MOSQUE))
            serviceList.add(
                ServiceListOnMapModel(
                    " المحلات التجارية",
                    false,
                    PlaceType.STORE
                )
            )
            serviceList.add(
                ServiceListOnMapModel(
                    "محطة قطار",
                    false,
                    PlaceType.TRAIN_STATION
                )
            )
            serviceList.add(
                ServiceListOnMapModel(
                    serviceName = "محطة باصات",
                    isSelected = false,
                    placeType = PlaceType.BUS_STATION
                )
            )
            serviceList.add(ServiceListOnMapModel("مطاعم", false, PlaceType.RESTAURANT))
            binding.serviceListOnMapRv.visibility = View.VISIBLE
            binding.comparisonPriceLinear.visibility = View.GONE
            serviceListOnMapAdapter.differ.submitList(serviceList)
            binding.serviceListOnMapRv.bringToFront()
            mMap.clear()
            findNearByServices(adsResponse, null)
        }

        binding.comparisonLinear.setOnClickListener {
            binding.serviceListOnMapRv.visibility = View.GONE
            binding.comparisonPriceLinear.visibility = View.VISIBLE
            binding.comparisonPriceLinear.bringToFront()
            binding.comparisonLinear.setBackgroundResource(R.drawable.rounded_button)
            binding.theLocationLinear.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.theServiceLinear.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.comparisonTv.setTextColor(Color.parseColor("#FFFFFF"))
            binding.theLocationTv.setTextColor(Color.parseColor("#000000"))
            binding.theServiceTv.setTextColor(Color.parseColor("#000000"))
            mMap.clear()
            adsByCategoryMapUpdated = HashMap()
            adsByCategoryMapUpdated["limit"] = 50
            adsByCategoryMapUpdated["lat"] = adsResponse.advertisement.location.coordinates[1]
            adsByCategoryMapUpdated["long"] = adsResponse.advertisement.location.coordinates[0]
            adsByCategoryMapUpdated["km"] = 3
            adsByCategoryMapUpdated["priceType"] = "NORMAL"
            adsByCategoryMapUpdated["category"] = adsResponse.advertisement.category.id
            viewModel.getAdsFromSocket(adsByCategoryMapUpdated)
            viewModel.getAdsFromSocketResponse.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        mMap.clear()
                        adsListFromSocket = it.data?.data as ArrayList<Data>
                        adsListFromSocket.forEach { itemData ->
                            val latLngTemp = LatLng(
                                itemData.location.coordinates[1],
                                itemData.location.coordinates[0]
                            )
                            mMap.addMarker(
                                MarkerOptions().position(latLngTemp)
                                    .title(itemData.title)
                                    .icon(getMarkerIconForOthers(itemData))
                            )
                        }
                        mMap.addMarker(
                            MarkerOptions().position(estateLocationOnTheMap)
                                .title(adsResponse.advertisement.title)
                                .icon(getMarkerIcon(adsResponse.advertisement))
                        )
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Error -> {
                        // Toast(context).showCustomToast(err,this)
                    }
                }
            })
        }

        binding.allPriceTv.setOnClickListener {
            binding.allPriceTv.setBackgroundResource(R.drawable.rounded_button_black)
            binding.lowerPriceTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            binding.higherPriceTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            lastComparisonPriceSelected = "all"
            mMap.clear()
            adsListFromSocket.forEach {
                val latLngTemp = LatLng(
                    it.location.coordinates[1],
                    it.location.coordinates[0]
                )
                mMap.addMarker(
                    MarkerOptions().position(latLngTemp)
                        .title(it.title)
                        .icon(getMarkerIconForOthers(it))
                )
            }
            mMap.addMarker(
                MarkerOptions().position(estateLocationOnTheMap)
                    .title(adsResponse.advertisement.title)
                    .icon(getMarkerIcon(adsResponse.advertisement))
            )
        }
        binding.lowerPriceTv.setOnClickListener {
            binding.lowerPriceTv.setBackgroundResource(R.drawable.rounded_button_black)
            binding.allPriceTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            binding.higherPriceTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            lastComparisonPriceSelected = "lower"
            mMap.clear()
            adsListFromSocket.forEach {
                val latLngTemp = LatLng(
                    it.location.coordinates[1],
                    it.location.coordinates[0]
                )
                if ((adsResponse.advertisement.price > it.price) or (adsResponse.advertisement.price == it.price))
                    mMap.addMarker(
                        MarkerOptions().position(latLngTemp)
                            .title(it.title)
                            .icon(getMarkerIconForOthers(it))
                    )
            }
            mMap.addMarker(
                MarkerOptions().position(estateLocationOnTheMap)
                    .title(adsResponse.advertisement.title)
                    .icon(getMarkerIcon(adsResponse.advertisement))
            )
        }
        binding.higherPriceTv.setOnClickListener {
            binding.higherPriceTv.setBackgroundResource(R.drawable.rounded_button_black)
            binding.allPriceTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            binding.lowerPriceTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            lastComparisonPriceSelected = "higher"
            mMap.clear()
            adsListFromSocket.forEach {
                val latLngTemp = LatLng(
                    it.location.coordinates[1],
                    it.location.coordinates[0]
                )
                if ((adsResponse.advertisement.price < it.price) or (adsResponse.advertisement.price == it.price))
                    mMap.addMarker(
                        MarkerOptions().position(latLngTemp)
                            .title(it.title)
                            .icon(getMarkerIconForOthers(it))
                    )
            }
            mMap.addMarker(
                MarkerOptions().position(estateLocationOnTheMap)
                    .title(adsResponse.advertisement.title)
                    .icon(getMarkerIcon(adsResponse.advertisement))
            )
        }
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
        binding.headerLinear.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        binding.serviceListOnMapRv.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.comparisonPriceLinear.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        //    }
    }

    private fun displayServiceList() {
        binding.serviceListOnMapRv.apply {
            adapter = serviceListOnMapAdapter
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        estateLocationOnTheMap = LatLng(
            adsResponse.advertisement.location.coordinates[1],
            adsResponse.advertisement.location.coordinates[0]
        )

        setMarkerForThisAdsAndZoom()
    }

    private fun setMarkerForThisAdsAndZoom() {
        mMap.addMarker(
            MarkerOptions().position(estateLocationOnTheMap)
                .title(adsResponse.advertisement.title)
                .icon(getMarkerIcon(adsResponse.advertisement))
        )
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(estateLocationOnTheMap))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(estateLocationOnTheMap, 14f))

    }

    private fun getMarkerIcon(ad: Advertisement): BitmapDescriptor? {
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
            this.bitmapDescriptorFromVector(
                R.drawable.ic_price_marker,
                "$numberString  ريال  ",
            )
        } else {
            this.bitmapDescriptorFromVector(
                R.drawable.ic_highest_marker,
                " ريال $numberString  السوم  ",
            )
        }
    }

    private fun getMarkerIconForOthers(ad: Data): BitmapDescriptor? {
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

        return this.bitmapDescriptorFromVector(
            R.drawable.ic_price_marker_light_black,
            "$numberString  ريال  ",
        )
    }

    override fun onItemClick(service: ServiceListOnMapModel, position: Int) {
        serviceList.forEach {
            it.isSelected = it.serviceName == service.serviceName
        }
        serviceListOnMapAdapter.notifyDataSetChanged()
        // putNearByPlacesMarkers(service.placeType, nearByPlacesList!!)
        findNearByServices(adsResponse, service.placeType)
    }

    private fun findNearByServices(adsResponse: AdsByIdResponse, placeType: PlaceType?) {
        val geoContext = GeoApiContext
            .Builder()
            .apiKey("AIzaSyBnn2fQWe9AllK1PMoCkHnEtwm7HCz0pYU")
            .build()

        try {
            MainScope().launch {
                nearByPlacesList = PlacesApi.nearbySearchQuery(
                    geoContext,
                    com.google.maps.model.LatLng(
                        adsResponse.advertisement.location.coordinates[1],
                        adsResponse.advertisement.location.coordinates[0]
                    )
                ).type(placeType).radius(1000).await().results.toList()

                putNearByPlacesMarkers(nearByPlacesList!!)
                Log.d("nearByPlacesResponse", nearByPlacesList.toString())
            }
        } catch (e: Exception) {
        }
    }

    private fun putNearByPlacesMarkers(
      //  placeType: PlaceType? = null,
        places: List<PlacesSearchResult>
    ) {
        mMap.clear()
        places.forEach {
            Log.d("placesName", it.types[0])
            var iconAsBitmap = 0

            iconAsBitmap = when {
                it.icon.toString().contains("islam") -> R.drawable.mosque_icon1
                it.icon.toString().contains("hosp") -> R.drawable.hospital_icon1
                it.icon.toString().contains("shop") -> R.drawable.shop_icon
                it.icon.toString().contains("rest") -> R.drawable.resturant_icon
                // if (it.icon.toString().contains("mosq")) R.drawable.red_icon
                else -> R.drawable.busniss_icon
            }

            try {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            it.geometry.location.lat,
                            it.geometry.location.lng
                        )
                    ).title(it.name)
                        .icon(BitmapDescriptorFactory.fromResource(iconAsBitmap))
                )
            } catch (e: Exception) {
            }
            Log.d("placesImage_", it.icon.toString())
        }
        setMarkerForThisAdsAndZoom()
    }
}