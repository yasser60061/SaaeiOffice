package com.saaei12092021.office.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken  // for review must don't delete it
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.saaei12092021.office.model.requestModel.*
import com.saaei12092021.office.model.socketRequest.addAdsRequest.AddAdsRequestForSocket
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.responseModel.adsRequestedResponse.AdsRequestedResponse
import com.saaei12092021.office.model.responseModel.areasResponse.AreasResponse
import com.saaei12092021.office.model.responseModel.citiesResponse.CitiesResponse
import com.saaei12092021.office.model.responseModel.contactRequesteResponse.ContactRequestResponse
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.EstateMainCategoryResponse
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.EstateSubCategoryResponse
import com.saaei12092021.office.model.responseModel.favoritesResponse.FavoritesResponse
import com.saaei12092021.office.model.responseModel.getAdsResponse.GetAdsResponse
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.MainFeaturesResponse
import com.saaei12092021.office.model.VerificationUserResponse
import com.saaei12092021.office.model.requestModel.rgeaAUTHValidation.RgeaAUTHValidationRequest
import com.saaei12092021.office.model.responseModel.AnoncementsResponse.AnoncementsResponse
import com.saaei12092021.office.model.responseModel.UploadFileInChatResponse
import com.saaei12092021.office.model.responseModel.adRequestedByIdResponse.AdRequestedByIdResponse
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.getAdsWithPaginationResponse.GetAdsWithPaginationResponse
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.model.responseModel.lastContact.LastContactResponse
import com.saaei12092021.office.model.responseModel.messagesInChatResponse.MessagesInChatResponse
import com.saaei12092021.office.model.responseModel.regionResponse.RegionResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.SignInResponse2
import com.saaei12092021.office.model.socketRequest.CloseRequestRequest
import com.saaei12092021.office.model.socketRequest.chatRequest.AcceptRequest
import com.saaei12092021.office.model.socketRequest.chatRequest.AddContactRequest
import com.saaei12092021.office.model.socketRequest.chatRequest.SendNewMessageRequest
import com.saaei12092021.office.model.socketRequest.CompleteRequestRequest
import com.saaei12092021.office.model.socketRequest.WithdrawalRequest
import com.saaei12092021.office.model.socketResponse.acceptContactRequestResponse.AcceptContactRequestResponse
import com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse.AddAdsRequestFromSocketResponse
import com.saaei12092021.office.model.socketResponse.addContactRequestResponse.AddContactRequestResponse
import com.saaei12092021.office.model.socketResponse.completeRequestResponse.CompleteRequestResponse
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.GetAdsFromSocketResponse
import com.saaei12092021.office.model.socketResponse.refuseContactRequestResponse.RefuseContactRequestResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.IOException

class MyViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    lateinit var mSocket: Socket

    //----------------------------------------------------------------------------------------------
    //  for api
    val error: MutableLiveData<String> = MutableLiveData()
    val verificationUser: MutableLiveData<Resource<VerificationUserResponse>> = MutableLiveData()
    val getUserLive: MutableLiveData<Resource<GetUserResponse>> = MutableLiveData()
    val recoverPassword: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val rgeaAUTHValidationLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val citiesResponse: MutableLiveData<Resource<CitiesResponse>> = MutableLiveData()
    val regionLiveData: MutableLiveData<Resource<RegionResponse>> = MutableLiveData()
    val areaResponse: MutableLiveData<Resource<AreasResponse>> = MutableLiveData()
    val mainCategoryResponse: MutableLiveData<Resource<EstateMainCategoryResponse>> =
        MutableLiveData()
    val subCategoryResponse: MutableLiveData<Resource<EstateSubCategoryResponse>> =
        MutableLiveData()
    val allSubCategoryResponse: MutableLiveData<Resource<EstateSubCategoryResponse>> =
        MutableLiveData()
    val mainFeaturesResponse: MutableLiveData<Resource<MainFeaturesResponse>> = MutableLiveData()
    val favoritesResponse: MutableLiveData<Resource<FavoritesResponse>> = MutableLiveData()
    val addNewFavoriteResponse: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val removeFromMyFavorites: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val announcementResponseLive: MutableLiveData<Resource<AnoncementsResponse>> = MutableLiveData()
    val getAdsResponse: MutableLiveData<Resource<GetAdsResponse>> = MutableLiveData()
    val getAdsWithPaginationResponse: MutableLiveData<Resource<GetAdsWithPaginationResponse>> =
        MutableLiveData()
    val requestedEstateResponse: MutableLiveData<Resource<AdsRequestedResponse>> = MutableLiveData()
    val requestedEstateByIdResponseLive: MutableLiveData<Resource<AdRequestedByIdResponse>> =
        MutableLiveData()
    val deleteMyRequestedAdLive: MutableLiveData<Resource<SuccessResponse>> = MutableLiveData()
    val contactRequestResponseLive: MutableLiveData<Resource<ContactRequestResponse>> =
        MutableLiveData()
    val deleteContactRequestResponseLive: MutableLiveData<Resource<SuccessResponse>> =
        MutableLiveData()
    val uploadFileInChatLive: MutableLiveData<Resource<UploadFileInChatResponse>> =
        MutableLiveData()
    val adsByIdLive: MutableLiveData<Resource<AdsByIdResponse>> = MutableLiveData()

    // for socket
    val getAdsFromSocketResponse: MutableLiveData<Resource<GetAdsFromSocketResponse>> =
        MutableLiveData()
    val addAdsRequestRequestFromSocketLive: MutableLiveData<Resource<AddAdsRequestFromSocketResponse>> =
        MutableLiveData()
    val addContactRequestInSocketLive: MutableLiveData<Resource<AddContactRequestResponse>> =
        MutableLiveData()
    val acceptContactRequestSocketLive: MutableLiveData<Resource<AcceptContactRequestResponse>> =
        MutableLiveData()
    val refuseContactRequestResponseSocketLive: MutableLiveData<Resource<RefuseContactRequestResponse>> =
        MutableLiveData()
    val newMessageSocketLive: MutableLiveData<Resource<SendNewMessageRequest>> =
        MutableLiveData()
    val allChatMessagesLive: MutableLiveData<Resource<MessagesInChatResponse>> =
        MutableLiveData()
    val allLastContactsLive: MutableLiveData<Resource<LastContactResponse>> =
        MutableLiveData()
    val completeRequestInSocketLive: MutableLiveData<Resource<CompleteRequestResponse>> =
        MutableLiveData()
    val closeRequestInSocketLive: MutableLiveData<Resource<CompleteRequestResponse>> =
        MutableLiveData()
    val withdrawalRequestInSocketLive: MutableLiveData<Resource<CompleteRequestResponse>> =
        MutableLiveData()

    // for functionality task while run the app

    val googleMapType: MutableLiveData<String> = MutableLiveData()
    val moveCameraInGoogleMap: MutableLiveData<LatLng> = MutableLiveData()
    val currentDeletedAdsId: MutableLiveData<Int> = MutableLiveData()
    val currentDeletedRequestedEstateId: MutableLiveData<Int> = MutableLiveData()
    val currentDeletedContactRequestId: MutableLiveData<Int> = MutableLiveData()

  //----------------------------------------------------------------------------------------------
    fun getUser(token: String, userId: String) = viewModelScope.launch {
        getUserLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getUser(token = token, userId = userId)
                if (response.isSuccessful) {
                    getUserLive.postValue(Resource.Success(response.body()!!))
                    Log.d("getUserResponse_", response.body().toString())
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
                else -> error.postValue(("Conversion Error"))
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    fun verificationUser(verificationUserRequest: VerificationUserRequest) =
        viewModelScope.launch {
            verificationUser.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = repository.verificationUser(verificationUserRequest)
                    if (response.isSuccessful)
                        verificationUser.postValue(Resource.Success(response.body()!!))
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
                    else -> error.postValue(("Conversion Error"))
                }
            }
        }

    //----------------------------------------------------------------------------------------------
    fun recoverPassword(phoneNumber: ForgetPasswordByPhoneRequest) = viewModelScope.launch {
        recoverPassword.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.recoverPassword(phoneNumber)
                if (response.isSuccessful)
                    recoverPassword.postValue(Resource.Success(response.body()!!))
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
                else -> error.postValue("Conversion Error")
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    fun rgeaAUTHValidation(
        token: String,
        myLang: String,
        rgeaAUTHValidationRequest: RgeaAUTHValidationRequest
    ) = viewModelScope.launch {
        rgeaAUTHValidationLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    repository.rgeaAUTHValidation(token, myLang, rgeaAUTHValidationRequest)
                if (response.isSuccessful)
                    rgeaAUTHValidationLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    rgeaAUTHValidationLive.postValue(Resource.Error("error"))
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

    //-----------------------------------------------------------------------------------------------
    fun getAllCityWithoutPagination(token: String, regionId: String, myLang: String) =
        viewModelScope.launch {
            citiesResponse.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = repository.getAllCityWithoutPagination(
                        token = token,
                        regionId = regionId,
                        myLang = myLang,
                    )
                    if (response.isSuccessful) {
                        citiesResponse.postValue(Resource.Success(response.body()!!))
                        Log.d("cityResponse", response.body().toString())
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
                    else -> error.postValue("Conversion Error")
                }
            }
        }

    //-----------------------------------------------------------------------------------------------
    fun getAllCityInAllCountryWithoutPagination(
        token: String,
        regionId: String,
        myLang: String,
        country: String
    ) =
        viewModelScope.launch {
            citiesResponse.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = repository.getAllCityInAllCountryWithoutPagination(
                        token = token,
                        regionId = regionId,
                        myLang = myLang,
                        country = country
                    )
                    if (response.isSuccessful)
                        citiesResponse.postValue(Resource.Success(response.body()!!))
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
                    else -> error.postValue("Conversion Error")
                }
            }
        }

    //-----------------------------------------------------------------------------------------------
    fun getAllRegionWithoutPagination(
        token: String,
        countryId: String,
        myLang: String
    ) = viewModelScope.launch {
        regionLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    repository.getAllRegionWithoutPagination(
                        token = token,
                        countryId = countryId,
                        myLang = myLang
                    )
                if (response.isSuccessful)
                    regionLiveData.postValue(Resource.Success(response.body()!!))
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


    //-----------------------------------------------------------------------------------------------
    fun getAllAreaWithoutPagination(token: String, cityId: String, myLang: String) =
        viewModelScope.launch {
            areaResponse.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response =
                        repository.getAllAreaWithoutPagination(
                            token = token,
                            cityId = cityId,
                            myLang = myLang
                        )
                    if (response.isSuccessful)
                        areaResponse.postValue(Resource.Success(response.body()!!))
                    else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        //  error.postValue(errorResponse2?.errors?.first()?.msg) // تخرج رسالة City غير موجود
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
    fun getMainCategory(myLang: String) = viewModelScope.launch {
        mainCategoryResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getMainCategory(myLang)
                if (response.isSuccessful)
                    mainCategoryResponse.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    mainCategoryResponse.postValue(Resource.Error("error"))
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
    fun getSubCategory(mainId: String, myLang: String) = viewModelScope.launch {
        subCategoryResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getSubCategory(mainId, myLang)
                if (response.isSuccessful)
                    subCategoryResponse.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    subCategoryResponse.postValue(Resource.Error("error"))
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
    fun getAllSubCategory(myLang: String) = viewModelScope.launch {
        allSubCategoryResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getAllSubCategory(myLang)
                if (response.isSuccessful)
                    allSubCategoryResponse.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    allSubCategoryResponse.postValue(Resource.Error("error"))
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
    fun getMainFeatures(myLang: String , category : Int) = viewModelScope.launch {
        mainFeaturesResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getMainFeatures(myLang = myLang , category = category)
                if (response.isSuccessful)
                    mainFeaturesResponse.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun getMyFavorites(token: String, userId: String) = viewModelScope.launch {
        favoritesResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getMyFavorites(token, userId)
                if (response.isSuccessful)
                    favoritesResponse.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun addToMyFavorites(token: String, adsId: String) = viewModelScope.launch {
        addNewFavoriteResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.addToMyFavorites(token, adsId)
                if (response.isSuccessful)
                    addNewFavoriteResponse.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun removeFromMyFavorites(token: String, adsId: String) = viewModelScope.launch {
        removeFromMyFavorites.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.removeFromMyFavorites(token, adsId)
                if (response.isSuccessful)
                    removeFromMyFavorites.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun getAnnouncement() = viewModelScope.launch {
        announcementResponseLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getAnnouncement()
                if (response.isSuccessful)
                    announcementResponseLive.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun getAdsFromSocket(getAdsMap: HashMap<String, Any>? = null) {
        getAdsFromSocketResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                mSocket.on("getAds") {
                    if (it.isNotEmpty()) {
                        val response =
                            Gson().fromJson(
                                it.first().toString(),
                                GetAdsFromSocketResponse::class.java
                            )
                        getAdsFromSocketResponse.postValue(Resource.Success(response))
                        Log.d("adsFromSocket_",response.toString())
                    } else {
                        error.postValue("Error from server")
                        getAdsFromSocketResponse.postValue(Resource.Error(""))
                    }
                }
                val jsonData: JSONObject = JSONObject(getAdsMap as Map<*, *>)
                mSocket.emit("getAds", jsonData)
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
    fun addAdsRequestFromSocket(requestedObject: AddAdsRequestForSocket) {
        addAdsRequestRequestFromSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(requestedObject)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("AddAdsRequest", jsonData)
                Log.d("AddAdsRequest-the-sent", jsonData.toString())
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

    fun listenToAdsRequestFromSocket() {
        mSocket.on("AddAdsRequest") {
            Log.d("AddAdsRequest-get", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        AddAdsRequestFromSocketResponse::class.java
                    )
                if (response.data?.errors == null) {
                    addAdsRequestRequestFromSocketLive.postValue(Resource.Success(response))
                    Log.d("AddAdsRequest-response", response.toString())
                } else {
                    error.postValue(response.data.errors[0].msg)
                    addAdsRequestRequestFromSocketLive.postValue(Resource.Error("error"))
                    Log.d("----error", response.toString())
                }
            } else {
                error.postValue("Some thing error")
            }
            Log.d("AddAdsRequest", it.first().toString())
        }
    }

    //-----------------------------------------------------------------------------------------------
    fun getAds(token: String, myLang: String, mainCategory: Int?, owner: Int?) =
        viewModelScope.launch {
            getAdsResponse.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response =
                        repository.getAds(
                            token = token,
                            myLang = myLang,
                            mainCategory = mainCategory,
                            owner = owner
                        )
                    if (response.isSuccessful)
                        getAdsResponse.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun getAdsWithPagination(token: String, myLang: String, page: Int) =
        viewModelScope.launch {
            getAdsWithPaginationResponse.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response =
                        repository.getAdsWithPagination(
                            token = token,
                            myLang = myLang,
                            page = page,
                        )
                    if (response.isSuccessful) {
                        getAdsWithPaginationResponse.postValue(Resource.Success(response.body()!!))
                        Log.d("getAdsWithPagination", response.toString())
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
    fun getRequestedAds(token: String, myLang: String) = viewModelScope.launch {
        requestedEstateResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getRequestedAds(token, myLang)
                if (response.isSuccessful)
                    requestedEstateResponse.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun getRequestedAdsById(adRequestedId: String, myLang: String) = viewModelScope.launch {
        requestedEstateByIdResponseLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getRequestedAdsById(adRequestedId, myLang)
                if (response.isSuccessful)
                    requestedEstateByIdResponseLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    requestedEstateByIdResponseLive.postValue(Resource.Error(""))
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
    fun deleteMyRequestedAd(token: String, requestId: String) = viewModelScope.launch {
        deleteMyRequestedAdLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.deleteMyRequestedAd(token, requestId)
                if (response.isSuccessful)
                    deleteMyRequestedAdLive.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------

    fun getContactRequest(token: String, language: String) = viewModelScope.launch {
        contactRequestResponseLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getContactRequest(token, language)
                if (response.isSuccessful)
                    contactRequestResponseLive.postValue(Resource.Success(response.body()!!))
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

    fun deleteContactRequest(token: String, contactRequestId: String) = viewModelScope.launch {
        deleteContactRequestResponseLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.deleteContactRequest(token, contactRequestId)
                if (response.isSuccessful)
                    deleteContactRequestResponseLive.postValue(Resource.Success(response.body()!!))
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

    //-----------------------------------------------------------------------------------------------
    fun getAdsById(adsId: String) = viewModelScope.launch {
        adsByIdLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getAdsById(adsId)
                Log.d("ads_by_id", response.toString())
                if (response.isSuccessful)
                    adsByIdLive.postValue(Resource.Success(response.body()!!))
                else {
                    val gson = Gson()
                    val type = object : TypeToken<GeneralErrorResponse>() {}.type
                    val errorResponse2: GeneralErrorResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    error.postValue(errorResponse2?.errors?.first()?.msg)
                    adsByIdLive.postValue(Resource.Error(""))
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

    //----------------------------------------------------------------------------------------------

    fun uploadFileInMessage(
        token: String,
        fileInChat: File?
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            uploadFileInChatLive.postValue(Resource.Loading())
            if (hasInternetConnection()) {
                try {
                    val response = repository.uploadFileInMessage(
                        token = token,
                        fileInChat = fileInChat,
                    )
                    if (response.isSuccessful) {
                        uploadFileInChatLive.postValue(Resource.Success(response.body()!!))
                        Log.d("file_success_resp", response.body().toString())
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<GeneralErrorResponse>() {}.type
                        val errorResponse2: GeneralErrorResponse? =
                            gson.fromJson(response.errorBody()?.string(), type)
                        error.postValue(errorResponse2?.errors?.first()?.msg)
                        Log.d("file_failure_resp", response.body().toString())
                    }
                    Log.d("file_any_resp", response.body().toString())
                } catch (t: Throwable) {
                    when (t) {
                        is IOException -> error.postValue(t.message)
                        else -> {
                            error.postValue("Conversion Error")
                            Log.d("file_exception", t.toString())
                        }
                    }
                }
            }
        }

//---------------  All Chat Operation ----------------------------------------------------

    //-------------------------------- Chat from API --------------------------------
    fun getAllMessage(token: String, userId: String, friendId: String, contactRequest: String) =
        viewModelScope.launch {
            allChatMessagesLive.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = repository.getAllMessage(token, userId, friendId, contactRequest)
                    if (response.isSuccessful)
                        allChatMessagesLive.postValue(Resource.Success(response.body()!!))
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

    fun getLastContacts(token: String, id: String) = viewModelScope.launch {
        allLastContactsLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getLastContacts(token, id)
                if (response.isSuccessful)
                    allLastContactsLive.postValue(Resource.Success(response.body()!!))
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

    //-------------------------------- Chat from Socket -----------------------------
    fun addContactRequestInSocket(addContactRequest: AddContactRequest) {
        addContactRequestInSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(addContactRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("AddContactRequest", jsonData)
                Log.d("contactRequest-the-sent", jsonData.toString())
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

    fun listenToAddContactRequestInSocket() {
        try {
            mSocket.on("AddContactRequest") {
                Log.d("contactRequest-get", it.first().toString())
                if (it.isNotEmpty()) {
                    val response =
                        Gson().fromJson(
                            it.first().toString(),
                            AddContactRequestResponse::class.java
                        )
                    if (response.data!!.errors == null) {
                        addContactRequestInSocketLive.postValue(Resource.Success(response))
                        Log.d("contactRequest-response", response.toString())
                    } else {
                        error.postValue(response.data.errors.toString())
                        addContactRequestInSocketLive.postValue(Resource.Error("error"))
                        Log.d("contactRequest-error", response.toString())
                    }
                } else {
                    error.postValue("Some thing error")
                }
                Log.d("contactRequest-any", it.first().toString())
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }

    //-------------------------------------------------------------------------------------
    fun sendAcceptContactRequestInSocket(acceptRequest: AcceptRequest) {
        acceptContactRequestSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(acceptRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("AcceptRequest", jsonData)
                Log.d("acceptRequest-the-sent", jsonData.toString())
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

    fun listenToAcceptContactRequestInSocket() {
        mSocket.on("AcceptRequest") {
            Log.d("acceptRequest-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        AcceptContactRequestResponse::class.java
                    )
                //  if (response.success)
                acceptContactRequestSocketLive.postValue(Resource.Success(response))
                //  else  error.postValue(response.data.errors.toString())
                Log.d("acceptRequest-response", response.toString())
            } else {
                error.postValue("Some thing error")
            }
            Log.d("acceptRequest-any", it.first().toString())
        }
    }

    //-------------------------------------------------------------------------------------
    fun sendRefuseContactRequestInSocket(refuseRequest: AcceptRequest) {
        refuseContactRequestResponseSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(refuseRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("RefuseRequest", jsonData)
                Log.d("RefuseRequest-the-sent", jsonData.toString())
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

    fun listenToRefuseContactRequestInSocket() {
        mSocket.on("RefuseRequest") {
            Log.d("refuseRequest-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        RefuseContactRequestResponse::class.java
                    )
                refuseContactRequestResponseSocketLive.postValue(Resource.Success(response))
                Log.d("refuseRequest-response", response.toString())
            } else {
                error.postValue("Some thing error")
            }
            Log.d("refuseRequest-any", it.first().toString())
        }
    }

    //----------------------- For new Message (send and listen)------------------------------
    fun sendNewMessageInSocket(sendNewMessageRequest: SendNewMessageRequest) {
        newMessageSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(sendNewMessageRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("newMessage", jsonData)
                Log.d("contactRequest-the-sent", jsonData.toString())
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

    fun listenToNewMessageInSocket() {
        mSocket.on("newMessage") {
            Log.d("newMessage-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        SendNewMessageRequest::class.java
                    )
                newMessageSocketLive.postValue(Resource.Success(response))
                Log.d("newMessage-response", response.toString())
//                if (response.data.errors == null) {
//                    newMessageSocketLive.postValue(Resource.Success(response))
//                    Log.d("newMessage-response", response.toString())
//                } else {
//                    error.postValue(response.data.errors[0].msg)
//                    Log.d("newMessage-error", response.toString())
//                }
            } else {
                error.postValue("Some thing error")
            }
            Log.d("newMessage-any", it.first().toString())
        }
    }

    //-------------------------------------------------------------------------------------

    fun sendActionCompleteRequestInSocket(completeRequestRequest: CompleteRequestRequest) {
        completeRequestInSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(completeRequestRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("CompleteRequest", jsonData)
                Log.d("CompleteRequest-sent", jsonData.toString())
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

    fun listenToCompleteRequestInSocket() {
        mSocket.on("CompleteRequest") {
            Log.d("CompleteRequest-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        CompleteRequestResponse::class.java
                    )
                if (response.data.errors == null) {
                    completeRequestInSocketLive.postValue(Resource.Success(response))
                    Log.d("CompleteRequest-resp", response.toString())
                } else {
                    error.postValue(response.data.errors.toString())
                    completeRequestInSocketLive.postValue(Resource.Error("Error"))
                    Log.d("CompleteRequest-error", response.toString())
                }
            }
            Log.d("CompleteRequest-any", it.first().toString())
        }
    }

    //-------------------------------------------------------------------------------------

    fun sendActionCloseRequestInSocket(closeRequestRequest: CloseRequestRequest) {
        closeRequestInSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(closeRequestRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("CloseRequest", jsonData)
                Log.d("CloseRequest-sent", jsonData.toString())
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

    fun listenToCloseRequestInSocket() {
        mSocket.on("CloseRequest") {
            Log.d("CloseRequest-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        CompleteRequestResponse::class.java
                    )
                if (response.data.errors == null) {
                    closeRequestInSocketLive.postValue(Resource.Success(response))
                    Log.d("CloseRequest-resp", response.toString())
                } else {
                    error.postValue(response.data.errors.toString())
                    closeRequestInSocketLive.postValue(Resource.Error("Error"))
                    Log.d("CloseRequest-error", response.toString())
                }
            }
            Log.d("CloseRequest-any", it.first().toString())
        }
    }

    fun sendActionWithdrawalRequestInSocket(withdrawalRequest: WithdrawalRequest) {
        withdrawalRequestInSocketLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(withdrawalRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("Withdrawal", jsonData)
                Log.d("Withdrawal-sent", jsonData.toString())
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

    fun listenToWithdrawalRequestInSocket() {
        mSocket.on("Withdrawal") {
            Log.d("Withdrawal-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        CompleteRequestResponse::class.java
                    )
                if (response.data.errors == null) {
                    withdrawalRequestInSocketLive.postValue(Resource.Success(response))
                    Log.d("Withdrawal-resp", response.toString())
                } else {
                    error.postValue(response.data.errors.toString())
                    withdrawalRequestInSocketLive.postValue(Resource.Error("Error"))
                    Log.d("Withdrawal-error", response.toString())
                }
            }
            Log.d("Withdrawal-any", it.first().toString())
        }
    }

//-------------------------------------------------------------------------------------

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