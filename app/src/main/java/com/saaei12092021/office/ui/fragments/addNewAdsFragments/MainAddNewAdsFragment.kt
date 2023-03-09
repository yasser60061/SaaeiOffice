package com.saaei12092021.office.ui.fragments.addNewAdsFragments

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentMainAddNewAdsBinding
import com.saaei12092021.office.model.responseModel.UploadImagesModel
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.PropertiesItem
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.Unit
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.areasResponse.Area
import com.saaei12092021.office.model.responseModel.liveSearchResponse.Data
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.showCustomToast
import java.io.File

class MainAddNewAdsFragment : Fragment() {

    private var _binding: FragmentMainAddNewAdsBinding? = null
    val binding get() = _binding!!
    lateinit var viewModel: MyViewModel
    lateinit var addNewEstateAndModifyViewModel: AddNewEstateAndModifyViewModel
    var title_en: String = "apartment test Y"
    var title_ar: String = "اعلان شقه تجريبىY"
    var description_en: String = ""
    var description_ar: String = ""
    var bestFeature_en: String = ""
    var bestFeature_ar: String = ""
    var address_en: String = ""
    var address_ar: String = ""
    var contractType: String = "SALE" // SALE - RENT
    var rentType: String? = null //if contract type is RENT ("DAILY","MONTHLY","YEARLY","OPENING")
    var size: Int? = null
    var priceType: String = "NORMAL" // NORMAL - ON-SOOM
    var price: Int = 0
    lateinit var location: ArrayList<Double>
    var category: Int? = null
    var subCategory: Int? = null
    var city: Int = 0
    var area: Int? = null
    var imgs: ArrayList<String> = ArrayList()
    var imgs3D: ArrayList<String>? =
        ArrayList() // we don't use this now .. may be used it in the future
    var link3D: ArrayList<String>? = ArrayList() // we well used this that send from edit text .
    var adsFile: ArrayList<String>? = ArrayList()
    lateinit var properties: ArrayList<PropertiesItem>
    lateinit var features: ArrayList<Int>
    var enableInstallment: Boolean? = null
    var enablePhoneContact: Boolean? = null
    var region: Int? = null
    var streetName: String = ""
    var contraindications: Boolean? = null // هل يوجد موانع تمنع التصرف والانتفاع بالعقار؟
    var contraindicationsDesc: String = "" // تفاصيل المانع
    var undocumentedRights: Boolean? = null // هل يوجد بعض الحقوق الغير موثقه؟
    var undocumentedRightsDesc: String = "" // تفاصيل الحقوق
    var influentialInformation: Boolean? = null // هل يوجد معلومات تؤثر على العقار؟
    var influentialInformationDesc: String = "" // تتفاصيل المعلومات
    var saudiBuildingCode: Boolean = false
    var guarantees: Boolean? = null // هل هناك ضمانات
    var guaranteesDesc: String = "" // تفاصيل الضمانات
    var advertiserClass: String = "OWNER" // OWNER - COMMISSIONER صفه المعلن
    var commissionNumber: Int? = null // رقم التفويض اذا كان صفه المعلن C
    var deposit: Int? = null
    var hasDeposit: Boolean? = null
    var unitNumber: String? = null
    var floors: Int? = null
    var startSaleDate = ""
    var unitsListInfo: ArrayList<Data>? = ArrayList()
    var landsListInfo: ArrayList<Data> = ArrayList()
    var units: ArrayList<Unit>? = ArrayList()
    var oldUnits : ArrayList<Int>? = ArrayList()
    var lands: ArrayList<Int>? = ArrayList()
    var oldLands : ArrayList<Int>? = ArrayList()
    var pickedImagesList: ArrayList<UploadImagesModel> =
        ArrayList() // to save the the selected image on it
    var threeDImagesFile: File? = null
    var adsFileEstateFile: File? = null
    var owner: Int = 0
    lateinit var adNumber: String

