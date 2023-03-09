package com.saaei12092021.office.ui.activities.packagesActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.saaei12092021.office.adapters.PackageSliderAdapter
import com.saaei12092021.office.databinding.ActivityPackageBinding
import com.saaei12092021.office.model.responseModel.packagesResponse.Data
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.Transformer.DepthPageTransformer
import com.saaei12092021.office.util.showCustomToast
import java.util.*
import kotlin.collections.ArrayList

class PackageActivity : AppCompatActivity() {

    lateinit var binding: ActivityPackageBinding
    lateinit var myLang: String
    lateinit var viewModel: PackagesViewModel
    lateinit var packageList: ArrayList<Data>
    lateinit var packageSliderAdapter: PackageSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(PackagesViewModel::class.java)
        myLang = Constant.getMyLanguage(this)
        setUpLanguageViewAndDirection()
        initialRecyclerView()

        viewModel.error.observe(this, Observer {
            Toast(this).showCustomToast(it.toString(), this)
        })

        binding.backRl.setOnClickListener {
            finish()
        }

//        binding.contactUsBtn.setOnClickListener {
//            Constant.makeToastMessage(this, "سيتم تفعيلها قريبا")
//        }

        viewModel.getPackages(myLang)
        viewModel.packagesResponseLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    packageList = it.data!!.data as ArrayList<Data>
                    //    binding.actionButtonsLinear.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
//                    binding.subscribeInPackageBtn.setOnClickListener {
//
//                    }
                    packageSliderAdapter.differ.submitList(packageList)

                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
      //  if (deviceLanguage == "العربية") {
            binding.headerRl.layoutDirection = LAYOUT_DIRECTION_RTL
            binding.backIv.rotation = 180F
     //   }
    }

    private fun initialRecyclerView() {
        packageSliderAdapter = PackageSliderAdapter()
        binding.packagesRv.apply {
            adapter = packageSliderAdapter
            layoutManager =
                LinearLayoutManager(this@PackageActivity, RecyclerView.HORIZONTAL, false)
        }
    }
}