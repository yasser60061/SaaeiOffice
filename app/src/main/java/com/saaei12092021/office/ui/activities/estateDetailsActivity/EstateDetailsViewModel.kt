package com.saaei12092021.office.ui.activities.estateDetailsActivity

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
import com.saaei12092021.office.model.responseModel.BookingCalenderResponse
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.socketRequest.ViewAdsRequest
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class EstateDetailsViewModel(app: Application) : AndroidViewModel(app) {

    lateinit var mSocket: Socket
    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    var bookingCalenderLive: MutableLiveData<Resource<BookingCalenderResponse>> = MutableLiveData()
    val deleteAdsByIdLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val makeAdsAvailableLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val makeAdsUnavailableLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()

    //----------------------------------------------------------------------------------------------
    fun sendViewAds(viewAdsRequest: ViewAdsRequest) {
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(viewAdsRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("viewAds", jsonData)
                Log.d("viewAds-sent", jsonData.toString())
            }
        } catch (t: Throwable) {
        }
    }

    //-----------------------------------------------------------------------------------------------
    fun makeAdsAvailable(token: String, adsId: String) = viewModelScope.launch {
        makeAdsAvailableLive.postValue(Resource.Loading())
        try {
         //   if (hasInternetConnection()) {
                val response = repository.makeAdsAvailable(token, adsId)
                if (response.isSuccessful){
                    makeAdsAvailableLive.postValue(Resource.Success(response.body()!!))
                    Log.d("makeAdsAvailableRes_" , Gson().toJson(response))
                }
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                }
//            } else {
//                error.postValue("No internet connection")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Log.d("exption_", t.toString())
                //error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }
    //-----------------------------------------------------------------------------------------------
    fun makeAdsUnavailable(token: String, adsId: String) = viewModelScope.launch {
        makeAdsUnavailableLive.postValue(Resource.Loading())
        try {
        //    if (hasInternetConnection()) {
                val response = repository.makeAdsUnavailable(token, adsId)
                if (response.isSuccessful){
                    makeAdsUnavailableLive.postValue(Resource.Success(response.body()!!))
                    Log.d("makeAdsUnavailableRes_" , Gson().toJson(response))
                }
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                }
//            } else {
//                error.postValue("No internet connection")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Log.d("exption_", t.toString())
                //error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }

    //-----------------------------------------------------------------------------------------------
    fun deleteAdsById(token: String, adsId: String) = viewModelScope.launch {
        deleteAdsByIdLive.postValue(Resource.Loading())
        try {
       //     if (hasInternetConnection()) {
                val response = repository.deleteAdsById(token, adsId)
                if (response.isSuccessful)
                    deleteAdsByIdLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                }
//            } else {
//                error.postValue("No internet connection")
       //     }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Log.d("exption_", t.toString())
                //error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    fun getBookingCalender(
        token: String,
        adsId: String,
        startDate: String?,
        endDate: String?
    ) = viewModelScope.launch {
        bookingCalenderLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBookingCalender(token, adsId, startDate, endDate)
                Log.d("bookingCalender_", response.body().toString())
                if (response.isSuccessful)
                    bookingCalenderLive.postValue(Resource.Success(response.body()!!))
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