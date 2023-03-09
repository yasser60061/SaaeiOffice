package com.saaei12092021.office.ui.fragments.requestEstateFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentRequestEstatePage3Binding
import com.saaei12092021.office.model.socketRequest.addAdsRequest.AddAdsRequestForSocket
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import org.json.JSONObject
import kotlin.collections.HashMap

class RequestEstatePage3Fragment : Fragment() {

    private var _binding: FragmentRequestEstatePage3Binding? = null
    val binding get() = _binding!!
    lateinit var parentFrag: MainRequestEstateFragment
    lateinit var viewModel: MyViewModel
    var renType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestEstatePage3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFrag = this@RequestEstatePage3Fragment.parentFragment as MainRequestEstateFragment
        viewModel = (activity as HomeActivity).viewModel

        if (parentFrag.contractType == "RENT") {
            if (parentFrag.rentType == "DAILT")
                renType = " -" +getString(R.string.daily)
            if (parentFrag.rentType == "MONTHLY")
                renType = " -" +getString(R.string.monthly)
            if (parentFrag.rentType == "YEARLY")
                renType = " -" + getString(R.string.yearly)
            if (parentFrag.rentType == "OPINING")
                renType = " -" + getString(R.string.opining)
        }

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

        binding.previousBtn.setOnClickListener {
            parentFrag.displayPage2()
        }

        binding.sentBtn.setOnClickListener {
            sendToRequestAd()
        }

        parentFrag.binding.pageNameTv.setOnClickListener {
            parentFrag.displayPage1()
        }
    }

    private fun sendToRequestAd() {
        val requestedObject = AddAdsRequestForSocket(
            owner = parentFrag.owner,
            title_en = parentFrag.title_en,
            title_ar = parentFrag.title_ar,
            description_en = parentFrag.description_en,
            description_ar = parentFrag.description_ar,
            contractType = parentFrag.contractType,
            rentType = parentFrag.rentType,
            sizeFrom = parentFrag.sizeFrom,
            sizeTo = parentFrag.sizeTo,
            priceType = parentFrag.priceType,
            priceFrom = parentFrag.priceFrom,
            priceTo = parentFrag.priceTo,
            category = parentFrag.category!!,
            subCategory = parentFrag.subCategory!!,
            city = parentFrag.city!!,
            properties = parentFrag.properties,
            features = parentFrag.features,
            enableInstallment = parentFrag.enableInstallment!!,
            enablePhoneContact = parentFrag.enablePhoneContact!!,
            area = parentFrag.selectedAreaList
        )

        // ----- for delete later ---
        val requestedMap: HashMap<String, Any> = HashMap()
        requestedMap["owner"] = parentFrag.owner
        requestedMap["title_en"] = parentFrag.title_en
        requestedMap["title_ar"] = parentFrag.title_ar
        requestedMap["description_en"] = parentFrag.description_en
        requestedMap["description_ar"] = parentFrag.description_ar
        requestedMap["contractType"] = parentFrag.contractType
        requestedMap["sizeFrom"] = parentFrag.sizeFrom
        requestedMap["sizeTo"] = parentFrag.sizeTo
        requestedMap["priceType"] = parentFrag.priceType
        requestedMap["priceFrom"] = parentFrag.priceFrom
        requestedMap["priceTo"] = parentFrag.priceTo
        requestedMap["category"] = parentFrag.category!!
        requestedMap["subCategory"] = parentFrag.subCategory!!
        requestedMap["city"] = parentFrag.city!!
        requestedMap["properties"] = parentFrag.properties
        requestedMap["features"] = parentFrag.features
        requestedMap["enableInstallment"] = parentFrag.enableInstallment!!
        requestedMap["enablePhoneContact"] = parentFrag.enablePhoneContact!!
        requestedMap["area"] = parentFrag.selectedAreaList

        // ----- for delete later ---

        val jsonObject = JSONObject().apply {
            put("owner", parentFrag.owner)
            put("title_en", parentFrag.title_en)
            put("title_ar", parentFrag.title_ar)
            put("description_en", parentFrag.description_en)
            put("description_ar", parentFrag.description_ar)
            put("contractType", parentFrag.contractType)
            put("sizeFrom", parentFrag.sizeFrom)
            put("sizeTo", parentFrag.sizeTo)
            put("priceType", parentFrag.priceType)
            put("priceFrom", parentFrag.priceFrom)
            put("priceTo", parentFrag.priceTo)
            put("category", parentFrag.category!!)
            put("subCategory", parentFrag.subCategory!!)
            put("city", parentFrag.city)
            put("properties", parentFrag.properties)
            put("features", parentFrag.features)
            put("enableInstallment", parentFrag.enableInstallment!!)
            put("enablePhoneContact", parentFrag.enablePhoneContact!!)
            put("area", parentFrag.selectedAreaList)
        }

        viewModel.addAdsRequestFromSocket(requestedObject)
        viewModel.listenToAdsRequestFromSocket()
    }
}
