package com.saaei12092021.office.ui.activities.estateLocationOnMapActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.GetAdsFromSocketResponse
import com.saaei12092021.office.util.Resource
import io.socket.client.Socket
import org.json.JSONObject
import java.io.IOException

class EstateLocationOnMapViewModel(app: Application) : AndroidViewModel(app) {
    lateinit var mSocket: Socket

    //----------------------------------------------------------------------------------------------
    val getAdsFromSocketResponse: MutableLiveData<Resource<GetAdsFromSocketResponse>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    //-----------------------------------------------------------------------------------------------
    fun getAdsFromSocket(getAdsMap: HashMap<String, Any>? = null) {
        getAdsFromSocketResponse.postValue(Resource.Loading())
        try {
         //  if (hasInternetConnection()) {
                mSocket.on("getAds") {
                  //  Log.d("0000yas",it.toString())
                    if (it.isNotEmpty()) {
                        val response =
                            Gson().fromJson(
                                it.first().toString(),
                                GetAdsFromSocketResponse::class.java
                            )
                        getAdsFromSocketResponse.postValue(Resource.Success(response))
                    } else error.postValue("Error from server")
                }
                val jsonData: JSONObject = JSONObject(getAdsMap as Map<*, *>)
                mSocket.emit("getAds", jsonData)

//            } else {
//                error.postValue("No internet connection")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> error.postValue("Network Failure")
                else -> error.postValue(t.toString())
            }
        }
    }
}