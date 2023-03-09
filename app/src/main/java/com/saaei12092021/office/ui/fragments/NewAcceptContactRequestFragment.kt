package com.saaei12092021.office.ui.fragments

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.PropertiesAdapter
import com.saaei12092021.office.databinding.FragmentNewAcceptContactRequestBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.getAdsResponse.Property
import com.saaei12092021.office.model.socketResponse.acceptContactRequestResponse.AcceptContactRequestResponse
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import java.lang.Exception

class NewAcceptContactRequestFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewAcceptContactRequestBinding? = null
    private val binding get() = _binding!!
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var currentAcceptContactRequestResponse: AcceptContactRequestResponse
    lateinit var toStartChatMainInfo: ToStartChatMainInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAcceptContactRequestBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeRl.setOnClickListener {
            dismiss()
        }

        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(requireContext(), notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        currentAcceptContactRequestResponse =
            (activity as HomeActivity).currentAcceptedContactRequest
        toStartChatMainInfo = ToStartChatMainInfo(
            contactRequest = currentAcceptContactRequestResponse.data.id,
            toId = currentAcceptContactRequestResponse.data.sender.id,
            fromId = currentAcceptContactRequestResponse.data.reciever.id
        )

        if (currentAcceptContactRequestResponse.data.contactOn == "ADS") {
            binding.contactOnAdsLinear.visibility = View.VISIBLE
            binding.contactOnRequestEstateRl.visibility = View.GONE

            (activity as HomeActivity).viewModel.getAdsById(currentAcceptContactRequestResponse.data.ads.toString())

            (activity as HomeActivity).viewModel.adsByIdLive.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Glide.with(this).load(it.data!!.advertisement.imgs[0])
                            .into(binding.estateIv)
                        binding.priceTv.text = GeneralFunctions.reformatPrice(it.data.advertisement.price)
                        binding.titleTv.text = it.data.advertisement.title
                        binding.descriptionTv.text = it.data.advertisement.description
                        binding.cityNameTv.text = it.data.advertisement.city.cityName
                        binding.areaNameTv.text = it.data.advertisement.area.areaName

                        val propertyList: ArrayList<Property> = ArrayList()
                        it.data.advertisement.properties.forEach { propertyItem ->
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
                        binding.propertyRv.apply {
                            adapter = propertyAdapter
                            propertyAdapter.differ.submitList(propertyList)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            })
        }
        if (currentAcceptContactRequestResponse.data.contactOn == "ADS-REQUEST") {
            binding.contactOnRequestEstateRl.visibility = View.VISIBLE
            binding.contactOnAdsLinear.visibility = View.GONE

            (activity as HomeActivity).viewModel.getRequestedAdsById(
                currentAcceptContactRequestResponse.data.ads.toString(),
                (activity as HomeActivity).myLang
            )

            (activity as HomeActivity).viewModel.requestedEstateByIdResponseLive.observe(this, Observer {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility =
                            View.GONE
                        Glide.with(this)
                        Glide.with(this)
                            .load(it.data!!.advertisement.owner.img)
                            .into(binding.userProfileImage)
                        binding.nameOfRequestedTv.text =
                            it.data.advertisement.owner.fullname
                              binding.requestedAdsTitleTv.text =
                            it.data.advertisement.title
                        binding.contactRequestDetails.text =
                            it.data.advertisement.description

                        binding.ratingBar.rating =
                            currentAcceptContactRequestResponse.data.sender.rate.toFloat()
                        binding.numberOfReviews.text =
                            currentAcceptContactRequestResponse.data.sender.rateCount.toString()
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility =
                            View.GONE
                    }
                }
            })
        }

        binding.startChatTv.setOnClickListener {
            (activity as HomeActivity).currentContactRequest = toStartChatMainInfo.contactRequest
            val intent = Intent(
                requireActivity(),
                ChatActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
            startActivity(intent)
            dismiss()
        }
    }
}