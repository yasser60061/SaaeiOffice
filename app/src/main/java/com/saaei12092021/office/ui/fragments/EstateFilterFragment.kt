package com.saaei12092021.office.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.CityAdapter
import com.saaei12092021.office.databinding.FragmentEstateFilterBinding
import com.saaei12092021.office.model.responseModel.areasResponse.Area
import com.saaei12092021.office.model.responseModel.citiesResponse.City
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class EstateFilterFragment : BottomSheetDialogFragment(), CityAdapter.OnItemClickListener {

    private var _binding: FragmentEstateFilterBinding? = null
    private val binding get() = _binding!!
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    var areaId: Int = 0
    var cityId: Int = 0
    var cityLat: Double = 0.0
    var cityLong: Double = 0.0

    var category: Int = 0
    var subCategory: Int = 0
    var contractType: String = ""
    var priceType: String = ""
    var priceTo: Int? = null
    var startDate = ""
    var endDate = ""

    lateinit var cityAdapter: CityAdapter
    lateinit var viewModel: MyViewModel
    lateinit var cityList: ArrayList<City>
    lateinit var areaList: ArrayList<Area>
    lateinit var areaNameList: ArrayList<String>
    lateinit var mainCategoryList: ArrayList<Category>
    lateinit var mainCategoryNameList: ArrayList<String>
    lateinit var subCategoryList: ArrayList<SubCategory>
    lateinit var subCategoryNameList: ArrayList<String>
    lateinit var periodList: ArrayList<String>
    lateinit var myCountry: String
    lateinit var adsFromSocketFilterMap: HashMap<String, Any>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstateFilterBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeRl.setOnClickListener {
            dismiss()
        }

        viewModel = (activity as HomeActivity).viewModel
        cityList = ArrayList()
        cityList.clear()
        areaList = ArrayList()
        areaList.clear()
        areaNameList = ArrayList<String>()
        areaNameList.add("")
        displayAreaInSpinner()
        periodList = ArrayList<String>()
        periodList.clear()
        periodList.add("اختر الفترة")
        displayPeriodInSpinner()
        mainCategoryList = ArrayList<Category>()
        mainCategoryList.clear()
        mainCategoryNameList = ArrayList<String>()
        subCategoryList = ArrayList<SubCategory>()
        subCategoryList.clear()
        subCategoryNameList = ArrayList<String>()
        myCountry = Constant.getMyCountryId(requireContext())

        displayCity()
        viewModel.getAllCityInAllCountryWithoutPagination(
            token = (activity as HomeActivity).token.toString(),
            regionId = "1",
            myLang = (activity as HomeActivity).myLang,
            country = myCountry
        )

        binding.estateTypeSaleTv.setOnClickListener {
            binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.estateTypeSaleTv.setTextColor(Color.parseColor("#00B483"))
            binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.estateTypeRentTv.setTextColor(Color.parseColor("#ABA9AF"))
            contractType = "SALE"
        }

        binding.estateTypeRentTv.setOnClickListener {
            binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.estateTypeSaleTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.estateTypeRentTv.setTextColor(Color.parseColor("#00B483"))
            contractType = "RENT"
        }

        binding.priceToSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                priceTo = progressChangedValue
                binding.priceToTv.text = priceTo.toString() + "  " + "ريال"

            }
        })

        binding.priceTypeNormalCb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.priceTypeOnsoomCb.isChecked = false
                priceType = "NORMAL"
            } else {
                priceType = ""
            }
        }

        binding.priceTypeOnsoomCb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.priceTypeNormalCb.isChecked = false
                priceType = "ON-SOOM"
            } else {
                priceType = ""
            }
        }

        binding.searchBtn.setOnClickListener {
            if (areaList.isNotEmpty())
                if (binding.neighborhoodSp.selectedItemPosition != 0)
                    areaId = areaList[binding.neighborhoodSp.selectedItemPosition - 1].id
            if (mainCategoryList.isNotEmpty())
                if (binding.mainCategorySp.selectedItemPosition != 0)
                    category = mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].id
            if (subCategoryList.isNotEmpty())
                if (binding.subCategorySp.selectedItemPosition != 0)
                    subCategory = subCategoryList[binding.subCategorySp.selectedItemPosition - 1].id
            adsFromSocketFilterMap = HashMap()
            adsFromSocketFilterMap.clear()
            adsFromSocketFilterMap["limit"] = 50

            if (cityId != 0)
                adsFromSocketFilterMap["city"] = cityId

            if (areaId != 0)
                adsFromSocketFilterMap["area"] = areaId
            if (category != 0)
                adsFromSocketFilterMap["category"] = category
            if (subCategory != 0)
                adsFromSocketFilterMap["subCategory"] = subCategory
            if (contractType != "")
                adsFromSocketFilterMap["contractType"] = contractType
            if (priceType != "")
                adsFromSocketFilterMap["priceType"] = priceType
            if (priceTo != null) {
                adsFromSocketFilterMap["priceFrom"] = 0
                adsFromSocketFilterMap["priceTo"] = priceTo!!
            }
            if ((startDate != "") and (endDate != "")) {
                adsFromSocketFilterMap["startDate"] = startDate
                adsFromSocketFilterMap["endDate"] = endDate
            }
            if (adsFromSocketFilterMap.size == 1) {
                Constant.makeToastMessage(requireContext(), "اختر بعض التصفيات")
            } else {
                if ((activity as HomeActivity).currentLat != 0.0) {
                    when {
                        areaId != 0 -> {
                            try { // i used try and catch because some of location in integer from backend
                                val areaLat =
                                    areaList[binding.neighborhoodSp.selectedItemPosition - 1].location.coordinates[1]
                                val areaLong =
                                    areaList[binding.neighborhoodSp.selectedItemPosition - 1].location.coordinates[0]
                                adsFromSocketFilterMap["lat"] = areaLat
                                adsFromSocketFilterMap["long"] = areaLong
                                (activity as HomeActivity).moveCameraInGoogleMapLatLong =
                                    LatLng(areaLat, areaLong)
                            } catch (e: Exception) {
                            }
                        }
                        cityId != 0 -> {
                            adsFromSocketFilterMap["lat"] = cityLat
                            adsFromSocketFilterMap["long"] = cityLong
                            (activity as HomeActivity).moveCameraInGoogleMapLatLong =
                                LatLng(cityLat, cityLong)
                            //  Toast(requireActivity()).showCustomToast(cityLat.toString(),requireActivity())
                        }
                        else -> {
                            adsFromSocketFilterMap["lat"] =
                                (activity as HomeActivity).currentLat!!.toDouble()
                            adsFromSocketFilterMap["long"] =
                                (activity as HomeActivity).currentLong!!.toDouble()
                        }
                    }
                    adsFromSocketFilterMap["km"] = 4
                }

                (activity as HomeActivity).viewModel.getAdsFromSocket(adsFromSocketFilterMap)
                var unselectedAllSubCategory: Boolean = false
                if (adsFromSocketFilterMap.size != 1)
                    unselectedAllSubCategory = true
                (activity as HomeActivity).uncheckedAllCategory(unselectedAllSubCategory)
                Log.d("adsFromSocketFilterMap_", adsFromSocketFilterMap.toString())
                dismiss()
            }
        }

        //----------------------------------------------------------------------------------
        viewModel.citiesResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    cityList = it.data?.cities as ArrayList<City>
                    cityAdapter.differ.submitList(cityList)
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
        viewModel.areaResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    areaNameList.clear()
                    areaNameList.add("اختر اسم الحي")
                    areaList = it.data?.areas as ArrayList<Area>
                    for (item in areaList)
                        areaNameList.add(item.areaName)
                    displayAreaInSpinner()
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {}
            }
        })

        viewModel.mainCategoryResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    mainCategoryNameList.clear()
                    mainCategoryNameList.add("الرئيسي")

                    val mainCategoryListTemp = it.data?.categories as ArrayList<Category>
                    mainCategoryListTemp.forEach { categoryItem ->
                        if (categoryItem.id != 0)
                            mainCategoryList.add(categoryItem)
                    }

                    for (item in mainCategoryList)
                        mainCategoryNameList.add(item.categoryName)
                    displayMainCategoryInSpinner()
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {}
            }
        })

        //----------------------------------------------------------------------------------
        binding.mainCategorySp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    subCategoryList.clear()
                    subCategoryNameList.clear()
                    subCategoryNameList.add("")
                    displaySubCategorySpinner()
                    subCategory = 0
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        subCategoryList.clear()
                        subCategoryNameList.clear()
                        subCategoryNameList.add("")
                        displaySubCategorySpinner()
                        category = 0
                    }
                    if (position != 0) {
                        binding.subCatPb.visibility = View.VISIBLE
                        viewModel.getSubCategory(
                            mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].id.toString(),
                            (activity as HomeActivity).myLang
                        )
                        viewModel.subCategoryResponse.observe(viewLifecycleOwner, Observer {
                            when (it) {
                                is Resource.Success -> {
                                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                        View.GONE
                                    subCategoryNameList.clear()
                                    subCategoryNameList.add("الفرعي")
                                    subCategoryList =
                                        it.data?.subSubCategory as ArrayList<SubCategory>
                                    for (item in subCategoryList)
                                        subCategoryNameList.add(item.categoryName)
                                    displaySubCategorySpinner()
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
                }
            }

        binding.dateOfAdsSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    startDate = ""
                    endDate = ""
                } else if (position != 0) {
                    val currentTime = Calendar.getInstance().time.toString()
                    val currentDatAndTimeAsLong = Date(currentTime)
                    val currentSdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val currentDate = currentSdf.format(currentDatAndTimeAsLong)
                    endDate = currentDate.toString()
                    Log.d("currentDate_", currentDate.toString())

                    if (position == 1) {
                        val datAndTimeAsLong =
                            Date(currentDatAndTimeAsLong.time - (1 * 24 * 60 * 60 * 1000))
                        startDate = currentSdf.format(datAndTimeAsLong).toString()
                        Log.d("beforeOneDayDate_", currentSdf.format(datAndTimeAsLong).toString())
                    }
                    if (position == 2) {
                        val datAndTimeAsLong =
                            Date(currentDatAndTimeAsLong.time - (7 * 24 * 60 * 60 * 1000))
                        startDate = currentSdf.format(datAndTimeAsLong).toString()
                        Log.d("beforeOneWeekDate_", currentSdf.format(datAndTimeAsLong).toString())
                    }
                    if (position == 3) {
                        val datAndTimeAsLong =
                            Date(currentDatAndTimeAsLong.time - (14 * 24 * 60 * 60 * 1000))
                        startDate = currentSdf.format(datAndTimeAsLong).toString()
                        Log.d("beforeTowWeeksDate_", currentSdf.format(datAndTimeAsLong).toString())
                    }
                    if (position == 4) {
                        val datAndTimeAsLong =
                            Date(currentDatAndTimeAsLong.time - (15 * 24 * 60 * 60 * 1000) - (15 * 24 * 60 * 60 * 1000))
                        startDate = currentSdf.format(datAndTimeAsLong).toString()
                        Log.d(
                            "beforeOneMonthsDate_",
                            currentSdf.format(datAndTimeAsLong).toString()
                        )
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun displayCity() {
        cityAdapter = CityAdapter(this)
        binding.cityRv.apply {
            adapter = cityAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    private fun displayAreaInSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, areaNameList
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.neighborhoodSp.adapter = adapter
        if (areaId != 0)
            areaList.forEachIndexed { index, element ->
                if (element.id == areaId)
                    binding.neighborhoodSp.setSelection(index + 1)
            }
    }

    private fun displayPeriodInSpinner() {
        periodList.add("اليوم")
        periodList.add("اخر اسبوع")
        periodList.add("اخر اسبوعين")
        periodList.add("اخر شهر")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, periodList
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.dateOfAdsSp.adapter = adapter

    }

    override fun onItemClick(city: City, position: Int) {
        this.cityId = city.id
        cityLat = city.location.coordinates[1]
        cityLong = city.location.coordinates[0]
        viewModel.getAllAreaWithoutPagination(
            token = (activity as HomeActivity).token.toString(),
            cityId = city.id.toString(),
            (activity as HomeActivity).myLang
        )

        cityList.forEach {
            it.selected = city.id == it.id
            cityAdapter.notifyDataSetChanged()
            // cityAdapter.differ.submitList(cityList)
        }
    }

    private fun displayMainCategoryInSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, mainCategoryNameList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mainCategorySp.adapter = adapter
        binding.subCategorySp.setBackgroundResource(R.drawable.rounded_shape_with_green_border)

        if (category != 0)
            mainCategoryList.forEachIndexed { index, categoryItem ->
                if (category == categoryItem.id)
                    binding.mainCategorySp.setSelection(index + 1)
            }

        displaySubCategorySpinner()
    }

    private fun displaySubCategorySpinner() {
        // Create an ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, subCategoryNameList
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.subCategorySp.adapter = adapter
        binding.subCatPb.visibility = View.GONE

        if (subCategory != 0)
            subCategoryList.forEachIndexed { index, subCategoryItem ->
                if (subCategory == subCategoryItem.id)
                    binding.subCategorySp.setSelection(index + 1)
            }
    }
}