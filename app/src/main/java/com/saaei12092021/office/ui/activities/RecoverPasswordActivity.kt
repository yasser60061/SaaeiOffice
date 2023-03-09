package com.saaei12092021.office.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.databinding.ActivityRecoverPasswordBinding
import com.saaei12092021.office.model.requestModel.ForgetPasswordByPhoneRequest
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import java.util.*

class RecoverPasswordActivity : AppCompatActivity() {

    lateinit var viewModel: MyViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var phoneNumber: String
    lateinit var binding: ActivityRecoverPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        loadingBar = ProgressDialog(this)
        setUpLanguageViewAndDirection()

        binding.countryCodePicker.isClickable = false

        binding.recoverPasswordBtn.setOnClickListener {
            phoneNumber = binding.phoneNumberEt.text.toString()
            if (TextUtils.isEmpty(phoneNumber)) binding.phoneNumberEt.error = "اكتب رقم الجوال"
            else if (phoneNumber.length < 9) binding.phoneNumberEt.error = "ادخل رقم هاتف صحيح"
            else
                viewModel.recoverPassword(ForgetPasswordByPhoneRequest("+966$phoneNumber"))
        }

        binding.backLinear.setOnClickListener {
            finish()
        }


        viewModel.error.observe(this, Observer {
            Toast.makeText(
                this@RecoverPasswordActivity,
                it.toString(),
                Toast.LENGTH_LONG
            ).show()
            loadingBar!!.dismiss()
        })

        viewModel.recoverPassword.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري تفعيل الطلب")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }
                is Resource.Success -> {
                    val intent = Intent(this@RecoverPasswordActivity, VerificationActivity::class.java)
                    intent.putExtra(Constant.VERIFICATION_MODE, Constant.FORGET_PASSWORD)
                    intent.putExtra(Constant.PHONE_NUMBER, "+966$phoneNumber")
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
    //    }
    }
}