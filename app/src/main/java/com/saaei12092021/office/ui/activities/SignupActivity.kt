package com.saaei12092021.office.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.ActivitySignUpBinding
import com.saaei12092021.office.databinding.BottomSheetForChooseFileTypeInChatBinding
import com.saaei12092021.office.ui.BottomSheetForConditionAndTerms
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.*
import com.saaei12092021.office.util.Constant.COMMERCIAL_FILE_CODE
import com.saaei12092021.office.util.Constant.IMAGE_FILE_CODE
import com.saaei12092021.office.util.Constant.NEW_REGISTER
import com.saaei12092021.office.util.Constant.PHONE_NUMBER
import com.saaei12092021.office.util.Constant.VERIFICATION_MODE
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SignupActivity : AppCompatActivity() {

    lateinit var userViewModel: UserViewModel
    private var loadingBar: ProgressDialog? = null
    lateinit var country: String
    lateinit var officeName: String
    lateinit var phoneNumber: String
    lateinit var email: String
    lateinit var password: String
    lateinit var img: String
    private lateinit var compressedImageFile: File
    private var imageProfileFile: File? = null
    private var resultUri: Uri = Uri.EMPTY
    lateinit var pickerType: String
    lateinit var myLang: String
    var idType: Int = 3
    var idNumber: String? = null
    var adNumber: Int? = null
    var suffixOfFile = ""
    var agencyType = "OFFICE"  // COMPANY','OFFICE','FREEWORK'
    private var termsContent = ""

    lateinit var binding: ActivitySignUpBinding

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .getIntent(this@SignupActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Constant.setUpStatusBar(this)
        //  overridePendingTransition(0, 0)

        binding.loginTv.paintFlags = binding.loginTv.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        binding.skipTx.paintFlags = binding.skipTx.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        binding.conditionAndTermsTv.paintFlags =
            binding.skipTx.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)
        myLang = Constant.getMyLanguage(this)
        setUpLanguageViewAndDirection()

        userViewModel.getTerms(Constant.getMyLanguage(this))
        userViewModel.getTermsLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    termsContent = it.data!!.Termss[0].terms
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                imageProfileFile = File(uri.path!!)
                //  resultUri = Uri.EMPTY
                if (pickerType == "officeLogo") {
                    userViewModel.logoImageFile = imageProfileFile
                    binding.officeLogoIv.setImageURI(uri)
                    binding.imageNameTv.text = userViewModel.logoImageFile!!.name
                    //  Toast(this).showCustomToast(uri.toString(),this)
                }
            }
        }

        binding.freeWorkRb.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.commercialRegisterRl.visibility = View.GONE
                binding.commercialFileTitle.visibility = View.GONE
                binding.commercialNumberTitle.text = "رقم الهوية"
            } else {
                binding.commercialRegisterRl.visibility = View.VISIBLE
                binding.commercialFileTitle.visibility = View.VISIBLE
                binding.commercialNumberTitle.text = "رقم السجل التجاري"
            }
        }
        binding.officeLogoIv.setOnClickListener {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                cropActivityResultLauncher.launch(null)
            //  cropActivityResultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) // use this in other case
            //  uploadProfileImage() // when i use on activity result
            else GeneralFunctions.pickFromGallery(this)
            pickerType = "officeLogo"
        }

        binding.commercialRegisterRl.setOnClickListener {
            bottomSheetForChooseFileType()
            pickerType = "commercial"
        }

        binding.commercialRegisterEt.setOnClickListener {
            bottomSheetForChooseFileType()
            pickerType = "commercial"
        }

        binding.registerBtn.setOnClickListener {
            if (binding.companyRb.isChecked)
                agencyType = "COMPANY"
            if (binding.officeRb.isChecked)
                agencyType = "OFFICE"
            if (binding.freeWorkRb.isChecked)
                agencyType = "FREEWORK"

            idNumber = null
            adNumber = null
            officeName = binding.officeNameEt.text.toString()
            phoneNumber = binding.phoneNumberEt.text.toString()
            password = binding.passwordEt.text.toString()
            email = binding.emailAddressEt.text.toString()
            if (!TextUtils.isEmpty(binding.idNumberEt.text.toString()))
                idNumber = (binding.idNumberEt.text.toString())
            if (!TextUtils.isEmpty(binding.adNumberEt.text.toString()))
                adNumber = Integer.parseInt(binding.adNumberEt.text.toString())

            if (TextUtils.isEmpty(officeName)) binding.officeNameEt.error = "اكتب اسم المكتب"
            else if (TextUtils.isEmpty(phoneNumber)) binding.phoneNumberEt.error = "اكتب رقم الجوال"
            else if (TextUtils.isEmpty(password)) binding.passwordEt.error = "ادخل كلمة المرور"
            else if (password.length < 8) binding.passwordEt.error = "ادخل ثماني ارقام على الاقل"
            else if (!Constant.DIGIT_PASSWORD_PATTERN.matcher(password)
                    .matches()
            ) binding.passwordEt.error = "يجب ان تحتوي كلمة المرور على ارقام"
            else if (!Constant.LETTER_PASSWORD_PATTERN.matcher(password)
                    .matches()
            ) binding.passwordEt.error = "يجب ان تحتوي كلمة المرور على حرف بالانجليزية"
            else if (TextUtils.isEmpty(email)) binding.emailAddressEt.error = "اكتب الايميل"
            else if (!GeneralFunctions.isValidEmail2(binding.emailAddressEt.text.toString())) Toast(
                this
            ).showCustomToast(
                "أدخل إيميل صالح",
                this
            )
            else if ((idNumber == null) and (agencyType != "FREEWORK")) binding.idNumberEt.error = "ادخل رقم السجل التجاري"
            else if ((idNumber == null) and (agencyType == "FREEWORK"))
                binding.idNumberEt.error = "ادخل رقم الهوية"
            else if (adNumber == null) binding.adNumberEt.error = "ادخل رقم المعلن"
            else if (phoneNumber.length < 9) binding.phoneNumberEt.error = "ادخل رقم هاتف صحيح"
            else if ((userViewModel.commercialFile == null) and ((agencyType == "OFFICE") or (agencyType == "COMPANY")))
                Toast.makeText(
                this@SignupActivity,
                "الرجاء ارفاق الملف التجاري",
                Toast.LENGTH_LONG
            ).show()
            else if (!binding.conditionAndTermsCb.isChecked) Toast(this).showCustomToast(
                "الرجاء الموافقة على الشروط والأحكام",
                this
            )
            else{
                if (agencyType == "FREEWORK"){
                    userViewModel.commercialFile = null
                    idType = 1
                } else idType = 3

                userViewModel.signUpUser(
                    myLang = myLang,
                    country = "1",
                    fullname = officeName,
                    phone = "+966$phoneNumber",
                    email = email,
                    type = "OFFICE" ,
                    agencyType = agencyType,
                    password = password,
                    idType = idType,
                    idNumber = idNumber,
                    adNumber = adNumber
                )
        }}

        userViewModel.errorLive.observe(this, Observer {
            if (it != "") {
                Toast.makeText(
                    this@SignupActivity,
                    it.toString(),
                    Toast.LENGTH_LONG
                ).show()
                loadingBar!!.dismiss()
                userViewModel.errorLive.postValue("")
            }
        })

        userViewModel.userSignUpLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    loadingBar!!.setMessage("جاري انشاء الحساب")
                    loadingBar!!.setCanceledOnTouchOutside(false)
                    loadingBar!!.show()
                }
                is Resource.Success -> {
                    Constant.editor(this).apply {
                        clear()
                        apply()
                        putString(Constant.SIGNUP_RESPONSE, Gson().toJson(it.data?.user!!))
                        putString(Constant.USER_ID, it.data.user.id.toString())
                        putBoolean(Constant.IS_VERIFIED, it.data.user.isVerified)
                        apply()
                        Log.d("signup_resp", Gson().toJson(it.data.user))
                    }
                    val intent = Intent(this@SignupActivity, VerificationActivity::class.java)
                    intent.putExtra(VERIFICATION_MODE, NEW_REGISTER)
                    intent.putExtra(PHONE_NUMBER, "+966$phoneNumber")
                    startActivity(intent)
                    finish()
                    //overridePendingTransition(0, 0)
                }
                is Resource.Error ->
                    loadingBar!!.dismiss()
            }
        })

        binding.loginTv.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            //  overridePendingTransition(0, 0)
        }

        binding.conditionAndTermsTv.setOnClickListener {
            BottomSheetForConditionAndTerms(this, termsContent)
        }

        binding.skipRl.setOnClickListener {
            val intent = Intent(this@SignupActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.skipIv.rotation = 180F
        //  }
    }

    private fun uploadProfileImage() {
        CropImage.activity(resultUri)
            .setAspectRatio(1, 1)
            .start(this@SignupActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((resultCode == Activity.RESULT_OK) && (data != null)) {
            when (requestCode) {
                COMMERCIAL_FILE_CODE -> {
                    tryOpenDocument(requestCode, resultCode, data)
                }
                IMAGE_FILE_CODE -> {
                    resultUri = data.data!!
                    launchImageCrop(resultUri)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    imageProfileFile = File(resultUri.path!!)
                    userViewModel.logoImageFile = imageProfileFile
                    binding.officeLogoIv.setImageURI(resultUri)
                    binding.imageNameTv.text = userViewModel.logoImageFile!!.name
                    resultUri = Uri.EMPTY
                }
            }
        }
    }

    private fun tryOpenDocument(requestCode: Int, resultCode: Int, data: Intent?) {
        when (val openFileResult =
            this@SignupActivity.tryHandleOpenDocumentResult(requestCode, resultCode, data)) {
            is OpenFileResult.FileWasOpened -> {
                userViewModel.commercialFile = getFileFromInputStream(openFileResult.content)
                if (pickerType == "commercial")
                    binding.commercialRegisterEt.text = userViewModel.commercialFile!!.name
            }
            OpenFileResult.DifferentResult -> {}
            OpenFileResult.ErrorOpeningFile -> {}
            OpenFileResult.OpenFileWasCancelled -> {}
        }
    }

    private fun getFileFromInputStream(inputStream: InputStream): File {
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        val tempFile =
            File.createTempFile("commercial", suffixOfFile, this@SignupActivity?.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        outputStream.write(buffer)
        inputStream.close()
        outputStream.close()
        return tempFile
    }

    private fun bottomSheetForChooseFileType() {
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_choose_file_type_in_chat)
        val binding: BottomSheetForChooseFileTypeInChatBinding =
            BottomSheetForChooseFileTypeInChatBinding.inflate(
                LayoutInflater.from(this),
                null,
                false
            )
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()

        binding.chooseVideoFileLinear.visibility = View.GONE
        binding.chooseImageLinear.setOnClickListener {
            openDocumentAndPickImageOrPdf(Constant.COMMERCIAL_FILE_CODE, "image/*")
            suffixOfFile = ".jpg"
            bottomSheetDialog.dismiss()
        }

        binding.choosePdfLinear.setOnClickListener {
            openDocumentAndPickImageOrPdf(Constant.COMMERCIAL_FILE_CODE, "application/pdf")
            suffixOfFile = ".pdf"
            bottomSheetDialog.dismiss()
        }

    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .start(this)
    }

}