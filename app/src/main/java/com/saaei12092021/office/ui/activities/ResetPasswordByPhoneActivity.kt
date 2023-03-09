package com.saaei12092021.office.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.databinding.ActivityResetPasswordByPhoneBinding
import com.saaei12092021.office.model.requestModel.ResetPasswordByPhoneRequest
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.makeToastMessage
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.util.showCustomToast

class ResetPasswordByPhoneActivity : AppCompatActivity() {

    lateinit var userViewModel: UserViewModel
    private var loadingBar: ProgressDialog? = null
    private var phoneNumber: String? = null
    lateinit var password: String
    lateinit var confirmPassword: String
    lateinit var binding: ActivityResetPasswordByPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordByPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)
        phoneNumber = intent.getStringExtra(Constant.PHONE_NUMBER).toString()

        setUpLanguageViewAndDirection()

        binding.recoverPasswordBtn.setOnClickListener {
            password = binding.passwordEt.text.toString()
            confirmPassword = binding.confirmPasswordEt.text.toString()
            when {
                TextUtils.isEmpty(password) -> binding.passwordEt.error = "اكتب كلمة المرور"
                TextUtils.isEmpty(confirmPassword) -> binding.confirmPasswordEt.error =
                    "أعد كتابة كلمة المرور"
                password != confirmPassword -> makeToastMessage(
                    this,
                    "كلمتي المرور غير متطابقتين"
                )
                (password.length < 8 ) -> makeToastMessage(
                    this,
                    "يجب ان تكون كلمة المرور اكبر من 8 خانات"
                )

                (!Constant.DIGIT_PASSWORD_PATTERN.matcher(password).matches()) -> makeToastMessage(
                    this,
                    "يجب ان تحتوي كلمة المرور على ارقام"
                )
                (!Constant.LETTER_PASSWORD_PATTERN.matcher(password).matches()) -> makeToastMessage(
                    this,
                    "يجب ان تحتوي كلمة المرور على حرف بالانجليزية"
                )

                else -> userViewModel.resetPasswordByPhone(
                    myLang = "ar",
                    ResetPasswordByPhoneRequest(
                        password,
                        phoneNumber!!
                    )
                )
            }
        }

        binding.backLinear.setOnClickListener {
            val intent = Intent(this@ResetPasswordByPhoneActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        userViewModel.errorLive.observe(this, Observer {
            makeToastMessage(this@ResetPasswordByPhoneActivity, it.toString())
            loadingBar!!.dismiss()
        })

        userViewModel.resetPasswordByPhoneLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري تفعيل الطلب")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }

                is Resource.Success -> {
                    val intent =
                        Intent(this@ResetPasswordByPhoneActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                    Toast(this).showCustomToast("تم تغيير كلمة المرور بنجاح", this)
                }

                is Resource.Error -> loadingBar!!.dismiss()
            }
        })
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //    }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ResetPasswordByPhoneActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}