package com.saaei12092021.office.ui.fragments.addNewAdsFragments

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentAddNewAdsPage3Binding
import com.saaei12092021.office.model.requestModel.rgeaAUTHValidation.RgeaAUTHValidationRequest
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class AddNewAdsPage3Fragment : Fragment() {

    private var _binding: FragmentAddNewAdsPage3Binding? = null
    val binding get() = _binding!!
    lateinit var parentFrag: MainAddNewAdsFragment
    lateinit var viewModel: MyViewModel
    var isFinalCheck = false
    lateinit var idType: String
    lateinit var idNumber: String

    var getUserResponse: GetUserResponse? = null
    lateinit var signUpResponse: com.saaei12092021.office.model.responseModel.SignUpResponse.User
    lateinit var loginResponse: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewAdsPage3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.validateAdNumberTv.paintFlags =
            binding.validateAdNumberTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        parentFrag = this@AddNewAdsPage3Fragment.parentFragment as MainAddNewAdsFragment
        parentFrag.binding.titleLinear.visibility = View.GONE
        viewModel = (activity as HomeActivity).viewModel

        displayDataIfExist()

        binding.ownerTv.setOnClickListener {
            binding.commissionerNumberEt.visibility = View.GONE
            binding.ownerTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.ownerTv.setTextColor(Color.parseColor("#00B483"))
            binding.commissionerTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.commissionerTv.setTextColor(Color.parseColor("#ABA9AF"))
            parentFrag.advertiserClass = "OWNER"
        }

        binding.commissionerTv.setOnClickListener {
            binding.commissionerNumberEt.visibility = View.VISIBLE
            binding.commissionerTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.commissionerTv.setTextColor(Color.parseColor("#00B483"))
            binding.ownerTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.ownerTv.setTextColor(Color.parseColor("#ABA9AF"))
            parentFrag.advertiserClass = "COMMISSIONER"
        }

        binding.priceTypeOnNormalTv.setOnClickListener {
            binding.priceTypeOnNormalTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.priceTypeOnNormalTv.setTextColor(Color.parseColor("#00B483"))
            binding.priceTypeOnSoomTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.priceTypeOnSoomTv.setTextColor(Color.parseColor("#ABA9AF"))
            parentFrag.priceType = "NORMAL"
        }

        binding.priceTypeOnSoomTv.setOnClickListener {
            binding.priceTypeOnSoomTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.priceTypeOnSoomTv.setTextColor(Color.parseColor("#00B483"))
            binding.priceTypeOnNormalTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.priceTypeOnNormalTv.setTextColor(Color.parseColor("#ABA9AF"))
            parentFrag.priceType = "ON-SOOM"
        }

        binding.depositYesRb.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                binding.depositValueEt.visibility = View.VISIBLE
            } else {
                binding.depositValueEt.visibility = View.GONE
            }
        }

        binding.validateAdNumberRl.setOnClickListener {
            checkValidateAdNumber()
        }

        viewModel.rgeaAUTHValidationLive.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.adNumberPb.visibility = View.VISIBLE
                    binding.validateAdNumberTv.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.adNumberPb.visibility = View.GONE
                    binding.validateAdNumberTv.visibility = View.GONE
                    binding.validateIv.visibility = View.VISIBLE
                    Log.d("rgeaAUTHValidation", it.data?.success.toString())
                    if (isFinalCheck) {
                        binding.adNumberPb.visibility = View.GONE
                        binding.validateAdNumberTv.visibility = View.VISIBLE
                        binding.validateIv.visibility = View.GONE
                        viewModel.rgeaAUTHValidationLive.postValue(Resource.Error(""))
                        parentFrag.displayPage4()
                    }
                }
                is Resource.Error -> {
                    binding.adNumberPb.visibility = View.GONE
                    binding.validateAdNumberTv.visibility = View.VISIBLE
                    binding.validateIv.visibility = View.GONE
                }
            }
        })

        binding.previousBtn.setOnClickListener {
            parentFrag.displayPage2()
        }

        binding.nextBtn.setOnClickListener {
            var price = 0
            var deposit = 0
            if (!(TextUtils.isEmpty(binding.priceEt.text.toString())))
                price = Integer.parseInt(binding.priceEt.text.toString())
            if (!(TextUtils.isEmpty(binding.depositValueEt.text.toString()))) {
                deposit = Integer.parseInt(binding.depositValueEt.text.toString())
            }
            if (TextUtils.isEmpty(binding.otherDescriptionEt.text.toString()))
                Constant.makeToastMessage(
                    requireContext(),
                    "الرجاء ادخال بعض الوصف للعقار"
                )
            else if (TextUtils.isEmpty(binding.bestFeaturesEt.text.toString()))
                Constant.makeToastMessage(
                    requireContext(),
                    "الرجاء إدخال افضل مميزات للعقار"
                )
            else if (TextUtils.isEmpty(binding.estateAddressEt.text.toString()))
                Constant.makeToastMessage(
                    requireContext(),
                    "الرجاء إدخال عنوان العقار بالتفصيل"
                )
            else if ((TextUtils.isEmpty(binding.priceEt.text.toString())) and (parentFrag.categoryType != "BUILDING"))
                Constant.makeToastMessage(requireContext(), "الرجاء ادخال سعر العقار")
            else if ((TextUtils.isEmpty(binding.priceEt.text.toString())) and (parentFrag.categoryType == "BUILDING") and (parentFrag.units.isNullOrEmpty()))
                Constant.makeToastMessage(requireContext(), "الرجاء ادخال سعر العقار")
            else if ((parentFrag.advertiserClass == "COMMISSIONER") and
                (TextUtils.isEmpty(binding.commissionerNumberEt.text.toString()))
            ) {
                Constant.makeToastMessage(requireContext(), "الرجاء ادخال رقم المفوض")
            } else if (TextUtils.isEmpty(binding.adNumberEt.text.toString()))
                Constant.makeToastMessage(
                    requireContext(),
                    "الرجاء إدخال رقم المعلن"
                )
            else if (!binding.depositYesRb.isChecked and !binding.depositNoRb.isChecked)
                Constant.makeToastMessage(
                    requireContext(),
                    "الرجاء اختيار حالة العربون"
                )
            else if ((binding.depositYesRb.isChecked)
                and (TextUtils.isEmpty(binding.depositValueEt.text.toString()))
            )
                Constant.makeToastMessage(
                    requireContext(),
                    "الرجاء كتابة قيمة العربون"
                )
            else if ((binding.depositYesRb.isChecked) and (price < deposit) and (parentFrag.categoryType != "PLANNED") and (parentFrag.categoryType != "LAND"))
                Constant.makeToastMessage(
                    requireContext(),
                    "يجب ان تكون قيمة العربون اقل من السعر"
                )
            else {
                parentFrag.description_en = binding.otherDescriptionEt.text.toString()
                parentFrag.description_ar = binding.otherDescriptionEt.text.toString()
                parentFrag.bestFeature_en = binding.bestFeaturesEt.text.toString()
                parentFrag.bestFeature_ar = binding.bestFeaturesEt.text.toString()
                parentFrag.address_en = binding.estateAddressEt.text.toString()
                parentFrag.address_ar = binding.estateAddressEt.text.toString()
                if (!TextUtils.isEmpty(binding.priceEt.text.toString()))
                    parentFrag.price = Integer.parseInt(binding.priceEt.text.toString())
                else parentFrag.price = 0
                parentFrag.adNumber = binding.adNumberEt.text.toString()
                parentFrag.enablePhoneContact = binding.allowCommunicationViaCb.isChecked
                parentFrag.enableInstallment = binding.doYouNeedInstallmentCb.isChecked
                if (parentFrag.advertiserClass == "COMMISSIONER")
                    parentFrag.commissionNumber =
                        Integer.parseInt(binding.commissionerNumberEt.text.toString())
                else parentFrag.commissionNumber = 0
                if (binding.depositYesRb.isChecked) {
                    parentFrag.hasDeposit = true
                    parentFrag.deposit = Integer.parseInt(binding.depositValueEt.text.toString())
                } else if (binding.depositNoRb.isChecked) {
                    parentFrag.hasDeposit = false
                    parentFrag.deposit = null
                }
                isFinalCheck = true
                checkValidateAdNumber()
            }
        }
    }

    private fun checkValidateAdNumber() {
        val userByIdData = Constant.getUserByIdResponse(requireActivity())
        val parentByIdData = Constant.getParentUserResponse(requireActivity())
        val userJsonLoginData = Constant.getLoginResponse(requireActivity())
        val userJsonSignUpData = Constant.getSignUpResponse(requireActivity())
        if (userByIdData != "") {
            getUserResponse =
                Gson().fromJson(userByIdData, GetUserResponse::class.java) as GetUserResponse
            idType = getUserResponse!!.idType
            if (getUserResponse!!.type != "SUPERVISOR")
                idNumber = getUserResponse!!.idNumber
            else if (parentByIdData != "") {
                val getParentByIdResponse =
                    Gson().fromJson(parentByIdData, GetUserResponse::class.java) as GetUserResponse
                idNumber = getParentByIdResponse.idNumber
            }
        } else if (userJsonLoginData != "") {
            loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
            idType = loginResponse.idType
            idNumber = loginResponse.idNumber
        } else if (userJsonSignUpData != "") {
            signUpResponse = Gson().fromJson(
                userJsonSignUpData,
                com.saaei12092021.office.model.responseModel.SignUpResponse.User::class.java
            ) as com.saaei12092021.office.model.responseModel.SignUpResponse.User
            idType = signUpResponse.idType
            idNumber = signUpResponse.idNumber
        }
        if (TextUtils.isEmpty(binding.adNumberEt.text.toString()))
            Toast(activity).showCustomToast("اكتب رقم المعلن", requireActivity())
        else {
            if (parentFrag.advertiserClass == "COMMISSIONER") {
                if (TextUtils.isEmpty(binding.commissionerNumberEt.text.toString())) {
                    Toast(activity).showCustomToast("اكتب رقم رقم المفوض", requireActivity())
                } else {
                    viewModel.rgeaAUTHValidation(
                        (activity as HomeActivity).token,
                        (activity as HomeActivity).myLang,
                        RgeaAUTHValidationRequest(
                            idType = idType,
                            idNumber = idNumber,
                            adNumber = binding.adNumberEt.text.toString(),
                            authNumber = binding.commissionerNumberEt.text.toString()
                        )
                    )
                }
            } else {
                viewModel.rgeaAUTHValidation(
                    (activity as HomeActivity).token,
                    (activity as HomeActivity).myLang,
                    RgeaAUTHValidationRequest(
                        idType = idType,
                        idNumber = idNumber,
                        adNumber = binding.adNumberEt.text.toString()
                    )
                )
            }
        }
    }

    private fun displayDataIfExist() {

        binding.adNumberPb.visibility = View.GONE
        binding.validateAdNumberTv.visibility = View.VISIBLE
        binding.validateIv.visibility = View.GONE

        if (parentFrag.contractType == "RENT") {
            binding.doYouNeedInstallmentRl.visibility = View.GONE
            binding.depositLinear.visibility = View.GONE
            binding.depositValueEt.visibility = View.GONE
            binding.priceTypeLabel.visibility = View.GONE
            binding.priceTypeLinear.visibility = View.GONE
            parentFrag.enableInstallment = false
            binding.doYouNeedInstallmentCb.isChecked = false
            parentFrag.hasDeposit = false
            when (parentFrag.rentType) {
                "DAILY" -> binding.priceLabelTv.text = "سعر الايجار اليومي"
                "MONTHLY" -> binding.priceLabelTv.text = "سعر الايجار الشهري"
                "YEARLY" -> binding.priceLabelTv.text = "سعر الايجار السنوي"
                "OPENING" -> binding.priceLabelTv.text = "سعر الايجار حسب الفترة المحددة"
            }
        } else {
            binding.doYouNeedInstallmentRl.visibility = View.VISIBLE
            binding.depositLinear.visibility = View.VISIBLE
            binding.priceTypeLabel.visibility = View.VISIBLE
            binding.priceTypeLinear.visibility = View.VISIBLE
            if (parentFrag.categoryType == "PLANNED")
                binding.priceLabelTv.text = "سعر المتر داخل المخطط"
            if (parentFrag.categoryType == "LAND")
                binding.priceLabelTv.text = "سعر المتر"
        }

        if (!TextUtils.isEmpty(parentFrag.description_ar)) {
            if ((activity as HomeActivity).myLang == "ar")
                binding.otherDescriptionEt.setText(parentFrag.description_ar)
            else binding.otherDescriptionEt.setText(parentFrag.description_en)
        }
        if (!TextUtils.isEmpty(parentFrag.bestFeature_ar)) {
            if ((activity as HomeActivity).myLang == "ar")
                binding.bestFeaturesEt.setText(parentFrag.bestFeature_ar)
            else binding.bestFeaturesEt.setText(parentFrag.bestFeature_en)
        }
        if (!TextUtils.isEmpty(parentFrag.address_ar)) {
            if ((activity as HomeActivity).myLang == "ar")
                binding.estateAddressEt.setText(parentFrag.address_ar)
            else binding.estateAddressEt.setText(parentFrag.address_en)
        }
        if (parentFrag.price != 0)
            binding.priceEt.setText(parentFrag.price.toString())
        if (parentFrag.priceType == "NORMAL") {
            binding.priceTypeOnNormalTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.priceTypeOnNormalTv.setTextColor(Color.parseColor("#00B483"))
            binding.priceTypeOnSoomTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.priceTypeOnSoomTv.setTextColor(Color.parseColor("#ABA9AF"))
        } else if (parentFrag.priceType == "ON-SOOM") {
            binding.priceTypeOnSoomTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.priceTypeOnSoomTv.setTextColor(Color.parseColor("#00B483"))
            binding.priceTypeOnNormalTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.priceTypeOnNormalTv.setTextColor(Color.parseColor("#ABA9AF"))
            parentFrag.priceType = "ON-SOOM"
        }
        if (parentFrag.advertiserClass == "OWNER") {
            binding.commissionerNumberEt.visibility = View.GONE
            binding.ownerTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.ownerTv.setTextColor(Color.parseColor("#00B483"))
            binding.commissionerTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.commissionerTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.commissionerNumberEt.visibility = View.GONE
        } else if (parentFrag.advertiserClass == "COMMISSIONER") {
            binding.commissionerNumberEt.visibility = View.VISIBLE
            binding.commissionerTv.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
            binding.commissionerTv.setTextColor(Color.parseColor("#00B483"))
            binding.ownerTv.setBackgroundResource(R.drawable.rounded_edit_text)
            binding.ownerTv.setTextColor(Color.parseColor("#ABA9AF"))
            binding.commissionerNumberEt.visibility = View.VISIBLE
            // binding.commissionerNumberEt.setText(parentFrag.commissionNumber.toString())
        }
        if (parentFrag.hasDeposit == true) {
            binding.depositYesRb.isChecked = true
            binding.depositValueEt.visibility = View.VISIBLE
            binding.depositValueEt.setText(parentFrag.deposit.toString())
        }
        if (parentFrag.hasDeposit == false) {
            binding.depositNoRb.isChecked = true
            binding.depositValueEt.visibility = View.GONE
        }

        if (parentFrag.enableInstallment == true)
            binding.doYouNeedInstallmentCb.isChecked = true
        if (parentFrag.enablePhoneContact == true)
            binding.allowCommunicationViaCb.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.adNumberPb.visibility = View.GONE
        binding.validateAdNumberTv.visibility = View.VISIBLE
        binding.validateIv.visibility = View.GONE
    }
}
