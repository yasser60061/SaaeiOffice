package com.saaei12092021.office.util

import android.app.Application
import android.content.Context
import com.saaei12092021.office.util.Constant.BASE_URL
import company.tap.gosellapi.GoSellSDK
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import java.net.URISyntaxException

class MyApplication : Application() {

    //-------------------  Socket operation -----------------
    private lateinit var mSocket: Socket
    lateinit var mContext: Context

    override fun onCreate() {
        super.onCreate()
        mContext = this
      //  createSocket()
       // GoSellSDK.init(this, "sk_test_w2UJdsmqgK7WPonECR5pNLuB", "com.saaei12092021.office")
    }

    private fun createSocket() {
        val myId = Constant.getUserId(mContext)
        try {
             val uri: URI = URI.create(BASE_URL+"chat")
         //  val uri: URI = URI.create("https://api.saaei.co/chat")
            val option = IO.Options.builder().build()
            option.query = "id=$myId"
            mSocket = IO.socket(uri, option)
        } catch (e: URISyntaxException) {
            throw RuntimeException()
        }
    }

}
