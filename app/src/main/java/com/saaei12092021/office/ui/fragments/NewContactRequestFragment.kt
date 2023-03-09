package com.saaei12092021.office.ui.fragments

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.PropertiesAdapter
import com.saaei12092021.office.databinding.FragmentNewContactRequestBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.getAdsResponse.Property
import com.saaei12092021.office.model.socketRequest.chatRequest.AcceptRequest
import com.saaei12092021.office.model.socketResponse.addContactRequestResponse.AddContactRequestResponse
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class NewContactRequestFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewContactRequestBinding? = null
    private val binding get() = _binding!!
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var currentContactRequestResponse: AddContactRequestResponse
    lateinit var toStartChatMainInfo: ToStartChatMainInfo
    var adsListFromSocket = ArrayList<Data>()
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewContactRequestBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = GeneralFunctions.getUserIdIfSupervisorOrNot(requireActivity())

        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(requireContext(), notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        currentContactRequestResponse = (activity as HomeActivity).currentContactRequestResponse
        toStartChatMainInfo = ToStartChatMainInfo(
            contactRequest = currentContactRequestResponse.data!!.id!!,
            toId = currentContactRequestResponse.data!!.sender!!.id,
            fromId = currentContactRequestResponse.data!!.reciever!!.id
        )
        adsListFromSocket = (activity as HomeActivity).adsListFromSocket

        if (currentContactRequestResponse.data!!.contactOn == "ADS") {
            binding.contactOnAdsLinear.visibility = View.VISIBLE
            (activity as HomeActivity).viewModel.getAdsById(currentContactRequestResponse.data!!.ads.toString())

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
        if (currentContactRequestResponse.data!!.contactOn == "ADS-REQUEST") {
            binding.contactOnRequestEstateRl.visibility = View.VISIBLE

            (activity as HomeActivity).viewModel.getRequestedAdsById(
                currentContactRequestResponse.data!!.ads.toString(),
                (activity as HomeActivity).myLang
            )

            (activity as HomeActivity).viewModel.requestedEstateByIdResponseLive.observe(
                this,
                Observer {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility =
                                View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility =
                                View.GONE
                            Glide.with(this)
                                .load(it.data!!.advertisement.owner.img)
                                .into(binding.userProfileImage)
                            binding.nameOfRequestedTv.text =
                                it.data.advertisement.owner.fullname

                            val date = it.data.advertisement.createdAt.getDateWithServerTimeStamp()
                            var formatter: DateTimeFormatter? = null
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                formatter =
                                    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(
                                        Locale.ENGLISH)
                                val dateTime: LocalDateTime =
                                    LocalDateTime.parse(date.toString(), formatter)
                                val formatter2: DateTimeFormatter =
                                    DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy | hh:mm a",
                                           Locale("ar") // For english use Locale.ENGLISH
                                       // Locale.getDefault()
                                    )
                                binding.dateTv.text = dateTime.format(formatter2)
                            } else
                                binding.dateTv.text =
                                    Constant.dateAndTimeReformat(it.data.advertisement.createdAt)
                                        .trim().replace(" ", "  ")

                            binding.requestedAdsTitleTv.text =
                                it.data.advertisement.title
                            binding.contactRequestDetails.text =
                                it.data.advertisement.description

                            binding.ratingBar.rating =
                                currentContactRequestResponse.data!!.sender!!.rate.toFloat()
                            binding.numberOfReviews.text =
                                currentContactRequestResponse.data!!.sender!!.rateCount.toString()
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility =
                                View.GONE
                        }
                    }
                })
        }
        binding.acceptTv.setOnClickListener {
//            (activity as HomeActivity).viewModel.sendAcceptContactRequestInSocket(
//                AcceptRequest(
//                    currentContactRequestResponse.data!!.id!!,
//                    (activity as HomeActivity).myLang,
//                    Integer.parseInt(userId)
//                )
//            )
            val intent = Intent(
                requireActivity(),
                ChatActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
            startActivity(intent)
          //  (activity as HomeActivity).viewModel.listenToAcceptContactRequestInSocket()
            dismiss()
        }

        binding.denyTv.setOnClickListener {
//            (activity as HomeActivity).viewModel.sendRefuseContactRequestInSocket(
//                AcceptRequest(
//                    currentContactRequestResponse.data!!.id!!,
//                    (activity as HomeActivity).myLang,
//                    Integer.parseInt(userId)
//                )
//            )
            dismiss()
        }
    }
}