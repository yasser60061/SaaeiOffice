package com.saaei12092021.office.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentNewAdsRequestBinding
import com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse.AddAdsRequestFromSocketResponse
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import android.media.RingtoneManager

import android.net.Uri
import com.saaei12092021.office.util.GeneralFunctions
import java.lang.Exception

class NewAdsRequestFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewAdsRequestBinding? = null
    private val binding get() = _binding!!
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var addAdsRequestRequestFromSocketResponse: AddAdsRequestFromSocketResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAdsRequestBinding.inflate(inflater, container, false)
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

        addAdsRequestRequestFromSocketResponse = AddAdsRequestFromSocketResponse()

        addAdsRequestRequestFromSocketResponse = (activity as HomeActivity).currentNewAdsRequestFromSocketResponse
        binding.priceTv.text = GeneralFunctions.reformatPrice(addAdsRequestRequestFromSocketResponse.data?.priceFrom.toString().toFloat())
        binding.requestTitleTv.text = addAdsRequestRequestFromSocketResponse.data?.title.toString()
        binding.descriptionTv.text = addAdsRequestRequestFromSocketResponse.data?.description.toString()
        binding.cityNameTv.text = addAdsRequestRequestFromSocketResponse.data?.city!!.cityName
        binding.areaNameTv.text = addAdsRequestRequestFromSocketResponse.data?.area!![0].areaName

        binding.root.setOnClickListener {
            (activity as HomeActivity).currentRequestedEstateId = addAdsRequestRequestFromSocketResponse.data!!.id.toString()
            val dialogFragment = RequestedEstateDetailsFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "signature")
        }
    }
}