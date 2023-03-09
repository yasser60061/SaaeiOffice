package com.saaei12092021.office.ui.activities.packagesActivity

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
import com.saaei12092021.office.model.requestModel.BillInfoRequest
import com.saaei12092021.office.model.responseModel.BillInfoResponse
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.packagesResponse.PackagesResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

class PackagesViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val packagesResponseLive: MutableLiveData<Resource<PackagesResponse>> = MutableLiveData()

    //----------------------------------------------------------------------------------------------

    fun getPackages(myLang: String) = viewModelScope.launch {
        packagesResponseLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getPackages(myLang)
                if (response.isSuccessful) {
                    packagesResponseLive.postValue(Resource.Success(response.body()!!))
                    Log.d("package_response", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    packagesResponseLive.postValue(Resource.Error("error"))
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