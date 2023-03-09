package com.saaei12092021.office.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.AdsFromSocketAsListAdapter
import com.saaei12092021.office.adapters.AnnouncementsAdapter
import com.saaei12092021.office.databinding.FragmentAdsFromSocketAsListBinding
import com.saaei12092021.office.model.responseModel.AnoncementsResponse.Anoncement
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.Transformer.DepthPageTransformer
import com.saaei12092021.office.util.Transformer.FixedSpeedScroller
import java.lang.Exception
import java.lang.reflect.Field
import java.util.*

class AdsFromSocketAsListFragment : Fragment(), AdsFromSocketAsListAdapter.OnClickListener  {

    private var _binding: FragmentAdsFromSocketAsListBinding? = null
    lateinit var favoriteAdapter: AdsFromSocketAsListAdapter
    val binding get() = _binding!!
    lateinit var viewModel: MyViewModel
    var adsList = ArrayList<Data>()

    private var announcementsList: ArrayList<Anoncement> = ArrayList<Anoncement>()
    private var announcementsAdapter: AnnouncementsAdapter? = null
    var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdsFromSocketAsListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayRequiredBars()
        (activity as HomeActivity).viewModel.getAnnouncement()
        (activity as HomeActivity).binding.homeTitleTv.text = "عرض القائمة"
        displayAdsList()

        (activity as HomeActivity).viewModel.getAdsFromSocketResponse.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Success -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE

                     //   adsList.clear()
                        adsList = it.data?.data as ArrayList<Data>

                        (activity as HomeActivity).adsListFromSocket = adsList
                        favoriteAdapter.differ.submitList(adsList)

                        (activity as HomeActivity).binding.contentHomeId.searchResultTv.text =
                            "  تم العثور على  " + adsList.size + "   عقار  "

                        if (adsList.isNullOrEmpty())
                            binding.adsAsListResultTv.visibility = View.VISIBLE
                        if ((activity as HomeActivity).subCategoryList.isNullOrEmpty())
                            binding.tempView.visibility = View.GONE
                        else binding.tempView.visibility = View.VISIBLE

                    }
                    is Resource.Loading -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.VISIBLE
                        binding.adsAsListResultTv.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        // Toast(context).showCustomToast(err,this)
                        binding.adsAsListResultTv.visibility = View.VISIBLE
                        binding.adsAsListResultTv.text = "حدث خطا .. حاول تحديث الصفحة"
                    }
                }
            })

        (activity as HomeActivity).viewModel.announcementResponseLive.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is Resource.Success -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE
                        announcementsList = it.data?.anoncements as ArrayList<Anoncement>
                        if (!announcementsList.isNullOrEmpty()) {
                            binding.viewPagerRelative.visibility = View.VISIBLE
                            announcementsAdapter =
                                AnnouncementsAdapter(announcementsList, requireActivity())
                            binding.homeAnnouncementViewPager.adapter = announcementsAdapter
                            binding.homeAnnouncementViewPager.setPageTransformer(
                                true,
                                DepthPageTransformer()
                            )
                            try {
                                val mScroller: Field =
                                    ViewPager::class.java.getDeclaredField("mScroller")
                                mScroller.isAccessible = true
                                val scroller =
                                    FixedSpeedScroller(binding.homeAnnouncementViewPager.context)
                                //    scroller.setFixedDuration(5000);
                                mScroller.set(binding.homeAnnouncementViewPager, scroller)
                            } catch (e: NoSuchFieldException) {
                            }
                            createIndicator(announcementsList.size)
                        }
                    }
                    is Resource.Loading -> {
//                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
//                            View.VISIBLE
                    }
                    is Resource.Error -> {
                        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                            View.GONE
                    }
                }
            })
    }

    private fun createIndicator(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        layoutParams.height = 20
        layoutParams.width = 20
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]!!
                .setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_slider_indicator_inactive
                    )
                )
            indicators[i]!!.layoutParams = layoutParams
            binding.layoutSliderIndicators.addView(indicators[i])
            changeIndicator(0)
            startShow(announcementsList.size)
        }
    }

    private fun startShow(count: Int) {
        //  binding.homeAnnouncementViewPager.setCurrentItem(++currentPosition, true)
        val handler = Handler()
        val runnable = Runnable {
            //   for (i in 0 until count) {
            if (currentPosition == count) {
                currentPosition = 0
                changeIndicator(0)
            } else {
                changeIndicator(currentPosition)
            }
            binding.homeAnnouncementViewPager.setCurrentItem(++currentPosition, true)
            //   }
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 3000, 3000)
    }

    private fun changeIndicator(currentPosition: Int) {
        val childCount = binding.layoutSliderIndicators.childCount
        for (i in 0 until childCount) {
            try {
                val imageView = binding.layoutSliderIndicators.getChildAt(i) as ImageView
                if (i == currentPosition) {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.background_slider_indecator_active
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.background_slider_indicator_inactive
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displayRequiredBars() {
        (activity as HomeActivity).binding.notificationRl.visibility = View.INVISIBLE
        (activity as HomeActivity).binding.backRl.visibility = View.VISIBLE
    }

    private fun displayAdsList() {
        favoriteAdapter = AdsFromSocketAsListAdapter(this)
        binding.adsRv.apply {
            adapter = favoriteAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        displayRequiredBars()
    }

    override fun onItemClick(ads_Id: String) {
        val intent = Intent(requireContext(), EstateDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("adsId", ads_Id)
        startActivity(intent)
    }

}