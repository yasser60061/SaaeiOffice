package com.saaei12092021.office.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.ActivitySplashBinding
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import java.util.*

class SplashActivity : AppCompatActivity() {

    lateinit var loginResponse: User
    lateinit var signUpResponse: com.saaei12092021.office.model.responseModel.SignUpResponse.User
    var isVerified = false
    private var adsIdFromDeepLink = ""
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constant.setUpStatusBar(this)

        isVerified = Constant.getIsVerification(this)

//*   ----  work perfectly from android to android  ---- i stop it now because i used firebase deeplink
     /*
        val uri: Uri? = intent.data
        if (uri != null) {
            val params: List<String> = uri.pathSegments
           // adsIdFromDeepLink = params.get(params.size - 1)
            Log.d("deepLinkUri_" , uri.toString())
            // deepLinkUri_: https://api.saaei.co/office?officeId=3988  /// this is the deep link i receive it
        }
     */

        setLocale("en")

        binding.splashLogo.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.splash_in
            )
        )

        // for force update in android studio
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    it, AppUpdateType.IMMEDIATE, this,
                    Constant.UPDATE_REQUEST_CODE
                )
                Log.e("ImmediateUpdate_S", "Failed to check for update: $it")
            } else displaySplashScreen()
        }.addOnFailureListener {
            Log.e("ImmediateUpdate_", "Failed to check for update: $it")
            displaySplashScreen()
        }
    }

    private fun displaySplashScreen() {
        Handler().postDelayed({
            binding.splashLogo.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.splash_out
                )
            )
            Handler().postDelayed({
                binding.splashLogo.visibility = View.GONE
//                if ((Constant.getMyLanguage(this) == "ar")) {
//                    setLocale("ar")
//                }
//                if ((Constant.getMyLanguage(this) == "en")) {
//                    setLocale("en")
//                }
                if ((Constant.getUserId(this) == "0") and (Constant.getSignUpResponse(this) == "")) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            SignupActivity::class.java
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            overridePendingTransition(0, 0)
                            finish()
                        })
                } else {
                    getStoreUserDataAndCheckWhereWeGo()
                }
            }, 900)
        }, 1500)
    }

    private fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
     //   conf.setLayoutDirection(myLocale)
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
     //   finish()
    }

    private fun getStoreUserDataAndCheckWhereWeGo() {
        val userJsonLoginData = Constant.getLoginResponse(this)
        val userJsonSignUpData = Constant.getSignUpResponse(this)
        if (userJsonLoginData != "") {
            loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
            if ((loginResponse.accountType == "SIGNUP-PROCESS") and (!isVerified)) {
                val intent = Intent(this, VerificationActivity::class.java)
                intent.putExtra(Constant.VERIFICATION_MODE, Constant.NEW_REGISTER)
                intent.putExtra(Constant.PHONE_NUMBER, loginResponse.phone)
                startActivity(intent)
                finish()
            } else if ((loginResponse.accountType == "SIGNUP-PROCESS") and (isVerified)) {
                goToHome()
            } else if ((loginResponse.accountType != "SIGNUP-PROCESS")) goToHome()
        } else if (userJsonSignUpData != "") {
            signUpResponse = Gson().fromJson(
                userJsonSignUpData,
                com.saaei12092021.office.model.responseModel.SignUpResponse.User::class.java
            ) as com.saaei12092021.office.model.responseModel.SignUpResponse.User
            if (!isVerified) {
                val intent = Intent(this, VerificationActivity::class.java)
                intent.putExtra(Constant.VERIFICATION_MODE, Constant.NEW_REGISTER)
                intent.putExtra(Constant.PHONE_NUMBER, signUpResponse.phone)
                startActivity(intent)
                finish()
            } else
                goToHome()
        } else goToHome()
    }

    private fun goToHome() {

        val tokenGeneratedDate = Constant.getTokenGenerateTime(this)
        //  "Thu Mar 15 01:26:51 GMT+03:00 2022" // i used this date for test
        val datAsLong1 = Date(tokenGeneratedDate)

        // if we wand to display the date time with format
        // val sdf1 = SimpleDateFormat("dd/MM/yyyy")
        // val date1 = sdf1.format(datAsLong1)

        val currentTime = Calendar.getInstance().time.toString()
        val datAsLong2 = Date(currentTime)

        // if we wand to display the date time with format
        // val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        //val date2 = sdf2.format(datAsLong2)

        // Finding the absolute difference between  // the two dates.time (in milli seconds)
        val mDifference = kotlin.math.abs(datAsLong2.time - datAsLong1.time)
        val mDifferenceDates =
            mDifference / (24 * 60 * 60 * 1000) // Converting milli seconds to dates

        Log.d("compare_", mDifferenceDates.toString())

        if ((mDifferenceDates < 5) or (Constant.getToken(this) == "")) {
//            Constant.editor(this@SplashActivity).apply {
//                putString(Constant.TOKEN_GENERATE_DATE, "Thu Mar 15 01:26:51 GMT+03:00 2022")
//                apply()
//            }
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("adsIdFromDeepLink", adsIdFromDeepLink)
                overridePendingTransition(0, 0)
                finish()
            })
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java).apply {
                overridePendingTransition(0, 0)
                finish()
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            //  finish()
            displaySplashScreen()
        }
    }
}