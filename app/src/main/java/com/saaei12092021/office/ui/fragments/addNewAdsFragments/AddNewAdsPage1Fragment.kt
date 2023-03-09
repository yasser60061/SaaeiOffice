package com.saaei12092021.office.ui.fragments.addNewAdsFragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saaei12092021.office.adapters.CityAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.LandsInPlannedAdapter
import com.saaei12092021.office.adapters.UnitsInFloorsInAddEstateAdapter
import com.saaei12092021.office.databinding.FragmentAddNewAdsPage1Binding
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.Unit
import com.saaei12092021.office.model.responseModel.areasResponse.Area
import com.saaei12092021.office.model.responseModel.citiesResponse.City
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory
import com.saaei12092021.office.model.responseModel.liveSearchResponse.Data
import com.saaei12092021.office.model.responseModel.regionResponse.Region
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import kotlin.collections.ArrayList

class AddNewAdsPage1Fragment : Fragment(),
    CityAdapter.OnItemClickListener,
    AddNewEstateLocationOnMapFragment.OnLocationOfEstateSubmit,
    UnitsInFloorsInAddEstateAdapter.OnUnitChooseChangeListener,
    LandsInPlannedAdapter.OnChooseChangeListener {

    private var _binding: FragmentAddNewAdsPage1Binding? = null
    lateinit var cityAdapter: CityAdapter
    val binding get() = _binding!!
    lateinit var parentFrag: MainAddNewAdsFragment
    lateinit var viewModel: MyViewModel
    lateinit var addNewEstateAndModifyViewModel: AddNewEstateAndModifyViewModel
    lateinit var regionsList: ArrayList<Region>
    lateinit var cityList: ArrayList<City>
    lateinit var regionsNameList: ArrayList<String>
    lateinit var areaNameList: ArrayList<String>
    lateinit var mainCategoryList: ArrayList<Category>
    lateinit var mainCategoryNameList: ArrayList<String>
    lateinit var subCategoryList: ArrayList<SubCategory>
    lateinit var subCategoryNameList: ArrayList<String>
    lateinit var landInPlannedAdapter: LandsInPlannedAdapter
    lateinit var unitsInFloorsInAddEstateAdapter: UnitsInFloorsInAddEstateAdapter
    private var startDateSetListener: OnDateSetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewAdsPage1Binding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFrag = this@AddNewAdsPage1Fragment.parentFragment as MainAddNewAdsFragment
        viewModel = (activity as HomeActivity).viewModel
        addNewEstateAndModifyViewModel =
            ViewModelProvider(this).get(AddNewEstateAndModifyViewModel::class.java)

        parentFrag.binding.titleLinear.visibility = View.VISIBLE
        parentFrag.binding.progressPointerLinear.visibility = View.VISIBLE
        parentFrag.binding.pageNameTv.visibility = View.VISIBLE

        regionsList = ArrayList()
        regionsList.clear()
        regionsNameList = ArrayList()
        cityList = ArrayList()
        cityList.clear()
        areaNameList = ArrayList()
        areaNameList.add("")
        mainCategoryList = ArrayList()
        mainCategoryList.clear()
        mainCategoryNameList = ArrayList()
        mainCategoryNameList.add("")
        subCategoryList = ArrayList()
        subCategoryList.clear()
        subCategoryNameList = ArrayList()

        (activity as HomeActivity).moveCameraGoogleMapToEstateLocationLatLong = LatLng(0.0, 0.0)

        initialCityRv()
        initialLandsNumberRv()
        initialUnitsInBuildingToChooseFloorRv()
        setViewIfContractTypeSaleOrRent()
        getAndDisplayMainCategory()
        displayOtherDataIfExist()

        viewModel.getAllRegionWithoutPagination(
            (activity as HomeActivity).token,
            (activity as HomeActivity).myCountry,
            (activity as HomeActivity).myLang
        )

        binding.cityTempRl.setOnClickListener {
            Toast(context).showCustomToast("الرجاء اختيار المنطقة أولا", requireActivity())
        }

        binding.estateLocationOnMapRl.setOnClickListener {
            setNeighborhoodLocationIfChosen()
        }

        binding.estateLocationOnMapIv.setOnClickListener {
            setNeighborhoodLocationIfChosen()
        }

        binding.startSealDateTv.setOnClickListener {
            val datePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DatePickerDialog(
                    requireActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    startDateSetListener,
                    2022,
                    1,
                    1
                )
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()

        }

        startDateSetListener =
            OnDateSetListener { view, takenYear, takenMonth, takenDay ->
                binding.startSealDateTv.text =
                    takenYear.toString() + "-" + (takenMonth + 1) + "-" + takenDay
                parentFrag.startSaleDate = binding.startSealDateTv.text.toString()
            }

        binding.nextBtn.setOnClickListener {
            if ((mainCategoryList.isNullOrEmpty()) or (regionsList.isNullOrEmpty())) {
                Toast(requireActivity()).showCustomToast("انتظر تحميل البيانات", requireActivity())
            } else {
                if (binding.mainCategorySp.selectedItemPosition != 0)
                    if (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING") {
                        val unitsTempList: ArrayList<Unit> = ArrayList()
                        if (parentFrag.floors != null)
                            for (i in 1..parentFrag.floors!!) {
                                val tempUnitListInFloor: ArrayList<Int> = ArrayList()
                                parentFrag.unitsListInfo!!.forEachIndexed { index, unitElement ->
                                    if (unitElement.isSelected) {
                                        if (unitElement.selectedFloorNumber == i)
                                            tempUnitListInFloor.add(unitElement.id)
                                    }
                                    if ((tempUnitListInFloor.isNotEmpty()) and (parentFrag.unitsListInfo!!.size - 1 == index))
                                        unitsTempList.add(
                                            Unit(
                                                floor = i.toString(),
                                                unit = tempUnitListInFloor
                                            )
                                        )
                                }
                            }
                        parentFrag.units = unitsTempList
                        parentFrag.oldUnits!!.clear()
                        parentFrag.oldLands!!.clear()
                        if ((activity as HomeActivity).adsByIdResponse != null)
                            if (!(activity as HomeActivity).adsByIdResponse!!.advertisement.units.isNullOrEmpty()) {
                                parentFrag.unitsListInfo!!.forEach { unit1 ->
                                    var isInOldUnit = false
                                    (activity as HomeActivity).adsByIdResponse!!.advertisement.units.forEach { unitsInFloors ->
                                        unitsInFloors.unit.forEach { unit2 ->
                                            if (!unit1.isSelected)
                                                if (unit2.id == unit1.id)
                                                    isInOldUnit = true
                                        }
                                    }
                                    if (isInOldUnit) parentFrag.oldUnits!!.add(unit1.id)
                                }
                            }
                        Log.d("unitsInFloor", parentFrag.units.toString())
                    }

                var landSelectedNumber = 0
                if (binding.mainCategorySp.selectedItemPosition != 0)
                    if (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED") {
                        parentFrag.lands!!.clear()
                        parentFrag.landsListInfo.forEach { landElement ->
                            if (landElement.isSelected) {
                                landSelectedNumber += 1
                                parentFrag.lands!!.add(landElement.id)
                            }
                        }
                        parentFrag.oldUnits!!.clear()
                        parentFrag.oldLands!!.clear()
                        if ((activity as HomeActivity).adsByIdResponse != null)
                            if (!(activity as HomeActivity).adsByIdResponse!!.advertisement.lands.isNullOrEmpty()) {
                                parentFrag.landsListInfo.forEach { landsInAllList ->
                                    var isInOldLands = false
                                    (activity as HomeActivity).adsByIdResponse!!.advertisement.lands!!.forEach { land ->
                                        if (!landsInAllList.isSelected)
                                            if (landsInAllList.id == land.id) {
                                                isInOldLands = true
                                            }
                                    }
                                    if (isInOldLands) parentFrag.oldLands!!.add(landsInAllList.id)
                                }
                            }
                        Log.d("lands_", parentFrag.lands.toString())
                        Log.d("oldLands_", parentFrag.oldLands.toString())
                    }

                if (parentFrag.region == null)
                    Toast(context).showCustomToast("الرجاء اختيار المنطقة", requireActivity())
                else if (parentFrag.city == 0)
                    Toast(context).showCustomToast("الرجاء اختيار المدينة", requireActivity())
                else if (binding.neighborhoodSp.selectedItemPosition == 0)
                    Toast(context).showCustomToast("الرجاء اختيار الحي", requireActivity())
                else if (binding.mainCategorySp.selectedItemPosition == 0)
                    Toast(context).showCustomToast(
                        "الرجاء اختيار التصنيف الرئيسي",
                        requireActivity()
                    )
                else if (subCategoryList.isNullOrEmpty())
                    Toast(context).showCustomToast(
                        "أنتظر تحميل البيانات",
                        requireActivity()
                    )
                else if (binding.subCategorySp.selectedItemPosition == 0)
                    Toast(context).showCustomToast(
                        "الرجاء اختيار التصنيف الفرعي",
                        requireActivity()
                    )
                else if (parentFrag.location[0] == 0.0)
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء تحديد موقع العقار على الخريطة"
                    )
                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING") and
                    (binding.floorsNumberSp.selectedItemPosition == 0)
                )
                    Toast(context).showCustomToast(
                        "الرجاء إختيار عدد الطوابق",
                        requireActivity()
                    )
                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING")
                    and (TextUtils.isEmpty(binding.buildingNumberEt.text.toString()))
                )
                    Toast(context).showCustomToast(
                        "الرجاء كتابة رقم العمارة",
                        requireActivity()
                    )
                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED")
                    and (TextUtils.isEmpty(binding.buildingNumberEt.text.toString()))
                )
                    Toast(context).showCustomToast(
                        "الرجاء كتابة رقم المخطط",
                        requireActivity()
                    )
                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED")
                    and (binding.startSealDateTv.text.toString() == "تاريخ بدء البيع")
                )
                    Toast(context).showCustomToast(
                        "الرجاء اختيار تاريخ بدء البيع",
                        requireActivity()
                    )
                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED")
                    and (landSelectedNumber == 0)
                )
                    Toast(context).showCustomToast(
                        "الرجاء اختيار بعض الأراضي ضمن المخطط",
                        requireActivity()
                    )
