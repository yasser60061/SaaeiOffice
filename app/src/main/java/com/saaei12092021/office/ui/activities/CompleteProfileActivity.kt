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
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.*
import com.saaei12092021.office.databinding.ActivityCompleteProfileBinding
import com.saaei12092021.office.databinding.BottomSheetForChooseFileTypeInChatBinding
import com.saaei12092021.office.model.requestModel.CompleteProfileRequest
import com.saaei12092021.office.model.responseModel.areasResponse.Area
import com.saaei12092021.office.model.responseModel.citiesResponse.City
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.ui.activities.packagesActivity.PackageActivity
import com.saaei12092021.office.util.*
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CompleteProfileActivity : AppCompatActivity(),
    CityAdapter.OnItemClickListener,
    WorkCityAdapter.OnChooseChangeListener,
    WorkAreaAdapter.OnChooseChangeListener,
    WorkMainCategoryAdapter.OnChooseChangeListener,
    WorkSubCategoryAdapter.OnChooseChangeListener {

    lateinit var binding: ActivityCompleteProfileBinding
    lateinit var viewModel: MyViewModel
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
    private var chosenWorkCityList: ArrayList<Int> = ArrayList()
    var chosenWorkAreaList: ArrayList<Int> = ArrayList()
    var chosenWorkCategoryList: ArrayList<Int> = ArrayList()
    var chosenWorkSubCategoryList: ArrayList<Int> = ArrayList()
    var idType: Int = 3
    var idNumber: String = ""
    var adNumber: Int = 0
 //   var agencyType = "OFFICE"  // COMPANY','OFFICE','MARKETER','FREEWORK'

    // this list and variable for som operation only
    var cityAddressList: ArrayList<City> = ArrayList() // for choose office address used
    var allCityList: ArrayList<City> = ArrayList()
    var allAreaList: ArrayList<Area> = ArrayList()
    var mainCategoryList: ArrayList<Category> = ArrayList()
    var subCategoryList: ArrayList<SubCategory> = ArrayList()
    lateinit var cityForAddressAdapter: CityAdapter
    lateinit var workCityAdapter: WorkCityAdapter
    lateinit var workAreaAdapter: WorkAreaAdapter
    lateinit var workMainCategoryAdapter: WorkMainCategoryAdapter
    lateinit var workSubCategoryAdapter: WorkSubCategoryAdapter
    lateinit var pickerType: String
    private var resultUri: Uri = Uri.EMPTY
    private var loadingBar: ProgressDialog? = null

    //   lateinit var loginUserResponse: User
    var getUserResponse: GetUserResponse? = null
    lateinit var token: String
    lateinit var myLanguage: String
    lateinit var country: String
    var suffixOfFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        loadingBar = ProgressDialog(this)
        myLang = Constant.getMyLanguage(this)
        country = Constant.getMyCountryId(this)

        token = Constant.getToken(this)
        myLang = Constant.getMyLanguage(this)

        setUpLanguageViewAndDirection()

        displayAddressCity()
        displayWorkCity()
        displayWorkArea()
        displayWorkCategory()
        displayWorkSubCategory()

//        val jsonData = Constant.getLoginResponse(this)
//        loginUserResponse = Gson().fromJson(jsonData, User::class.java) as User
//
//        if (loginUserResponse.city != null)
//            city = loginUserResponse.city.id

        displayOfficeInfoIfExist()

        //     Log.d("loginUserResponse", loginUserResponse.toString())

        binding.backLinear.setOnClickListener {
            finish()
        }


        binding.officeLogoIv.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                uploadProfileImage()
            else
                GeneralFunctions.pickFromGallery(this)
            pickerType = "officeLogo"
        }

        binding.commercialRegisterRl.setOnClickListener {
            pickerType = "commercial"
            bottomSheetForChooseFileType()
        }

        binding.commercialRegisterTv.setOnClickListener {
            bottomSheetForChooseFileType()
            pickerType = "commercial"
        }

        binding.completeProfileBtn.setOnClickListener {
            fullname = binding.officeNameEt.text.toString()
            phone = binding.phoneNumberEt.text.toString()
            email = binding.emailAddressEt.text.toString()

            if (binding.companyRb.isChecked) {
         //       agencyType = "COMPANY"
                binding.typeTitleTv.text = "شركة"
            }
            if (binding.officeRb.isChecked)
              //  agencyType = "OFFICE"
            if (binding.freeWorkRb.isChecked) {
             //   agencyType = "FREEWORK"
                binding.typeTitleTv.text = "عمل حر"
            }

            chosenWorkCityList.clear()
            chosenWorkAreaList.clear()
            chosenWorkCategoryList.clear()
            chosenWorkSubCategoryList.clear()

            cityAddressList.forEach {
                if (it.selected)
                    city = it.id
            }

            allCityList.forEach {
                if (it.selectedWork)
                    chosenWorkCityList.add(it.id)
            }
            allAreaList.forEach {
                if (it.selected)
                    chosenWorkAreaList.add(it.id)
            }
            mainCategoryList.forEach {
                if (it.isSelected)
                    chosenWorkCategoryList.add(it.id)
            }
            subCategoryList.forEach {
                if (it.isSelected)
                    chosenWorkSubCategoryList.add(it.id)
            }

            when {
                TextUtils.isEmpty(fullname) -> binding.officeNameEt.error = "اكتب اسم المكتب"
                TextUtils.isEmpty(email) -> binding.emailAddressEt.error = "اكتب الايميل"
                !GeneralFunctions.isValidEmail2(binding.emailAddressEt.text.toString()) -> binding.emailAddressEt.error =
                    "أدخيل إيميل صالح"
                (TextUtils.isEmpty(binding.idNumberEt.text.toString())) -> binding.idNumberEt.error =
                    "ادخل رقم الهوية"
                (TextUtils.isEmpty(binding.adNumberEt.text.toString())) -> binding.adNumberEt.error =
                    "ادخل رقم المعلن"
                /* // we disable update commercial file
                 (userViewModel.commercialFile == null) and (getUserResponse?.commercialFile.isNullOrEmpty()) -> {
                     Toast.makeText(
                         this@CompleteProfileActivity,
                         "الرجاء ارفاق الملف التجاري",
                         Toast.LENGTH_LONG
                     ).show()
                 }

                 */
                city == 0 -> {
                    Constant.makeToastMessage(this, "الرجاء أختيار المدينة")
                }
                chosenWorkCityList.size == 0 -> {
                    Constant.makeToastMessage(
                        this,
                        "الرجاء أختيار المدن التي تعمل بها"
                    )
                }
                chosenWorkAreaList.size == 0 -> {
                    Constant.makeToastMessage(
                        this,
                        "الرجاء أختيار الأحياء التي تعمل بها"
                    )
                }
                chosenWorkCategoryList.size == 0 -> {
                    Constant.makeToastMessage(
                        this,
                        "الرجاء أختيار التصنيفات الرئيسية التي تعمل بها"
                    )
                }
                chosenWorkSubCategoryList.size == 0 -> Constant.makeToastMessage(
                    this,
                    "الرجاء أختيار التصنيفات الفرعية التي تعمل بها"
                )
                else -> {
                    idNumber = (binding.idNumberEt.text.toString())
                    adNumber = Integer.parseInt(binding.adNumberEt.text.toString())
                    //     if (loginUserResponse.accountType == "ACTIVE") {
                    userViewModel.completeProfile(
                        CompleteProfileRequest(
                            country = country,
                            phone = phone,
                            fullname = fullname,
                            email = email,
                            //    type = agencyType,
                            city = city,
                            completeProfile = completeProfile,
                            workCity = chosenWorkCityList,
                            workArea = chosenWorkAreaList,
                            workCategory = chosenWorkCategoryList,
                            workSubCategory = chosenWorkSubCategoryList,
                            //   idType = idType,
                            idNumber = idNumber,
                            adNumber = adNumber
                        ),
                        myLang = myLang,
                        //   token = token,
                        userId = getUserResponse!!.id.toString()
                    )
                    // for delete later
                    val jsonString = Gson().toJson(
                        CompleteProfileRequest(
                            country = country,
                            phone = phone,
                            fullname = fullname,
                            email = email,
                            //        type = agencyType,
                            city = city,
                            completeProfile = completeProfile,
                            workCity = chosenWorkCityList,
                            workArea = chosenWorkAreaList,
                            workCategory = chosenWorkCategoryList,
                            workSubCategory = chosenWorkSubCategoryList,
                            //    idType = idType,
                            idNumber = idNumber,
                            adNumber = adNumber
                        )
                    )
                    val jsonData: JSONObject = JSONObject(jsonString)
                    Log.d("update_profile_sent", jsonData.toString())
                }
            }
        }

        viewModel.error.observe(this, Observer
        {
            if (it != "") {
                if (it.toString().length < 70)
                    Toast.makeText(
                        this@CompleteProfileActivity,
                        it.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                loadingBar!!.dismiss()
                viewModel.error
                    .postValue("")
            }
        })

        userViewModel.errorLive.observe(this, Observer {
            if (it != "") {
                if (it.toString().length < 70)
                    Toast.makeText(
                        this@CompleteProfileActivity,
                        it.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                loadingBar!!.dismiss()
                userViewModel.errorLive
                    .postValue("")
            }
        })

//----------------------------------------------------------------------------------
        viewModel.getAllCityInAllCountryWithoutPagination(
            token = token,
            regionId = "1",
            myLang = myLang,
            country = country
        )
        viewModel.citiesResponse.observe(this, Observer
        {
            when (it) {
                is Resource.Success -> {
                    binding.mainPb.visibility = View.GONE
                    cityAddressList = it.data!!.cities as ArrayList<City>
                    allCityList = it.data.cities as ArrayList<City>
                    cityAddressList.forEach { cityItem ->
                        cityItem.selected = false
                    }
                    allCityList.forEach { cityItem ->
                        cityItem.selectedWork = false
                    }
                    cityForAddressAdapter.differ.submitList(cityAddressList)
                    workCityAdapter.differ.submitList(allCityList)
                    binding.workCityTempRl.visibility = View.GONE
                    binding.cityAddressTempRl.visibility = View.GONE
                    displayCityForAddressAndCityWorkIfExist()
                }
                is Resource.Loading -> {
                    binding.mainPb.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

        //----------------------------------------------------------------------------------
        viewModel.areaResponse.observe(this, Observer
        {
            when (it) {
                is Resource.Success -> {
                    binding.mainPb.visibility = View.GONE
                    binding.workAreaTempRl.visibility = View.GONE
                    var tempAreaList: ArrayList<Area> = ArrayList()
                    if (it.data?.areas != null)
                        tempAreaList = it.data.areas as ArrayList<Area>
                    tempAreaList.forEach { areaItem ->
                        areaItem.selected = false
                    }
                    tempAreaList.forEach { areaItem ->
                        allAreaList.add(areaItem)
                    }
                    getUserResponse?.workArea?.forEach { workAreaItem ->
                        allAreaList.forEach { allAreaItem ->
                            if (workAreaItem.id == allAreaItem.id)
                                allAreaItem.selected = true
                        }
                    }
                    workAreaAdapter.differ.submitList(allAreaList)
                    workAreaAdapter.notifyDataSetChanged()
                    viewModel.areaResponse.postValue(Resource.Error(""))
                    Log.d("workArea", allAreaList.toString())
                }
                is Resource.Loading -> {
                    binding.mainPb.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

        //----------------------------------------------------------------------------------
        viewModel.getMainCategory(myLang)
        viewModel.mainCategoryResponse.observe(this, Observer
        {
            when (it) {
                is Resource.Success -> {
                    binding.mainPb.visibility = View.GONE
                    mainCategoryList = it.data?.categories as ArrayList<Category>
                    binding.workMainCategoryTempRl.visibility = View.GONE

                    getUserResponse?.workCategory?.forEach { workMainCategoryItem ->
                        mainCategoryList.forEachIndexed { mainCategoryIndex, mainCategoryItem ->
                            if (workMainCategoryItem.id == mainCategoryItem.id)
                                mainCategoryList[mainCategoryIndex].isSelected = true

                            Log.d(
                                "workMainCategoryItem",
                                workMainCategoryItem.id.toString() + "-" + mainCategoryItem.id.toString()
                            )
                        }
                    }
                    Log.d("mainCategoryList", mainCategoryList.toString())
                    Log.d("workCategoryList", getUserResponse?.workCategory.toString())
                    workMainCategoryAdapter.differ.submitList(mainCategoryList)
                    workMainCategoryAdapter.notifyDataSetChanged()
                }
                is Resource.Loading -> {
                    binding.mainPb.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

        // ----------------------------------------------------------------------------------
        viewModel.getAllSubCategory(myLang)
        viewModel.allSubCategoryResponse.observe(this, Observer
        {
            when (it) {
                is Resource.Success -> {
                    binding.mainPb.visibility = View.GONE
                    val temp = it.data?.data as ArrayList<SubCategory>

                    binding.workSubCategoryTempRl.visibility = View.GONE
                    getUserResponse?.workSubCategory?.forEach { workSubCategoryItem ->
                        temp.forEach { subCategoryItem ->
                            if (workSubCategoryItem.id == subCategoryItem.id)
                                subCategoryItem.isSelected = true
                        }
                    }
                    subCategoryList = temp
                    workSubCategoryAdapter.differ.submitList(subCategoryList)
                    workSubCategoryAdapter.notifyDataSetChanged()

                    Log.d("subCategoryList", subCategoryList.toString())
                }
                is Resource.Loading -> {
                    binding.mainPb.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.mainPb.visibility = View.GONE
                }
            }
        })
        //----------------------------------------------------------------------------------
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
                        this@CompleteProfileActivity,
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

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
        //   if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //  }
    }

    private fun displayCityForAddressAndCityWorkIfExist() {
        if (getUserResponse?.city?.id != 0) {
            cityAddressList.forEach {
                if (getUserResponse?.city?.id == it.id) {
                    it.selected = true
                }
            }
            cityForAddressAdapter.differ.submitList(cityAddressList)
            cityForAddressAdapter.notifyDataSetChanged()
        }

        getUserResponse?.workCity?.forEach { workCityItem ->
            allCityList.forEach { allCityItem ->
                if (workCityItem.id == allCityItem.id)
                    allCityItem.selectedWork = true
            }
            viewModel.getAllAreaWithoutPagination(token, workCityItem.toString(), myLang)
            workCityAdapter.differ.submitList(allCityList)
            //   workCityAdapter.notifyDataSetChanged()
        }
    }
//----------------------------------------------------------------------------------

    private fun displayOfficeInfoIfExist() {

        if (intent.getSerializableExtra("getUserResponse") != null) {
            getUserResponse = intent.getSerializableExtra("getUserResponse") as GetUserResponse
            if (getUserResponse!!.hasAdsPackage) {
                binding.packageNameTv.text =
                    getUserResponse!!.adsPackage.name
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateString =
                    simpleDateFormat.format(getUserResponse!!.packageEndDateMillSec)
                binding.dateOfPackageExpiredTv.text = dateString
                binding.availableAdsTv.text = getUserResponse!!.availableAds.toString()

                binding.officeRatingValueRb.rating = getUserResponse!!.rate
                binding.ratingNoTv.text = "(${getUserResponse!!.rateNumbers})"
            } else {
                binding.packageLinear.visibility = View.GONE
            }
            if (!getUserResponse?.img.isNullOrEmpty()) {
                Glide.with(this).load(getUserResponse!!.img).placeholder(R.drawable.profile_image)
                    .into(binding.officeLogoIv)
            }

            binding.officeNameEt.setText(getUserResponse!!.fullname)
            binding.phoneNumberEt.setText(getUserResponse!!.phone)
            binding.emailAddressEt.setText(getUserResponse!!.email)

            binding.freeWorkRb.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    binding.commercialRegisterRl.visibility = View.GONE
                    binding.commercialNumberTitle.text = "رقم الهوية"
                } else {
                    binding.commercialRegisterRl.visibility = View.VISIBLE
                    binding.commercialNumberTitle.text = "رقم السجل التجاري"
                }
            }

            if (getUserResponse!!.agencyType == "COMPANY") {
             //   agencyType = "COMPANY"
                binding.typeTitleTv.text = "شركة"
                binding.companyRb.isChecked = true
            }
            if (getUserResponse!!.agencyType == "OFFICE")
                binding.officeRb.isChecked = true
            if (getUserResponse!!.agencyType == "FREEWORK") {
            //    agencyType = "FREEWORK"
                binding.typeTitleTv.text = "عمل حر"
                binding.freeWorkRb.isChecked = true
            }

            // COMPANY','OFFICE','MARKETER','FREEWORK'
            if (!(getUserResponse!!.commercialFile.isNullOrEmpty()))
                binding.commercialRegisterTv.text = getUserResponse!!.commercialFile[0]

            binding.idNumberEt.setText(getUserResponse!!.idNumber)
            binding.adNumberEt.setText(getUserResponse!!.adNumber)
            binding.phoneNumberEt.isEnabled = false

            binding.upgradeThePackageLinear.setOnClickListener {
                val intent = Intent(this, PackageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun uploadProfileImage() {
        CropImage.activity(resultUri)
            .setAspectRatio(1, 1)
            .start(this@CompleteProfileActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((resultCode == Activity.RESULT_OK) && (data != null)) {
            when (requestCode) {
                Constant.COMMERCIAL_FILE_CODE -> {
                    tryOpenDocument(requestCode, resultCode, data)
                }
                Constant.IMAGE_FILE_CODE -> {
                    resultUri = data.data!!
                    launchImageCrop(resultUri)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    imageProfileFile = File(resultUri.path!!)

//            suspend {
//                compressedImageFile = Compressor.compress(this, imageProfileFile!!) {
//                    resolution(1080, 1080)
//                    quality(80)
//                    format(Bitmap.CompressFormat.JPEG)
//                    size(2_097_152) // 2 MB
//                    viewModel.logoImageFile = compressedImageFile
//                    binding.officeLogoIv.setImageURI(Uri.fromFile(viewModel.logoImageFile))
//                }
//            }

                    if (pickerType == "officeLogo") {
                        userViewModel.logoImageFile = imageProfileFile
                        binding.officeLogoIv.setImageURI(Uri.fromFile(userViewModel.logoImageFile))
                        resultUri = Uri.EMPTY
                    }
                }
            }
        }
    }

    private fun tryOpenDocument(requestCode: Int, resultCode: Int, data: Intent?) {
        when (val openFileResult =
            this@CompleteProfileActivity.tryHandleOpenDocumentResult(
                requestCode,
                resultCode,
                data
            )) {
            is OpenFileResult.FileWasOpened -> {
                userViewModel.commercialFile = getFileFromInputStream(openFileResult.content)
                if (pickerType == "commercial")
                    binding.commercialRegisterTv.text = userViewModel.commercialFile!!.name
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
            File.createTempFile("commercial", suffixOfFile, this@CompleteProfileActivity.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        outputStream.write(buffer)
        inputStream.close()
        outputStream.close()
        return tempFile
    }

    private fun displayAddressCity() {
        cityForAddressAdapter = CityAdapter(this)
        binding.cityRv.apply {
            adapter = cityForAddressAdapter
            layoutManager =
                LinearLayoutManager(this@CompleteProfileActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun displayWorkCity() {
        workCityAdapter = WorkCityAdapter(this@CompleteProfileActivity)
        binding.workCityRv.apply {
            adapter = workCityAdapter
            layoutManager =
                LinearLayoutManager(this@CompleteProfileActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun displayWorkArea() {
        workAreaAdapter = WorkAreaAdapter(this@CompleteProfileActivity)
        binding.workAreaRv.apply {
            adapter = workAreaAdapter
            layoutManager =
                LinearLayoutManager(this@CompleteProfileActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun displayWorkCategory() {
        workMainCategoryAdapter = WorkMainCategoryAdapter(this@CompleteProfileActivity)
        binding.workMainCategoryRv.apply {
            adapter = workMainCategoryAdapter
            layoutManager =
                LinearLayoutManager(this@CompleteProfileActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun displayWorkSubCategory() {
        workSubCategoryAdapter = WorkSubCategoryAdapter(this@CompleteProfileActivity)
        binding.workSubCategoryRv.apply {
            adapter = workSubCategoryAdapter
            layoutManager =
                LinearLayoutManager(this@CompleteProfileActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    override fun onItemClick(city: City, position: Int) {
        cityAddressList.forEach {
            it.selected = city.id == it.id
            if (city.id == it.id)
                this.city = city.id
        }
        cityForAddressAdapter.differ.submitList(cityAddressList)
        cityForAddressAdapter.notifyDataSetChanged()
    }

    override fun workCityOnItemChecked(cityPosition: Int, cityId: Int, isSelectedCity: Boolean) {
        allCityList[cityPosition].selectedWork = isSelectedCity
        workCityAdapter.differ.submitList(allCityList)
        if (isSelectedCity) {
            viewModel.getAllAreaWithoutPagination(token, cityId.toString(), myLang)
        } else {
            val temp: ArrayList<Area> = ArrayList()
            allAreaList.forEach {
                if (it.city != cityId)
                    temp.add(it)
            }
            allAreaList = temp
            workAreaAdapter.differ.submitList(allAreaList)
            Log.d("workArea", allAreaList.toString())
        }
    }

    override fun workAreaOnItemClick(areaPosition: Int, areaId: Int, selected: Boolean) {
        allAreaList[areaPosition].selected = selected
        workAreaAdapter.differ.submitList(allAreaList)
    }

    override fun mainCategoryOnItemChecked(
        categoryPosition: Int,
        categoryId: Int,
        isSelectedCategory: Boolean
    ) {
        mainCategoryList[categoryPosition].isSelected = isSelectedCategory
        workMainCategoryAdapter.differ.submitList(mainCategoryList)

    }

    override fun subCategoryOnItemChecked(
        subCategoryPosition: Int,
        subCategoryId: Int,
        isSelectedSubCategory: Boolean
    ) {
        subCategoryList[subCategoryPosition].isSelected = isSelectedSubCategory
        workSubCategoryAdapter.differ.submitList(subCategoryList)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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