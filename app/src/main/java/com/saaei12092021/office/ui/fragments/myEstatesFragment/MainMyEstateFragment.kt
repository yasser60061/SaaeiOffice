package com.saaei12092021.office.ui.fragments.myEstatesFragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentMainMyEstateBinding
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.showCustomToast


class MainMyEstateFragment : Fragment() {

    private var _binding: FragmentMainMyEstateBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainMyEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility = View.VISIBLE
            (activity as HomeActivity).binding.contentHomeId.allMainCategoryRv.visibility =
                View.GONE
            (activity as HomeActivity).binding.contentHomeId.allSubCategoryRv.visibility = View.GONE
            (activity as HomeActivity).binding.contentHomeId.searchResultTv.visibility = View.GONE
            (activity as HomeActivity).binding.notificationRl.visibility = View.VISIBLE
            (activity as HomeActivity).binding.contentHomeId.mainBarLinear.visibility = View.VISIBLE
            (activity as HomeActivity).binding.backRl.visibility = View.GONE
            (activity as HomeActivity).binding.homeTitleTv.text = getString(R.string.my_estate)

            if (((activity as HomeActivity).currentMyEstatePage == "requestEstate") or
                ((activity as HomeActivity).currentMyEstatePage == "myRequestEstate")
            ) {
                displayPage2()
            } else if ((activity as HomeActivity).currentMyEstatePage == "contactRequest") {
                displayPage3()
                (activity as HomeActivity).currentMyEstatePage = ""
            } else {
                displayPage1()
            }
        } catch (e: Exception) {
        }

        binding.addedEstateTv.setOnClickListener {
            displayPage1()
        }

        binding.requestedEstateTv.setOnClickListener {
            displayPage2()
        }

        binding.deliverRequestTv.setOnClickListener {
            displayPage3()
        }
    }

    private fun displayPage1() {
        val addedEstatesFragment1: Fragment = AddedEstatesFragment()
        val transaction1: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction1.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_right,
            R.anim.slide_in_right,
            R.anim.slide_out_right,
        ).replace(R.id.child_fragment_container, addedEstatesFragment1)
        transaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        transaction1.commit()
        binding.addedEstateTv.setBackgroundResource(R.drawable.rounded_button)
        binding.deliverRequestTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.requestedEstateTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.addedEstateTv.setTextColor(Color.parseColor("#FFFFFF"))
        binding.deliverRequestTv.setTextColor(Color.parseColor("#000000"))
        binding.requestedEstateTv.setTextColor(Color.parseColor("#000000"))
    }

    private fun displayPage2() {
        val requestedEstatesFragment: Fragment = RequestedEstatesFragment()
        val transaction2: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction2.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out,
        ).replace(R.id.child_fragment_container, requestedEstatesFragment)
        transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        transaction2.commit()
        binding.addedEstateTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.deliverRequestTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.requestedEstateTv.setBackgroundResource(R.drawable.rounded_button)
        binding.addedEstateTv.setTextColor(Color.parseColor("#000000"))
        binding.deliverRequestTv.setTextColor(Color.parseColor("#000000"))
        binding.requestedEstateTv.setTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun displayPage3() {
        val communicationRequestFragment: Fragment = ContactRequestsFragment()
        val transaction3: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction3.setCustomAnimations(
            R.anim.slide_in_left,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_left,
        ).replace(R.id.child_fragment_container, communicationRequestFragment)
        transaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction3.commit()
        binding.addedEstateTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.deliverRequestTv.setBackgroundResource(R.drawable.rounded_button)
        binding.requestedEstateTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.addedEstateTv.setTextColor(Color.parseColor("#000000"))
        binding.deliverRequestTv.setTextColor(Color.parseColor("#FFFFFF"))
        binding.requestedEstateTv.setTextColor(Color.parseColor("#000000"))
    }

}