    // this info needed for display for used only
    var areaList: ArrayList<Area> = ArrayList()
    var categoryName: String? = null
    var subCategoryName: String? = null
    var featuresName = ArrayList<String>()
    var propertiesName: ArrayList<String>? = null
    lateinit var areaName: String
    var categoryType: String? = ""
    lateinit var floorList: ArrayList<String>
    lateinit var adsResponse: AdsByIdResponse
    var remainingPackageNumber = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainAddNewAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility = View.GONE
        (activity as HomeActivity).binding.notificationRl.visibility = View.INVISIBLE
        (activity as HomeActivity).binding.backRl.visibility = View.VISIBLE
        (activity as HomeActivity).binding.homeTitleTv.text = getString(R.string.add_estate)
        viewModel = (activity as HomeActivity).viewModel
        addNewEstateAndModifyViewModel =
            ViewModelProvider(this).get(AddNewEstateAndModifyViewModel::class.java)

        //  imgs = ArrayList()
        //  imgs3D = ArrayList()
        owner = Integer.parseInt((activity as HomeActivity).myId)
        properties = ArrayList()
        features = ArrayList()
        featuresName = ArrayList()
        propertiesName = ArrayList()
        areaList = ArrayList()
        areaList.clear()
        location = ArrayList()
        location.add(0.0)
        location.add(0.0)

        floorList = ArrayList()

        floorList.add("أختر عدد الأدوار")
        for (i in 1..30) {
            floorList.add(i.toString() + " " + getString(R.string.floors))
        }

        addNewEstateAndModifyViewModel.error.observe(viewLifecycleOwner, Observer {
            Toast(requireContext()).showCustomToast(it.toString(), requireActivity())
        })


        if ((activity as HomeActivity).getUserResponse != null)
            if ((activity as HomeActivity).getUserResponse?.hasAdsPackage != null)
                remainingPackageNumber = (activity as HomeActivity).getUserResponse!!.availableAds

        val string11 = getColoredSpanned(
            " هي خدمة تمكنك من عرض عقاراتك بالتطبيق ولديك عدد معين حسب الباقة ، المتبقي لعدد باقتك هو ",
            "#707070"
        )
        val string22 = getColoredSpanned("  $remainingPackageNumber إضافات ", "#00B483")
        binding.addEstateTv.text = Html.fromHtml(string11 + string22)

        // --------------- this block used for display data and update it only -------------------

