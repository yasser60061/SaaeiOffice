package com.saaei12092021.office.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.saaei12092021.office.adapters.FavoriteAdapter
import com.saaei12092021.office.databinding.FragmentFavoriteBinding
import com.saaei12092021.office.model.responseModel.favoritesResponse.Data
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import java.util.ArrayList

class FavoriteFragment : Fragment(), FavoriteAdapter.OnClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    lateinit var favoriteAdapter: FavoriteAdapter
    val binding get() = _binding!!
    lateinit var viewModel: MyViewModel
    var favoriteList = ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel

        initialFavoriteRvAndAdapter()

        (activity as HomeActivity).binding.notificationRl.visibility = View.GONE
        (activity as HomeActivity).binding.backRl.visibility = View.VISIBLE
        (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility = View.INVISIBLE
        (activity as HomeActivity).binding.homeTitleTv.text = "قائمة المفضلة"
        binding.favoriteResultTv.visibility = View.GONE

      //----------------------------------------------------------------------------------


        viewModel.favoritesResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    favoriteList = it.data?.data as ArrayList<Data>
                    favoriteAdapter.differ.submitList(favoriteList)
                    if (favoriteList.isNullOrEmpty())
                        binding.favoriteResultTv.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                    binding.favoriteResultTv.visibility = View.GONE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })
    }

    private fun initialFavoriteRvAndAdapter() {
        favoriteAdapter = FavoriteAdapter(this)
        binding.favoriteRv.apply {
            adapter = favoriteAdapter
        }
    }

    override fun onItemClick(_adsId: String) {
        val intent = Intent(requireContext(), EstateDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("adsId", _adsId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyFavorites(
            Constant.getToken(requireContext()),
            (activity as HomeActivity).myId
        )
    }
}