package com.saaei12092021.office.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.saaei12092021.office.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Constant {

//    const val BASE_URL = "https://saaei-api-oci.algorithms.ws/"
//    const val WEB_VIW_URL = "https://saaei-frontend-oci.algorithms.ws/"

    const val BASE_URL = "https://api.saaei.co/"
    const val WEB_VIW_URL = "https://saaei.co/"

    // for signup and register
    const val PHONE_NUMBER = "phone_number"
    const val TOKEN = "token"
    const val TOKEN_GENERATE_DATE = "token_generate_date"
    const val FCM_TOKEN = "fcmToken"
    const val FCM_INFO = "fcm_info"
    const val IS_VERIFIED: String = "isVerified"
    const val USER_ID = "user_id"
    const val USER_TYPE = "user_type"

    const val VERIFICATION_MODE = "verification_mode"
    const val FORGET_PASSWORD = "forget_password"
    const val NEW_REGISTER = "new_register"
    const val IMAGE_FILE_CODE = 111
    const val COMMERCIAL_FILE_CODE = 115
    const val PDF_FILE_CODE = 116
    const val VIDEO_FILE_CODE = 175
    const val MY_LANGUAGE = "my_language"
    const val MY_COUNTRY = "my_country"

    //  const val MY_PROFILE_IMAGE = "img"
    const val LOGIN_RESPONSE = "loginResponse"
    const val SIGNUP_RESPONSE = "signUpResponse"
    const val USER_BY_ID_RESPONSE = "userByIdResponse"
    const val PARENT_USER_BY_ID_RESPONSE = "parentUserByIdResponse"
    const val NORMAL_PRICE_TYPE = "NORMAL"
    const val QUERY_PAGE_SIZE = 20

    var STORAGE_REQUEST_CODE = 1000
    var CAMERA_REQUEST_CODE = 1100
    var CONTACTS_REQUEST_CODE = 2000
    var RECORDING_REQUEST_CODE = 3000

    const val NOTIFICATION_ID = 100
    const val NOTIFICATION_ID_BIG_IMAGE = 101
    const val UPDATE_REQUEST_CODE = 123

    val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{4,}" +  //at least 4 characters
                "$"
    )
    val DIGIT_PASSWORD_PATTERN: Pattern = Pattern.compile("(.*[0-9].*)")   //at least 1 digit
    val LETTER_PASSWORD_PATTERN: Pattern = Pattern.compile("(.*[a-zA-Z].*)") //any letter

    //---------------------- for notification uses ----------------------------------------------------
    const val CHANNEL_1_ID = "channel1"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setUpStatusBar(activity: Activity) {
        val window: Window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.green_for_app)
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    }

    fun makeToastMessage(context: Context, message: String) {
        return Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun dateAndTimeReformat(dateTime: String): String {
        val string = StringBuilder(dateTime).also {
            it.setCharAt(10, ' ')
            it.setCharAt(16, ' ')
            it.setCharAt(17, ' ')
            it.setCharAt(18, ' ')
            it.setCharAt(19, ' ')
            it.setCharAt(20, ' ')
            it.setCharAt(21, ' ')
            it.setCharAt(22, ' ')
            it.setCharAt(23, ' ')
        }
        return string.toString()
    }

    fun dateAndTimeReformatToDateOnly(dateTime: String): String {
        val string = StringBuilder(dateTime).also {
            it.setCharAt(10, ' ')
            it.setCharAt(11, ' ')
            it.setCharAt(12, ' ')
            it.setCharAt(13, ' ')
            it.setCharAt(14, ' ')
            it.setCharAt(15, ' ')
            it.setCharAt(16, ' ')
            it.setCharAt(17, ' ')
            it.setCharAt(18, ' ')
            it.setCharAt(19, ' ')
            it.setCharAt(20, ' ')
            it.setCharAt(21, ' ')
            it.setCharAt(22, ' ')
            it.setCharAt(23, ' ')
        }
        return string.toString()
    }

    fun String.getDateWithServerTimeStamp(): Date? {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.ENGLISH
        )
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")  // IMP !!!
        try {
            return dateFormat.parse(this)
        } catch (e: ParseException) {
            return null
        }
    }

    private fun getSharedPref(context: Context) =
        context.getSharedPreferences("office_pref", Activity.MODE_PRIVATE)

    fun editor(context: Context): SharedPreferences.Editor = getSharedPref(context).edit()
    fun getToken(context: Context): String = getSharedPref(context).getString(TOKEN, "").toString()
    fun getTokenGenerateTime(context: Context): String =
        getSharedPref(context).getString(TOKEN_GENERATE_DATE, "Thu Mar 15 01:26:51 GMT+03:00 2022")
            .toString()

    fun getFcmToken(context: Context): String =
        getSharedPref(context).getString(FCM_TOKEN, "").toString()

    fun getFcmInfo(context: Context): String =
        getSharedPref(context).getString(FCM_INFO, "").toString()

    fun getIsVerification(context: Context) = getSharedPref(context).getBoolean(IS_VERIFIED, false)
    fun getLoginResponse(context: Context): String =
        getSharedPref(context).getString(LOGIN_RESPONSE, "")!!

    fun getSignUpResponse(context: Context): String =
        getSharedPref(context).getString(SIGNUP_RESPONSE, "")!!

    fun getUserByIdResponse(context: Context): String =
        getSharedPref(context).getString(USER_BY_ID_RESPONSE, "")!!

    fun getParentUserResponse(context: Context): String =
        getSharedPref(context).getString(PARENT_USER_BY_ID_RESPONSE , "")!!

    fun getUserId(context: Context): String =
        getSharedPref(context).getString(USER_ID, "0").toString()

    fun getUsertype(context: Context): String =
        getSharedPref(context).getString(USER_TYPE, "").toString()

    fun getMyLanguage(context: Context): String =
        getSharedPref(context).getString(MY_LANGUAGE, "ar").toString()

    fun getMyCountryId(context: Context): String =
        getSharedPref(context).getString(MY_COUNTRY, "1").toString()

    //------ for use later ---------------------------------------

    fun askAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestId
        )
    }

    fun checkAccessFineLocationGranted(context: Context): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(context: Context): Boolean {
        val gfgLocationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return gfgLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || gfgLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps)) // context.getString(R.string.gps_gfg_enabled)
            .setMessage("يجب السماح بتحديد الموقع لعرض العقار على الخريطة") // context.getString(R.string.required_for_this_app)
            .setCancelable(false)
            .setPositiveButton("تفعيل") { _, _ ->
                context.startActivity(Intent("Settings.ACTION_LOCATION_SOURCE_SETTINGS"))
            }
            .show()
    }
}