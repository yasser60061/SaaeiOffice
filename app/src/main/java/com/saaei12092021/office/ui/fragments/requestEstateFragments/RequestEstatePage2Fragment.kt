package com.saaei12092021.office.ui.fragments.requestEstateFragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.saaei12092021.office.adapters.SubFeaturesFromListAdapter
import com.saaei12092021.office.adapters.MainFeaturesListInRequestEstateAdapter
import com.saaei12092021.office.databinding.FragmentRequestEstatePage2Binding
import com.saaei12092021.office.model.socketRequest.addAdsRequest.Property
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Feature
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class RequestEstatePage2Fragment : Fragment(),
    SubFeaturesFromListAdapter.OnCheckedChangeListener,
    MainFeaturesListInRequestEstateAdapter.OnCheckedChangeListener,
    MainFeaturesListInRequestEstateAdapter.OptionOnItemClick2 {

    var _binding: FragmentRequestEstatePage2Binding? = null
    val binding get() = _binding!!
    lateinit var parentFrag: MainRequestEstateFragment
    lateinit var viewModel: MyViewModel
    lateinit var subFeaturesFromListAdapter: SubFeaturesFromListAdapter
    lateinit var mainFeaturesListInRequestEstateAdapter: MainFeaturesListInRequestEstateAdapter
    lateinit var emptyList: ArrayList<Feature>
    lateinit var allFeaturesList: ArrayList<Feature>
    lateinit var mainFeaturesList: ArrayList<Feature>
    lateinit var subFeaturesList: ArrayList<Feature>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestEstatePage2Binding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFrag = this@RequestEstatePage2Fragment.parentFragment as MainRequestEstateFragment

        allFeaturesList = ArrayList()
        subFeaturesList = ArrayList()
        mainFeaturesList = ArrayList()
        emptyList = ArrayList()

        displayMainFeatures()
        displaySubFeatures()
        displayDataIfExist()

        binding.displayMainFeatureTv.setOnClickListener {
            binding.displayMainFeatureTv.visibility = View.INVISIBLE
            binding.hideMainFeatureTv.visibility = View.VISIBLE
            binding.mainFeaturesRv.visibility = View.VISIBLE
            parentFrag.binding.titleLinear.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }
        binding.hideMainFeatureTv.setOnClickListener {
            binding.hideMainFeatureTv.visibility = View.INVISIBLE
            binding.displayMainFeatureTv.visibility = View.VISIBLE
            binding.mainFeaturesRv.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }

        binding.displaySubFeatureTv.setOnClickListener {
            binding.displaySubFeatureTv.visibility = View.INVISIBLE
            binding.hideSubFeatureTv.visibility = View.VISIBLE
            binding.subFeaturesRv.visibility = View.VISIBLE
            parentFrag.binding.titleLinear.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }
        binding.hideSubFeatureTv.setOnClickListener {
            binding.hideSubFeatureTv.visibility = View.INVISIBLE
            binding.displaySubFeatureTv.visibility = View.VISIBLE
            binding.subFeaturesRv.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }

        binding.previousBtn.setOnClickListener {
            parentFrag.displayPage1()
        }

        binding.nextBtn.setOnClickListener {
            if (mainFeaturesList.isNullOrEmpty() or subFeaturesList.isNullOrEmpty()) {
                Toast(requireActivity()).showCustomToast("انتظر تحميل البيانات", requireActivity())
            } else {
                parentFrag.features.clear()
                parentFrag.featuresName.clear()
                parentFrag.properties.clear()
                parentFrag.propertiesName?.clear()
                subFeaturesList.forEach {
                    if (it.isSelected) {
                        parentFrag.features.add(it.id)
                        parentFrag.featuresName.add(it.name_ar)
                    }
                }
                mainFeaturesList.forEach { featureItem ->
                    if (featureItem.isSelected) {
                        if (featureItem.type == "NUMBER") {
                            parentFrag.properties.add(
                                Property(
                                    from = featureItem.from,
                                    property = featureItem.id.toString(),
                                    to = featureItem.to,
                                    value = null
                                )
                            )
                        }
                        if (featureItem.type == "LIST") {
                            val valueTemp: ArrayList<String> = ArrayList()
                            featureItem.options!!.forEach { optionItem ->
                                if (optionItem.isSelected)
                                    valueTemp.add(optionItem.id.toString())
                            }
                            parentFrag.properties.add(
                                Property(
                                    property = featureItem.id.toString(),
                                    from = null,
                                    to = null,
                                    value = valueTemp
                                )
                            )
                        }
                        parentFrag.propertiesName?.add(featureItem.name)
                    }
                }
//            if (parentFrag.features.isEmpty())
//                Constant.makeToastMessage(requireContext(), "الرجاء اختيار بعض المميزات للعقار")
//            else
                if (parentFrag.properties.isEmpty())
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء ادخال بعض محتويات العقار بالارقام"
                    )
                else if (TextUtils.isEmpty(binding.otherSpecificationTv.text.toString()))
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء ادخال بعض المواصفات الاخرى"
                    )
                else if (TextUtils.isEmpty(binding.minBudgetEt.text.toString()))
                    Constant.makeToastMessage(requireContext(), "الرجاء ادخال اقل ميزانية متوقعة")
                else if (TextUtils.isEmpty(binding.maxBudgetEt.text.toString()))
                    Constant.makeToastMessage(requireContext(), "الرجاء ادخال اكبر ميزانية متوقعة")
                else {
                    parentFrag.description_ar = binding.otherSpecificationTv.text.toString().trim()
                    parentFrag.description_en = binding.otherSpecificationTv.text.toString().trim()
                    parentFrag.priceFrom = Integer.parseInt(binding.minBudgetEt.text.toString())
                    parentFrag.priceTo = Integer.parseInt(binding.maxBudgetEt.text.toString())
                    parentFrag.enablePhoneContact = binding.allowCommunicationViaCb.isChecked
                    parentFrag.enableInstallment = binding.doYouNeedInstallmentCb.isChecked

                    parentFrag.mainFeaturesList = mainFeaturesList

                    parentFrag.displayPage3()
                }
            }
        }

        viewModel = (activity as HomeActivity).viewModel
