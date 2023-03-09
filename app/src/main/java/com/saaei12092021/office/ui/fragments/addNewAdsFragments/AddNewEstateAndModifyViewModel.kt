package com.saaei12092021.office.ui.fragments.addNewAdsFragments

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
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.AddNewAdsRequest
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.responseModel.addNewAdsResponse.AddNewAdsResponse2
import com.saaei12092021.office.model.responseModel.addNewAdsResponse.UploadEstateImagesResponse
import com.saaei12092021.office.model.responseModel.liveSearchResponse.LiveSearchResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AddNewEstateAndModifyViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val updateAdsLiveData: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val addNewEstateLiveData2: MutableLiveData<Resource<AddNewAdsResponse2>> = MutableLiveData()
    val uploadEstateImagesLiveData: MutableLiveData<Resource<UploadEstateImagesResponse>> =
        MutableLiveData()
    val liveSearchLiveData : MutableLiveData<Resource<LiveSearchResponse>> = MutableLiveData()

    //----------------------------------------------------------------------------------------------

    fun uploadImagesForAddNewEstate(
        token: String,
        myLang: String,
        estateImageFile1: File?,
        threeDFile: File?,
        adsFile: File?,
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            uploadEstateImagesLiveData.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val response = repository.uploadImagesForAddNewEstate(
                        token = token,
                        myLang = myLang,
                        estateImageFile1 = estateImageFile1,
                        threeDFile = threeDFile,
                        adsFile = adsFile
                    )
                    if (response.isSuccessful) {
                        uploadEstateImagesLiveData.postValue(Resource.Success(response.body()!!))
                        Log.d("images_success_resp", response.toString())
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        error.postValue(errorResponse2?.errors?.first()?.msg)
                        Log.d("images_failure_resp", response.toString())
                    }
                    Log.d("images_any_resp", response.toString())
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> error.postValue(t.message)
                        else -> {
                            error.postValue("Conversion Error")
                            Log.d("images_exception", t.toString())
                        }
                    }
                }
            }
        }
    //----------------------------------------------------------------------------------------------

    fun addNewEstate(
        token: String,
        myLang: String,
        addNewAdsRequest: AddNewAdsRequest
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            addNewEstateLiveData2.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val response = repository.addNewEstate(
                        token = token,
                        myLang = myLang,
                        addNewAdsRequest = addNewAdsRequest,
                    )
                    if (response.isSuccessful) {
                        addNewEstateLiveData2.postValue(Resource.Success(response.body()!!))
                        Log.d("add_new_ads_success", response.toString())
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        error.postValue(errorResponse2?.errors?.first()?.msg)
                        addNewEstateLiveData2.postValue(Resource.Error("error"))
                        Log.d("add_new_ads_error", errorResponse2?.errors?.first()?.msg.toString())
                    }
                    Log.d("add_new_ads_any", response.toString())
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> error.postValue(t.message)
                        else ->// error.postValue("Conversion Error")
                            Log.d("add_new_ads_exeption", t.toString())
                    }
                }
            }
        }

// -------------------------------------------------------------------------------------------

    fun updateAds(
        token: String,
        myLang: String,
        updateAds: AddNewAdsRequest, // the same object element
        adsId: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            updateAdsLiveData.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val response = repository.updateAds(
                        token = token,
                        myLang = myLang,
                        addNewAdsRequest = updateAds,
                        adsId = adsId
                    )
                    if (response.isSuccessful) {
                        updateAdsLiveData.postValue(Resource.Success(response.body()!!))
                        Log.d("update_ads_success", response.toString())
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        error.postValue(errorResponse2?.errors?.first()?.msg)
                        updateAdsLiveData.postValue(Resource.Error("error"))
                        Log.d("update_ads_error", response.toString())
                    }
                    Log.d("update_ads_any", response.toString())
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> error.postValue(t.message)
                        else ->// error.postValue("Conversion Error")
                            Log.d("update_ads_exception", t.toString())
                    }
                }
            }
        }

  //---------------------------------------------------------------------------------------

    fun getLiveSearch(token: String , myLang: String , myId: Int , category : String , related : Boolean , area : Int) =
        viewModelScope.launch {
        liveSearchLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getLiveSearch(
                    token = token ,
                    myLang = myLang ,
                    owner = myId ,
                    category = category ,
                    related = related ,
                    area = area
                )
                Log.d("LiveSearch_response", response.toString())
                if (response.isSuccessful)
                    liveSearchLiveData.postValue(Resource.Success(response.body()!!))
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

   //----------------------------------------------------------------------------------------

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