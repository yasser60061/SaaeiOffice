package com.saaei12092021.office.ui.fragments.addAndRequestEstateDialogFragment

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
//import com.google.gson.reflect.TypeToken // for review must don't delete it
import com.google.gson.Gson
import com.saaei12092021.office.model.responseModel.settingResponse.SettingResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch

class AddAndRequestEstateDialogViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val settingResponseLive: MutableLiveData<Resource<SettingResponse>> = MutableLiveData()

    // we may listen to the response if any error happened but in normal we don't need

    //-----------------------------------------------------------------------------------------------
    fun getSettingInfo() = viewModelScope.launch {
        settingResponseLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getSetting()
                if (response.isSuccessful) {
                    settingResponseLive.postValue(Resource.Success(response.body()!!))
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    Log.d("notification_response", json.toString())
                }
            }
        } catch (t: Throwable) {
        }
    }

    //--------------------------------------------------------------------------------------------

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