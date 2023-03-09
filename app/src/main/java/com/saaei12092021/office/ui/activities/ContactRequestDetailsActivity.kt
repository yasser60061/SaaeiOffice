package com.saaei12092021.office.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.PropertiesAdapter
import com.saaei12092021.office.databinding.ActivityContactRequestDetailsBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.adRequestedByIdResponse.AdRequestedByIdResponse
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.getAdsResponse.Property
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.model.socketRequest.chatRequest.AcceptRequest
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.GeneralFunctions.getUserIdIfSupervisorOrNot
import com.saaei12092021.office.util.GeneralFunctions.toIntString
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import io.socket.client.Socket
import java.util.*
import kotlin.collections.ArrayList

class ContactRequestDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityContactRequestDetailsBinding
    lateinit var requestedEstateDetails: AdRequestedByIdResponse
    lateinit var adsByIdResponse: AdsByIdResponse
    var renType: String = ""
    lateinit var contactRequestDetails: com.saaei12092021.office.model.responseModel.contactRequesteResponse.Data
    lateinit var viewModel: MyViewModel
    lateinit var mSocket: Socket
    lateinit var myLang: String
    lateinit var myId: String
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactRequestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(0, 0)

        myLang = Constant.getMyLanguage(this)
        myId = getUserIdIfSupervisorOrNot(this)
        token = Constant.getToken(this)
        setUpLanguageViewAndDirection()

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        mSocket = GeneralFunctions.createSocket(this)
        viewModel.mSocket = mSocket
        viewModel.mSocket.connect()

        contactRequestDetails =
            intent.getSerializableExtra("contactRequestDetails") as com.saaei12092021.office.model.responseModel.contactRequesteResponse.Data
        binding.requestDetailsId.headerLinear.visibility = View.GONE
        binding.requestDetailsId.mainScroll.visibility = View.GONE
        binding.requestDetailsId.progressBar.visibility = View.GONE
        binding.adsInfoCard.visibility = View.GONE
        binding.mainProgressBar.visibility = View.GONE

        if (contactRequestDetails.sender.id.toString() == myId) {
            Glide.with(this@ContactRequestDetailsActivity)
                .load(contactRequestDetails.reciever.img).placeholder(R.drawable.profile_image)
                .into(binding.contactIv)
            binding.contactNameTv.text = contactRequestDetails.reciever.fullname
        } else {
            Glide.with(this@ContactRequestDetailsActivity)
                .load(contactRequestDetails.sender.img).placeholder(R.drawable.profile_image)
                .into(binding.contactIv)
            binding.contactNameTv.text = contactRequestDetails.sender.fullname
        }

        if (contactRequestDetails.contactOn == "ADS") {
            viewModel.getAdsById(contactRequestDetails.ads.toString())
            getAdsByIdIfExist()
        } else if (contactRequestDetails.contactOn == "ADS-REQUEST") {
            viewModel.getRequestedAdsById(
                contactRequestDetails.ads.toString(),
                myLang
            )
        }

        binding.backRl.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        binding.acceptTv.setOnClickListener {
            viewModel.sendAcceptContactRequestInSocket(
                AcceptRequest(
                    contactRequestDetails.id,
                    myLang,
                    Integer.parseInt(myId)
                )
            )
            viewModel.listenToAcceptContactRequestInSocket()
            viewModel.acceptContactRequestSocketLive.observe(this) {
                when (it) {
                    is Resource.Loading -> {
                        binding.mainProgressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (it.data!!.success) {
                            val toStartChatMainInfo = ToStartChatMainInfo(
                                contactRequest = contactRequestDetails.id,
                                toId = contactRequestDetails.sender.id,
                                fromId = Integer.parseInt(myId)
                            )
                            val intent = Intent(
                                this,
                                ChatActivity::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
                            startActivity(intent)
                            viewModel.listenToAcceptContactRequestInSocket()
//                            viewModel.getContactRequest(
//                                token,
//                                myLang
//                            )
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        binding.mainProgressBar.visibility =
                            View.GONE
                    }
                }
            }
        }

        binding.denyTv.setOnClickListener {
            viewModel.sendRefuseContactRequestInSocket(
                AcceptRequest(
                    contactRequestDetails.id,
                    myLang,
                    Integer.parseInt(myId)
                ) // we use the same model in accept and refuse
            )
            viewModel.listenToRefuseContactRequestInSocket()
            viewModel.refuseContactRequestResponseSocketLive.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> {
                        binding.mainProgressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.mainProgressBar.visibility =
                            View.GONE
                        if (it.data!!.success) {
                            Constant.makeToastMessage(this, "تم رفض طلب التواصل")
//                            viewModel.getContactRequest(
//                                token,
//                                myLang
//                            )
                            finish()
                        } else {
                            if (it.data.data.errors != null)
                                Toast(this).showCustomToast(
                                    it.data.data.errors.toString(),
                                    this
                                )
                        }
//                        viewModel.getContactRequest(
//                            token,
//                            myLang
//                        )
                    }
                    is Resource.Error -> {
                        binding.mainProgressBar.visibility =
                            View.VISIBLE
                    }
                }
            })
        }

        binding.deleteContactRequestTv.setOnClickListener {
            viewModel.deleteContactRequest(
                token,
                contactRequestDetails.id.toString()
            )
//            viewModel.getContactRequest(
//                token,
//                myLang
//            )
            finish()
        }
        viewModel.deleteContactRequestResponseLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.mainProgressBar.visibility =
                        View.VISIBLE
                    binding.deleteContactRequestTv.isEnabled = false
                }
                is Resource.Success -> {
                    binding.mainProgressBar.visibility =
                        View.GONE
                    if (it.data!!.success) {
                        Constant.makeToastMessage(this, "تم حذف طلب التواصل")
                        // makeNewApiRequest()
                        //  viewModel.currentDeletedContactRequestId.postValue(contactRequestDetails.id)
                        finish()
                    }
                }
                is Resource.Error -> {
                    binding.mainProgressBar.visibility =
                        View.GONE
                    binding.deleteContactRequestTv.isEnabled = true
                }
            }
        })

        // for requested details display
        viewModel.requestedEstateByIdResponseLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.mainProgressBar.visibility =
                        View.VISIBLE
                    //    binding.requestDetailsId.progressBar.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.mainProgressBar.visibility =
                        View.GONE
                    //       binding.requestDetailsId.progressBar.visibility = View.GONE
                    requestedEstateDetails = it.data!!
                    Log.d("RequestedEstateDetails", requestedEstateDetails.toString())
                    if (requestedEstateDetails.success)
                        displayThisRequestData()
                    else binding.resultTv.visibility = View.VISIBLE
                    displayActionBtnLinear()
                }
                is Resource.Error -> {
                    binding.mainProgressBar.visibility =
                        View.GONE
                    binding.requestDetailsId.progressBar.visibility = View.GONE
                    binding.resultTv.visibility = View.VISIBLE
                    displayActionBtnLinear()
                }
            }
        })

    }

    private fun displayActionBtnLinear() {
        binding.actionButtonsLinear.visibility = View.VISIBLE
        if (contactRequestDetails.sender.id.toString() == myId) {
            binding.deleteContactRequestTv.visibility = View.VISIBLE
            binding.acceptTv.visibility = View.GONE
        } else binding.denyTv.visibility = View.VISIBLE
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
        //  if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //   }
    }

    private fun getAdsByIdIfExist() {
        viewModel.adsByIdLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.mainProgressBar.visibility = View.VISIBLE
                    //  binding.requestDetailsId.progressBar.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.mainProgressBar.visibility = View.GONE
                    adsByIdResponse = it.data!!
                    binding.adsInfoCard.visibility = View.VISIBLE
                    //      binding.requestDetailsId.progressBar.visibility = View.GONE
                    binding.apply {
                        titleTv.text = adsByIdResponse.advertisement.title
                        cityNameTv.text = adsByIdResponse.advertisement.city.cityName
                        areaNameTv.text = adsByIdResponse.advertisement.area.areaName
                        priceTv.text = GeneralFunctions.reformatPrice(adsByIdResponse.advertisement.price)
                        descriptionTv.text = adsByIdResponse.advertisement.description
                        try {
                            Glide.with(this@ContactRequestDetailsActivity)
                                .load(adsByIdResponse.advertisement.imgs[0])
                                .into(binding.estateIv)
                        } catch (e: Exception) {
                        }
                        val propertyList: ArrayList<Property> = ArrayList()
                        adsByIdResponse.advertisement.properties.forEach { propertyItem ->
                            propertyList.add(
                                Property(
                                    propertyItem.id!!,
                                    propertyItem.img!!,
                                    propertyItem.name!!,
                                    propertyItem.type!!,
                                    propertyItem.value!!
                                )
                            )
                        }
                        val propertyAdapter = PropertiesAdapter()
                        propertyRv.apply {
                            adapter = propertyAdapter
                            propertyAdapter.differ.submitList(propertyList)
                        }
                        binding.addEstateLinear.setOnClickListener {
                            val intent = Intent(
                                this@ContactRequestDetailsActivity,
                                EstateDetailsActivity::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("adsId", adsByIdResponse.advertisement.id.toString())
                            startActivity(intent)
                        }
                    }
                    displayActionBtnLinear()
                }
                is Resource.Error -> {
                    binding.mainProgressBar.visibility = View.GONE
                    binding.resultTv.visibility = View.VISIBLE
                    displayActionBtnLinear()

                }
            }
        })
    }

    private fun displayThisRequestData() {

        binding.requestDetailsId.mainScroll.visibility = View.VISIBLE
        binding.requestDetailsId.actionRl.visibility = View.GONE
        binding.requestDetailsId.progressBar.visibility = View.GONE

        binding.requestDetailsId.statusTv.text =
            GeneralFunctions.translateEnumToWord(requestedEstateDetails.advertisement.status)
        when (requestedEstateDetails.advertisement.status) {
            "NEW" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_blue)
            "ON-PROGRESS" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_orange)
            "COMPLETED" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_green)
            "CANCELED" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_red)
        }

        if (contactRequestDetails.status != "ACCEPTED")
            if (contactRequestDetails.sender.id.toString() != myId)
                binding.actionButtonsLinear.visibility = View.VISIBLE

        if (requestedEstateDetails.advertisement.contractType == "RENT") {
            if (requestedEstateDetails.advertisement.rentType == "DAILT")
                renType = " -" + getString(R.string.daily)
            if (requestedEstateDetails.advertisement.rentType == "MONTHLY")
                renType = " -" + getString(R.string.monthly)
            if (requestedEstateDetails.advertisement.rentType == "YEARLY")
                renType = " -" + getString(R.string.yearly)
            if (requestedEstateDetails.advertisement.rentType == "OPINING")
                renType = " -" + getString(R.string.opining)
        }
        Glide.with(this@ContactRequestDetailsActivity)
            .load(requestedEstateDetails.advertisement.owner.img)
            .placeholder(R.drawable.profile_image).into(binding.requestDetailsId.ownerIv)
        binding.requestDetailsId.ownerNameTv.text =
            requestedEstateDetails.advertisement.owner.fullname
        binding.requestDetailsId.requestNoTv.text =
            requestedEstateDetails.advertisement.id.toString()
        binding.requestDetailsId.cityNameTv.text =
            requestedEstateDetails.advertisement.city.cityName
        if (!requestedEstateDetails.advertisement.area.isNullOrEmpty()) {
            var areasName = ""
            requestedEstateDetails.advertisement.area!!.forEach {
                areasName = if (areasName.isNotEmpty())
                    areasName + " - " + it.areaName
                else areasName + " [ " + it.areaName
            }
            areasName += " ]"
            binding.requestDetailsId.neighborhoodNameTv.text = areasName
        }
        binding.requestDetailsId.mainCategoryTv.text =
            requestedEstateDetails.advertisement.category.categoryName
        binding.requestDetailsId.subCategoryTv.text =
            requestedEstateDetails.advertisement.subCategory.categoryName
        if (requestedEstateDetails.advertisement.contractType == "SALE")
            binding.requestDetailsId.typeOfContractTv.text = getString(R.string.sale)
        if (requestedEstateDetails.advertisement.contractType == "RENT")
            binding.requestDetailsId.typeOfContractTv.text = getString(R.string.rent) + renType
        binding.requestDetailsId.realEstateAriaTv.text =
            " من " + toIntString(requestedEstateDetails.advertisement.sizeFrom.toString()) + " الى " + toIntString(
                requestedEstateDetails.advertisement.sizeTo.toString()
            )

        var tempFeatureText = "["
        requestedEstateDetails.advertisement.features.forEachIndexed { index, element ->
            if (index != requestedEstateDetails.advertisement.features.size - 1) {
                if (tempFeatureText.length > 2)
                    tempFeatureText += "," + element.name
                else tempFeatureText += "" + element.name
            } else tempFeatureText += "]"
        }
        if (tempFeatureText.trim().length == 1)
            binding.requestDetailsId.featuresTv.text = ""
        else binding.requestDetailsId.featuresTv.text = (tempFeatureText)

        var concatenationProperties = ""
        requestedEstateDetails.advertisement.properties.forEachIndexed { featureIndex, featureItem ->
            if (featureItem.type == "NUMBER") {
                concatenationProperties =
                    concatenationProperties + featureItem.name + " : " +
                            toIntString(featureItem.from.toString()) + " " + getString(R.string.to) + " " + toIntString(
                        featureItem.to.toString()
                    )
            } else if (featureItem.type == "LIST") {
                concatenationProperties =
                    concatenationProperties + featureItem.name + " : "
                featureItem.optionsName.forEachIndexed { optionIndex, optionItem ->
                    concatenationProperties =
                        concatenationProperties + " " + optionItem
                }
            }
            if (requestedEstateDetails.advertisement.properties.size - 1 != featureIndex)
                if (concatenationProperties.isNotEmpty())
                    concatenationProperties += "\n"
        }

        binding.requestDetailsId.subPropertySpecificationsTv.text = concatenationProperties
        binding.requestDetailsId.morInfoTv.text =
            requestedEstateDetails.advertisement.description_ar

        if (requestedEstateDetails.advertisement.enablePhoneContact)
            binding.requestDetailsId.allowCommunicationViaIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.requestDetailsId.allowCommunicationViaIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

        if (requestedEstateDetails.advertisement.enableInstallment)
            binding.requestDetailsId.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.requestDetailsId.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0, 0)
    }

}