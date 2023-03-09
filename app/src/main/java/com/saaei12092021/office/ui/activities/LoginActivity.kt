package com.saaei12092021.office.ui.activities

//import com.saaei12092021.office.util.Constant.MY_PROFILE_IMAGE
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.saaei12092021.office.databinding.ActivityLoginBinding
import com.saaei12092021.office.model.requestModel.SignInRequest
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.IS_VERIFIED
import com.saaei12092021.office.util.Constant.LOGIN_RESPONSE
import com.saaei12092021.office.util.Constant.TOKEN
import com.saaei12092021.office.util.Constant.TOKEN_GENERATE_DATE
import com.saaei12092021.office.util.Constant.USER_ID
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.util.Constant.USER_TYPE
import com.saaei12092021.office.util.showCustomToast
import java.util.*

class LoginActivity : AppCompatActivity() {

    lateinit var userViewModel: UserViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var phoneNumber: String
    lateinit var password: String
    var fcmToken = ""

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constant.setUpStatusBar(this)
        // overridePendingTransition(0, 0)
        checkIfGetFirebaseTokenOrNot()

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)
        binding.countryCodePicker.isClickable = false

        binding.createAccountTv.paintFlags =
            binding.createAccountTv.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        binding.skipTx.paintFlags = binding.skipTx.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.forgetPasswordTv.paintFlags =
            binding.forgetPasswordTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        setUpLanguageViewAndDirection()

        binding.loginBtn.setOnClickListener {
            phoneNumber = binding.phoneNumberEt.text.toString()
            password = binding.passwordEt.text.toString()
            if (TextUtils.isEmpty(phoneNumber)) binding.phoneNumberEt.error = "اكتب رقم الجوال"
            else if (TextUtils.isEmpty(password)) binding.passwordEt.error = "ادخل كلمة المرور"
            else if (phoneNumber.length < 9) binding.phoneNumberEt.error = "ادخل رقم هاتف صحيح"
            else if (password.length < 6) binding.passwordEt.error = "ادخل ست ارقام على الاقل"
            else
                userViewModel.signInUser(SignInRequest("+966$phoneNumber", password, fcmToken, ""))
        }

        binding.forgetPasswordTv.setOnClickListener {
            val intent = Intent(this@LoginActivity, RecoverPasswordActivity::class.java)
            startActivity(intent)
        }

        userViewModel.errorLive.observe(this, Observer {
            Toast(this).showCustomToast("كلمة المرور أو رقم الجوال غير صحيح", this)
            loadingBar!!.dismiss()
        })

        userViewModel.signInUserLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري تسجيل الدخول")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }
                is Resource.Success -> {
                    var isVerified: Boolean = false
                    if (it.data?.isVerified != null)
                        isVerified = it.data.isVerified
                    if (it.data?.user != null)
                        if (it.data.user.isVerified != null)
                            isVerified = it.data.user.isVerified
                    loadingBar!!.dismiss()
                    if (!isVerified) {
                        val intent = Intent(this, VerificationActivity::class.java)
                        intent.putExtra(Constant.VERIFICATION_MODE, Constant.NEW_REGISTER)
                        intent.putExtra(Constant.PHONE_NUMBER, "+966$phoneNumber")
                        startActivity(intent)
                        finish()
                    } else
                        if (it.data != null)
                            if (it.data.user.type != "USER") {
                                val currentTime = Calendar.getInstance().time.toString()
                                Constant.editor(this).apply {
                                    clear()
                                    apply()
                                    putString(USER_TYPE, it.data.user.type)
                                    putString(USER_ID, it.data.user.id.toString())
                                    putString(TOKEN, it.data.token)
                                    putString(TOKEN_GENERATE_DATE, currentTime)
                                    putBoolean(IS_VERIFIED, true)
                                    putString(LOGIN_RESPONSE, Gson().toJson(it.data.user))
                                    apply()
                                    Log.d("signin_resp", Gson().toJson(it.data.user))
                                    Log.d("current-time", currentTime)
                                }
                                goToHomeAndFinish()
                            } else {
                                Toast(this).showCustomToast(
                                    "ليس لديك صلاحيات الدخول لتطبيق المكتب",
                                    this
                                )
                                loadingBar!!.dismiss()
                            }
                }
                is Resource.Error -> {
                    loadingBar!!.dismiss()
                }
            }
        })

        binding.createAccountTv.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            //  overridePendingTransition(0, 0)
        }

        binding.skipRl.setOnClickListener {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            //  overridePendingTransition(0, 0)
        }
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.skipIv.rotation = 180F
        //   }
    }

    private fun goToHomeAndFinish() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun checkIfGetFirebaseTokenOrNot() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("token_failed", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            fcmToken = task.result
            Constant.editor(this).apply {
                putString(Constant.FCM_TOKEN, fcmToken)
                apply()
            }
            // Log and toast
            Log.d("the_fcm_token", fcmToken)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }
}