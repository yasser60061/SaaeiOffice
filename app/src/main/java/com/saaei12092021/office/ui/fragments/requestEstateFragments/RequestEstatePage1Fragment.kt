package com.saaei12092021.office.ui.fragments.requestEstateFragments

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.saaei12092021.office.adapters.CityAdapter
import com.saaei12092021.office.databinding.FragmentRequestEstatePage1Binding
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.WorkAreaAdapter
import com.saaei12092021.office.model.responseModel.areasResponse.Area
import com.saaei12092021.office.model.responseModel.citiesResponse.City
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory
import com.saaei12092021.office.model.responseModel.regionResponse.Region
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class RequestEstatePage1Fragment : Fragment(),
    CityAdapter.OnItemClickListener,
    WorkAreaAdapter.OnChooseChangeListener {

    private var _binding: FragmentRequestEstatePage1Binding? = null
    lateinit var cityAdapter: CityAdapter
    lateinit var areaAdapter: WorkAreaAdapter // we use this adapter to multi select area in request estate
    val binding get() = _binding!!
    lateinit var parentFrag: MainRequestEstateFragment
    lateinit var viewModel: MyViewModel
    lateinit var cityList: ArrayList<City>
    lateinit var mainCategoryList: ArrayList<Category>
    lateinit var mainCategoryNameList: ArrayList<String>
    lateinit var subCategoryList: ArrayList<SubCategory>
    lateinit var subCategoryNameList: ArrayList<String>
    lateinit var regionsList: ArrayList<Region>
    lateinit var regionsNameList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestEstatePage1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFrag = this@RequestEstatePage1Fragment.parentFragment as MainRequestEstateFragment

        regionsList = ArrayList()
        regionsList.clear()
        regionsNameList = ArrayList()
        cityList = ArrayList()
        cityList.clear()
        mainCategoryList = ArrayList<Category>()
        mainCategoryList.clear()
        mainCategoryNameList = ArrayList<String>()
        subCategoryList = ArrayList<SubCategory>()
        subCategoryList.clear()
        subCategoryNameList = ArrayList<String>()

        initialAreaInThisCityRvAndAdapter()
        initialCityRvAndAdapter()

        parentFrag.binding.titleLinear.visibility = View.VISIBLE
        parentFrag.binding.progressPointerLinear.visibility = View.VISIBLE
        parentFrag.binding.pageNameTv.visibility = View.VISIBLE

        binding.cityTempRl.setOnClickListener {
            Toast(context).showCustomToast("الرجاء اختيار المنطقة أولا", requireActivity())
        }

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

        binding.nextBtn.setOnClickListener {
            parentFrag.selectedAreaList.clear()
            parentFrag.areaList.forEach { areaListItem ->
                if (areaListItem.selected)
                    parentFrag.selectedAreaList.add(areaListItem.id)
            }
            if (parentFrag.region == null)
                Toast(context).showCustomToast("الرجاء اختيار المنطقة", requireActivity())
            else if (parentFrag.city == 0)
                Toast(context).showCustomToast("الرجاء اختيار المدينة", requireActivity())
            else if (parentFrag.selectedAreaList.isNullOrEmpty())
                Toast(context).showCustomToast("الرجاء اختيار حي او اكثر", requireActivity())
            else if (binding.mainCategorySp.selectedItemPosition == 0)
                Toast(context).showCustomToast("الرجاء اختيار التصنيف الرئيسي", requireActivity())
            else if (binding.subCategorySp.selectedItemPosition == 0)
                Toast(context).showCustomToast("الرجاء اختيار التصنيف الفرعي", requireActivity())
            else if ((parentFrag.contractType == "RENT") and (parentFrag.rentType == null))
                Toast(context).showCustomToast("الرجاء اختيار مدة الإيجار", requireActivity())
            else if (binding.realEstateAreaMinTv.text.toString() == "")
                Toast(context).showCustomToast(
                    "اكتب الحد الأدنى من المساحة بالارقام",
                    requireActivity()
                )
            else if (binding.realEstateAreaMaxTv.text.toString() == "")
                Toast(context).showCustomToast(
                    "أكتب الحد الأقصى من المساحة بالارقام",
                    requireActivity()
                )
            else if (Integer.parseInt(binding.realEstateAreaMinTv.text.toString()) <= 10)
                Toast(context).showCustomToast(
                    "تاكد من كتابة الحد الأدنى من المساحة بشكل صحيح",
                    requireActivity()
                )
            else if (Integer.parseInt(binding.realEstateAreaMaxTv.text.toString()) >= 500000)
                Toast(context).showCustomToast(
                    "تاكد من كتابة الحد الأقصى من المساحة بشكل صحيح",
                    requireActivity()
                )
            else {
                parentFrag.category =
                    mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].id
                parentFrag.categoryName =
                    mainCategoryList[binding.mainCategorySp.selectedItemPosition - 1].categoryName
                parentFrag.subCategory =
                    subCategoryList[binding.subCategorySp.selectedItemPosition - 1].id
                parentFrag.subCategoryName =
                    subCategoryList[binding.subCategorySp.selectedItemPosition - 1].categoryName
                parentFrag.sizeFrom = Integer.parseInt(binding.realEstateAreaMinTv.text.toString())
                parentFrag.sizeTo = Integer.parseInt(binding.realEstateAreaMaxTv.text.toString())
                parentFrag.displayPage2()
            }
        }

        viewModel = (activity as HomeActivity).viewModel

        //----------------------------------------------------------------------------------
        viewModel.getAllRegionWithoutPagination(
            (activity as HomeActivity).token,
            (activity as HomeActivity).myCountry,
            (activity as HomeActivity).myLang
        )
        viewModel.regionLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
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
        viewModel.areaResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    parentFrag.areaList = it.data?.areas as ArrayList<Area>
                    initialAreaInThisCityRvAndAdapter()
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
        //  viewModel.getMainCategory() // it is called from home activity
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
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

