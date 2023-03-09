package com.saaei12092021.office.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.ActivitySupervisorProfileBinding
import com.saaei12092021.office.model.requestModel.CompleteProfileRequest
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SupervisorProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivitySupervisorProfileBinding
    lateinit var userViewModel: UserViewModel
    lateinit var fullname: String
    lateinit var phone: String
    lateinit var email: String
    var city: Int = 0
    var completeProfile: Boolean = true
    lateinit var img: String
    private var imageProfileFile: File? = null
    private lateinit var compressedImageFile: File
    lateinit var myLang: String
    var agencyType = "SUPERVISOR"

    // this list and variable for som operation only
    private var resultUri: Uri = Uri.EMPTY
    private var loadingBar: ProgressDialog? = null

    //   lateinit var loginUserResponse: User
    var getUserResponse: GetUserResponse? = null
    lateinit var token: String
    lateinit var myLanguage: String
    lateinit var country: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupervisorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)
        myLang = Constant.getMyLanguage(this)
        country = Constant.getMyCountryId(this)

        token = Constant.getToken(this)
        myLang = Constant.getMyLanguage(this)
        setUpLanguageViewAndDirection()
        displayOfficeInfoIfExist()

        binding.backLinear.setOnClickListener {
            finish()
        }

        binding.officeLogoIv.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                uploadProfileImage()
            else
                GeneralFunctions.pickFromGallery(this)
        }

        binding.completeProfileBtn.setOnClickListener {
            fullname = binding.supervisorNameEt.text.toString()
            phone = binding.phoneNumberEt.text.toString()
            email = binding.emailAddressEt.text.toString()

            when {
                TextUtils.isEmpty(fullname) -> binding.supervisorNameEt.error = "اكتب اسم المشرف"
                TextUtils.isEmpty(email) -> binding.emailAddressEt.error = "اكتب الايميل"
                !GeneralFunctions.isValidEmail2(binding.emailAddressEt.text.toString()) -> binding.emailAddressEt.error =
                    "أدخيل إيميل صالح"
                else -> {
                    userViewModel.updateProfile(
                        CompleteProfileRequest(
                            country = country,
                            phone = phone,
                            fullname = fullname,
                            email = email,
                        //    type = agencyType,
                            city = getUserResponse!!.city.id,
                            completeProfile = completeProfile,
                            workCity = ArrayList(),
                            workArea = ArrayList(),
                            workCategory = ArrayList(),
                            workSubCategory = ArrayList(),
                       //     idType = 3,
                            idNumber = null,
                            adNumber = null
                        ),
                        myLang = myLang,
                        token = token,
                        userId = getUserResponse!!.id.toString()
                    )
                    // for delete later
                    val jsonString = Gson().toJson(
                        CompleteProfileRequest(
                            country = country,
                            phone = phone,
                            fullname = fullname,
                            email = email,
                      //      type = agencyType,
                            city = getUserResponse!!.city.id,
                            completeProfile = completeProfile,
                            workCity = ArrayList(),
                            workArea = ArrayList(),
                            workCategory = ArrayList(),
                            workSubCategory = ArrayList(),
                         //   idType = 3,
                            idNumber = null,
                            adNumber = null
                        )
                    )
                    val jsonData: JSONObject = JSONObject(jsonString)
                    Log.d("update_profile_sent", jsonData.toString())
                }
            }
            userViewModel.errorLive.observe(this, Observer {
                if (it != "") {
                    if (it.toString().length < 70)
                        Toast.makeText(
                            this,
                            it.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    loadingBar!!.dismiss()
                    userViewModel.errorLive
                        .postValue("")
                }
            })

            userViewModel.completeOrUpdateProfileLive.observe(this, Observer
            {
                when (it) {
                    is Resource.Loading -> {
                        loadingBar!!.setMessage("جاري الحفظ")
                        loadingBar!!.setCanceledOnTouchOutside(false)
                        loadingBar!!.show()
                    }
                    is Resource.Success -> {
                        val currentTime = Calendar.getInstance().time.toString()
                        Constant.editor(this).apply {
                            putString(Constant.USER_ID, it.data?.user?.id.toString())
                            putString(Constant.TOKEN, it.data?.token)
                            putString(Constant.TOKEN_GENERATE_DATE, currentTime)
                            putString(Constant.LOGIN_RESPONSE, Gson().toJson(it.data?.user!!))
                            apply()
                            Log.d("signin_resp", Gson().toJson(it.data.user))
                            Log.d("signin_token", it.data.token)
                        }
                        Toast.makeText(
                            this,
                            "تم حفظ البيانات",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        finish()
                    }
                    is Resource.Error ->
                        loadingBar!!.dismiss()
                }
            })

        }

    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
        //   if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //  }
    }

    private fun displayOfficeInfoIfExist() {

        if (intent.getSerializableExtra("getUserResponse") != null) {
            getUserResponse = intent.getSerializableExtra("getUserResponse") as GetUserResponse

            if (!getUserResponse?.img.isNullOrEmpty()) {
                Glide.with(this).load(getUserResponse!!.img).placeholder(R.drawable.profile_image)
                    .into(binding.officeLogoIv)
            }

            binding.supervisorNameEt.setText(getUserResponse!!.fullname)
            binding.phoneNumberEt.setText(getUserResponse!!.phone)
            binding.emailAddressEt.setText(getUserResponse!!.email)
            binding.phoneNumberEt.isEnabled = false

        }
    }

    private fun uploadProfileImage() {
        CropImage.activity(resultUri)
            .setAspectRatio(1, 1)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((resultCode == Activity.RESULT_OK) && (data != null)) {
            when (requestCode) {
                Constant.IMAGE_FILE_CODE -> {
                    resultUri = data.data!!
                    launchImageCrop(resultUri)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    imageProfileFile = File(resultUri.path!!)

                    userViewModel.logoImageFile = imageProfileFile
                    binding.officeLogoIv.setImageURI(Uri.fromFile(userViewModel.logoImageFile))
                    resultUri = Uri.EMPTY
                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .start(this)
    }
}