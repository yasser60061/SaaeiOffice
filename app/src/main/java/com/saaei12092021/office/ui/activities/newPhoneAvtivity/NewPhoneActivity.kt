package com.saaei12092021.office.ui.activities.newPhoneAvtivity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.databinding.ActivityNewPhoneBinding
import com.saaei12092021.office.ui.activities.UpdatePhoneActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import java.util.*

class NewPhoneActivity : AppCompatActivity() {

    lateinit var viewModel: NewPhoneViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var newPhoneNumber: String
    lateinit var token: String
    private lateinit var binding: ActivityNewPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = Constant.getToken(this)
        viewModel = ViewModelProvider(this).get(NewPhoneViewModel::class.java)
        loadingBar = ProgressDialog(this)
        binding.countryCodePicker.isClickable = false

        setUpLanguageViewAndDirection()

        binding.submitBtn.setOnClickListener {
            newPhoneNumber = binding.newPhoneNumberEt.text.toString()
            if (TextUtils.isEmpty(newPhoneNumber)) binding.newPhoneNumberEt.error = "أدخل رقم الجوال الجديد"
            else if (newPhoneNumber.length < 9) binding.newPhoneNumberEt.error =
                "ادخل رقم جوال صحيح"
            else
                viewModel.requestCodeToNewPhone(token ,"+966$newPhoneNumber")
        }

        binding.backLinear.setOnClickListener {
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

        viewModel.codeToNewPhoneLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري الطلب")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }

                is Resource.Success -> {
                    val intent = Intent(this, UpdatePhoneActivity::class.java)
                    intent.putExtra("newPhoneNumber", "+966$newPhoneNumber")
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
     //   }
    }
}