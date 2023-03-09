package com.saaei12092021.office.ui.activities.addToMarketPlaceActivity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.saaei12092021.office.model.requestModel.AddToMarketPlaceRequest
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.responseModel.UploadImagesModel
import com.saaei12092021.office.model.responseModel.marketPlaceResponse.AddToMarketPlaceResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch

class AddToMarketPlaceViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    var addToAddToMarketPlaceLive: MutableLiveData<Resource<AddToMarketPlaceResponse>> =
        MutableLiveData()
    var updateMarketPlaceLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    var licenseImagesList: ArrayList<UploadImagesModel> = ArrayList()
    var idImagesList: ArrayList<UploadImagesModel> = ArrayList()

    //----------------------------------------------------------------------------------------------

    fun addToMarketPlace(
        token: String,
        addBookingRequest: AddToMarketPlaceRequest,
    ) = viewModelScope.launch {
        addToAddToMarketPlaceLive.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = repository.addToMarketPlace(
                    token,
                    addBookingRequest,
                    licenseImagesList,
                    idImagesList
                )
                if (response.isSuccessful) {
                    addToAddToMarketPlaceLive.postValue(Resource.Success(response.body()!!))
                    Log.d("addToMarketPlaceR_", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    addToAddToMarketPlaceLive.postValue(Resource.Error("error"))
                    Log.d("addToMarketPlaceR_", response.body().toString())
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
        }
    }

    //----------------------------------------------------------------------------------------------

    fun updateMarketPlace(
        marketPlaceId : String ,
        token: String,
        addBookingRequest: AddToMarketPlaceRequest,
    ) = viewModelScope.launch {
        updateMarketPlaceLive.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = repository.updateMarketPlace(
                    marketPlaceId ,
                    token,
                    addBookingRequest,
                    licenseImagesList,
                    idImagesList
                )
                if (response.isSuccessful) {
                    updateMarketPlaceLive.postValue(Resource.Success(response.body()!!))
                    Log.d("updateMarketPlaceR_", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    updateMarketPlaceLive.postValue(Resource.Error("error"))
                    Log.d("updateMarketPlaceR_", response.body().toString())
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
        }
    }

//----------------------------------------------------------------------------------------------

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MyApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}