        if ((activity as HomeActivity).adsByIdResponse != null) {
            (activity as HomeActivity).binding.homeTitleTv.text = "تعديل بيانات العقار"

            adsResponse = (activity as HomeActivity).adsByIdResponse!!
            title_en = adsResponse.advertisement.title_en
            title_ar = adsResponse.advertisement.title
            description_en = adsResponse.advertisement.description_en
            description_ar = adsResponse.advertisement.description_ar
            bestFeature_en = adsResponse.advertisement.bestFeature_en
            bestFeature_ar = adsResponse.advertisement.bestFeature_ar
            address_en = adsResponse.advertisement.address_en
            address_ar = adsResponse.advertisement.address_ar
            contractType = adsResponse.advertisement.contractType // SALE - RENT
            rentType =
                adsResponse.advertisement.rentType //if contract type is RENT ("DAILY","MONTHLY","YEARLY","OPENING")
            size = adsResponse.advertisement.size.toInt()
            try {
                deposit = adsResponse.advertisement.deposit.toInt()
                price = adsResponse.advertisement.price.toInt()
            } catch (e: Exception) {
            }

            priceType = adsResponse.advertisement.priceType // NORMAL - ON-SOOM
            location = adsResponse.advertisement.location.coordinates as ArrayList<Double>
            category = adsResponse.advertisement.category.id
            subCategory = adsResponse.advertisement.subCategory.id
            city = adsResponse.advertisement.city.id
            area = adsResponse.advertisement.area.id
            imgs = adsResponse.advertisement.imgs as ArrayList<String>
            //  imgs3D = adsResponse.advertisement.imgs3D as ArrayList<String>
            link3D = adsResponse.advertisement.link3D as ArrayList<String>
            Log.d("link3D_", link3D.toString())
            //   var adsFile: ArrayList<String>? = ArrayList()
            adsResponse.advertisement.properties.forEach {
                properties.add(
                    PropertiesItem(
                        property = it.id.toString(),
                        value = it.value.toString()
                    )
                )
            }

            adsResponse.advertisement.features.forEach {
                features.add(it.id!!)
            }

            enableInstallment = adsResponse.advertisement.enableInstallment
            enablePhoneContact = adsResponse.advertisement.enablePhoneContact
            region = adsResponse.advertisement.region.id
            if (adsResponse.advertisement.streetName != null)
                streetName = adsResponse.advertisement.streetName!!
            contraindications =
                adsResponse.advertisement.contraindications // هل يوجد موانع تمنع التصرف والانتفاع بالعقار؟
            contraindicationsDesc = adsResponse.advertisement.contraindicationsDesc // تفاصيل المانع
            undocumentedRights =
                adsResponse.advertisement.undocumentedRights // هل يوجد بعض الحقوق الغير موثقه؟
            undocumentedRightsDesc =
                adsResponse.advertisement.undocumentedRightsDesc // تفاصيل الحقوق
            influentialInformation =
                adsResponse.advertisement.influentialInformation // هل يوجد معلومات تؤثر على العقار؟
            influentialInformationDesc =
                adsResponse.advertisement.influentialInformationDesc // تتفاصيل المعلومات
            saudiBuildingCode = adsResponse.advertisement.saudiBuildingCode
            guarantees = adsResponse.advertisement.guarantees // هل هناك ضمانات
            guaranteesDesc = adsResponse.advertisement.guaranteesDesc // تفاصيل الضمانات
            advertiserClass =
                adsResponse.advertisement.advertiserClass // OWNER - COMMISSIONER صفه المعلن
            if (advertiserClass == "COMMISSIONER")
                commissionNumber =
                    Integer.parseInt(adsResponse.advertisement.commissionNumber) // رقم التفويض اذا كان صفه المعلن C

            hasDeposit = adsResponse.advertisement.hasDeposit
            if ((activity as HomeActivity).adsByIdResponse != null)
                if ((adsResponse.advertisement.category.type == "BUILDING") and
                    (adsResponse.advertisement.unitNumber != null) and
                    (!adsResponse.advertisement.units.isNullOrEmpty())
                ) {
                    unitNumber = adsResponse.advertisement.unitNumber.toString()
                }

            if ((activity as HomeActivity).adsByIdResponse != null)
                if (adsResponse.advertisement.category.type == "PLANNED") {
                    if (adsResponse.advertisement.unitNumber != null)
                        unitNumber = adsResponse.advertisement.unitNumber.toString()
                    startSaleDate =
                        Constant.dateAndTimeReformat(adsResponse.advertisement.startSaleDate!!)
                }
        }
        // ----------------------------------------------------------------------------------

        displayPage1()

    }

    fun displayPage1() {
        val addNewAdsPage1Fragment: Fragment = AddNewAdsPage1Fragment()
        val transaction: FragmentTransaction =
            childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
        ).replace(
            R.id.child_fragment_container_add_new_estate,
            addNewAdsPage1Fragment
        ).commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageFourNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageNameTv.text = getString(R.string.first_step)
    }

    fun displayPage2() {
        val addNewAdsPage2Fragment: Fragment = AddNewAdsPage2Fragment()
        val transaction2: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction2.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,

            ).replace(
            R.id.child_fragment_container_add_new_estate,
            addNewAdsPage2Fragment
        ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageFourNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageNameTv.text = getString(R.string.second_step)
    }

    fun displayPage3() {
        val addedEstatesFragment3: Fragment = AddNewAdsPage3Fragment()
        val transaction3: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction3.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
        ).replace(
            R.id.child_fragment_container_add_new_estate,
            addedEstatesFragment3
        )
        transaction3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction3.setCustomAnimations(0, 0)
        transaction3.commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)
        binding.pageFourNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)

        binding.pageNameTv.text = getString(R.string.third_step)
    }

    fun displayPage4() {
        val addedEstatesFragment4: Fragment = AddNewAdsPage4Fragment()
        val transaction4: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction4.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,

            ).replace(
            R.id.child_fragment_container_add_new_estate,
            addedEstatesFragment4
        ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        binding.pageOneNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageTowNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageThreeNodeTv.setBackgroundResource(R.drawable.elementary1_circle_bg)
        binding.pageFourNodeTv.setBackgroundResource(R.drawable.elementary2_circle_bg)
        binding.pageNameTv.text = getString(R.string.review)
    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }
}