package com.saaei12092021.office.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.saaei12092021.office.databinding.ActivityUpdatePhoneBinding
import com.saaei12092021.office.model.requestModel.UpdatePhoneRequest
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.util.*

class UpdatePhoneActivity : AppCompatActivity() {

    lateinit var userViewModel: UserViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var currentPhoneNumber: String
    lateinit var newPhoneNumber: String
    lateinit var verificationCode: String
    lateinit var token: String

    lateinit var binding: ActivityUpdatePhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)
        GeneralFunctions.hideKeyboard(this)
        setUpLanguageViewAndDirection()

        newPhoneNumber = intent.getStringExtra("newPhoneNumber").toString()
        binding.newPhoneNumberEt.setText(newPhoneNumber)
        binding.newPhoneNumberEt.setTextColor(Color.parseColor("#21305D"))
        binding.newPhoneNumberEt.isEnabled = false
        token = Constant.getToken(this)
        getAndDisplayCurrentPhoneNumber()
        GeneralFunctions.hideKeyboard(this)

        startDownCounter()

        binding.counterTv.setOnClickListener {
            startDownCounter()
        }
        binding.backLinear.setOnClickListener {
            finish()
        }

        binding.exitBtn.setOnClickListener {
            finish()
        }

        binding.saveBtn.setOnClickListener {
            currentPhoneNumber = binding.currentPhoneNumberEt.text.toString()
            newPhoneNumber = binding.newPhoneNumberEt.text.toString()
            verificationCode = binding.verificationCodeEt.text.toString()
            when {
                TextUtils.isEmpty(currentPhoneNumber) -> binding.currentPhoneNumberEt.error =
                    "اكتب رقم الجوال الحالي"
                TextUtils.isEmpty(newPhoneNumber) -> binding.newPhoneNumberEt.error =
                    "ادخل رقم الجوال الجديد"
                TextUtils.isEmpty(verificationCode) -> binding.verificationCodeEt.error =
                    "أكتب رمز التفعيل"
                newPhoneNumber.length < 4 -> binding.verificationCodeEt.error =
                    "ادخل رقم جوال صحيح"
                else -> {
                    userViewModel.updatePhone(
                        token,
                        UpdatePhoneRequest(currentPhoneNumber, newPhoneNumber, verificationCode)
                    )
                }
            }
        }

        userViewModel.errorLive.observe(this, Observer {
            if (it != null) Constant.makeToastMessage(this, it.toString())
            loadingBar!!.dismiss()
        })

        userViewModel.updatePhoneLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري تحديث رقم الجوال")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }
                is Resource.Success -> {
                    Constant.editor(this).apply {
                        clear()
                        apply()
                    }
                    Toast(this).showCustomToast("تم تحديث رقم الجوال بنجاح", this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
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
   //     }
    }

    private fun getAndDisplayCurrentPhoneNumber() {
        val userJsonLoginData = Constant.getLoginResponse(this)
        when {
            userJsonLoginData != "" -> {
                val loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
                currentPhoneNumber = loginResponse.phone
                binding.currentPhoneNumberEt.setText(currentPhoneNumber)
                binding.currentPhoneNumberEt.setTextColor(Color.parseColor("#21305D"))
                binding.currentPhoneNumberEt.isEnabled = false
                if (currentPhoneNumber == newPhoneNumber) {
                    binding.titleTv.text = "عملية غير مسموحة : الرقمين متطابقين"
                    binding.titleTv.setTextColor(Color.parseColor("#F45656"))
                    binding.counterTv.visibility = View.GONE
                    binding.verificationCodeTitleTv.visibility = View.GONE
                    binding.verificationCodeEt.visibility = View.GONE
                    binding.saveBtn.visibility = View.GONE
                    binding.exitBtn.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun startDownCounter() {
        binding.counterTv.isEnabled = false
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.counterTv.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.counterTv.text = "حاول مرة أخرى" + "(30)"
                binding.counterTv.isEnabled = true
            }
        }.start()
    }
}