//                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
//                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING") and
//                    (parentFrag.units?.size == 0)
//                )
//                    Toast(context).showCustomToast(
//                        "الرجاء اختيار بعض وحدات العقار",
//                        requireActivity()
//                    )
                else if ((binding.mainCategorySp.selectedItemPosition != 0) and
                    (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED") and (parentFrag.contractType == "RENT")
                )
                    Toast(requireContext()).showCustomToast("المخطط للبيع فقط", requireActivity())
                else if ((parentFrag.contractType == "RENT") and (parentFrag.rentType == null))
                    Toast(context).showCustomToast("الرجاء اختيار مدة الإيجار", requireActivity())
                else {
                    parentFrag.area =
                        parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].id
                    parentFrag.areaName =
                        parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].areaName
                    parentFrag.category =
                        mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].id
                    parentFrag.categoryType =
                        mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type
                    parentFrag.categoryName =
                        mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].categoryName
                    parentFrag.subCategory =
                        subCategoryList[binding.subCategorySp.selectedItemPosition - 1].id
                    parentFrag.subCategoryName =
                        subCategoryList[binding.subCategorySp.selectedItemPosition - 1].categoryName
                    if (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING") {
                        parentFrag.unitNumber = binding.buildingNumberEt.text.toString()
                    }
                    if (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED") {
                        parentFrag.unitNumber = binding.buildingNumberEt.text.toString()
                    }
                    if (!TextUtils.isEmpty(binding.streetNameEt.text.toString()))
                        parentFrag.streetName = binding.streetNameEt.text.toString()

                    if ((activity as HomeActivity).adsByIdResponse == null) {
                        if ((activity as HomeActivity).getUserResponse!!.hasAdsPackage) {
                            if ((((activity as HomeActivity).getUserResponse!!.adsPackage.building) and
                                        (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING")) or
                                (((activity as HomeActivity).getUserResponse!!.adsPackage.plan) and (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED")) or
                                ((mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type != "BUILDING") and (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type != "PLANNED"))
                            )
                                parentFrag.displayPage2()
                            else {
                                if ((mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING"))
                                    Toast(requireContext()).showCustomToast(
                                        "باقتك لا تعدعم اضافة عمارة",
                                        requireActivity()
                                    )
                                if ((mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED"))
                                    Toast(requireContext()).showCustomToast(
                                        "باقتك لا تعدعم اضافة مخطط",
                                        requireActivity()
                                    )
                            }
                        } else Toast(requireContext()).showCustomToast(
                            "يجب الاشتراك في باقة لتتمكن من إضافة إعلان",
                            requireActivity()
                        )
                    } else parentFrag.displayPage2()
                }
            }
        }

        //----------------------------------------------------------------------------------
        viewModel.regionLiveData.observe(viewLifecycleOwner, Observer
        {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    regionsList.clear()
                    regionsNameList.clear()
                    regionsNameList.add("اختر المنطقة")
                    regionsList = it.data?.regions as ArrayList<Region>
                    for (item in regionsList)
                        regionsNameList.add(item.regionName)
                    displayRegionInSpinner()
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })
//----------------------------------------------------------------------------------
        viewModel.areaResponse.observe(viewLifecycleOwner, Observer
        {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    parentFrag.areaList.clear()
                    areaNameList.clear()
                    areaNameList.add("اختر اسم الحي")
                    parentFrag.areaList = it.data?.areas as ArrayList<Area>
                    for (item in parentFrag.areaList)
                        areaNameList.add(item.areaName)
                    displayAreaInSpinner()
                    //   parentFrag.areaIsLoading = true
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

//----------------------------------------------------------------------------------

        viewModel.subCategoryResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.subCatPb.visibility = View.GONE
                    resetSubCategory()
                    subCategoryList =
                        it.data?.subSubCategory as ArrayList<SubCategory>
                    for (item in subCategoryList)
                        subCategoryNameList.add(item.categoryName)
                    displaySubCategorySpinner()
                }
                is Resource.Loading -> {
                    binding.subCatPb.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })
//----------------------------------------------------------------------------------
        addNewEstateAndModifyViewModel.liveSearchLiveData.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Success -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE
                        if (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "PLANNED") {
                            parentFrag.landsListInfo =
                                it.data!!.data as ArrayList<Data>
                            parentFrag.landsListInfo.forEach { landItem ->
                                landItem.isSelected = false
                            }

                            if ((activity as HomeActivity).adsByIdResponse != null)
                                if ((parentFrag.adsResponse.advertisement.category.type == "PLANNED") and
                                    (parentFrag.adsResponse.advertisement.unitNumber != null) and
                                    (!parentFrag.adsResponse.advertisement.lands.isNullOrEmpty())
                                ) {
                                    parentFrag.adsResponse.advertisement.lands!!.forEach { landItemFromServer ->
                                        parentFrag.landsListInfo.add(
                                            0,
                                            Data(
                                                id = landItemFromServer.id,
                                                title = landItemFromServer.title,
                                                unitNumber = landItemFromServer.unitNumber,
                                                isSelected = true,
                                                totalFloorNumber = 0,
                                                selectedFloorNumber = 0
                                            )
                                        )
                                    }
                                }

                            binding.floorsAndUnitsRv.visibility = View.GONE
                            binding.landNumberRv.visibility = View.VISIBLE
                            parentFrag.binding.titleLinear.visibility = View.GONE
                            landInPlannedAdapter.differ.submitList(parentFrag.landsListInfo)
                            if (parentFrag.landsListInfo.isNullOrEmpty()) {
                                binding.floorAndUnitTitle.visibility = View.VISIBLE
                                binding.floorAndUnitTitle.text =
                                    "لا يوجد أراضي مخصصة لهذا المخطط بحسب الموقع"
                            } else binding.floorAndUnitTitle.text =
                                "اختر قطعة الارض المخصصة للبيع داخل المخطط"

                            displayOtherDataIfExist()

                        } else if (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING") {
                            val tempUnitListInfo = it.data!!.data as ArrayList<Data>
                            //   initialUnitsInBuildingToChooseFloorRv()

                            tempUnitListInfo.forEach { unitItem ->
                                unitItem.totalFloorNumber = parentFrag.floors!!
                                unitItem.isSelected = false
                                unitItem.selectedFloorNumber = 200
                            }
                            if ((activity as HomeActivity).adsByIdResponse != null)
                                if ((parentFrag.adsResponse.advertisement.category.type == "BUILDING") and
                                    (parentFrag.adsResponse.advertisement.unitNumber != null) and
                                    (!parentFrag.adsResponse.advertisement.units.isNullOrEmpty())
                                ) {
                                    parentFrag.unitNumber =
                                        parentFrag.adsResponse.advertisement.unitNumber.toString()
                                    var maxFloor = 0
                                    parentFrag.adsResponse.advertisement.units.forEach { unitsFromResponseItem ->
                                        if (unitsFromResponseItem.floor >= maxFloor)
                                            maxFloor = unitsFromResponseItem.floor
                                    }

                                    parentFrag.adsResponse.advertisement.units.forEach { unitItemFromServer ->
                                        unitItemFromServer.unit.forEach { unitX ->
                                            tempUnitListInfo.add(
                                                0,
                                                Data(
                                                    id = unitX.id,
                                                    title = unitX.title,
                                                    unitNumber = unitX.unitNumber,
                                                    isSelected = true,
                                                    totalFloorNumber = maxFloor,
                                                    selectedFloorNumber = unitItemFromServer.floor
                                                )
                                            )
                                        }
                                    }
                                }

                            parentFrag.unitsListInfo = tempUnitListInfo

                            Log.d(
                                "floor_main_list",
                                parentFrag.unitsListInfo.toString()
                            )

                            binding.floorsAndUnitsRv.visibility = View.VISIBLE
                            binding.floorAndUnitTitle.visibility = View.VISIBLE
                            binding.floorAndUnitTitle.text =
                                "اختر الوحدات المخصصة للبيع أو الإيجار داخل العمارة"
                            binding.landNumberRv.visibility = View.GONE
                            parentFrag.binding.titleLinear.visibility = View.GONE
                            unitsInFloorsInAddEstateAdapter.differ.submitList(parentFrag.unitsListInfo)

                            if (parentFrag.unitsListInfo.isNullOrEmpty()) {
                                binding.floorAndUnitTitle.visibility = View.VISIBLE
                                binding.floorAndUnitTitle.text =
                                    "لا يوجد وحدات مخصصة لهذه العمارة بحسب الموقع"
                            }
                            displayOtherDataIfExist()
                        }

                    }
                    is Resource.Loading -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Error -> {
                        // Toast(context).showCustomToast(err,this)
                    }
                }
            })
//----------------------------------------------------------------------------------
        binding.regionsSp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0)
                    //    if ((activity as HomeActivity).adsByIdResponse != null)
                        if (parentFrag.region != null) {
                            cityList.clear()
                            parentFrag.city = 0
                            cityAdapter.differ.submitList(cityList)
                            binding.cityTempRl.visibility = View.VISIBLE
                            parentFrag.region = null
                            parentFrag.areaList.clear()
                            areaNameList.clear()
                            areaNameList.add("")
                            displayAreaInSpinner()
                            resetAllViews()
                        }
                    if (position != 0) {
                        if (parentFrag.region != null)
                        //   if ((activity as HomeActivity).adsByIdResponse != null)
                            if (regionsList[binding.regionsSp.selectedItemPosition - 1].id != parentFrag.region) {
                                cityList.clear()
                                parentFrag.city = 0
                                cityAdapter.differ.submitList(cityList)
                                binding.cityTempRl.visibility = View.VISIBLE
                                //   parentFrag.region = null
                                parentFrag.areaList.clear()
                                areaNameList.clear()
                                areaNameList.add("")
                                displayAreaInSpinner()
                                resetAllViews()
                            }
                        viewModel.getAllCityWithoutPagination(
                            token = (activity as HomeActivity).token,
                            regionId = regionsList[binding.regionsSp.selectedItemPosition - 1].id.toString(),
                            myLang = (activity as HomeActivity).myLang,
                        )
                        parentFrag.region =
                            regionsList[binding.regionsSp.selectedItemPosition - 1].id
                        //----------------------------------------------------------------------------------
                        viewModel.citiesResponse.observe(viewLifecycleOwner, Observer {
                            when (it) {
                                is Resource.Success -> {
                                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                        View.GONE
                                    binding.cityTempRl.visibility = View.GONE
                                    cityAdapter.differ.submitList(it.data?.cities?.toList())
                                    cityList = it.data?.cities as ArrayList<City>
                                    //   getAndDisplayMainCategory()
                                    //   displayMainCategoryInSpinner()
                                    displayDataIfExist()
                                }
                                is Resource.Loading -> {
                                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                        View.VISIBLE
                                }
                                is Resource.Error -> {
                                }
                            }
                        })
                    }
                }
            }
