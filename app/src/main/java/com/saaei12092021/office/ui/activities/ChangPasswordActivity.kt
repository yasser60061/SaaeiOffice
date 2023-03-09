package com.saaei12092021.office.ui.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.databinding.ActivityChangPasswordBinding
import com.saaei12092021.office.model.requestModel.UpdatePasswordRequest
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.makeToastMessage
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.util.showCustomToast

class ChangPasswordActivity : AppCompatActivity() {

    lateinit var userViewModel: UserViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var oldPassword: String
    lateinit var newPassword: String
    lateinit var confirmedNewPassword: String
    var token: String? = null

    lateinit var binding: ActivityChangPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)

        token = Constant.getToken(this)
        setUpLanguageViewAndDirection()

        binding.saveBtn.setOnClickListener {
            oldPassword = binding.oldPasswordEt.text.toString()
            newPassword = binding.newPasswordEt.text.toString()
            confirmedNewPassword = binding.confirmNewPasswordEt.text.toString()
            when {
                TextUtils.isEmpty(oldPassword) -> binding.oldPasswordEt.error = "أكتب كلمة المرور"
                TextUtils.isEmpty(newPassword) -> binding.newPasswordEt.error =
                    "أكتب كلمة المرور الجديدة"
                TextUtils.isEmpty(confirmedNewPassword) -> binding.confirmNewPasswordEt.error =
                    " أعد كتابة كلمة المرور الجديدة"
                newPassword.length < 8 -> binding.newPasswordEt.error =
                    "يجب ان تكون كلمة المرور اكبر من 8 خانات"
                confirmedNewPassword != newPassword -> Toast.makeText(
                    this@ChangPasswordActivity,
                    "كلمة المرور غير متاطبقة",
                    Toast.LENGTH_LONG
                ).show()
                (!Constant.DIGIT_PASSWORD_PATTERN.matcher(newPassword).matches()) -> makeToastMessage(
                    this,
                    "يجب ان تحتوي كلمة المرور على ارقام"
                )
                (!Constant.LETTER_PASSWORD_PATTERN.matcher(newPassword).matches()) -> makeToastMessage(
                    this,
                    "يجب ان تحتوي كلمة المرور على حرف بالانجليزية"
                )
                else -> {
                        userViewModel.updatePassword(
                            token!!,
                            Constant.getMyLanguage(this) ,
                            UpdatePasswordRequest(oldPassword, newPassword)
                        )
                }
            }
        }

        binding.backLinear.setOnClickListener {
            finish()
        }

        userViewModel.errorLive.observe(this, Observer {
            if (it != null) makeToastMessage(this, it.toString())
            loadingBar!!.dismiss()
        })

        userViewModel.updatePasswordLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري تحديث كلمة المرور")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }
                is Resource.Success -> {
                    Toast(this).showCustomToast("تم تحديث كلمة المرور", this)
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
//        }
    }
}