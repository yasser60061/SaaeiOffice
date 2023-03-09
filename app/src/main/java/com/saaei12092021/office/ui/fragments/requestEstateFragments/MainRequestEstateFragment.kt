package com.saaei12092021.office.ui.fragments.requestEstateFragments

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentMainRequestEstateBinding
import com.saaei12092021.office.model.responseModel.areasResponse.Area
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Feature
import com.saaei12092021.office.model.socketRequest.addAdsRequest.Property
import com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse.AddAdsRequestFromSocketResponse
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.ui.fragments.addAndRequestEstateDialogFragment.AddAndRequestEstateDialogViewModel
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource

class MainRequestEstateFragment : Fragment() {

    private var _binding: FragmentMainRequestEstateBinding? = null
    val binding get() = _binding!!
    lateinit var viewModel: MyViewModel
    lateinit var addAndRequestEstateDialogViewModel: AddAndRequestEstateDialogViewModel

    var owner: Int = 0
    var title_en = "test  ads request"
    var title_ar = "طلب اعلان تجريبى"
    var description_en: String = ""
    var description_ar = ""
    var contractType = "SALE" // SALE or RENT
    var rentType: String? = null //if contract type is RENT ("DAILY","MONTHLY","YEARLY","OPENING")
    var sizeFrom: Int = 0
    var sizeTo: Int = 0
    var priceType = "NORMAL"
    var priceFrom: Int = 0
    var priceTo: Int = 0
    var category: Int? = null
    var categoryName: String? = null
    var subCategory: Int? = null
    var subCategoryName: String? = null
    var region: Int? = null
    var city: Int? = 0
    var cityName: String? = null
    var features = ArrayList<Int>()
    var featuresName = ArrayList<String>()
    lateinit var properties: ArrayList<Property>
    var propertiesName: ArrayList<String>? = null
    var enableInstallment: Boolean? = false
    var enablePhoneContact: Boolean? = false
    var areaList: ArrayList<Area> = ArrayList()
    var selectedAreaList: ArrayList<Int> = ArrayList()
    var areaName: String? = null
    lateinit var mainFeaturesList: ArrayList<Feature>

    lateinit var addAdsRequestFromSocketResponse: AddAdsRequestFromSocketResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainRequestEstateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility = View.GONE
        (activity as HomeActivity).binding.notificationRl.visibility = View.INVISIBLE
        (activity as HomeActivity).binding.backRl.visibility = View.VISIBLE
        (activity as HomeActivity).binding.homeTitleTv.text = "طلب عقار"
        viewModel = (activity as HomeActivity).viewModel
        addAndRequestEstateDialogViewModel = ViewModelProvider(this).get(AddAndRequestEstateDialogViewModel::class.java)

        owner = Integer.parseInt((activity as HomeActivity).myId)
        properties = ArrayList()
        features = ArrayList()
        featuresName = ArrayList()
        propertiesName = ArrayList()
        mainFeaturesList = ArrayList()

        displayPage1()

        viewModel.addAdsRequestRequestFromSocketLive.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    addAdsRequestFromSocketResponse = it.data!!
                    if (addAdsRequestFromSocketResponse.data?.errors == null) {
                        Constant.makeToastMessage(
                            requireActivity(),
                            getString(R.string.the_order_is_sent)
                        )
                        displayPage4()
                        viewModel.addAdsRequestRequestFromSocketLive.postValue(Resource.Error(""))
                    }
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility = View.GONE
                }
            }
        })

        addAndRequestEstateDialogViewModel.getSettingInfo()
        addAndRequestEstateDialogViewModel.settingResponseLive.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    val string1 = getColoredSpanned(
                        " هي خدمة تمكنك من طلب عقار بمواصفات خاصة تصل الى أكثر من ",
                        "#707070"
                    )
                    val string2 =
                        getColoredSpanned("${it.data!!.setting.officeNumbers} مكتب", "#00B483")
                    val string3 = getColoredSpanned(" مسجل معتمد لدينا في التطبيق ", "#707070")
                    binding.requestEstateTv.text = Html.fromHtml(string1 + string2 + string3)
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })
    }

    fun displayPage1() {
        val requestEstatesPage1Fragment: Fragment = RequestEstatePage1Fragment()
        val transaction: FragmentTransaction =
            childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
        ).replace(
            R.id.child_fragment_container_request_estate,
            requestEstatesPage1Fragment
        ).commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageNameTv.text = "الخطوة الأولى"
    }

    fun displayPage2() {
        val addedEstatesFragment1: Fragment = RequestEstatePage2Fragment()
        val transaction1: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction1.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,

            ).replace(
            R.id.child_fragment_container_request_estate,
            addedEstatesFragment1
        ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageNameTv.text = "الخطوة الثانية"
    }

    fun displayPage3() {
        val addedEstatesFragment2: Fragment = RequestEstatePage3Fragment()
        val transaction2: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction2.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
        ).replace(
            R.id.child_fragment_container_request_estate,
            addedEstatesFragment2
        )
        transaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction2.setCustomAnimations(0, 0)
        transaction2.commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)

        binding.pageNameTv.text = "مراجعة"
    }

    fun displayPage4() {
        val addedEstatesFragment4: Fragment = RequestEstatePage4Fragment()
        val transaction4: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction4.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,

            ).replace(
            R.id.child_fragment_container_request_estate,
            addedEstatesFragment4
        ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

}