//----------------------------------------------------------------------------------
        binding.neighborhoodSp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        parentFrag.area =
                            parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].id
                        getAndDisplayMainCategory()
                    }
                }
            }

//----------------------------------------------------------------------------------
        binding.mainCategorySp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        if (mainCategoryList[position - 1].type == "BUILDING") {
                            if (parentFrag.unitsListInfo.isNullOrEmpty()) {
                                resetAllViews()
                                binding.buildingNumberEt.setText("")
                            }
                            displayBuildingView()
                            binding.buildingNumberEt.hint = "أدخل رقم العمارة"
                            parentFrag.binding.titleLinear.animate()
                            getSubCategory()
                            displayFloorsSpinner()
                        } else if (mainCategoryList[position - 1].type == "PLANNED") {
                            displayPlannedView()
                            getSubCategory()
                            if (!parentFrag.areaList.isNullOrEmpty())
                                if (binding.neighborhoodSp.selectedItemPosition != 0) {
                                    if (parentFrag.landsListInfo.isNullOrEmpty()) {
                                        resetAllViews()
                                        displayPlannedView()
                                        binding.buildingNumberEt.setText("")
                                        binding.buildingNumberEt.hint = "أدخل رقم المخطط"
                                        parentFrag.binding.titleLinear.animate()
                                        getAndDisplayInfoIfMainCategoryIsPlannedOrBuilding(
                                            mainCategoryList[position - 1].type,
                                            area = parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].id
                                        )
                                    } else {
                                        displayOtherDataIfExist()
                                        landInPlannedAdapter.differ.submitList(parentFrag.landsListInfo)
                                    }
                                } else {
                                    if (parentFrag.city == 0)
                                        Toast(requireContext()).showCustomToast(
                                            "اختر المدينة اولا",
                                            requireActivity()
                                        )
                                    else if ((binding.neighborhoodSp.selectedItemPosition == 0) and (parentFrag.area == null))
                                        Toast(requireContext()).showCustomToast(
                                            "اختر الحي اولا",
                                            requireActivity()
                                        )
                                }
                        } else {
                            resetAllViews()
                            getSubCategory()
                        }
                    } else resetAllViews()
                }
            }
