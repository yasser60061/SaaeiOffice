package com.saaei12092021.office.ui.activities.homeActivity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.saaei12092021.office.model.requestModel.SignInRequest
import com.saaei12092021.office.model.responseModel.signInResponse2.SignInResponse2
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val refreshTokenLive: MutableLiveData<Resource<SignInResponse2>> = MutableLiveData()

    //-----------------------------------------------------------------------------------------------
    fun refreshToken(user: SignInRequest, oldToken: String) = viewModelScope.launch {
        refreshTokenLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.refreshToken(user, oldToken)
                if (response.isSuccessful) {
                    refreshTokenLive.postValue(Resource.Success(response.body()!!))
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    Log.d("refreshToken_response", json.toString())
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