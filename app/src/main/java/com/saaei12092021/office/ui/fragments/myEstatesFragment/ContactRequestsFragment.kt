package com.saaei12092021.office.ui.fragments.myEstatesFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.saaei12092021.office.adapters.ContactRequestAdapter
import com.saaei12092021.office.databinding.FragmentContactRequestBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.contactRequesteResponse.Data
import com.saaei12092021.office.model.socketRequest.chatRequest.AcceptRequest
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.ContactRequestDetailsActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import java.util.ArrayList

class ContactRequestsFragment : Fragment(),
    ContactRequestAdapter.OnActionClickedListener {

    private var _binding: FragmentContactRequestBinding? = null
    lateinit var contactRequestAdapter: ContactRequestAdapter
    val binding get() = _binding!!
    lateinit var contactRequestList: ArrayList<Data>
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactRequestList = ArrayList()
        userId = GeneralFunctions.getUserIdIfSupervisorOrNot(requireActivity())

        (activity as HomeActivity).isNewMessageInCurrentMyEstatePage = false

        initialContactRequestRv()
        makeNewApiRequest()

        (activity as HomeActivity).viewModel.contactRequestResponseLive.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Success -> {
                      //  contactRequestList.clear()

                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.INVISIBLE
                        contactRequestList = it.data?.data as ArrayList<Data>
                        contactRequestList.forEach { dataItem ->
                            dataItem.myId = (activity as HomeActivity).myId
                            Log.d("dataItem_", dataItem.toString())
                        }
                      displayContactRequest()
                    }
                    is Resource.Loading -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.VISIBLE
                    }
                    is Resource.Error -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.INVISIBLE
                    }
                }
            })
    }

    private fun displayContactRequest() {
        if (contactRequestList.isNotEmpty()) {
            binding.contactRequestRv.visibility = View.VISIBLE
            binding.resultTv.visibility = View.GONE
            contactRequestAdapter.differ.submitList(contactRequestList)
        } else {
            binding.contactRequestRv.visibility = View.GONE
            binding.resultTv.visibility = View.VISIBLE
            binding.resultTv.text = "لا يوجد طلبات .. أبق على إتصال"
        }
    }

    private fun initialContactRequestRv() {
        contactRequestAdapter = ContactRequestAdapter(this)
        binding.contactRequestRv.apply {
            adapter = contactRequestAdapter
        }
    }

    // all task in this function is disabled and the action become in other place -- maybe used later
    override fun contactRequestAction(contactRequestItemInfo: Data, action: String) {
        if (action == "AcceptRequest") {
            (activity as HomeActivity).viewModel.sendAcceptContactRequestInSocket(
                AcceptRequest(
                    contactRequest = contactRequestItemInfo.id,
                    lang = (activity as HomeActivity).myLang,
                    userId = Integer.parseInt(userId)
                )
            )
            (activity as HomeActivity).viewModel.listenToAcceptContactRequestInSocket()
            (activity as HomeActivity).viewModel.acceptContactRequestSocketLive.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Loading -> {
                            (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                View.VISIBLE
                        }
                        is Resource.Success -> {
                            (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                View.GONE
                            if (it.data!!.success) {
                                if (it.data.data.id == contactRequestItemInfo.id) {
                                    val toStartChatMainInfo = ToStartChatMainInfo(
                                        contactRequest = contactRequestItemInfo.id,
                                        toId = contactRequestItemInfo.sender.id,
                                        fromId = contactRequestItemInfo.reciever.id
                                    )
                                    (activity as HomeActivity).currentContactRequest =
                                        toStartChatMainInfo.contactRequest
                                    val intent = Intent(
                                        requireActivity(),
                                        ChatActivity::class.java
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
                                    startActivity(intent)
                                }
                            //    makeNewApiRequest()
                            }
                            // يتم تكرارها اكثر من مرة لو ارسلت اكثر من طلب
//                        else {
//                            Constant.makeToastMessage(requireContext(), "هذا الطلب غير متاح")
//                        }
                        }
                        is Resource.Error -> {}
                    }
                })
        }
        if (action == "startChat") {
            (activity as HomeActivity).currentContactRequest = contactRequestItemInfo.id
        }
        if (action == "RefuseRequest") {
            (activity as HomeActivity).viewModel.sendRefuseContactRequestInSocket(
                AcceptRequest(
                    contactRequest = contactRequestItemInfo.id,
                    lang = (activity as HomeActivity).myLang,
                    userId = Integer.parseInt(userId)
                )
            )
            (activity as HomeActivity).viewModel.listenToRefuseContactRequestInSocket()
            (activity as HomeActivity).viewModel.refuseContactRequestResponseSocketLive.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                View.GONE
                            if (it.data!!.success) {
                                //   Constant.makeToastMessage(requireContext(), "تم رفض طلب التواصل")
                             //   makeNewApiRequest()
                            } else {
                                if (it.data.data.errors != null)
                                    Toast(context).showCustomToast(
                                        it.data.data.errors.toString(),
                                        requireActivity()
                                    )
                            }
                        }
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                    }
                })
        }
        if (action == "DeleteRequest") {
            (activity as HomeActivity).viewModel.deleteContactRequest(
                (activity as HomeActivity).token ,
                contactRequestItemInfo.id.toString()
            )
            (activity as HomeActivity).viewModel.deleteContactRequestResponseLive.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is Resource.Success -> {
                            (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                View.GONE
                            if (it.data!!.success) {
                                //  Constant.makeToastMessage(requireContext(), "تم حذف طلب التواصل")
                                // makeNewApiRequest()
                                (activity as HomeActivity).viewModel.currentDeletedContactRequestId.postValue(
                                    contactRequestItemInfo.id
                                )
                            }
                        }
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                    }
                })
        }
        if (action == "GoToContactRequestDetails") {
            val intent = Intent(requireContext(), ContactRequestDetailsActivity::class.java)
            intent.putExtra("contactRequestDetails", contactRequestItemInfo)
            startActivity(intent)
        }
    }

    private fun makeNewApiRequest() {
        (activity as HomeActivity).viewModel.getContactRequest(
            (activity as HomeActivity).token ,
            (activity as HomeActivity).myLang
        )
    }

}
