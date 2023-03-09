package com.saaei12092021.office.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.util.Constant.BASE_URL
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import java.net.URISyntaxException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object GeneralFunctions {

    fun hasInternetConnection(): Boolean {
        val connectivityManager = MyApplication().getSystemService(
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

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }

    fun createSocket(context: Context): Socket {
        val mSocket: Socket
        try {
            var myId = ""
            // ------------------  if supervisor or not ---------------------------------------
            val userJsonLoginData = Constant.getLoginResponse(context)
            if (userJsonLoginData != "") {
                var loginResponse: User? = null
                loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
                if (loginResponse.type == "SUPERVISOR")
                    myId = loginResponse.parent.id.toString()
                else myId = Constant.getUserId(context)
            } else myId = Constant.getUserId(context)
            //---------------------------------------------------------------------------------
            val uri: URI = URI.create(BASE_URL + "chat")
            // val uri: URI = URI.create("https://api.saaei.co/chat")
            val option = IO.Options.builder().build()
            option.query = "id=$myId"
            mSocket = IO.socket(uri, option)
        } catch (e: URISyntaxException) {
            throw RuntimeException()
        }
        return mSocket
    }

    fun translateEnumToWord(enumString: String): String {
        return when (enumString) {
            "NEW" -> "جديد"
            "ON-PROGRESS" -> "تحت الإجراء"
            "ACCEPTED" -> "محادثة مفتوحة"
            "CANCELED" -> "ملغية"
            "COMPLETED" -> "مكتمل"
            "WITHDRAWAL" -> "منسحب"
            "REFUSED" -> "تم الرفض"
            "CLOSED" -> "مغلق"
            "SALE" -> "بيع"
            "RENT" -> "إيجار"
            "DAILY" -> "يومي"
            "MONTHLY" -> "شهري"
            "YEARLY" -> "سنوي"
            "OPENING" -> "مفتوح"
            else -> enumString
        }
    }

    fun translateEnumToWordInMarketPlace(enumString: String): String {
        return when (enumString) {
            "PENDING" -> "طلبك في الانتظار"
            "REFUSED" -> "تم رفض الطلب"
            "ACCEPTED" -> "منضم"
            "FIXING-PHASE" -> "يحتاج تعديل"
            else -> enumString
        }
    }

    fun deleteCountryCodeFromPhoneAndReformat(dateTime: String): String {
        val string = StringBuilder(dateTime).also {
            it.setCharAt(0, ' ')
            it.setCharAt(1, ' ')
            it.setCharAt(2, ' ')
            it.setCharAt(3, ' ')
        }
        return string.toString().trim()
    }

    fun toIntString(theString: String): String {
        val string = StringBuilder(theString).also {
            it.setCharAt(theString.length - 1, ' ')
            it.setCharAt(theString.length - 2, ' ')
        }
        return string.toString().trim()
    }

//    fun reformatPrice(thePrice: Float): String {
//        val amount = thePrice.toDouble()
//        val formatter = DecimalFormat("#,###.0")
//        return formatter.format(amount)
//    }
    fun reformatPrice(thePrice: Float): String =
        NumberFormat.getInstance(Locale("en"))
            .format(thePrice)

    fun isValidEmail(email: String): Boolean {
        //   val expression = "^[\\w\\.]+@([\\w]+\\.)+[A-Z]{2,7}$"
        val expression = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*\$"
        val inputString: CharSequence = email
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(inputString)
        return matcher.matches()
    }

    fun isValidEmail2(email: String): Boolean {
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    fun pickFromGallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivityForResult(intent, Constant.IMAGE_FILE_CODE)
    }

    fun pickFromCamera(activity: Activity) {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(takePicture, 0)
    }

    fun getUserIdIfSupervisorOrNot(activity: Activity): String {
        var userId = ""
        val userJsonLoginData = Constant.getLoginResponse(activity)
        if (userJsonLoginData != "") {
            var loginResponse: User? = null
            loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
            if (loginResponse.type == "SUPERVISOR")
                userId = loginResponse.parent.id.toString()
            else userId = Constant.getUserId(activity)
        } else userId = Constant.getUserId(activity)
        return userId
    }

    fun ifUserIsSupervisor (activity: Activity):Boolean {
        var isSupervisor = false
        val userJsonLoginData = Constant.getLoginResponse(activity)
        if (userJsonLoginData != "") {
            var loginResponse: User? = null
            loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
            isSupervisor = loginResponse.type == "SUPERVISOR"
        }
        return  isSupervisor
    }

    fun isValidLink (theLink : String) : Boolean{
        val URL_REGEX =
            "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
        val pattern: Pattern = Pattern.compile(URL_REGEX)
        val matcher: Matcher = pattern.matcher(theLink) //replace with string to compare
        return ((matcher.find()) and ((theLink.contains("http://") or (theLink.contains("https://")))))

    }
}