package com.saaei12092021.office.ui.fragments.notificationsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.NotificationAdapter
import com.saaei12092021.office.databinding.FragmentNotificationsBinding
import com.saaei12092021.office.model.responseModel.notificationsResponse.Data
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class NotificationsFragment : Fragment(), NotificationAdapter.OnClickListener {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var notificationAdapter: NotificationAdapter
    val binding get() = _binding!!
    lateinit var viewModel: NotificationsViewModel
    private var notificationList = ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayTheNotification()

        (activity as HomeActivity).binding.notificationRl.visibility = View.GONE
        (activity as HomeActivity).binding.backRl.visibility = View.VISIBLE
        (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility = View.INVISIBLE
        (activity as HomeActivity).binding.homeTitleTv.text = "الإشعارات"

        notificationList = ArrayList()
        viewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast(context).showCustomToast(it, requireActivity())
        })

        viewModel.getNotifications(
            (activity as HomeActivity).token,
            (activity as HomeActivity).myLang
        )

        viewModel.notificationsResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    notificationList = it.data?.data as ArrayList<Data>
                    notificationAdapter.differ.submitList(notificationList)

                    if (notificationList.isNullOrEmpty())
                        binding.resultTv.visibility = View.VISIBLE
                    else binding.resultTv.visibility = View.GONE
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

        viewModel.readTheNotificationLive.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).notificationsViewModel.getNotificationsUnreadCount((activity as HomeActivity).token)
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })


    }

    private fun displayTheNotification() {
        notificationAdapter = NotificationAdapter(this)
        binding.notificatioRv.apply {
            adapter = notificationAdapter
        }
    }

    override fun onItemClick(notification: Data, position: Int) {
        // Type of notification : APP ADS RATE MESSAGE FAVOURITE ADS-REQUEST CONTACT-REQUEST
        if (notification.type == "ADS-REQUEST") {
            (activity as HomeActivity).currentMyEstatePage = "requestEstate"
            (activity as HomeActivity).navController.navigate(R.id.mainMyEstateFragment)

        }

        if (notification.type == "CONTACT-REQUEST") {
            (activity as HomeActivity).currentMyEstatePage = "contactRequest"
            (activity as HomeActivity).navController.navigate(R.id.mainMyEstateFragment)
        }

        if (notification.type == "MESSAGE") {
            (activity as HomeActivity).currentMyEstatePage = "contactRequest"
            (activity as HomeActivity).isNewMessageInCurrentMyEstatePage = true
            (activity as HomeActivity).navController.navigate(R.id.mainMyEstateFragment)
        }

        if (notification.type == "APP") {
            Toast(requireContext()).showCustomToast(notification.description, requireActivity())
            notificationList[position].read = true
            notificationAdapter.differ.submitList(notificationList)
        }

        if (!notification.read) {
            viewModel.sendReadTheNotification(
                (activity as HomeActivity).token,
                notification.id.toString()
            )
            (activity as HomeActivity).binding.notificationUnreadCountTv.text = (--(activity as HomeActivity).unReadNotificationNumber).toString()
        }
    }
}


