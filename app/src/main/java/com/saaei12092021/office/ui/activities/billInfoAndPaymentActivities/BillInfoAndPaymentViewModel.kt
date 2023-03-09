package com.saaei12092021.office.ui.activities.billInfoAndPaymentActivities

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
import com.saaei12092021.office.model.requestModel.AddBookingRequest
import com.saaei12092021.office.model.requestModel.BillInfoRequest
import com.saaei12092021.office.model.responseModel.BillInfoResponse
import com.saaei12092021.office.model.responseModel.CheckOfficeMarketPlaceResponse
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.addBookingResponse.AddBookingResponse
import com.saaei12092021.office.model.responseModel.paymentTearms.PaymentsTermsResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import io.socket.client.Socket
import kotlinx.coroutines.launch
import java.io.IOException

class BillInfoAndPaymentViewModel(app: Application) : AndroidViewModel(app) {

    lateinit var mSocket: Socket
    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    var addBookingLive: MutableLiveData<Resource<AddBookingResponse>> = MutableLiveData()
    val getBillInfoLive: MutableLiveData<Resource<BillInfoResponse>> = MutableLiveData()
    val getPaymentTermsLive: MutableLiveData<Resource<PaymentsTermsResponse>> = MutableLiveData()
    var checkMarketPlaceLive: MutableLiveData<Resource<CheckOfficeMarketPlaceResponse>> = MutableLiveData()
    //----------------------------------------------------------------------------------------------

    fun getBillInfo(billInfoRequest: BillInfoRequest) = viewModelScope.launch {
        getBillInfoLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBillInfo(billInfoRequest = billInfoRequest)
                if (response.isSuccessful)
                    getBillInfoLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    getBillInfoLive.postValue(Resource.Error("error"))
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue("Conversion Error")
            }
        }
    }

    fun getPaymentTerms(myLang: String) = viewModelScope.launch {
        getPaymentTermsLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getPaymentTerms(myLang)
                if (response.isSuccessful) {
                    getPaymentTermsLive.postValue(Resource.Success(response.body()!!))
                    Log.d("payment_terms_response", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    getPaymentTermsLive.postValue(Resource.Error("error"))
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue("Conversion Error")
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    fun addBooking(
        token: String,
        adsId: String,
        addBookingRequest: AddBookingRequest,
    ) = viewModelScope.launch {
        addBookingLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.addBooking(token, adsId, addBookingRequest)
                if (response.isSuccessful) {
                    addBookingLive.postValue(Resource.Success(response.body()!!))
                    Log.d("addBookingS_", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    addBookingLive.postValue(Resource.Error("error"))
                    Log.d("addBookingE_", response.body().toString())
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
        }
    }

    //----------------------------------------------------------------------------------------------

    fun checkOfficeMarketPlace(
        token: String,
        officeId: String,
    ) = viewModelScope.launch {
        checkMarketPlaceLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.checkOfficeMarketPlace(token, officeId)
                if (response.isSuccessful) {
                    checkMarketPlaceLive.postValue(Resource.Success(response.body()!!))
                    Log.d("checkOfficeMPR_", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    checkMarketPlaceLive.postValue(Resource.Error("error"))
                    Log.d("checkOfficeMPE_", response.body().toString())
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