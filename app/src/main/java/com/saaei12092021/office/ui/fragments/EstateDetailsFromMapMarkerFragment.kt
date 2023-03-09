package com.saaei12092021.office.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.PropertiesInAdsInfoFromMapAdapter
import com.saaei12092021.office.databinding.FragmentEstateDetailsFromMapMarkerBinding
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.GeneralFunctions

class EstateDetailsFromMapMarkerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEstateDetailsFromMapMarkerBinding? = null
    private val binding get() = _binding!!
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var clickedAdsData: Data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstateDetailsFromMapMarkerBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeRl.setOnClickListener {
            dismiss()
        }

        clickedAdsData = (activity as HomeActivity).clickedAdsData

        binding.titleTv.text = clickedAdsData.title
        binding.cityNameTv.text = clickedAdsData.city.cityName
        binding.areaNameTv.text = clickedAdsData.area.areaName
        binding.priceTv.text = GeneralFunctions.reformatPrice(clickedAdsData.price)
        binding.descriptionTv.text = clickedAdsData.description
        if (clickedAdsData.imgs.isNotEmpty())
        Glide.with(requireContext()).load(clickedAdsData.imgs[0]).into(binding.estateIv)

        val propertyAdapter = PropertiesInAdsInfoFromMapAdapter()
        binding.propertyRv.apply {
            adapter = propertyAdapter
            propertyAdapter.differ.submitList(clickedAdsData.properties)
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), EstateDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("adsId", clickedAdsData.id.toString())
            startActivity(intent)
            dismiss()
            (activity as HomeActivity).mustUpdateLocation = false
        }
    }
}