package com.saaei12092021.office.ui.fragments.notificationsFragment

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
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.responseModel.notificationUnreadCountResponse.NotificationUnreadCountResponse
import com.saaei12092021.office.model.responseModel.notificationsResponse.NotificatiosResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

class NotificationsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val notificationsResponse: MutableLiveData<Resource<NotificatiosResponse>> = MutableLiveData()
    val notificationsCountLive: MutableLiveData<Resource<NotificationUnreadCountResponse>> = MutableLiveData()

    // we may listen to the response if any error happened but in normal we don't need
       val readTheNotificationLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()

    //-----------------------------------------------------------------------------------------------
    fun getNotifications(token: String, myLang: String) = viewModelScope.launch {
        notificationsResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getNotifications(token, myLang)
                if (response.isSuccessful) {
                    notificationsResponse.postValue(Resource.Success(response.body()!!))
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    Log.d("notification_response", json.toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }

    //-----------------------------------------------------------------------------------------------

    fun sendReadTheNotification(token: String, notificationId: String) = viewModelScope.launch {
         readTheNotificationLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                   val response =
                repository.sendReadTheNotification(token, notificationId)
                 if (response.isSuccessful)
                      readTheNotificationLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                }
            } else {
                error.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }

    //--------------------------------------------------------------------------------------------

    fun getNotificationsUnreadCount(token: String) = viewModelScope.launch {
        try {
            if (hasInternetConnection()) {
                val response = repository.getNotificationsUnreadCount(token)
                if (response.isSuccessful) {
                    notificationsCountLive.postValue(Resource.Success(response.body()!!))
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