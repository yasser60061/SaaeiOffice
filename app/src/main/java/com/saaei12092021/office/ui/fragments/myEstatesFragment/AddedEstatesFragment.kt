package com.saaei12092021.office.ui.fragments.myEstatesFragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.AddedEstateAdapter
import com.saaei12092021.office.adapters.AddedEstateWithPaginationAdapter
import com.saaei12092021.office.databinding.FragmentAddedEstatesBinding
import com.saaei12092021.office.model.responseModel.getAdsResponse.Ad
import com.saaei12092021.office.model.responseModel.getAdsWithPaginationResponse.Data
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.QUERY_PAGE_SIZE
import com.saaei12092021.office.util.GeneralFunctions.getUserIdIfSupervisorOrNot
import com.saaei12092021.office.util.Resource
import java.util.ArrayList

class AddedEstatesFragment : Fragment(),
    AddedEstateAdapter.OnClickListener,
    AddedEstateWithPaginationAdapter.OnClickListener {

    private var _binding: FragmentAddedEstatesBinding? = null
    lateinit var addedEstateAdapter: AddedEstateAdapter
    lateinit var addedEstateWithPaginationAdapter: AddedEstateWithPaginationAdapter
    val binding get() = _binding!!
    lateinit var allEstatesList: ArrayList<Data>
    lateinit var myEstatesList: ArrayList<Ad>
    lateinit var myId: String
    var tabNumber = 1

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var currentPage = 1
    var totalsPage = 1
    var totalItemCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddedEstatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allEstatesList = ArrayList()
        myEstatesList = ArrayList()
        allEstatesList.clear()
        myEstatesList.clear()

        initialMyEstateRv()
        myId = getUserIdIfSupervisorOrNot(requireActivity())
        getMyEstateRequest()
        getAllEstateRequest(page = 1)

        binding.myEstatesTv.setOnClickListener {
            displayMyEstate()
        }

        binding.otherEstatesTv.setOnClickListener {
            displayOthersEstate()
        }

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                //   val totalItemCount = layoutManager.itemCount

                val isNoErrors = !isError
                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isAtLastItem =
                    firstVisibleItemPosition + visibleItemCount >= layoutManager.itemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
                //
                val shouldPaginate = isAtLastItem &&
                        isNotLoadingAndNotLastPage && isNotAtBeginning && isTotalMoreThanVisible &&
                        isNoErrors
                        && isScrolling
                if (shouldPaginate) {
                    getAllEstateRequest(page = ++currentPage)
                    isScrolling = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        }

        addedEstateWithPaginationAdapter = AddedEstateWithPaginationAdapter(this)
        binding.addedEstateRv.apply {
            adapter = addedEstateWithPaginationAdapter
            addOnScrollListener(scrollListener)
        }

        listenToAllEstateResponse()
        listenToMyEstateResponse()
    }

    private fun getMyEstateRequest() {
        (activity as HomeActivity).viewModel.getAds(
            (activity as HomeActivity).token,
            (activity as HomeActivity).myLang,
            mainCategory = null,
            owner = Integer.parseInt(myId)
        )
    }

    private fun listenToAllEstateResponse() {
        (activity as HomeActivity).viewModel.getAdsWithPaginationResponse.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Success -> {

                        binding.paginationProgressBar.visibility =
                            View.INVISIBLE

                        val tempArray = it.data?.data as ArrayList<Data>
                        tempArray.forEach { resultItem ->
                          //  if (resultItem.owner.id.toString() != myId)
                                allEstatesList.add(resultItem)
                        }
                        val totalPages = it.data.pageCount
                        isLastPage = it.data.page == totalPages
                        totalItemCount = it.data.totalCount
                        isLoading = false
                        if (tabNumber == 1) {
                            binding.myEstateRv.visibility = View.GONE
                            binding.addedEstateRv.visibility = View.VISIBLE
                            binding.resultTv.visibility = View.GONE
                            addedEstateWithPaginationAdapter.differ.submitList(allEstatesList)
                            binding.addedEstateRv.visibility = View.GONE
                            binding.addedEstateRv.visibility = View.VISIBLE
                        }

                    }
                    is Resource.Loading -> {
                        if (currentPage > 1)
                            binding.paginationProgressBar.visibility =
                                View.VISIBLE
                        else
                            (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                                View.VISIBLE
                        isLoading = true
                    }
                    is Resource.Error -> {
                        // Toast(context).showCustomToast(err,this)
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE
                        binding.paginationProgressBar.visibility =
                            View.INVISIBLE
                        isLoading = false
                        isError = true
                    }
                }
            })
    }

    private fun listenToMyEstateResponse() {
        (activity as HomeActivity).viewModel.getAdsResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    myEstatesList.clear()

                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    myEstatesList = it.data?.ads as ArrayList<Ad>
                    if (tabNumber == 2) {
                        binding.myEstateRv.visibility = View.VISIBLE
                        binding.addedEstateRv.visibility = View.GONE
                        addedEstateAdapter.differ.submitList(myEstatesList)
                        if (myEstatesList.isNullOrEmpty())
                            binding.resultTv.visibility = View.VISIBLE
                        else binding.resultTv.visibility = View.GONE
                    }
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                    binding.resultTv.visibility = View.GONE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })
    }

    private fun getAllEstateRequest(page: Int) {
        (activity as HomeActivity).viewModel.getAdsWithPagination(
            token = (activity as HomeActivity).token,
            myLang = (activity as HomeActivity).myLang,
            page = page,
        )
    }

    private fun displayMyEstate() {
        tabNumber = 2
        binding.addedEstateRv.visibility = View.GONE
        if (myEstatesList.isNotEmpty()) {
            binding.myEstateRv.visibility = View.VISIBLE
            binding.resultTv.visibility = View.GONE
            addedEstateAdapter.differ.submitList(myEstatesList)
        } else {
            binding.resultTv.visibility = View.VISIBLE
            binding.addedEstateRv.visibility = View.GONE
        }
        binding.myEstatesTv.setBackgroundResource(R.drawable.rounded_button)
        binding.otherEstatesTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.myEstatesTv.setTextColor(Color.parseColor("#FFFFFF"))
        binding.otherEstatesTv.setTextColor(Color.parseColor("#000000"))
    }

    private fun displayOthersEstate() {
        tabNumber = 1
        binding.myEstatesTv.setBackgroundResource(R.drawable.nothing_shap_bg)
        binding.otherEstatesTv.setBackgroundResource(R.drawable.rounded_button)
        binding.myEstatesTv.setTextColor(Color.parseColor("#000000"))
        binding.otherEstatesTv.setTextColor(Color.parseColor("#FFFFFF"))
        binding.myEstateRv.visibility = View.GONE
        binding.addedEstateRv.visibility = View.VISIBLE
        binding.resultTv.visibility = View.GONE
        addedEstateWithPaginationAdapter.differ.submitList(allEstatesList)
    }

    private fun initialMyEstateRv() {
        addedEstateAdapter = AddedEstateAdapter(this)
        binding.myEstateRv.apply {
            adapter = addedEstateAdapter
        }
    }

    override fun onItemClick(ads_Id: String) {
        val intent = Intent(requireContext(), EstateDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("adsId", ads_Id)
        startActivity(intent)
    }


}
