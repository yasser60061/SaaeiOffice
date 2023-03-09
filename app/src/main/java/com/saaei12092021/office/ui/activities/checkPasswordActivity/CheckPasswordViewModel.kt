package com.saaei12092021.office.ui.activities.checkPasswordActivity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
//import com.google.gson.reflect.TypeToken // for review must don't delete it
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.saaei12092021.office.model.requestModel.CheckPasswordRequest
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.launch
import java.io.IOException

class CheckPasswordViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val checkPasswordLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()

   //----------------------------------------------------------------------------------------------
    fun checkPassword(token: String, checkPasswordRequest: CheckPasswordRequest) = viewModelScope.launch {
        checkPasswordLive.postValue(Resource.Loading())
        try {
          //  if (hasInternetConnection()) {
                val response = repository.checkPassword(token, checkPasswordRequest)
                if (response.isSuccessful) {
                    checkPasswordLive.postValue(Resource.Success(response.body()!!))
                    Log.d("checkPasswordResponse", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    checkPasswordLive.postValue(Resource.Error("error"))
                    Log.d("checkPasswordError", response.body().toString())
                }
                Log.d("checkPasswordAny", response.body().toString())
//            }
//       {
//                error.postValue("No internet connection")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> {
                    error.postValue("Conversion Error")
                    Log.d("checkPasswordException", t.toString())
                }
            }
        }
    }
}