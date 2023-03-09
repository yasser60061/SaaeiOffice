package com.saaei12092021.office.ui.activities.chatActivity

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
import com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse.GeneralErrorResponse
import com.saaei12092021.office.model.responseModel.getContactRequestById.ContactRequestByIdResponse
import com.saaei12092021.office.model.socketRequest.SeenMessageInChatRequest
import com.saaei12092021.office.model.socketRequest.TypingAndStoppingTypingAndOnLineRequest
import com.saaei12092021.office.model.socketResponse.SeenMessageInChatResponse
import com.saaei12092021.office.repository.Repository
import com.saaei12092021.office.util.MyApplication
import com.saaei12092021.office.util.Resource
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository()
    val error: MutableLiveData<String> = MutableLiveData()
    val typingLive: MutableLiveData<Resource<TypingAndStoppingTypingAndOnLineRequest>> =
        MutableLiveData()
    val stopTypingLive: MutableLiveData<Resource<TypingAndStoppingTypingAndOnLineRequest>> =
        MutableLiveData()
    val seenMessageLive: MutableLiveData<Resource<SeenMessageInChatResponse>> =
        MutableLiveData()
    val contactRequestByIdResponseResponseLive: MutableLiveData<Resource<ContactRequestByIdResponse>> =
        MutableLiveData()
    lateinit var mSocket: Socket

    //----------------------------------------------------------------------------------------------
    fun sendActionTypingInSocket(typingAndOnLineRequest: TypingAndStoppingTypingAndOnLineRequest) {
        typingLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(typingAndOnLineRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("typing", jsonData)
                Log.d("typing-sent", jsonData.toString())
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

    fun listenToTypingInSocket() {
        mSocket.on("typing") {
            Log.d("typing-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        TypingAndStoppingTypingAndOnLineRequest::class.java
                    )
                typingLive.postValue(Resource.Success(response))
            }
            Log.d("typing-any", it.first().toString())
        }
    }

    //----------------------------------------------------------------------------------------------
    fun sendActionStopTypingInSocket(typingAndOnLineRequest: TypingAndStoppingTypingAndOnLineRequest) {
        stopTypingLive.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(typingAndOnLineRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("stopTyping", jsonData)
                Log.d("stopTyping-sent", jsonData.toString())
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

    fun listenToStopTypingInSocket() {
        mSocket.on("stopTyping") {
            Log.d("stopTyping-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        TypingAndStoppingTypingAndOnLineRequest::class.java
                    )
                stopTypingLive.postValue(Resource.Success(response))
            }
            Log.d("stopTyping-any", it.first().toString())
        }
    }

    //----------------------------------------------------------------------------------------------
    fun sendSeenTheMessageInSocket(seenMessageInChatRequest: SeenMessageInChatRequest) {
        try {
            if (hasInternetConnection()) {
                val jsonString = Gson().toJson(seenMessageInChatRequest)
                val jsonData: JSONObject = JSONObject(jsonString)
                mSocket.emit("seen", jsonData)
                Log.d("seen-sent", jsonData.toString())
            }
        } catch (t: Throwable) {
            //when (t) {
//                is IOException -> error.postValue("Network Failure")
//                else -> error.postValue(t.toString())
            //     }
        }
    }

    fun listenToSeenTheMessageInSocket() {
        mSocket.on("seen") {
            Log.d("seen-Listen", it.first().toString())
            if (it.isNotEmpty()) {
                val response =
                    Gson().fromJson(
                        it.first().toString(),
                        SeenMessageInChatResponse::class.java
                    )
                seenMessageLive.postValue(Resource.Success(response))
                Log.d("seen-response", response.toString())
            }
            Log.d("seen-any", it.first().toString())
        }
    }

    //----------------------------------------------------------------------------------------------

    fun getContactRequestById(token: String, language: String, contactRequestId: String) =
        viewModelScope.launch {
            contactRequestByIdResponseResponseLive.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response =
                        repository.getContactRequestById(token, language, contactRequestId)
                    if (response.isSuccessful)
                        contactRequestByIdResponseResponseLive.postValue(Resource.Success(response.body()!!))
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