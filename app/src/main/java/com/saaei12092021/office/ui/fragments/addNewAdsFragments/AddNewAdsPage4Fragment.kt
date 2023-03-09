package com.saaei12092021.office.ui.fragments.addNewAdsFragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saaei12092021.office.databinding.FragmentAddNewAdsPage4Binding
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.AddNewAdsRequest
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class AddNewAdsPage4Fragment : Fragment() {

    private var _binding: FragmentAddNewAdsPage4Binding? = null
    val binding get() = _binding!!
    lateinit var parentFrag: MainAddNewAdsFragment
    lateinit var viewModel: MyViewModel
    lateinit var addAndUpdateViewModel: AddNewEstateAndModifyViewModel
    private var loadingBar: ProgressDialog? = null
    var startSaleDate: String? = ""
    var totalImagesFileNumber: Int = 0
    var threeDUploaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewAdsPage4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFrag = this@AddNewAdsPage4Fragment.parentFragment as MainAddNewAdsFragment
        parentFrag.binding.titleLinear.visibility = View.GONE
        viewModel = (activity as HomeActivity).viewModel
        addAndUpdateViewModel =
            ViewModelProvider(this).get(AddNewEstateAndModifyViewModel::class.java)
        if ((activity as HomeActivity).adsByIdResponse != null)
            binding.nextBtn.text = "حفظ التغيرات"

        addAndUpdateViewModel.error.observe(viewLifecycleOwner, Observer {
            Toast(requireContext()).showCustomToast(it.toString(), requireActivity())
        })

        displayData()

        when (parentFrag.categoryType) {
            "BUILDING" -> {
                parentFrag.lands = null
                startSaleDate = null
            }
            "PLANNED" -> {
                parentFrag.floors = null
                parentFrag.units = null
                startSaleDate = parentFrag.startSaleDate
            }
            else -> {
                parentFrag.unitNumber = null
                parentFrag.floors = null
                parentFrag.units = null
                parentFrag.lands = null
                startSaleDate = null
            }
        }

        // if an error occurred while saving new ads info .. must clear last success images uploaded
        parentFrag.imgs.clear()
        parentFrag.imgs3D?.clear()
        parentFrag.adsFile?.clear()
        addAndUpdateViewModel.addNewEstateLiveData2.postValue(Resource.Error(""))
        addAndUpdateViewModel.uploadEstateImagesLiveData.postValue(Resource.Error(""))

        loadingBar = ProgressDialog(requireContext())

        parentFrag.binding.pageNameTv.setOnClickListener {
            parentFrag.displayPage1()
        }

        binding.contraindicationsYesRb.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                binding.contraindicationsDescriptionEt.visibility = View.VISIBLE
            } else {
                binding.contraindicationsDescriptionEt.visibility = View.GONE
            }
        }

        binding.undocumentedYesRb.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                binding.undocumentedRightsDescriptionEt.visibility = View.VISIBLE
            } else {
                binding.undocumentedRightsDescriptionEt.visibility = View.GONE
            }
        }

        binding.influentialInformationYesRb.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                binding.influentialInformationDescriptionEt.visibility = View.VISIBLE
            } else {
                binding.influentialInformationDescriptionEt.visibility = View.GONE
            }
        }

        binding.guaranteesYesRb.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                binding.guaranteesDetailsEt.visibility = View.VISIBLE
            } else {
                binding.guaranteesDetailsEt.visibility = View.GONE
            }
        }

        binding.previousBtn.setOnClickListener {
            parentFrag.displayPage3()
        }

        binding.nextBtn.setOnClickListener {
            when {
                (binding.contraindicationsYesRb.isChecked)
                        and (TextUtils.isEmpty(binding.contraindicationsDescriptionEt.text.toString())) -> {
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء كتابة تفاصيل المانع"
                    )
                }
                (binding.undocumentedYesRb.isChecked)
                        and (TextUtils.isEmpty(binding.undocumentedRightsDescriptionEt.text.toString())) -> {
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء كتابة تفاصيل الحقوق"
                    )
                }
                (binding.influentialInformationYesRb.isChecked)
                        and (TextUtils.isEmpty(binding.influentialInformationDescriptionEt.text.toString())) -> {
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء كتابة تفاصيل المعلومات"
                    )
                }
                (binding.guaranteesYesRb.isChecked)
                        and (TextUtils.isEmpty(binding.guaranteesDetailsEt.text.toString())) -> {
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء كتابة تفاصيل الضمانات"
                    )
                }
                !binding.contraindicationsYesRb.isChecked and !binding.contraindicationsNoRb.isChecked ||
                        !binding.undocumentedYesRb.isChecked and !binding.undocumentedNoRb.isChecked ||
                        !binding.influentialInformationYesRb.isChecked and !binding.influentialInformationNoRb.isChecked ||
                        !binding.guaranteesYesRb.isChecked and !binding.guaranteesNoRb.isChecked ||
                        !binding.saudiBuildingCodeYesRb.isChecked and !binding.saudiBuildingCodeNoRb.isChecked -> {
                    Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء الاجابة على جميع الأسئلة"
                    )
                }
                else -> {
                    addAndUpdateViewModel.addNewEstateLiveData2.postValue(Resource.Loading())
                    addAndUpdateViewModel.uploadEstateImagesLiveData.postValue(Resource.Loading())
                    parentFrag.imgs.clear()
                    parentFrag.imgs3D!!.clear()
                    parentFrag.adsFile!!.clear()
                    // Constant.makeToastMessage(requireContext(), "test for reach") // for delete later
                    parentFrag.contraindications = binding.contraindicationsYesRb.isChecked
                    parentFrag.contraindicationsDesc =
                        binding.contraindicationsDescriptionEt.text.toString()
                    parentFrag.undocumentedRights = binding.undocumentedYesRb.isChecked
                    parentFrag.undocumentedRightsDesc =
                        binding.undocumentedRightsDescriptionEt.text.toString()
                    parentFrag.influentialInformation =
                        binding.influentialInformationYesRb.isChecked
                    parentFrag.influentialInformationDesc =
                        binding.influentialInformationDescriptionEt.text.toString()
                    parentFrag.guarantees = binding.guaranteesYesRb.isChecked
                    parentFrag.guaranteesDesc = binding.guaranteesDetailsEt.text.toString()
                    parentFrag.saudiBuildingCode = binding.saudiBuildingCodeYesRb.isChecked

                    parentFrag.pickedImagesList.forEach {
                        if (it.imageFile != null)
                            totalImagesFileNumber += 1
                    }

                    parentFrag.pickedImagesList.forEachIndexed { index, element ->

                        if ((parentFrag.threeDImagesFile != null) and (!threeDUploaded) and (element.imageFile != null)) {
                            addAndUpdateViewModel.uploadImagesForAddNewEstate(
                                token = (activity as HomeActivity).token,
                                myLang = (activity as HomeActivity).myLang,
                                estateImageFile1 = element.imageFile,
                                threeDFile = parentFrag.threeDImagesFile,
                                adsFile = parentFrag.adsFileEstateFile,
                            )
                            threeDUploaded = true
                        } else if (element.imageFile != null)
                            addAndUpdateViewModel.uploadImagesForAddNewEstate(
                                token = (activity as HomeActivity).token,
                                myLang = (activity as HomeActivity).myLang,
                                estateImageFile1 = element.imageFile,
                                threeDFile = null,
                                adsFile = null,
                            )
                        if ((totalImagesFileNumber == 0) and
                            (parentFrag.imgs3D!!.size == 0) and
                            (parentFrag.pickedImagesList.size - 1 == index)
                        )
                            sendAllDataWithImages()
                    }

                    addAndUpdateViewModel.uploadEstateImagesLiveData.observe(
                        viewLifecycleOwner,
                        Observer {
                            when (it) {
                                is Resource.Loading -> {
                                    loadingBar!!.setMessage("جاري رفع صور العقار")
                                    loadingBar!!.setCanceledOnTouchOutside(false)
                                    loadingBar!!.show()
                                }
                                is Resource.Success -> {
                                    if (!it.data!!.imgs!!.isNullOrEmpty())
                                        parentFrag.imgs.add(it.data.imgs!![0])
                                    if (!it.data.imgs3D.isNullOrEmpty())
                                        parentFrag.imgs3D!!.add(it.data.imgs3D[0])
                                    if (!it.data.adsFile.isNullOrEmpty())
                                        parentFrag.adsFile!!.add(it.data.adsFile[0])

                                    if (parentFrag.imgs.size == totalImagesFileNumber)
                                        sendAllDataWithImages()
                                }
                                is Resource.Error -> loadingBar!!.dismiss()
                            }
                        })
                }
            }
        }
    }

    private fun displayData() {
        if ((activity as HomeActivity).adsByIdResponse != null) {
            if (parentFrag.contraindications!!) {
                binding.contraindicationsYesRb.isChecked = true
                binding.contraindicationsDescriptionEt.visibility = View.VISIBLE
                binding.contraindicationsDescriptionEt.setText(parentFrag.contraindicationsDesc)
            } else binding.contraindicationsNoRb.isChecked = true

            if (parentFrag.undocumentedRights!!) {
                binding.undocumentedYesRb.isChecked = true
                binding.undocumentedRightsDescriptionEt.visibility = View.VISIBLE
                binding.undocumentedRightsDescriptionEt.setText(parentFrag.undocumentedRightsDesc)
            } else binding.undocumentedNoRb.isChecked = true

            if (parentFrag.influentialInformation!!) {
                binding.influentialInformationYesRb.isChecked = true
                binding.influentialInformationDescriptionEt.visibility = View.VISIBLE
                binding.influentialInformationDescriptionEt.setText(parentFrag.influentialInformationDesc)
            } else binding.influentialInformationNoRb.isChecked = true

            if (parentFrag.guarantees!!) {
                binding.guaranteesYesRb.isChecked = true
                binding.guaranteesDetailsEt.visibility = View.VISIBLE
                binding.guaranteesDetailsEt.setText(parentFrag.guaranteesDesc)
            } else binding.guaranteesNoRb.isChecked = true

            if (parentFrag.saudiBuildingCode)
                binding.saudiBuildingCodeYesRb.isChecked = true
            else binding.saudiBuildingCodeNoRb.isChecked = true
        }
    }

    private fun sendAllDataWithImages() {
        val tempImageLink: ArrayList<String> = ArrayList()
        parentFrag.pickedImagesList.forEach {
            if ((it.imageFile == null) and (it.imageUrlIfUpdate != ""))
                tempImageLink.add(it.imageUrlIfUpdate)
        }
        parentFrag.imgs.addAll(tempImageLink)
        val theRequestModel = AddNewAdsRequest(
            title_en = "إعلان" + " - " + parentFrag.categoryName,
            title_ar = "إعلان" + " - " + parentFrag.categoryName,
            description_en = parentFrag.description_en,
            description_ar = parentFrag.description_ar,
            bestFeature_en = parentFrag.bestFeature_en,
            bestFeature_ar = parentFrag.bestFeature_ar,
            address_en = parentFrag.address_en,
            address_ar = parentFrag.address_ar,
            contractType = parentFrag.contractType,
            rentType = parentFrag.rentType,
            size = parentFrag.size!!,
            priceType = parentFrag.priceType,
            price = parentFrag.price,
            location = parentFrag.location,
            category = parentFrag.category!!,
            subCategory = parentFrag.subCategory!!,
            city = parentFrag.city,
            area = parentFrag.area!!,
            imgs = parentFrag.imgs,
            imgs3D = parentFrag.imgs3D,
            link3D = parentFrag.link3D ,
            adsFile = parentFrag.adsFile,
            properties = parentFrag.properties,
            features = parentFrag.features,
            enableInstallment = parentFrag.enableInstallment!!,
            enablePhoneContact = parentFrag.enablePhoneContact!!,
            region = parentFrag.region!!,
            contraindications = parentFrag.contraindications!!,
            contraindicationsDesc = parentFrag.contraindicationsDesc,
            undocumentedRights = parentFrag.undocumentedRights!!,
            undocumentedRightsDesc = parentFrag.undocumentedRightsDesc,
            influentialInformation = parentFrag.influentialInformation!!,
            influentialInformationDesc = parentFrag.influentialInformationDesc,
            saudiBuildingCode = parentFrag.saudiBuildingCode,
            advertiserClass = parentFrag.advertiserClass,
            commissionNumber = parentFrag.commissionNumber!!,
            adNumber = parentFrag.adNumber,
            guarantees = parentFrag.guarantees!!,
            guaranteesDesc = parentFrag.guaranteesDesc,
            deposit = parentFrag.deposit,
            hasDeposit = parentFrag.hasDeposit,
            unitNumber = parentFrag.unitNumber,
            floors = parentFrag.floors,
            units = parentFrag.units,
            oldUnits = parentFrag.oldUnits ,
            lands = parentFrag.lands,
            oldLands = parentFrag.oldLands ,
            streetName = parentFrag.streetName ,
            startSaleDate = startSaleDate
        )
        if ((activity as HomeActivity).adsByIdResponse != null)
            addAndUpdateViewModel.updateAds(
                token = (activity as HomeActivity).token,
                myLang = (activity as HomeActivity).myLang,
                updateAds = theRequestModel,
                adsId = (activity as HomeActivity).adsByIdResponse!!.advertisement.id.toString()
            )
        else addAndUpdateViewModel.addNewEstate(
            token = (activity as HomeActivity).token,
            myLang = (activity as HomeActivity).myLang,
            addNewAdsRequest = theRequestModel
        )
        Toast.makeText(
            requireActivity(),
            "جاري حفظ البيانات",
            Toast.LENGTH_LONG
        ).show()
        Log.d("theRequestModel", theRequestModel.toString())
        Log.d("theRequestModel_deposit", theRequestModel.deposit.toString() + theRequestModel.hasDeposit)

        addAndUpdateViewModel.addNewEstateLiveData2.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.INVISIBLE
                    Constant.makeToastMessage(requireContext(), "تمت اضافة العقار")
                    (activity as HomeActivity).onBackPressed()
                    val intent = Intent(requireContext(), EstateDetailsActivity::class.java)
                    intent.putExtra("adsId", it.data!!.advertisement.id.toString())
                    startActivity(intent)
                    loadingBar!!.dismiss()
                }
                is Resource.Error -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    loadingBar!!.dismiss()
                }
            }
        })

        addAndUpdateViewModel.updateAdsLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.INVISIBLE
                    Constant.makeToastMessage(requireContext(), "تم تحديث العقار")
                    (activity as HomeActivity).finish()
                }
                is Resource.Error -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    loadingBar!!.dismiss()
                }
            }
        })
    }
}