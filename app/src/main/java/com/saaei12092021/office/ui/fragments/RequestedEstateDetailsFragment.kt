package com.saaei12092021.office.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentRequestedEstateDetailsBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.adRequestedByIdResponse.AdRequestedByIdResponse
import com.saaei12092021.office.model.socketRequest.chatRequest.AddContactRequest
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class RequestedEstateDetailsFragment : DialogFragment() {

    private var _binding: FragmentRequestedEstateDetailsBinding? = null
    val binding get() = _binding!!
    var renType: String = ""
    lateinit var requestedEstateId: String
    lateinit var requestedEstateDetails: AdRequestedByIdResponse
    override fun getTheme(): Int = R.style.DialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestedEstateDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestedEstateId = (activity as HomeActivity).currentRequestedEstateId
        setUpLanguageViewAndDirection()
        (activity as HomeActivity).viewModel.getRequestedAdsById(
            requestedEstateId,
            (activity as HomeActivity).myLang
        )

        //--------------------------------------------------------------------------------

        (activity as HomeActivity).viewModel.requestedEstateByIdResponseLive.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility =
                            View.VISIBLE
                        binding.mainScroll.visibility =
                            View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility =
                            View.GONE
                        requestedEstateDetails = it.data!!
                        displayThisRequestData()
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility =
                            View.GONE
                    }
                }
            })

        binding.backRl.setOnClickListener {
            dismiss()
        }
        binding.deleteRequestBtn.setOnClickListener {
            (activity as HomeActivity).viewModel.deleteMyRequestedAd(
                token = (activity as HomeActivity).token,
                requestId = requestedEstateDetails.advertisement.id.toString(),
            )
        }

        binding.shareRl.setOnClickListener {
            generateSharingRequestedEstateLink { shortLinkItem ->
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Saaei")
                val adsName =
                    requestedEstateDetails.advertisement.category.categoryName + " - " + requestedEstateDetails.advertisement.subCategory.categoryName
                var shareMessage = "عقار مطلوب\n $adsName \n"
                shareMessage += shortLinkItem
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "مشاركة مع .."))

            }
        }

        binding.sendMessageBtn.setOnClickListener {
            (activity as HomeActivity).viewModel.addContactRequestInSocket(
                AddContactRequest(
                    adsRequest = requestedEstateDetails.advertisement.id,
                    lang = (activity as HomeActivity).myLang,
                    sender = Integer.parseInt((activity as HomeActivity).myId),
                )
            )
        }

        (activity as HomeActivity).viewModel.deleteMyRequestedAdLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast(requireContext()).showCustomToast("تم الحذف", requireActivity())