//----------------------------------------------------------------------------------
        viewModel.getMainFeatures(myLang = (activity as HomeActivity).myLang , category = parentFrag.subCategory!!)
        viewModel.mainFeaturesResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    allFeaturesList = it.data?.features as ArrayList<Feature>
                    subFeaturesList.clear()
                    mainFeaturesList.clear()

                    val tempList: ArrayList<Feature> = ArrayList()
                    allFeaturesList.forEach { item ->
                        if (item.type == "BOOLEAN")
                            subFeaturesList.add(item)
                        else {
                            if (item.type == "NUMBER")
                                tempList.add(0, item)
                            else tempList.add(item)
                        }
                    }

                    tempList.forEachIndexed { index1, allFeatureItem ->
                        allFeatureItem.isSelected = false
                        allFeatureItem.from = ""
                        allFeatureItem.to = ""
                        if (allFeatureItem.type == "LIST") {
                            allFeatureItem.options!!.forEachIndexed { index2 , optionElements ->
                                optionElements.isSelected = false
                                optionElements.thisFeaturePosition = index1
                                optionElements.thisFeatureId = allFeatureItem.id
                            }
                        }
                    }
                    mainFeaturesList = tempList

                    if (parentFrag.features.isNotEmpty())
                        parentFrag.features.forEachIndexed { index1, element1 ->
                            subFeaturesList.forEachIndexed { index2, element2 ->
                                if (element1 == element2.id)
                                    element2.isSelected = true
                            }
                        }
                    subFeaturesFromListAdapter.differ.submitList(subFeaturesList)

                    if (parentFrag.properties.isNotEmpty())
                        parentFrag.properties.forEachIndexed { index1, element1 ->
                            mainFeaturesList.forEachIndexed { index2, element2 ->
                                if (element2.type == "NUMBER") {
                                    if (element1.property == element2.id.toString()) {
                                        element2.isSelected = true
                                        element2.from = element1.from!!
                                        element2.to = element1.to!!
                                    }
                                } else if (element2.type == "LIST") {
                                    if (element1.property == element2.id.toString()) {
                                        element2.isSelected = true
                                        element1.value!!.forEach { ValueItem ->
                                            element2.options!!.forEach { optionItem ->
                                                if (ValueItem == optionItem.id.toString())
                                                    optionItem.isSelected = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    mainFeaturesListInRequestEstateAdapter.differ.submitList(mainFeaturesList)
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

    private fun displaySubFeatures() {
        subFeaturesFromListAdapter = SubFeaturesFromListAdapter(this)
        binding.subFeaturesRv.apply {
            adapter = subFeaturesFromListAdapter
        }
    }

    private fun displayMainFeatures() {
        mainFeaturesListInRequestEstateAdapter = MainFeaturesListInRequestEstateAdapter(
            this@RequestEstatePage2Fragment,
            this@RequestEstatePage2Fragment
        )
        binding.mainFeaturesRv.apply {
            adapter = mainFeaturesListInRequestEstateAdapter
        }
    }

    override fun subFeatureOnItemClick(feature: Feature, position: Int, isSelected: Boolean) {
        if (isSelected) {
            subFeaturesList[position].isSelected = true
            subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
        } else {
            subFeaturesList[position].isSelected = false
            subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
        }
        subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
    }

    override fun specificationOnItemClick(
        feature: Feature,
        position: Int,
        fromNumber: String,
        toNumber: String,
        isSelected: Boolean
    ) {
        if (isSelected) {
            mainFeaturesList[position].isSelected = true
            mainFeaturesList[position].from = fromNumber
            mainFeaturesList[position].to = toNumber
            mainFeaturesListInRequestEstateAdapter.differ.submitList(mainFeaturesList)
        } else {
            mainFeaturesList[position].isSelected = false
            mainFeaturesList[position].from = fromNumber
            mainFeaturesList[position].to = toNumber
            mainFeaturesListInRequestEstateAdapter.differ.submitList(mainFeaturesList)
        }
        mainFeaturesListInRequestEstateAdapter.differ.submitList(mainFeaturesList)
    }

    private fun displayDataIfExist() {
        if (parentFrag.contractType == "RENT")
            binding.doYouNeedInstallmentCb.visibility = View.GONE
        if (!TextUtils.isEmpty(parentFrag.description_ar))
            binding.otherSpecificationTv.setText(parentFrag.description_ar)
        if (parentFrag.priceFrom != 0)
            binding.minBudgetEt.setText(parentFrag.priceFrom.toString())
        if (parentFrag.priceTo != 0)
            binding.maxBudgetEt.setText(parentFrag.priceTo.toString())
        if (parentFrag.enableInstallment == true)
            binding.doYouNeedInstallmentCb.isChecked = true
        if (parentFrag.enablePhoneContact == true)
            binding.allowCommunicationViaCb.isChecked = true
    }

    override fun optionOnItemClick2(
        thisFeaturePosition: Int,
        thisFeatureId: Int,
        optionPosition: Int,
        optionId: Int,
        isSelectedOption: Boolean
    ) {
        // for  main category type List
        var tempIsFeatureSelected: Boolean = false
        mainFeaturesList[thisFeaturePosition].options!![optionPosition].isSelected =
            isSelectedOption
        mainFeaturesList[thisFeaturePosition].options!!.forEachIndexed { index, element ->
            if (isSelectedOption)
                tempIsFeatureSelected = true
        }
        mainFeaturesList[thisFeaturePosition].isSelected = tempIsFeatureSelected
        mainFeaturesListInRequestEstateAdapter.differ.submitList(mainFeaturesList)

        Log.d("yasser_name", mainFeaturesList[thisFeaturePosition].name)
        Log.d("yasser_feature_position", thisFeaturePosition.toString())
        Log.d("yasser_selected_option", isSelectedOption.toString())
        Log.d("yasser_option_position", optionPosition.toString())
    }
}
