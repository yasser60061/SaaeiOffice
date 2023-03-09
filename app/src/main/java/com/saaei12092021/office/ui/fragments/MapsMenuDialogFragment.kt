package com.saaei12092021.office.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentMapsMenuDialogBinding
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity

class MapsMenuDialogFragment : DialogFragment() {

    private var _binding: FragmentMapsMenuDialogBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: MyViewModel
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsMenuDialogBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel

        viewModel.googleMapType.observe(viewLifecycleOwner, Observer {
            if (it == "satellite") {
                binding.googleMapNormalLinear.visibility = View.VISIBLE
                binding.googleMapSatelliteLinear.visibility = View.GONE
            } else {
                binding.googleMapNormalLinear.visibility = View.GONE
                binding.googleMapSatelliteLinear.visibility = View.VISIBLE
            }
        })
        binding.rootRl.setOnClickListener {
            dismiss()
        }

        binding.adsAsListLinear.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_adsFromSocketAsListFragment)
            dismiss()
            (activity as HomeActivity).mustUpdateLocation = true
        }

        binding.googleMapSatelliteLinear.setOnClickListener {
            viewModel.googleMapType.postValue("satellite")
            dismiss()
        }
        binding.googleMapNormalLinear.setOnClickListener {
            viewModel.googleMapType.postValue("normal")
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    fun expendDialog(activity: FragmentActivity?, logTag: String, performOnError: () -> Unit) {
        try {
            val bottomSheet = dialog!!.findViewById(R.id.design_bottom_sheet) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)
            behavior.peekHeight = displayMetrics.heightPixels
        } catch (e: NullPointerException) {
            Log.d(logTag, e.message ?: "NPE in onResume")
            performOnError()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}