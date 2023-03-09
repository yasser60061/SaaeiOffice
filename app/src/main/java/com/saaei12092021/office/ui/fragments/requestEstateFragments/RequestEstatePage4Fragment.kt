package com.saaei12092021.office.ui.fragments.requestEstateFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentRequestEstateReviewPage4Binding
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity

class RequestEstatePage4Fragment : Fragment() {

    private var _binding: FragmentRequestEstateReviewPage4Binding? = null
    val binding get() = _binding!!

    lateinit var parentFrag: MainRequestEstateFragment
    var renType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestEstateReviewPage4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFrag = this@RequestEstatePage4Fragment.parentFragment as MainRequestEstateFragment
        parentFrag.binding.titleLinear.visibility = View.GONE
        parentFrag.binding.progressPointerLinear.visibility = View.GONE
        parentFrag.binding.pageNameTv.visibility = View.GONE

        if (parentFrag.contractType == "RENT") {
            if (parentFrag.rentType == "DAILT")
                renType = " -" + getString(R.string.daily)
            if (parentFrag.rentType == "MONTHLY")
                renType = " -" + getString(R.string.monthly)
            if (parentFrag.rentType == "YEARLY")
                renType = " -" + getString(R.string.yearly)
            if (parentFrag.rentType == "OPINING")
                renType = " -" + getString(R.string.opining)
        }

        binding.closeBtn.setOnClickListener {
            (activity as HomeActivity).onBackPressed()
        }

        binding.requestNoTv.text = parentFrag.addAdsRequestFromSocketResponse.data?.id.toString()
        binding.cityNameTv.text = parentFrag.cityName
        binding.cityNameTv.text = parentFrag.cityName
        var areasName = ""
        parentFrag.areaList.forEach {
            if (it.selected){
                areasName = if (areasName.isNotEmpty())
                    areasName +" - "+ it.areaName_ar
                else areasName +" [ "+ it.areaName_ar
            }
        }
        areasName += " ]"
        binding.neighborhoodNameTv.text = areasName
        binding.mainCategoryTv.text = parentFrag.categoryName
        binding.subCategoryTv.text = parentFrag.subCategoryName
        if (parentFrag.contractType == "SALE")
            binding.typeOfContractTv.text = getString(R.string.sale)
        if (parentFrag.contractType == "RENT")
            binding.typeOfContractTv.text = getString(R.string.rent) + renType
        binding.realEstateAriaTv.text =
            " من " + parentFrag.sizeFrom.toString() + " الى " + parentFrag.sizeTo.toString()
        binding.featuresTv.text = (parentFrag.featuresName.toString())

        var concatenationProperties = ""
        parentFrag.mainFeaturesList.forEachIndexed { featureIndex, featureItem ->
            if (featureItem.isSelected) {
                if (featureItem.type == "NUMBER") {
                    concatenationProperties =
                        concatenationProperties + featureItem.name + " : " +
                                featureItem.from + " " + getString(R.string.to) + " " + featureItem.to
                } else if (featureItem.type == "LIST") {
                    concatenationProperties =
                        concatenationProperties + featureItem.name + " : "
                    featureItem.options!!.forEachIndexed { optionIndex, optionItem ->
                        if (optionItem.isSelected) {
                            concatenationProperties =
                                concatenationProperties + " " + optionItem.name
                        }
                        if (featureItem.options.size - 1 != optionIndex)
                            if (featureItem.options[optionIndex + 1].isSelected)
                                if (featureItem.options[optionIndex].isSelected)
                                    concatenationProperties += " - "
                    }
                }
            }
            if (parentFrag.mainFeaturesList.size - 1 != featureIndex)
                if (parentFrag.mainFeaturesList[featureIndex + 1].isSelected)
                    if (concatenationProperties.isNotEmpty())
                        concatenationProperties += "\n"
        }

        binding.subPropertySpecificationsTv.text = concatenationProperties
        binding.morInfoTv.text = parentFrag.description_ar

        if (parentFrag.enablePhoneContact == true)
            binding.allowCommunicationViaIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.allowCommunicationViaIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

        if (parentFrag.enableInstallment == true)
            binding.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

    }
}