//                        (activity as HomeActivity).viewModel.getRequestedAds(
//                            (activity as HomeActivity).token!!,
//                            (activity as HomeActivity).myLang
//                        )
                    (activity as HomeActivity).viewModel.deleteMyRequestedAdLive.postValue(
                        Resource.Error(
                            ""
                        )
                    )
                    (activity as HomeActivity).viewModel.currentDeletedRequestedEstateId.postValue(
                        requestedEstateDetails.advertisement.id
                    )
                    dismiss()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

        (activity as HomeActivity).viewModel.listenToAddContactRequestInSocket()
        (activity as HomeActivity).viewModel.addContactRequestInSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility =
                        View.VISIBLE
                    binding.sendMessageBtn.isEnabled = false
                }
                is Resource.Success -> {
                    if (it.data!!.data!!.ads.toString() == requestedEstateId) {
                        binding.sendMessageBtn.text = "تم الارسال"
                        binding.sendMessageBtn.setBackgroundResource(R.drawable.rounded_button_elementary)
                        binding.sendMessageBtn.isEnabled = false
                        binding.progressBar.visibility =
                            View.GONE

                        val toStartChatMainInfo = ToStartChatMainInfo(
                            contactRequest = it.data.data!!.id!!,
                            toId = it.data.data.reciever!!.id,
                            fromId = it.data.data.sender!!.id
                        )
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
                        startActivity(intent)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility =
                        View.GONE
                    //    binding.sendMessageBtn.isEnabled = true
                }
            }
        })
    }

    private fun setUpLanguageViewAndDirection() {
        //   if ((activity as HomeActivity).deviceLanguage == "العربية") {
        binding.headerLinear.layoutDirection = View.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //   }
    }

    private fun displayThisRequestData() {
        binding.mainScroll.visibility = View.VISIBLE
        binding.actionRl.visibility = View.VISIBLE
        binding.sendMessageBtn.text = "ارسال رسالة"
        binding.sendMessageBtn.isEnabled = true

        if (requestedEstateDetails.advertisement.owner.id.toString() == (activity as HomeActivity).myId) {
            binding.deleteRequestBtn.visibility = View.VISIBLE
            binding.sendMessageBtn.visibility = View.GONE
            binding.deleteRequestBtn.text = "حذف الطلب"
            binding.deleteRequestBtn.isEnabled = true
        } else {
            binding.sendMessageBtn.visibility = View.VISIBLE
            binding.sendMessageBtn.setBackgroundResource(R.drawable.rounded_button_green)
            binding.deleteRequestBtn.visibility = View.GONE
            if (requestedEstateDetails.advertisement.enablePhoneContact) {
                binding.callNowLinear.visibility = View.VISIBLE
                binding.callNowLinear.setOnClickListener {
                    createPhoneCall()
                }
            }
        }

        requestedEstateDetails.advertisement.contactsOffices!!.forEach {
            if ((activity as HomeActivity).myId == it.toString()) {
                binding.sendMessageBtn.visibility = View.VISIBLE
                binding.sendMessageBtn.isEnabled = false
                binding.sendMessageBtn.text = "تم الارسال"
                binding.sendMessageBtn.setBackgroundResource(R.drawable.rounded_button_elementary)
            }
        }

        Glide.with(this).load(requestedEstateDetails.advertisement.owner.img)
            .placeholder(R.drawable.profile_image)
            .into(binding.ownerIv)
        binding.ownerNameTv.text = requestedEstateDetails.advertisement.owner.fullname

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

       binding.statusTv.text = GeneralFunctions.translateEnumToWord(requestedEstateDetails.advertisement.status)
        when (requestedEstateDetails.advertisement.status) {
            "NEW" -> binding.statusTv.setBackgroundResource(R.drawable.rounded_button_blue)
            "ON-PROGRESS" -> binding.statusTv.setBackgroundResource(R.drawable.rounded_button_orange)
            "COMPLETED" -> binding.statusTv.setBackgroundResource(R.drawable.rounded_button_green)
            "CANCELED" -> binding.statusTv.setBackgroundResource(R.drawable.rounded_button_red)
        }

        binding.requestNoTv.text = requestedEstateDetails.advertisement.id.toString()
        binding.cityNameTv.text = requestedEstateDetails.advertisement.city.cityName
        if (!requestedEstateDetails.advertisement.area.isNullOrEmpty()) {
            var areasName = ""
            requestedEstateDetails.advertisement.area!!.forEach {
                areasName = if (areasName.isNotEmpty())
                    areasName + " - " + it.areaName
                else areasName + " [ " + it.areaName
            }
            areasName += " ]"
            binding.neighborhoodNameTv.text = areasName
        }
        binding.mainCategoryTv.text =
            requestedEstateDetails.advertisement.category.categoryName
        binding.subCategoryTv.text =
            requestedEstateDetails.advertisement.subCategory.categoryName
        if (requestedEstateDetails.advertisement.contractType == "SALE")
            binding.typeOfContractTv.text = getString(R.string.sale)
        if (requestedEstateDetails.advertisement.contractType == "RENT")
            binding.typeOfContractTv.text = getString(R.string.rent) + renType
        binding.realEstateAriaTv.text =
            " من " + toIntString(requestedEstateDetails.advertisement.sizeFrom.toString()) + " الى " + toIntString(
                requestedEstateDetails.advertisement.sizeTo.toString()
            )

        var tempFeatureText = "["
        requestedEstateDetails.advertisement.features.forEachIndexed { index, element ->
            if (index != requestedEstateDetails.advertisement.features.size - 1) {
                if (tempFeatureText.length > 2)
                    tempFeatureText += "," + element.name
                else tempFeatureText += "" + element.name
            } else {
                if (requestedEstateDetails.advertisement.features.size == 1)
                    tempFeatureText += "" + element.name
                tempFeatureText += "]"
            }
        }

        if (tempFeatureText.trim().length == 1)
            binding.featuresTv.text = ""
        else binding.featuresTv.text = (tempFeatureText)

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

        binding.subPropertySpecificationsTv.text = concatenationProperties
        binding.morInfoTv.text = requestedEstateDetails.advertisement.description_ar

        if (requestedEstateDetails.advertisement.enablePhoneContact) {
            binding.allowCommunicationViaIv.setBackgroundResource(R.drawable.green_circle_bg)
            binding.allowCommunicationViaIv.setOnClickListener {
                createPhoneCall()
            }
            binding.allowCommunicationViaTv.setOnClickListener {
                createPhoneCall()
            }
        } else binding.allowCommunicationViaIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

        if (requestedEstateDetails.advertisement.enableInstallment)
            binding.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

        (activity as HomeActivity).viewModel.requestedEstateByIdResponseLive.postValue(
            Resource.Error(
                ""
            )
        )

    }

    private fun createPhoneCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(
            "tel:" + requestedEstateDetails.advertisement.owner.phone
                .toString()
        )
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun toIntString(theString: String): String {
        val string = StringBuilder(theString).also {
            it.setCharAt(theString.length - 1, ' ')
            it.setCharAt(theString.length - 2, ' ')
        }
        return string.toString().trim()
    }

    private fun generateSharingRequestedEstateLink(
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            link = Uri.parse("https://appsaaei.page.link/request?reqId=$requestedEstateId")
            domainUriPrefix = "https://appsaaei.page.link"

            setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .build()
            )

            androidParameters("com.saaei12092021.office") {
                build()
            }

            iosParameters("com.office.saaeiApp") {
                appStoreId = "1586113986"
            }

            // Finally
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {
            }
        }
    }

}

