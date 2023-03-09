package com.saaei12092021.office.ui.activities.newPhoneAvtivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
//import com.google.gson.reflect.TypeToken // for review must don't delete it
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

class NewPhoneViewModel (app: Application) : AndroidViewModel(app){

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val codeToNewPhoneLive : MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()

    //----------------------------------------------------------------------------------------------
    fun requestCodeToNewPhone(token: String, newPhoneNumber: String) = viewModelScope.launch {
        codeToNewPhoneLive.postValue(Resource.Loading())
        try {
       //     if (hasInternetConnection()) {
                val response = repository.sendCodeToNewPhone(token, newPhoneNumber)
                if (response.isSuccessful)
                    codeToNewPhoneLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    codeToNewPhoneLive.postValue(Resource.Error("error"))
                }
//            }
//        else {
//                error.postValue("No internet connection")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue("Conversion Error")
            }
        }
    }

}