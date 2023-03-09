package com.saaei12092021.office.ui.fragments.myEstatesFragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.RequestedEstateAdapter
import com.saaei12092021.office.databinding.FragmentRequestedEstatesBinding
import com.saaei12092021.office.model.responseModel.adsRequestedResponse.Data
import com.saaei12092021.office.model.socketRequest.chatRequest.AddContactRequest
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.ui.fragments.RequestedEstateDetailsFragment
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import java.util.ArrayList

class RequestedEstatesFragment : Fragment(), RequestedEstateAdapter.OnActionClickedListener {

    private var _binding: FragmentRequestedEstatesBinding? = null
    lateinit var requestedEstateAdapter: RequestedEstateAdapter
    val binding get() = _binding!!
    lateinit var allRequestedEstateList: ArrayList<Data>
    lateinit var myRequestedEstateList: ArrayList<Data>
    lateinit var othersRequestedEstateList: ArrayList<Data>
    lateinit var myId: String
    var tabNumber = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestedEstatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allRequestedEstateList = ArrayList()
        myRequestedEstateList = ArrayList()
        othersRequestedEstateList = ArrayList()

        initializedRecyclerViewAndAdapter()
        makApiRequest()

        myId = GeneralFunctions.getUserIdIfSupervisorOrNot(requireActivity())

        if ((activity as HomeActivity).currentMyEstatePage == "requestEstate")
            tabNumber = 1
        if ((activity as HomeActivity).currentMyEstatePage == "myRequestEstate"){
            tabNumber = 2
            binding.myContactRequestTv.setBackgroundResource(R.drawable.rounded_button)
            binding.otherContactRequestTv.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.myContactRequestTv.setTextColor(Color.parseColor("#FFFFFF"))
            binding.otherContactRequestTv.setTextColor(Color.parseColor("#000000"))
        }

        (activity as HomeActivity).currentMyEstatePage = ""

        (activity as HomeActivity).viewModel.requestedEstateResponse.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Success -> {

                        allRequestedEstateList.clear()
                        myRequestedEstateList.clear()
                        othersRequestedEstateList.clear()

                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE
                        allRequestedEstateList = it.data?.data as ArrayList<Data>
                        allRequestedEstateList.forEach { contactItem ->
                            contactItem.myId = myId
                            Log.d(
                                "addRequested_owner",
                                contactItem.owner.id.toString() + "--" + myId
                            )
                            if (contactItem.owner.id.toString() == myId)
                                myRequestedEstateList.add(contactItem)
                            else othersRequestedEstateList.add(contactItem)
                        }

                     //   requestedEstateAdapter.differ.submitList(othersRequestedEstateList)

                        if (tabNumber == 1)
                            displayOthersRequestedEstate()
                        else displayMyRequestedEstate()

                        binding.myContactRequestTv.setOnClickListener {
                            displayMyRequestedEstate()
                        }

                        binding.otherContactRequestTv.setOnClickListener {
                            if (othersRequestedEstateList.isNotEmpty()) {
                                displayOthersRequestedEstate()
                            }
                        }
                        (activity as HomeActivity).viewModel.requestedEstateResponse.postValue(
                            Resource.Error("")
                        )
                    }
                    is Resource.Loading -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Error -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE
                    }
                }
            })

        (activity as HomeActivity).viewModel.currentDeletedRequestedEstateId.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null) {
//                    if (tabNumber == 1) {
//                        if (othersRequestedEstateList.isNotEmpty()) {
//                            var tempArray : ArrayList<Data> = ArrayList()
//                            tempArray = othersRequestedEstateList
//                            othersRequestedEstateList.clear()
//                            tempArray.forEachIndexed { index, element ->
//                                if (it != element.id)
//                                    othersRequestedEstateList.add(element)
//                            }
//                            displayOthersRequestedEstate()
//                         //   requestedEstateAdapter.differ.submitList(othersRequestedEstateList)
//
//                        }
//                    }
                    if (tabNumber == 2) {
                        if (myRequestedEstateList.isNotEmpty()) {
                            var tempArray: ArrayList<Data> = ArrayList()
                            val myRequestedEstateList2: ArrayList<Data> = ArrayList()
                            tempArray = myRequestedEstateList
                            tempArray.forEachIndexed { index, element ->
                                if (it != element.id)
                                    myRequestedEstateList2.add(element)
                                Log.d("deletedRequestedId1", it.toString() + "-" + element.id)
                            }
                            Log.d("deletedRequestedId2", it.toString())
                            myRequestedEstateList = myRequestedEstateList2
                            displayMyRequestedEstate()
                        }
                    }
                }
            })
    }

    private fun makApiRequest() {
        (activity as HomeActivity).viewModel.getRequestedAds(
            (activity as HomeActivity).token,
            (activity as HomeActivity).myLang
        )
    }

    private fun displayMyRequestedEstate() {
        tabNumber = 2
        if (myRequestedEstateList.isNotEmpty()) {
            binding.requestedEstateRv.visibility = View.VISIBLE
            requestedEstateAdapter.differ.submitList(myRequestedEstateList)
            binding.resultTv.visibility = View.GONE
        } else {
            binding.requestedEstateRv.visibility = View.GONE
            binding.resultTv.visibility = View.VISIBLE
        }
        binding.myContactRequestTv.setBackgroundResource(R.drawable.rounded_button)
        binding.otherContactRequestTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.myContactRequestTv.setTextColor(Color.parseColor("#FFFFFF"))
        binding.otherContactRequestTv.setTextColor(Color.parseColor("#000000"))
    }

    private fun displayOthersRequestedEstate() {
        tabNumber = 1
        binding.myContactRequestTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.otherContactRequestTv.setBackgroundResource(R.drawable.rounded_button)
        binding.myContactRequestTv.setTextColor(Color.parseColor("#000000"))
        binding.otherContactRequestTv.setTextColor(Color.parseColor("#FFFFFF"))
        binding.requestedEstateRv.visibility = View.VISIBLE
        binding.resultTv.visibility = View.GONE
        requestedEstateAdapter.differ.submitList(othersRequestedEstateList)
    }

    private fun initializedRecyclerViewAndAdapter() {
        requestedEstateAdapter = RequestedEstateAdapter(this)
        binding.requestedEstateRv.apply {
            adapter = requestedEstateAdapter
        }
    }

    override fun adsRequestedAction(adsRequestedInfo: Data, action: String) {
        if (action == "sendContactRequest") {
            (activity as HomeActivity).viewModel.addContactRequestInSocket(
                AddContactRequest(
                    adsRequest = adsRequestedInfo.id,
                    lang = (activity as HomeActivity).myLang,
                    sender = Integer.parseInt(myId),
                )
            )
        }

        if (action == "deleteMyRequestedAd") {
            (activity as HomeActivity).viewModel.deleteMyRequestedAd(
                token = (activity as HomeActivity).token,
                requestId = adsRequestedInfo.id.toString(),
            )
        }

        if (action == "openThisRequestedEstateDetails") {
            (activity as HomeActivity).currentRequestedEstateId = adsRequestedInfo.id.toString()
            val dialogFragment = RequestedEstateDetailsFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "signature")
        }
    }
}