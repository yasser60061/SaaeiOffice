package com.saaei12092021.office.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.databinding.ActivityVerificationBinding
import com.saaei12092021.office.model.requestModel.VerificationUserRequest
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.FORGET_PASSWORD
import com.saaei12092021.office.util.Constant.IS_VERIFIED
import com.saaei12092021.office.util.Constant.NEW_REGISTER
import com.saaei12092021.office.util.Constant.PHONE_NUMBER
import com.saaei12092021.office.util.Constant.VERIFICATION_MODE
import com.saaei12092021.office.util.Constant.makeToastMessage
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import java.util.*

class VerificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityVerificationBinding
    lateinit var viewModel: MyViewModel
    private var loadingBar: ProgressDialog? = null
    private var phoneNumber: String? = null
    private var newPhoneNumber: String? = null
    var verificationMode: String? = null
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        loadingBar = ProgressDialog(this)

        verificationMode = intent.getStringExtra(VERIFICATION_MODE).toString()
        binding.counterTv.paintFlags = binding.counterTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        startDownCounter()
        GeneralFunctions.hideKeyboard(this)

        setUpLanguageViewAndDirection()

        //  makeToastMessage(this, verificationMode!!)
        binding.backRl.setOnClickListener {
            checkVerificationModeToFinishAndWhereToGo()
        }

        viewModel.error.observe(this, Observer {
            makeToastMessage(this@VerificationActivity, it.toString())
            loadingBar!!.dismiss()
        })
//----------------------------------------------------------------------------------------------
        if (verificationMode.equals(NEW_REGISTER)) {
            phoneNumber = intent.getStringExtra(PHONE_NUMBER).toString()
            binding.phoneNumberTv.text = phoneNumber
            viewModel.verificationUser.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> {
                        loadingBar!!.setMessage("جاري تفعيل الحساب")
                        loadingBar!!.setCanceledOnTouchOutside(false)
                        loadingBar!!.show()
                    }
                    is Resource.Success -> {
                        //  makeToastMessage(this@VerificationActivity, it.data?.success.toString())
                        Constant.editor(this).apply{
                            putBoolean(IS_VERIFIED , true)
                            apply()
                        }
                        val intent = Intent(
                            this@VerificationActivity,
                            AccountUnderReviewActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    }
                    is Resource.Error -> loadingBar!!.dismiss()
                }
            })
        }

        //----------------------------------------------------------------------------------------------
        if (verificationMode.equals(FORGET_PASSWORD)) {
            phoneNumber = intent.getStringExtra(PHONE_NUMBER).toString()
            binding.phoneNumberTv.text = phoneNumber
            viewModel.verificationUser.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> {
                        loadingBar!!.setMessage("استعادة كلمة المرور ..")
                        loadingBar!!.setCanceledOnTouchOutside(false)
                        loadingBar!!.show()
                    }
                    is Resource.Success -> {
                        //  makeToastMessage(this@VerificationActivity, it.data?.success.toString())
                        val intent = Intent(
                            this@VerificationActivity,
                            ResetPasswordByPhoneActivity::class.java
                        )
                        intent.putExtra(PHONE_NUMBER, phoneNumber)
                        startActivity(intent)
                        finish()
                    }
                    is Resource.Error -> loadingBar!!.dismiss()
                }
            })
        }
//----------------------------------------------------------------------------------------------
        // for delete later
//        if (verificationMode.equals(CHANGE_PHONE_NUMBER)) {
//            phoneNumber = intent.getStringExtra(CURRENT_PHONE_NUMBER).toString()
//            newPhoneNumber = intent.getStringExtra(NEW_PHONE_NUMBER).toString()
//            token = intent.getStringExtra("token").toString()
//            binding.phoneNumberTv.setText(newPhoneNumber)
//            viewModel.updatePhone.observe(this, Observer {
//                when (it) {
//                    is Resource.Loading -> {
//                        loadingBar!!.setMessage("جار الحفظ")
//                        loadingBar!!.setCanceledOnTouchOutside(false)
//                        loadingBar!!.show()
//                    }
//
//                    is Resource.Success -> {
//                        //    makeToastMessage(this@VerificationActivity, it.data?.success.toString())
//                        val intent = Intent(
//                            this@VerificationActivity,
//                            HomeActivity::class.java
//                        )
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        startActivity(intent)
//                        finish()
//                    }
//                    is Resource.Error -> loadingBar!!.dismiss()
//                }
//            })
//        }
//----------------------------------------------------------------------------------------------

        binding.counterTv.setOnClickListener {
            startDownCounter()
        }

        binding.enableBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.codeEt.text.toString())) binding.codeEt.error =
                "اكتب رمز التفعيل"
            else if (binding.codeEt.text.toString().length < 4)
                Toast(this).showCustomToast("اكتب رمز تفعيل صحيح", this)
            else {
                if ((verificationMode.equals(NEW_REGISTER)) or (verificationMode.equals(
                        FORGET_PASSWORD
                    ))
                ) {
                    viewModel.verificationUser(VerificationUserRequest(phoneNumber!!, binding.codeEt.text.toString()))
                }
            }

//            if (verificationMode.equals(CHANGE_PHONE_NUMBER)) {
//                viewModel.updatePhone(
//                    token = token,
//                    UpdatePhoneRequest(phoneNumber!!, newPhoneNumber!!, "0000")
//                )
//            }
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

    private fun checkVerificationModeToFinishAndWhereToGo() {
        if (verificationMode == NEW_REGISTER){
            val intent = Intent(
                this@VerificationActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        } else finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkVerificationModeToFinishAndWhereToGo()
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