//-----------------------------------------------------------------------------------

        binding.floorsNumberSp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        if ((binding.neighborhoodSp.selectedItemPosition != 0) and (!parentFrag.areaList.isNullOrEmpty())) {
                            parentFrag.floors = position
                            if (!TextUtils.isEmpty(binding.buildingNumberEt.text.toString()))
                                parentFrag.unitNumber = binding.buildingNumberEt.text.toString()
                            else parentFrag.unitNumber = ""
                            if (!parentFrag.unitsListInfo.isNullOrEmpty()) {
                                //  if (position < parentFrag.unitsListInfo.size)
                                val unitsListInfoTemp = parentFrag.unitsListInfo
                                parentFrag.unitsListInfo!!.forEach {
                                    it.totalFloorNumber = position
                                }
                                parentFrag.unitsListInfo = unitsListInfoTemp
                                displayBuildingView()
                                displayOtherDataIfExist()
                                unitsInFloorsInAddEstateAdapter.differ.submitList(parentFrag.unitsListInfo)
                            } else {
                                getAndDisplayInfoIfMainCategoryIsPlannedOrBuilding(
                                    mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type,
                                    area = parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].id
                                )
                            }
                            Log.d(
                                "mainCategory",
                                mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type
                            )
                        } else {
                            if ((binding.neighborhoodSp.selectedItemPosition == 0) and (parentFrag.area == null))
                                Toast(requireContext()).showCustomToast(
                                    "اختر الحي اولا",
                                    requireActivity()
                                )
                            else if (parentFrag.city == 0)
                                Toast(requireContext()).showCustomToast(
                                    "اختر المدينة اولا",
                                    requireActivity()
                                )
                            binding.floorsNumberSp.setSelection(0)
                        }
                    }
                }
            }
    }

    private fun displayBuildingView() {
        binding.buildingLinear.visibility = View.VISIBLE
        parentFrag.binding.titleLinear.visibility = View.GONE
        binding.landNumberRv.visibility = View.GONE
        binding.floorNumberSpRl.visibility = View.VISIBLE
        binding.floorsAndUnitsRv.visibility = View.VISIBLE
        binding.startSealDateTvRl.visibility = View.GONE
    }

    private fun displayPlannedView() {
        binding.buildingLinear.visibility = View.VISIBLE
        parentFrag.binding.titleLinear.visibility = View.GONE
        binding.floorNumberSpRl.visibility = View.GONE
        binding.floorsAndUnitsRv.visibility = View.GONE
        binding.landNumberRv.visibility = View.VISIBLE
        binding.startSealDateTvRl.visibility = View.VISIBLE
    }

    private fun getAndDisplayMainCategory() {
        viewModel.mainCategoryResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    mainCategoryList.clear()
                    mainCategoryNameList.clear()
                    mainCategoryNameList.add("الرئيسي")
                    val mainCategoryListTemp =
                        it.data?.categories as java.util.ArrayList<Category>
                    mainCategoryListTemp.forEach { categoryItem ->
                        if (categoryItem.id != 0) {
                            mainCategoryList.add(categoryItem)
                            mainCategoryNameList.add(categoryItem.categoryName_ar)
                        }
                    }
                    displayMainCategoryInSpinner()
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

    }

    private fun getSubCategory() {
        resetSubCategory()
        viewModel.getSubCategory(
            mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].id.toString(),
            (activity as HomeActivity).myLang
        )
    }

    private fun resetSubCategory() {
        subCategoryList.clear()
        subCategoryNameList.clear()
        subCategoryNameList.add("الفرعي")
        displaySubCategorySpinner()
    }

    private fun resetAllViews() {
        if ((activity as HomeActivity).adsByIdResponse == null) {
            binding.floorAndUnitTitle.text = ""
            resetSubCategory()
            parentFrag.landsListInfo.clear()
            landInPlannedAdapter.differ.submitList(parentFrag.landsListInfo)
            parentFrag.lands?.clear()
            parentFrag.unitsListInfo?.clear()
            unitsInFloorsInAddEstateAdapter.differ.submitList(parentFrag.unitsListInfo)
            parentFrag.units?.clear()
            parentFrag.floors = null
            binding.floorAndUnitTitle.visibility = View.GONE
            binding.buildingLinear.visibility = View.GONE
            binding.floorsAndUnitsRv.visibility = View.GONE
            binding.landNumberRv.visibility = View.GONE
        }
    }

    private fun setViewIfContractTypeSaleOrRent() {
        binding.estateTypeSaleTv.setOnClickListener {
            binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.estateTypeSaleTv.setTextColor(Color.parseColor("#00B483"))
            binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.estateTypeRentTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rendPeriodLinear.visibility = View.GONE
            parentFrag.contractType = "SALE"
            parentFrag.rentType = null
        }

        binding.estateTypeRentTv.setOnClickListener {
            binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.estateTypeSaleTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.estateTypeRentTv.setTextColor(Color.parseColor("#00B483"))
            binding.rendPeriodLinear.visibility = View.VISIBLE
            binding.rentTypeDailyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeMonthlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeYearlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeOpiningTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeDailyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeMonthlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeYearlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeOpiningTv.setTextColor(Color.parseColor("#ABA9AF"))
            parentFrag.contractType = "RENT"
            parentFrag.rentType = null
        }

        binding.rentTypeDailyTv.setOnClickListener {
            binding.rentTypeDailyTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.rentTypeMonthlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeYearlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeOpiningTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeMonthlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeYearlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeOpiningTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeDailyTv.setTextColor(Color.parseColor("#00B483"))
            parentFrag.rentType = "DAILY"
        }

        binding.rentTypeMonthlyTv.setOnClickListener {
            binding.rentTypeMonthlyTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.rentTypeDailyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeYearlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeOpiningTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeDailyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeYearlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeOpiningTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeMonthlyTv.setTextColor(Color.parseColor("#00B483"))
            parentFrag.rentType = "MONTHLY"
        }

        binding.rentTypeYearlyTv.setOnClickListener {
            binding.rentTypeYearlyTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.rentTypeDailyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeMonthlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeOpiningTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeDailyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeMonthlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeOpiningTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeYearlyTv.setTextColor(Color.parseColor("#00B483"))
            parentFrag.rentType = "YEARLY"
        }

        binding.rentTypeOpiningTv.setOnClickListener {
            binding.rentTypeOpiningTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.rentTypeDailyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeMonthlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeYearlyTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.rentTypeDailyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeMonthlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeYearlyTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.rentTypeOpiningTv.setTextColor(Color.parseColor("#00B483"))
            parentFrag.rentType = "OPENING"
        }

    }

    private fun displayRegionInSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, regionsNameList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.regionsSp.adapter = adapter
        if (parentFrag.region != null) {
            regionsList.forEachIndexed { index, element ->
                if (element.id == parentFrag.region) {
                    binding.regionsSp.setSelection(index + 1)
                    viewModel.getAllCityWithoutPagination(
                        token = (activity as HomeActivity).token,
                        regionId = regionsList[binding.regionsSp.selectedItemPosition - 1].id.toString(),
                        myLang = (activity as HomeActivity).myLang,
                    )
                }
            }
        }

    }

    private fun setNeighborhoodLocationIfChosen() {
        if ((binding.neighborhoodSp.selectedItemPosition != 0) and (!parentFrag.areaList.isNullOrEmpty())) {
            try {
                (activity as HomeActivity).moveCameraGoogleMapToEstateLocationLatLong =
                    LatLng(
                        parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].location.coordinates[1],
                        parentFrag.areaList[binding.neighborhoodSp.selectedItemPosition - 1].location.coordinates[0]
                    )
            } catch (e: Exception) {
            }
        }
        val dialogFragment = AddNewEstateLocationOnMapFragment(this)
        dialogFragment.show(requireActivity().supportFragmentManager, "signature")
    }

    private fun initialUnitsInBuildingToChooseFloorRv() {
        unitsInFloorsInAddEstateAdapter = UnitsInFloorsInAddEstateAdapter(
            this
        )
        binding.floorsAndUnitsRv.apply {
            adapter = unitsInFloorsInAddEstateAdapter
        }
    }

    private fun getAndDisplayInfoIfMainCategoryIsPlannedOrBuilding(
        categoryType: String,
        area: Int
    ) {
        var categoryIds = ""
        if (categoryType == "PLANNED") {
            mainCategoryList.forEach {
                if (it.type == "LAND")
                    if (categoryIds.isEmpty())
                        categoryIds = it.id.toString()
                    else categoryIds = categoryIds + "," + it.id.toString()
            }
        }
        if (categoryType == "BUILDING") {
            mainCategoryList.forEach {
                if (it.type == "NORMAL")
                    if (it.type == "NORMAL")
                        if (categoryIds.isEmpty())
                            categoryIds = it.id.toString()
                        else categoryIds = categoryIds + "," + it.id.toString()
            }
        }
        addNewEstateAndModifyViewModel.getLiveSearch(
            token = (activity as HomeActivity).token,
            myLang = (activity as HomeActivity).myLang,
            myId = Integer.parseInt((activity as HomeActivity).myId),
            category = categoryIds,
            related = false,
            area = area
        )
    }

    private fun displayAreaInSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, areaNameList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.neighborhoodSp.adapter = adapter
        if (parentFrag.area != null)
            parentFrag.areaList.forEachIndexed { index, element ->
                if (element.id == parentFrag.area) {
                    binding.neighborhoodSp.setSelection(index + 1)
                }
            }
