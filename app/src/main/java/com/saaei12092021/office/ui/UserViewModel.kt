package com.saaei12092021.office.ui

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
import com.saaei12092021.office.model.requestModel.*
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SignUpResponse.SignUpResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.responseModel.getUserByTokenResponse.GetUserByTokenResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.SignInResponse2
import com.saaei12092021.office.model.responseModel.termsResponse.TermsResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val errorLive: MutableLiveData<String> = MutableLiveData()
    var userByTokenLive: MutableLiveData<Resource<GetUserByTokenResponse>> = MutableLiveData()
    val userSignUpLive: MutableLiveData<Resource<SignUpResponse>> = MutableLiveData()
    val signInUserLive: MutableLiveData<Resource<SignInResponse2>> = MutableLiveData()
    val enableNotifyLive: MutableLiveData<Resource<SignInResponse2>> = MutableLiveData()
    val completeOrUpdateProfileLive: MutableLiveData<Resource<SignInResponse2>> = MutableLiveData()
    val resetPasswordByPhoneLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val updatePasswordLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val updatePhoneLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val getTermsLive: MutableLiveData<Resource<TermsResponse>> = MutableLiveData()

    var logoImageFile: File? = null
    var commercialFile: File? = null

    //----------------------------------------------------------------------------------------------

    fun signUpUser(
        myLang: String,
        country: String,
        fullname: String,
        phone: String,
        email: String,
        type: String,
        agencyType : String ,
        password: String,
        idType: Int?,
        idNumber: String?,
        adNumber: Int?
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            userSignUpLive.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val signupRequest = UserSignupRequest(
                            country = country,
                            fullname = fullname,
                            phone = phone,
                            email = email,
                            type = type,
                            agencyType = agencyType ,
                            password = password,
                            idType = idType,
                            idNumber = idNumber,
                            adNumber = adNumber
                    )
                    val response = repository.signUpUser(
                        myLang = myLang,
                        signupRequest ,
                        logoImageFile = logoImageFile,
                        commercialFile = commercialFile
                    )
                    Log.d("signupRequest_",Gson().toJson(signupRequest).toString())
                    if (response.isSuccessful) {
                        userSignUpLive.postValue(Resource.Success(response.body()!!))
                        Log.d("signUpResponse", response.body().toString())
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                    }
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> errorLive.postValue(t.message)
                        else ->
                            errorLive.postValue("Conversion Error")
                    }
                }
            }
        }

    //----------------------------------------------------------------------------------------------

    fun getTerms(myLang: String) = viewModelScope.launch {
        getTermsLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getTerms(myLang)
                if (response.isSuccessful) {
                    getTermsLive.postValue(Resource.Success(response.body()!!))
                    Log.d("terms_response", response.body().toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                    getTermsLive.postValue(Resource.Error("error"))
                }
            } else {
                errorLive.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> errorLive.postValue("Network Failure")
                else -> errorLive.postValue("Conversion Error")
            }
        }
    }
    //----------------------------------------------------------------------------------------------

    fun signInUser(user: SignInRequest) = viewModelScope.launch {
        signInUserLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.signInUser(user)
                Log.d("signin", response.body().toString())
                if (response.isSuccessful)
                    signInUserLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                }
            } else {
                errorLive.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> errorLive.postValue("Network Failure")
                else -> errorLive.postValue(("Conversion Error"))
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    fun getUserByToken(
        token: String,
    ) = viewModelScope.launch {
        try {
            if (hasInternetConnection()) {
                val response = repository.getUserByToken(token)
                if (response.isSuccessful) {
                    userByTokenLive.postValue(Resource.Success(response.body()!!))
                    Log.d("userByTokenResponse_", response.body().toString())
                }
            } else {
                errorLive.postValue("No internet connection")
            }
        } catch (t: Throwable) {
        }
    }

    //----------------------------------------------------------------------------------------------

    fun updateNotifyStatus(
        enableNotifyInUpdateProfileRequest: EnableNotifyInUpdateProfileRequest,
        token: String,
        userId: String,
        myLang: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        enableNotifyLive.postValue(Resource.Loading())
        if (hasInternetConnection()) {
            try {
                val response = repository.updateNotifyStatus(
                    myLang = myLang,
                    token = token,
                    enableNotifyInUpdateProfileRequest = enableNotifyInUpdateProfileRequest,
                    userId = userId,
                )
                if (response.isSuccessful) {
                    enableNotifyLive.postValue(Resource.Success(response.body()!!))
                    Log.d("notify_result", response.body()!!.user.enableNotif.toString())
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                }
                Log.d("profile-any", response.toString())
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> errorLive.postValue(t.message)
                    else -> {
                        errorLive.postValue("Conversion Error")
                        Log.d("enableNotifyerror", t.toString())
                    }
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    fun completeProfile(
        completeProfileRequest: CompleteProfileRequest,
        userId: String,
        myLang: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            completeOrUpdateProfileLive.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val response = repository.completeProfile(
                        myLang = myLang,
                        completeProfileRequest = completeProfileRequest,
                        userId = userId,
                        logoImageFile = logoImageFile,
                        commercialFile = commercialFile
                    )
                    if (response.isSuccessful)
                        completeOrUpdateProfileLive.postValue(Resource.Success(response.body()!!))
                    else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                    }
                    Log.d("com-profile-response", response.body().toString())
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> errorLive.postValue(t.message)
                        else ->
                            errorLive.postValue("Conversion Error")
                    }
                }
            }
        }

    //----------------------------------------------------------------------------------------------
    // i used complete profile to complete and update and stay thid function don,t used at this time

    fun updateProfile(
        completeProfileRequest: CompleteProfileRequest,
        token: String,
        userId: String,
        myLang: String
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            completeOrUpdateProfileLive.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val response = repository.updateProfile(
                        myLang = myLang,
                        token = token,
                        completeProfileRequest = completeProfileRequest,
                        userId = userId,
                        logoImageFile = logoImageFile,
                        commercialFile = commercialFile
                    )
                    if (response.isSuccessful)
                        completeOrUpdateProfileLive.postValue(Resource.Success(response.body()!!))
                    else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                    }
                    Log.d("profile-response", response.body().toString())
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> errorLive.postValue(t.message)
                        else ->
                            errorLive.postValue("Conversion Error")
                    }
                }
            }
        }

    //----------------------------------------------------------------------------------------------
    fun resetPasswordByPhone(
        myLang: String,
        resetPasswordByPhoneRequest: ResetPasswordByPhoneRequest
    ) =
        viewModelScope.launch {
            resetPasswordByPhoneLive.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = repository.resetPasswordByPhone(
                        myLang = myLang,
                        resetPasswordByPhoneRequest = resetPasswordByPhoneRequest
                    )
                    if (response.isSuccessful)
                        resetPasswordByPhoneLive.postValue(Resource.Success(response.body()!!))
                    else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                    }
                } else {
                    errorLive.postValue("No internet connection")
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> errorLive.postValue("Network Failure")
                    else -> errorLive.postValue("Conversion Error")
                }
            }
        }

    //----------------------------------------------------------------------------------------------
    fun updatePassword(
        token: String,
        myLang: String,
        updatePasswordRequest: UpdatePasswordRequest
    ) =
        viewModelScope.launch {
            updatePasswordLive.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = repository.updatePassword(token, myLang, updatePasswordRequest)
                    if (response.isSuccessful)
                        updatePasswordLive.postValue(Resource.Success(response.body()!!))
                    else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                        updatePasswordLive.postValue(Resource.Error("error"))
                    }
                } else {
                    errorLive.postValue("No internet connection")
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> errorLive.postValue("Network Failure")
                    else -> errorLive.postValue("Conversion Error")
                }
            }
        }

    //----------------------------------------------------------------------------------------------
    fun updatePhone(token: String, updatePhoneRequest: UpdatePhoneRequest) = viewModelScope.launch {
        updatePhoneLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.updatePhone(token, updatePhoneRequest)
                if (response.isSuccessful)
                    updatePhoneLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    errorLive.postValue(errorResponse2?.errors?.first()?.msg)
                }
            } else {
                errorLive.postValue("No internet connection")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> errorLive.postValue("Network Failure")
                else -> errorLive.postValue("Conversion Error")
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