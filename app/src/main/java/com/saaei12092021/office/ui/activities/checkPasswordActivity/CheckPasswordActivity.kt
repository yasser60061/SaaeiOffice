package com.saaei12092021.office.ui.activities.checkPasswordActivity

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
import com.saaei12092021.office.databinding.ActivityCheckPasswordBinding
import com.saaei12092021.office.model.requestModel.CheckPasswordRequest
import com.saaei12092021.office.ui.activities.newPhoneAvtivity.NewPhoneActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import java.util.*

class CheckPasswordActivity : AppCompatActivity() {

    lateinit var viewModel: CheckPasswordViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var password: String
    lateinit var token: String
    lateinit var binding: ActivityCheckPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = Constant.getToken(this)
        viewModel = ViewModelProvider(this).get(CheckPasswordViewModel::class.java)
        loadingBar = ProgressDialog(this)
        setUpLanguageViewAndDirection()

        binding.checkPasswordBtn.setOnClickListener {
            password = binding.checkPasswordEt.text.toString()
            if (TextUtils.isEmpty(password)) binding.checkPasswordEt.error = "أدخل كلمة المرور"
            else
                viewModel.checkPassword(token, CheckPasswordRequest(password))
        }

        binding.backIv.setOnClickListener {
            finish()
        }

        viewModel.error.observe(this, Observer
        {
            Toast.makeText(
                this,
                it.toString(),
                Toast.LENGTH_LONG
            ).show()
            loadingBar!!.dismiss()
        })

        viewModel.checkPasswordLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري التحقق")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }
                is Resource.Success -> {
                    val intent = Intent(this, NewPhoneActivity::class.java)
                    startActivity(intent)
                    finish()
                    //overridePendingTransition(0, 0)
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