//        if (((activity as HomeActivity).adsByIdResponse != null) and (!areaList.isNullOrEmpty()))
//            getAndDisplayMainCategory()
    }

    private fun displayMainCategoryInSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, mainCategoryNameList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mainCategorySp.adapter = adapter
        binding.subCategorySp.setBackgroundResource(R.drawable.rounded_shape_with_green_border)

        if (parentFrag.category != null) {
            mainCategoryList.forEachIndexed { index, category ->
                if (category.id == parentFrag.category)
                    binding.mainCategorySp.setSelection(index + 1)
            }
        } else binding.mainCategorySp.setSelection(0)

    }

    private fun displaySubCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, subCategoryNameList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subCategorySp.adapter = adapter
        binding.subCatPb.visibility = View.GONE

        if (parentFrag.subCategory != null)
            subCategoryList.forEachIndexed { index, subCategoryItem ->
                if (subCategoryItem.id == parentFrag.subCategory)
                    binding.subCategorySp.setSelection(index + 1)
            }
    }

    private fun displayFloorsSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, parentFrag.floorList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.floorsNumberSp.adapter = adapter
        if ((activity as HomeActivity).adsByIdResponse != null) {
            if (!(activity as HomeActivity).adsByIdResponse!!.advertisement.units.isNullOrEmpty()) {
                var maxFloor = 0
                parentFrag.adsResponse.advertisement.units.forEach {
                    if (it.floor >= maxFloor)
                        maxFloor = it.floor
                }
                parentFrag.floors = maxFloor
                binding.floorsNumberSp.setSelection(parentFrag.floors!!)
            }
        } else {
            if ((parentFrag.floors != null) and
                (mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].type == "BUILDING")
            ) {
                binding.floorsNumberSp.setSelection(parentFrag.floors!!)
            } else binding.floorsNumberSp.setSelection(0)
        }
    }

    private fun initialCityRv() {
        cityAdapter = CityAdapter(this)
        binding.cityRv.apply {
            adapter = cityAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    private fun initialLandsNumberRv() {
        landInPlannedAdapter = LandsInPlannedAdapter(this)
        binding.landNumberRv.apply {
            adapter = landInPlannedAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    override fun onItemClick(city: City, position: Int) {
        if (((activity as HomeActivity).adsByIdResponse == null) or
            isNotRelatedBuildingOrPlanned()
        ) {
            parentFrag.city = city.id
            if (!parentFrag.areaList.isNullOrEmpty()) {
                if (city.id == parentFrag.areaList[0].city) {
                    displayAreaInSpinner()
                } else {
                    viewModel.getAllAreaWithoutPagination(
                        token = (activity as HomeActivity).token.toString(),
                        cityId = city.id.toString(),
                        (activity as HomeActivity).myLang
                    )
                }
            } else {
                viewModel.getAllAreaWithoutPagination(
                    token = (activity as HomeActivity).token.toString(),
                    cityId = city.id.toString(),
                    (activity as HomeActivity).myLang
                )
            }
            cityList.forEach {
                it.selected = city.id == it.id
                cityAdapter.notifyDataSetChanged()
            }
            (activity as HomeActivity).moveCameraGoogleMapToEstateLocationLatLong =
                LatLng(city.location.coordinates[1], city.location.coordinates[0])
        } else Toast(requireContext()).showCustomToast(
            "تغيير عنوان العمارات والمخططات المرتبطة غير مسموح",
            requireActivity()
        )
    }

    private fun isNotRelatedBuildingOrPlanned(): Boolean {
        if ((activity as HomeActivity).adsByIdResponse != null) {
            return (((activity as HomeActivity).adsByIdResponse!!.advertisement.units.isNullOrEmpty())
                    and ((activity as HomeActivity).adsByIdResponse!!.advertisement.lands.isNullOrEmpty())
                    )
        } else return true
    }

    private fun displayDataIfExist() {
        if ((parentFrag.city != 0) and (cityList.size > 0)) {
            cityList.forEach {
                it.selected = parentFrag.city == it.id
                cityAdapter.notifyDataSetChanged()
            }
            if (!parentFrag.areaList.isNullOrEmpty()) {
                if (parentFrag.city == parentFrag.areaList[0].city) {
                    displayAreaInSpinner()
                } else {
                    viewModel.getAllAreaWithoutPagination(
                        token = (activity as HomeActivity).token,
                        cityId = parentFrag.city.toString(),
                        (activity as HomeActivity).myLang
                    )
                }
            } else {
                viewModel.getAllAreaWithoutPagination(
                    token = (activity as HomeActivity).token,
                    cityId = parentFrag.city.toString(),
                    (activity as HomeActivity).myLang
                )
            }
            binding.streetNameEt.setText(parentFrag.streetName)
        }

        if (!TextUtils.isEmpty(parentFrag.contractType)) {
            if (parentFrag.contractType == "SALE") {
                binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                binding.estateTypeSaleTv.setTextColor(Color.parseColor("#00B483"))
                binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_edit_text)
                binding.estateTypeRentTv.setTextColor(Color.parseColor("#ABA9AF"))
                binding.rendPeriodLinear.visibility = View.GONE
            }
            if (parentFrag.contractType == "RENT") {
                binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_edit_text)
                binding.estateTypeSaleTv.setTextColor(Color.parseColor("#ABA9AF"))
                binding.estateTypeRentTv.setTextColor(Color.parseColor("#00B483"))
                binding.rendPeriodLinear.visibility = View.VISIBLE
                if (parentFrag.rentType == "DAILY") {
                    binding.rentTypeDailyTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                    binding.rentTypeDailyTv.setTextColor(Color.parseColor("#00B483"))
                }
                if (parentFrag.rentType == "MONTHLY") {
                    binding.rentTypeMonthlyTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                    binding.rentTypeMonthlyTv.setTextColor(Color.parseColor("#00B483"))
                }
                if (parentFrag.rentType == "YEARLY") {
                    binding.rentTypeYearlyTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                    binding.rentTypeYearlyTv.setTextColor(Color.parseColor("#00B483"))
                }
                if (parentFrag.rentType == "OPENING") {
                    binding.rentTypeOpiningTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                    binding.rentTypeOpiningTv.setTextColor(Color.parseColor("#00B483"))
                }
            }
        }

    }

    private fun displayOtherDataIfExist() {
        if (parentFrag.floors != null)
            binding.floorsNumberSp.setSelection(parentFrag.floors!!)

        if (!parentFrag.unitsListInfo.isNullOrEmpty()) {
            displayBuildingView()
            unitsInFloorsInAddEstateAdapter.differ.submitList(parentFrag.unitsListInfo)
            binding.floorAndUnitTitle.visibility = View.VISIBLE
            binding.buildingNumberEt.visibility = View.VISIBLE
            binding.floorAndUnitTitle.text =
                "اختر الوحدات المخصصة للبيع أو الإيجار داخل العمارة"
            binding.buildingNumberEt.setText(parentFrag.unitNumber)
        }

        if (!parentFrag.landsListInfo.isNullOrEmpty()) {
            displayPlannedView()
            landInPlannedAdapter.differ.submitList(parentFrag.landsListInfo)
            binding.floorAndUnitTitle.visibility = View.VISIBLE
            binding.startSealDateTvRl.visibility = View.VISIBLE
            binding.floorAndUnitTitle.text =
                "اختر قطعة الارض المخصصة للبيع داخل المخطط"
            binding.buildingNumberEt.visibility = View.VISIBLE
            binding.buildingNumberEt.setText(parentFrag.unitNumber)
            if (parentFrag.startSaleDate != "")
                binding.startSealDateTv.text = parentFrag.startSaleDate
        }

        GeneralFunctions.hideKeyboard(requireActivity())

        if ((activity as HomeActivity).adsByIdResponse != null)
            if (!((activity as HomeActivity).adsByIdResponse!!.advertisement.units.isNullOrEmpty())
                or (!(activity as HomeActivity).adsByIdResponse!!.advertisement.lands.isNullOrEmpty())
            ) {
                binding.mainCategorySpIv.visibility = View.GONE
                binding.mainCategorySp.isEnabled = false
                binding.regionIv.visibility = View.GONE
                binding.regionsSp.isEnabled = false
                binding.neighborhoodIv.visibility = View.GONE
                binding.neighborhoodSp.isEnabled = false
            }
    }

    override fun submitLocationOfEstate(location: ArrayList<Double>) {
        parentFrag.location = location
        //  Toast(context).showCustomToast(parentFrag.location.toString(), requireActivity())
    }

    override fun landOnItemClick(position: Int, landInfo: Data, isSelected: Boolean) {
        parentFrag.landsListInfo[position].isSelected = isSelected
        landInPlannedAdapter.differ.submitList(parentFrag.landsListInfo)
    }

    override fun unitOnItemClick(
        floorNumber: Int,
        unitData: Data,
        isSelected: Boolean,
        unitPosition: Int
    ) {
        if (isSelected) {
            parentFrag.unitsListInfo!![unitPosition].selectedFloorNumber = floorNumber
        } else {
            parentFrag.unitsListInfo!![unitPosition].selectedFloorNumber = 200
        }
        parentFrag.unitsListInfo!![unitPosition].isSelected = isSelected
        unitsInFloorsInAddEstateAdapter.differ.submitList(parentFrag.unitsListInfo)

        Log.d("unit_selected", isSelected.toString())
        Log.d("unit_selected_position", unitPosition.toString())
        Log.d("unit_selected_id", unitData.id.toString())
        Log.d("unit_floorNumber", unitData.selectedFloorNumber.toString())
        Log.d("unit_selectedFloor", unitData.selectedFloorNumber.toString())
    }

}
