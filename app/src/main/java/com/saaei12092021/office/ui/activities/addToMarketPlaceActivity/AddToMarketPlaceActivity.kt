package com.saaei12092021.office.ui.activities.addToMarketPlaceActivity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.adapters.IdImagesAdapter
import com.saaei12092021.office.adapters.LicenseImagesAdapter
import com.saaei12092021.office.databinding.ActivityAddToMarketPlaceBinding
import com.saaei12092021.office.model.requestModel.AddToMarketPlaceRequest
import com.saaei12092021.office.model.responseModel.UploadImagesModel
import com.saaei12092021.office.model.responseModel.getUserByTokenResponse.GetUserByTokenResponse
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddToMarketPlaceActivity : AppCompatActivity(),
    LicenseImagesAdapter.OnImageLicenseItemClickListener,
    LicenseImagesAdapter.OnDeleteLicenseItemClickListener,
    IdImagesAdapter.OnImageIdItemClickListener,
    IdImagesAdapter.OnDeleteIdItemClickListener {

    lateinit var binding: ActivityAddToMarketPlaceBinding
    lateinit var addToMarketPlaceViewModel: AddToMarketPlaceViewModel
    private var idImageFile: File? = null
    private var licenseImageFile: File? = null

    // private var licenseImageFile: File? = null
    private var resultUri: Uri = Uri.EMPTY
    private var documentImageType = ""
    var getUserByTokenResponse: GetUserByTokenResponse? = null
    private var mDateSetListener: OnDateSetListener? = null
    var licenseImagesList: ArrayList<UploadImagesModel> = ArrayList()
    var idImagesList: ArrayList<UploadImagesModel> = ArrayList()
    lateinit var licenseImagesAdapter: LicenseImagesAdapter
    lateinit var idImagesAdapter: IdImagesAdapter
    var currentImagePosition: Int = 0
    var typeOfMarketPlaceRequest = "" // ADD - UPDATE
    var mustClearLicenseImagesUrlToUpdateIt = true
    var mustClearIdentificationImagesUrlToUpdate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToMarketPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addToMarketPlaceViewModel =
            ViewModelProvider(this).get(AddToMarketPlaceViewModel::class.java)

        initialLicenseImageAdapterToStartPick()
        initialIdsImageAdapterToStartPick()
        setUpLanguageViewAndDirection()

        if (licenseImagesList.isNullOrEmpty()) {
            //    val tempImageList : ArrayList<UploadImagesModel> = ArrayList()
            for (i in 1..6) {
                licenseImagesList.add(
                    UploadImagesModel(
                        imageNumber = i,
                        imageName = "",
                        imageFile = null,
                        imageUrlIfUpdate = ""
                    )
                )
            }
            for (i in 1..6) {
                idImagesList.add(
                    UploadImagesModel(
                        imageNumber = i,
                        imageName = "",
                        imageFile = null,
                        imageUrlIfUpdate = ""
                    )
                )
            }
//            licenseImagesList = tempImageList
//            idImagesList = tempImageList
        }

        licenseImagesAdapter.differ.submitList(licenseImagesList)
        idImagesAdapter.differ.submitList(idImagesList)

        if (intent.getSerializableExtra("getUserByTokenResponse") != null) {
            getUserByTokenResponse =
                intent.getSerializableExtra("getUserByTokenResponse") as GetUserByTokenResponse
            if (getUserByTokenResponse!!.user.marketPlace != null)
                if (getUserByTokenResponse!!.user.marketPlace!!.status != null)
                    displayMarketPlace()
        }
        typeOfMarketPlaceRequest = intent.getStringExtra("typeOfMarketPlaceRequest").toString()

        binding.idsRg.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            if (radio.text.contains("هوية")) {
                binding.idIssuingDateTitle.text = "تاريخ إصدار بطاقة الهوية"
                binding.idExpiredDateTitle.text = "تاريخ إنتهاء بطاقة الهوية"
            } else if (radio.text.contains("جواز")) {
                binding.idIssuingDateTitle.text = "تاريخ إصدار جواز السفر"
                binding.idExpiredDateTitle.text = "تاريخ إنتهاء جواز السفر"
            }
        }

        binding.idIssuingDateEt.setOnClickListener {
            showCalenderToChooseDate(binding.idIssuingDateEt)
        }
        binding.idExpiredDateEt.setOnClickListener {
            showCalenderToChooseDate(binding.idExpiredDateEt)
        }
        binding.licenseIssuingDateEt.setOnClickListener {
            showCalenderToChooseDate(binding.licenseIssuingDateEt)
        }
        binding.licenseExpiredDateEt.setOnClickListener {
            showCalenderToChooseDate(binding.licenseExpiredDateEt)
        }

        binding.backLinear.setOnClickListener {
            finish()
        }

        binding.sendBtn.setOnClickListener {
            var idTypeToastMessage = ""
            if (binding.identityCardRb.isChecked)
                idTypeToastMessage = " الهوية مطلوب"
            if (binding.passportRb.isChecked)
                idTypeToastMessage = " جواز السفر مطلوب"
            when {
                TextUtils.isEmpty(binding.ipanEt.text.toString()) -> Toast(this).showCustomToast(
                    "رقم IPan مطلوب",
                    this
                )
                !isValidIban(binding.ipanEt.text.toString().trim()) -> Toast(this).showCustomToast(
                    "رقم IPan غير صالح",
                    this
                )
                TextUtils.isEmpty(binding.brandNumberArEt.text.toString()) -> Toast(this).showCustomToast(
                    "العلامة التجارية باللغة العربية مطلوبة",
                    this
                )
                TextUtils.isEmpty(binding.brandNameEnEt.text.toString()) -> Toast(this).showCustomToast(
                    "العلامة التجارية باللغة الانجليزية مطلوبة",
                    this
                )
                TextUtils.isEmpty(binding.firstNameEt.text.toString()) -> Toast(this).showCustomToast(
                    "الإسم الأول مطلوب",
                    this
                )
                TextUtils.isEmpty(binding.familyNameEt.text.toString()) -> Toast(this).showCustomToast(
                    "إسم العائلة مطلوب",
                    this
                )
                TextUtils.isEmpty(binding.phoneNumberEt.text.toString()) -> Toast(this).showCustomToast(
                    "رقم الجوال مطلوب",
                    this
                )

                binding.phoneNumberEt.text.toString().length < 9 -> Toast(this).showCustomToast(
                    "أدخل رقم هاتف صحيح",
                    this
                )

                TextUtils.isEmpty(binding.emailAddressEt.text.toString()) -> Toast(this).showCustomToast(
                    "الايميل مطلوب",
                    this
                )

                !GeneralFunctions.isValidEmail2(binding.emailAddressEt.text.toString()) -> Toast(this).showCustomToast(
                    "أدخل إيميل صالح",
                    this
                )

                TextUtils.isEmpty(binding.licenseNumberEt.text.toString()) -> Toast(this).showCustomToast(
                    "رقم الترخيص مطلوب",
                    this
                )
                TextUtils.isEmpty(binding.licenseIssuingDateEt.text.toString()) -> Toast(this).showCustomToast(
                    "تاريخ إصدار رقم الترخيص مطلوب",
                    this
                )
                TextUtils.isEmpty(binding.licenseExpiredDateEt.text.toString()) -> Toast(this).showCustomToast(
                    "تاريخ إنتهاء رقم الترخيص مطلوب",
                    this
                )
                ((licenseImageFile == null) and (getUserByTokenResponse?.user?.marketPlace?.entity_imgs.isNullOrEmpty()))
                -> Toast(this).showCustomToast(
                    "يرجى تحميل صور الترخيص",
                    this
                )
                (!binding.identityCardRb.isChecked and !binding.passportRb.isChecked) -> Toast(this).showCustomToast(
                    "إختر نوع الهوية",
                    this
                )

                TextUtils.isEmpty(binding.idIssuingDateEt.text.toString()) -> Toast(this).showCustomToast(
                    "تاريخ إصدار $idTypeToastMessage",
                    this
                )
                TextUtils.isEmpty(binding.idExpiredDateEt.text.toString()) -> Toast(this).showCustomToast(
                    "تاريخ انتهاء $idTypeToastMessage",
                    this
                )
                TextUtils.isEmpty(binding.taxNumberEt.text.toString()) -> Toast(this).showCustomToast(
                    "الرقم الضريبي مطلوب",
                    this
                )
                ((idImageFile == null) and (getUserByTokenResponse?.user?.marketPlace?.identification_imgs.isNullOrEmpty()))
                -> Toast(this).showCustomToast(
                    "يرجى تحميل صور الهوية",
                    this
                )
                else -> {
                    var idType = ""
                    if (binding.identityCardRb.isChecked)
                        idType = "IDENTITY-CARD"
                    else idType = "PASSPORT"
                    addToMarketPlaceViewModel.licenseImagesList = licenseImagesList
                    addToMarketPlaceViewModel.idImagesList = idImagesList
                    val addToMarketPlaceRequest = AddToMarketPlaceRequest(
                        bank_iban = binding.ipanEt.text.toString(),
                        brand_name_ar = binding.brandNumberArEt.text.toString(),
                        brand_name_en = binding.brandNameEnEt.text.toString(),
                        entity_number = binding.licenseNumberEt.text.toString(),
                        entity_ssuing_date = binding.licenseIssuingDateEt.text.toString(),
                        entity_expiry_date = binding.licenseExpiredDateEt.text.toString(),
                        owner_firstname = binding.firstNameEt.text.toString(),
                        owner_lastName = binding.familyNameEt.text.toString(),
                        owner_email = binding.emailAddressEt.text.toString(),
                        owner_phone = "+966${binding.phoneNumberEt.text}",
                        identification_type = idType,
                        identification_issuing_date = binding.idIssuingDateEt.text.toString(),
                        identification_expiry_date = binding.idExpiredDateEt.text.toString(),
                        office = Constant.getUserId(this),
                        tax_number = binding.taxNumberEt.text.toString(),
                    )
                    if (typeOfMarketPlaceRequest == "ADD") {
                        addToMarketPlaceViewModel.addToMarketPlace(
                            Constant.getToken(this),
                            addToMarketPlaceRequest,
                        )

                        addToMarketPlaceViewModel.addToAddToMarketPlaceLive.observe(this, Observer {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.sendBtn.isEnabled = false
                                }
                                is Resource.Success -> {
                                    Toast(this).showCustomToast("تم إستلام الطلب ", this)
                                    finish()
                                }
                                is Resource.Error -> {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    binding.sendBtn.isEnabled = true
                                }
                            }
                        })
                    } else if (typeOfMarketPlaceRequest == "UPDATE") {
                        addToMarketPlaceViewModel.updateMarketPlace(
                            getUserByTokenResponse!!.user.marketPlace?.id.toString(),
                            Constant.getToken(this),
                            addToMarketPlaceRequest,
                        )
                        addToMarketPlaceViewModel.updateMarketPlaceLive.observe(this, Observer {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.sendBtn.isEnabled = false
                                }
                                is Resource.Success -> {
                                    Toast(this).showCustomToast("تم تعديل الطلب ", this)
                                    finish()
                                }
                                is Resource.Error -> {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    binding.sendBtn.isEnabled = true
                                }
                            }
                        })
                    }

                    Log.d("addToMarketP_Request_", addToMarketPlaceRequest.toString())

                    addToMarketPlaceViewModel.error.observe(this, Observer
                    {
                        if (it != "") {
                            Toast.makeText(
                                this@AddToMarketPlaceActivity,
                                it.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.sendBtn.isEnabled = true
                            addToMarketPlaceViewModel.error.postValue("")
                        }
                    })
                }
            }
        }
    }

    private fun displayMarketPlace() {
        binding.ipanEt.setText(getUserByTokenResponse!!.user.marketPlace?.bank_iban)
        binding.brandNumberArEt.setText(getUserByTokenResponse!!.user.marketPlace?.brand_name_ar)
        binding.brandNameEnEt.setText(getUserByTokenResponse!!.user.marketPlace?.brand_name_en)
        binding.firstNameEt.setText(getUserByTokenResponse!!.user.marketPlace?.owner_firstname)
        binding.familyNameEt.setText(getUserByTokenResponse!!.user.marketPlace?.owner_lastName)
        binding.phoneNumberEt.setText(
            GeneralFunctions.deleteCountryCodeFromPhoneAndReformat(
                getUserByTokenResponse!!.user.marketPlace!!.owner_phone
            )
        )
        when (getUserByTokenResponse!!.user.marketPlace!!.status) {
//            "REFUSED" -> {
//                binding.rejectReasonLinear.visibility = View.VISIBLE
//                binding.rejectReasonTitleTv.text = " تم الرفض بسبب :"
//                binding.rejectReasonTv.text = getUserByTokenResponse!!.user.marketPlace!!.reason
//            }
            "FIXING-PHASE" -> {
                binding.rejectReasonLinear.visibility = View.VISIBLE
                binding.rejectReasonTitleTv.text = " يحتاج تعديل بسبب :"
                binding.rejectReasonTv.text = getUserByTokenResponse!!.user.marketPlace!!.reason
            }
        }
        binding.emailAddressEt.setText(getUserByTokenResponse!!.user.marketPlace?.owner_email)
        binding.taxNumberEt.setText(getUserByTokenResponse!!.user.marketPlace?.tax_number)
        binding.licenseNumberEt.setText(getUserByTokenResponse!!.user.marketPlace?.entity_number)
        binding.licenseIssuingDateEt.text =
            (getUserByTokenResponse!!.user.marketPlace?.entity_ssuing_date?.replaceAfter(
                "T",
                ""
            ))?.replace("T", "")?.trim()
        binding.licenseExpiredDateEt.text =
            (getUserByTokenResponse!!.user.marketPlace?.entity_expiry_date?.replaceAfter(
                "T",
                ""
            ))?.replace("T", "")?.trim()
        if (getUserByTokenResponse!!.user.marketPlace?.identification_type == "PASSPORT")
            binding.passportRb.isChecked = true
        else binding.identityCardRb.isChecked = true
        binding.idIssuingDateEt.text =
            (getUserByTokenResponse!!.user.marketPlace?.identification_issuing_date?.replaceAfter(
                "T",
                ""
            ))?.replace("T", "")?.trim()
        binding.idExpiredDateEt.text =
            (getUserByTokenResponse!!.user.marketPlace?.identification_expiry_date?.replaceAfter(
                "T",
                ""
            ))?.replace("T", "")?.trim()
        binding.sendBtn.text = "حفظ التغيرات"
        getUserByTokenResponse!!.user.marketPlace?.entity_imgs?.forEachIndexed { index, uploadedImageItem ->
            if (licenseImagesList.size > index)
                licenseImagesList[index].imageUrlIfUpdate = uploadedImageItem
        }
        getUserByTokenResponse!!.user.marketPlace?.identification_imgs?.forEachIndexed { index, uploadedImageItem ->
            if (idImagesList.size > index)
                idImagesList[index].imageUrlIfUpdate = uploadedImageItem
        }
        licenseImagesAdapter.differ.submitList(licenseImagesList)
        idImagesAdapter.differ.submitList(idImagesList)
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
        //   if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //  }
    }

    private fun getImageFile() {
        CropImage.activity(resultUri)
            .start(this@AddToMarketPlaceActivity)
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

                    if (documentImageType == "idImage$currentImagePosition") {
                        idImageFile = File(resultUri.path)
                        idImagesList[currentImagePosition].imageFile = File(resultUri!!.path)
                        idImagesList[currentImagePosition].imageName =
                            File(resultUri!!.path).name
                        resultUri = Uri.EMPTY
                        if (typeOfMarketPlaceRequest == "UPDATE")
                            if (mustClearIdentificationImagesUrlToUpdate) {
                                idImagesList.forEach {
                                    it.imageUrlIfUpdate = ""
                                }
                            }
                        idImagesAdapter.differ.submitList(idImagesList)
                        idImagesAdapter.notifyDataSetChanged()
                        mustClearIdentificationImagesUrlToUpdate = false
                    }

                    if (documentImageType == "licenseImage$currentImagePosition") {
                        licenseImageFile = File(resultUri.path)
                        licenseImagesList[currentImagePosition].imageFile = File(resultUri!!.path)
                        licenseImagesList[currentImagePosition].imageName =
                            File(resultUri!!.path).name
                        resultUri = Uri.EMPTY
                        if (typeOfMarketPlaceRequest == "UPDATE")
                            if (mustClearLicenseImagesUrlToUpdateIt) {
                                licenseImagesList.forEach {
                                    it.imageUrlIfUpdate = ""
                                }
                            }
                        licenseImagesAdapter.differ.submitList(licenseImagesList)
                        licenseImagesAdapter.notifyDataSetChanged()
                        mustClearLicenseImagesUrlToUpdateIt = false
                    }

                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .start(this)
    }

    private fun showCalenderToChooseDate(textView: TextView) {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val currentDate = "$year-${month + 1}-$day"

        val dialog = DatePickerDialog(
            this@AddToMarketPlaceActivity,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            mDateSetListener,
            year, month, day
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dialog.setOnDateSetListener { view, chosenYear, chosenMonth, chosenDayOfMonth ->
                var chosenMonth1 = chosenMonth
                chosenMonth1 += 1
                var chosenDayOfMonthAsString = ""
                var chosenMonth1AsString = ""
                if (chosenDayOfMonth < 10)
                    chosenDayOfMonthAsString = "0$chosenDayOfMonth"
                else chosenDayOfMonthAsString = "$chosenDayOfMonth"
                if (chosenMonth1 < 10)
                    chosenMonth1AsString = "0$chosenMonth1"
                else chosenMonth1AsString = "$chosenMonth1"
                val date = "$chosenYear-$chosenMonth1AsString-$chosenDayOfMonthAsString"
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                if ((textView == binding.idIssuingDateEt) or (textView == binding.licenseIssuingDateEt)) {
                    if ((sdf.parse(date).after(sdf.parse(currentDate))))
                        Toast(this).showCustomToast("اختر تاريخ اصدار حقيقي", this)
                    else
                        textView.text = date
                } else if ((textView == binding.idExpiredDateEt) or (textView == binding.licenseExpiredDateEt)) {
                    if ((sdf.parse(date).before(sdf.parse(currentDate))))
                        Toast(this).showCustomToast("التاريخ المختار منتهي", this)
                    else
                        textView.text = date
                } else
                    textView.text = date
            }
        }
    }

    private fun initialLicenseImageAdapterToStartPick() {
        licenseImagesAdapter = LicenseImagesAdapter(this, this)
        binding.licenseImagesRv.apply {
            adapter = licenseImagesAdapter
            layoutManager =
                LinearLayoutManager(
                    this@AddToMarketPlaceActivity,
                    RecyclerView.HORIZONTAL,
                    false
                )
        }
    }

    private fun initialIdsImageAdapterToStartPick() {
        idImagesAdapter = IdImagesAdapter(this, this)
        binding.idImagesRv.apply {
            adapter = idImagesAdapter
        }
    }

    override fun onIvIdClick(uploadImagesModel: UploadImagesModel, currentImagePosition: Int) {
        documentImageType = "idImage$currentImagePosition"
        this.currentImagePosition = currentImagePosition
        if (typeOfMarketPlaceRequest == "UPDATE")
            if (mustClearIdentificationImagesUrlToUpdate)
                Toast(this).showCustomToast("سيتم استبدال الصور السابقة", this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            getImageFile()
        else
            GeneralFunctions.pickFromGallery(this)
    }

    override fun onDeleteIdClick(currentImagePosition: Int) {
        idImagesList[currentImagePosition].imageFile = null
        idImagesList[currentImagePosition].imageUrlIfUpdate = ""
        idImageFile = null
        idImagesList.forEach {
            if (it.imageFile != null)
                idImageFile = it.imageFile
            it.imageUrlIfUpdate = ""
        }
        if (typeOfMarketPlaceRequest == "UPDATE") {
            if (!getUserByTokenResponse!!.user.marketPlace?.identification_imgs.isNullOrEmpty()) {
                getUserByTokenResponse!!.user.marketPlace?.identification_imgs = ArrayList()
                if (mustClearIdentificationImagesUrlToUpdate)
                    Toast(this).showCustomToast("يرجى تحميل الصور من البداية", this)
            }
        }
        idImagesAdapter.differ.submitList(idImagesList)
        idImagesAdapter.notifyDataSetChanged()
        mustClearIdentificationImagesUrlToUpdate = false
    }

    override fun onIvLicenseClick(
        uploadImagesModel: UploadImagesModel,
        currentImagePosition: Int
    ) {
        documentImageType = "licenseImage$currentImagePosition"
        this.currentImagePosition = currentImagePosition
        if (typeOfMarketPlaceRequest == "UPDATE")
            if (mustClearLicenseImagesUrlToUpdateIt)
                Toast(this).showCustomToast("سيتم استبدال الصور السابقة", this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            getImageFile()
        else
            GeneralFunctions.pickFromGallery(this)

    }

    override fun onDeleteLicenseClick(currentImagePosition: Int) {
        licenseImagesList[currentImagePosition].imageFile = null
        licenseImagesList[currentImagePosition].imageUrlIfUpdate = ""
        licenseImageFile = null
        licenseImagesList.forEach {
            if (it.imageFile != null)
                licenseImageFile = it.imageFile
            it.imageUrlIfUpdate = ""
        }
        if (typeOfMarketPlaceRequest == "UPDATE") {
            if (!getUserByTokenResponse!!.user.marketPlace?.entity_imgs.isNullOrEmpty()) {
                getUserByTokenResponse!!.user.marketPlace?.entity_imgs = ArrayList()
                if (mustClearLicenseImagesUrlToUpdateIt)
                    Toast(this).showCustomToast("يرجى تحميل الصور من البداية", this)
            }
        }
        licenseImagesAdapter.differ.submitList(licenseImagesList)
        licenseImagesAdapter.notifyDataSetChanged()
        mustClearLicenseImagesUrlToUpdateIt = false
    }

    fun isValidIban(iban: String): Boolean {
        if (!"^[0-9A-Z]*\$".toRegex().matches(iban)) {
            return false
        }

        val symbols = iban.trim { it <= ' ' }
        if (symbols.length < 15 || symbols.length > 34) {
            return false
        }
        val swapped = symbols.substring(4) + symbols.substring(0, 4)
        return swapped.toCharArray()
            .map { it.toInt() }
            .fold(0) { previousMod: Int, _char: Int ->
                val value = Integer.parseInt(Character.toString(_char.toChar()), 36)
                val factor = if (value < 10) 10 else 100
                (factor * previousMod + value) % 97
            } == 1
    }


}