//----------------------------------------------------------------------------------
        binding.regionsSp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    cityList.clear()
                    parentFrag.areaList.clear()
                    initialCityRvAndAdapter()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        parentFrag.region = null
                        cityList.clear()
                        parentFrag.city = 0
                        parentFrag.areaList.clear()
                        cityAdapter.differ.submitList(cityList)
                        areaAdapter.differ.submitList(parentFrag.areaList)
                        binding.cityTempRl.visibility = View.VISIBLE
                        binding.areaTempRl.visibility = View.VISIBLE
                        initialCityRvAndAdapter()
                    }
                    if (position != 0) {
                        if (parentFrag.region != null)
                            if (regionsList[binding.regionsSp.selectedItemPosition - 1].id != parentFrag.region) {
                                cityList.clear()
                                parentFrag.city = 0
                                parentFrag.areaList.clear()
                                cityAdapter.differ.submitList(cityList)
                                areaAdapter.differ.submitList(parentFrag.areaList)
                                binding.cityTempRl.visibility = View.VISIBLE
                                binding.areaTempRl.visibility = View.VISIBLE
                             //   parentFrag.region = null
                            }
                        viewModel.getAllCityWithoutPagination(
                            token = (activity as HomeActivity).token.toString(),
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
                                    displayDataIfExist()
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
//----------------------------------------------------------------------------------
        binding.mainCategorySp.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    subCategoryList.clear()
                    subCategoryNameList.clear()
                    subCategoryNameList.add("")
                    displaySubCategorySpinner()
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
                                    binding.subCatPb.visibility = View.GONE
                                }
                            }
                        })
                    }
                }
            }
    }

    private fun initialAreaInThisCityRvAndAdapter() {
        areaAdapter = WorkAreaAdapter(this)
        binding.areaRv.apply {
            adapter = areaAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
        areaAdapter.differ.submitList(parentFrag.areaList)
        binding.areaTempRl.visibility = View.GONE
    }

    private fun displayRegionInSpinner() {
        // Create an ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, regionsNameList
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.regionsSp.adapter = adapter
        if (parentFrag.region != null)
            regionsList.forEachIndexed { index, element ->
                if (element.id == parentFrag.region)
                    binding.regionsSp.setSelection(index + 1)
            }
    }


    private fun displayMainCategoryInSpinner() {
        // Create an ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custome_spinner_text, mainCategoryNameList
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.mainCategorySp.adapter = adapter
        binding.subCategorySp.setBackgroundResource(R.drawable.rounded_shape_with_green_border)

        if (parentFrag.category != null)
            mainCategoryList.forEachIndexed { index, category ->
                if (category.id == parentFrag.category)
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

        if (parentFrag.subCategory != null)
            subCategoryList.forEachIndexed { index, subCategory ->
                if (subCategory.id == parentFrag.subCategory)
                    binding.subCategorySp.setSelection(index + 1)
            }
    }

    private fun initialCityRvAndAdapter() {
        cityAdapter = CityAdapter(this)
        binding.cityRv.apply {
            adapter = cityAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
        cityAdapter.differ.submitList(cityList)
        areaAdapter.differ.submitList(parentFrag.areaList)
        binding.cityTempRl.visibility = View.VISIBLE
        binding.areaTempRl.visibility = View.VISIBLE
    }

    override fun onItemClick(city: City, position: Int) {
        parentFrag.city = city.id
        parentFrag.cityName = city.cityName
        //  Toast(context).showCustomToast(city.id.toString(), requireActivity())

        if (!parentFrag.areaList.isNullOrEmpty()) {
            if (city.id == parentFrag.areaList[0].city) {
                areaAdapter.differ.submitList(parentFrag.areaList)
                binding.areaTempRl.visibility = View.GONE
            } else {
                parentFrag.areaList.clear()
                areaAdapter.differ.submitList(parentFrag.areaList)
                binding.areaTempRl.visibility = View.VISIBLE
                viewModel.getAllAreaWithoutPagination(
                    token = (activity as HomeActivity).token.toString(),
                    cityId = city.id.toString(),
                    (activity as HomeActivity).myLang
                )
            }
        } else {
            parentFrag.areaList.clear()
            areaAdapter.differ.submitList(parentFrag.areaList)
            binding.areaTempRl.visibility = View.VISIBLE
            viewModel.getAllAreaWithoutPagination(
                token = (activity as HomeActivity).token.toString(),
                cityId = city.id.toString(),
                (activity as HomeActivity).myLang
            )
        }

        cityList.forEach {
            it.selected = city.id == it.id
            cityAdapter.notifyDataSetChanged()
            // cityAdapter.differ.submitList(cityList)
        }
    }

    private fun displayDataIfExist() {
        if ((parentFrag.city != 0) and (cityList.size > 1)) {
            cityList.forEach {
                it.selected = parentFrag.city == it.id
                cityAdapter.notifyDataSetChanged()

                if (!parentFrag.areaList.isNullOrEmpty()) {
                    if (parentFrag.city == parentFrag.areaList[0].city) {
                        areaAdapter.differ.submitList(parentFrag.areaList)
                        binding.areaTempRl.visibility = View.GONE
                    } else {
                        viewModel.getAllAreaWithoutPagination(
                            token = (activity as HomeActivity).token.toString(),
                            cityId = parentFrag.city.toString(),
                            (activity as HomeActivity).myLang
                        )
                    }
                } else {
                    viewModel.getAllAreaWithoutPagination(
                        token = (activity as HomeActivity).token.toString(),
                        cityId = parentFrag.city.toString(),
                        (activity as HomeActivity).myLang
                    )
                }
            }
        }

        if (!TextUtils.isEmpty(parentFrag.contractType)) {
            if (parentFrag.contractType == "SALE") {
                binding.estateTypeSaleTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                binding.estateTypeSaleTv.setTextColor(Color.parseColor("#00B483"))
                binding.estateTypeRentTv.setBackgroundResource(R.drawable.rounded_edit_text)
                binding.estateTypeRentTv.setTextColor(Color.parseColor("#ABA9AF"))
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

        if (parentFrag.sizeFrom != 0)
            binding.realEstateAreaMinTv.setText(parentFrag.sizeFrom.toString())
        if (parentFrag.sizeTo != 0)
            binding.realEstateAreaMaxTv.setText(parentFrag.sizeTo.toString())
    }

    override fun workAreaOnItemClick(areaPosition: Int, areaId: Int, selected: Boolean) {
        parentFrag.areaList[areaPosition].selected = selected
        areaAdapter.differ.submitList(parentFrag.areaList)
    }
}
