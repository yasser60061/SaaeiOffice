package com.saaei12092021.office.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.AddedEstateAdapter
import com.saaei12092021.office.databinding.ActivityOfficeOrUserPageBinding
import com.saaei12092021.office.model.responseModel.getAdsResponse.Ad
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import java.util.*

class OfficeOrUserPageActivity : AppCompatActivity(), AddedEstateAdapter.OnClickListener {

    lateinit var binding: ActivityOfficeOrUserPageBinding
    lateinit var viewModel: MyViewModel
    lateinit var allEstatesList: ArrayList<Ad>
    var token: String? = null
    lateinit var myLang: String
    var ownerId: Int = 0
    lateinit var addedEstateAdapter: AddedEstateAdapter
    lateinit var getUserResponse: GetUserResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfficeOrUserPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        token = Constant.getToken(this)
        myLang = Constant.getMyLanguage(this)
        setUpLanguageViewAndDirection()
        ownerId = intent.getIntExtra("owner", 0)

        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black_light)

        allEstatesList = ArrayList()
        viewModel.getUser(token = token!!, userId = ownerId.toString())

        initialRv()

        viewModel.getAds(
            token!!,
            myLang,
            mainCategory = null,
            owner = ownerId,
        )

        binding.backRl.setOnClickListener {
            finish()
        }

        viewModel.getUserLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    getUserResponse = it.data!!

                    Log.d("get_user_resp", getUserResponse.toString())
                    if (getUserResponse.type == "OFFICE") {
                        binding.userTypeTv.text = "مكتب عقاري"
                        binding.userTypeTv.setBackgroundResource(R.drawable.rounded_button_dark_blue)
                    }
                    else if (getUserResponse.type == "USER"){
                        binding.userTypeTv.text = "مستخدم"
                        binding.userTypeTv.setBackgroundResource(R.drawable.rounded_button_green)
                    }
                    binding.userOrOfficeNameTv.text = getUserResponse.fullname
                    Glide.with(this@OfficeOrUserPageActivity)
                        .load(getUserResponse.img).placeholder(R.drawable.profile_image)
                        .into(binding.userOrOfficeProfileIv)
                    binding.cityNameTv.text = getUserResponse.city?.cityName
                    if (getUserResponse.type == "OFFICE"){
                        binding.ratingTextLinear.visibility = View.VISIBLE
                        binding.officeRatingValueRb.visibility = View.VISIBLE
                        binding.ratingNoTv.text = "(${getUserResponse.rateNumbers})"
                        binding.officeRatingValueRb.rating = getUserResponse.rate
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        viewModel.getAdsResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    allEstatesList.clear()
                    binding.progressBar.visibility =
                        View.GONE
                    allEstatesList = it.data?.ads as ArrayList<Ad>
                    addedEstateAdapter.differ.submitList(allEstatesList)

                }
                is Resource.Loading -> {
                    binding.progressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility =
                        View.GONE
                }
            }
        })
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
            binding.headerRelative.layoutDirection = LAYOUT_DIRECTION_RTL
            binding.backIv.rotation = 180F
    //    }
    }

    private fun initialRv() {
        addedEstateAdapter = AddedEstateAdapter(this)
        binding.adsRv.apply {
            adapter = addedEstateAdapter
        }
    }

    override fun onItemClick(ads_Id: String) {
        val intent = Intent(this, EstateDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("adsId", ads_Id)
        startActivity(intent)
    }
}