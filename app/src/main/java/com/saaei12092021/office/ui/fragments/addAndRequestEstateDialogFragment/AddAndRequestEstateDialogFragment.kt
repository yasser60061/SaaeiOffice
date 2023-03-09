package com.saaei12092021.office.ui.fragments.addAndRequestEstateDialogFragment

import android.app.Dialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentAddAndRequestEstateDialogBinding
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.SignupActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.ui.activities.packagesActivity.PackageActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast

class AddAndRequestEstateDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddAndRequestEstateDialogBinding? = null
    private val binding get() = _binding!!
    var remainingPackageNumber = 0
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    lateinit var addAndRequestEstateDialogViewModel: AddAndRequestEstateDialogViewModel
    lateinit var myViewModel: MyViewModel
    var getUserResponseIfSupervisor : GetUserResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAndRequestEstateDialogBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if ((activity as HomeActivity).getUserResponse != null)
            if ((activity as HomeActivity).getUserResponse?.hasAdsPackage != null)
                remainingPackageNumber = (activity as HomeActivity).getUserResponse!!.availableAds

        displayDataInAddEstateCard()
        addAndRequestEstateDialogViewModel =
            ViewModelProvider(this).get(AddAndRequestEstateDialogViewModel::class.java)
        myViewModel =
            ViewModelProvider(this).get(MyViewModel::class.java)

        addAndRequestEstateDialogViewModel.getSettingInfo()

        addAndRequestEstateDialogViewModel.settingResponseLive.observe(
            viewLifecycleOwner,
            Observer {
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

        if (GeneralFunctions.ifUserIsSupervisor(requireActivity())) {
            binding.updatePackageLinear.visibility = View.GONE
            myViewModel.getUser(
                token = (activity as HomeActivity).token,
                userId = (activity as HomeActivity).myId
            )

            myViewModel.getUserLive.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        remainingPackageNumber = it.data!!.availableAds
                        getUserResponseIfSupervisor = it.data
                        Constant.editor(requireContext()).apply {
                            putString(Constant.PARENT_USER_BY_ID_RESPONSE, Gson().toJson(it.data))
                            apply()
                        }
                        displayDataInAddEstateCard()
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            })
        }
        binding.requestEstateLinear.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                findNavController().navigateUp()
                (activity as HomeActivity).navController.navigate(R.id.mainRequestEstateFragment)
                (activity as HomeActivity).mustUpdateLocation = true
                dismiss()
            }
        }

        binding.closeIv.setOnClickListener {
            dismiss()
        }

        binding.addEstateLinear.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                if (GeneralFunctions.ifUserIsSupervisor(requireActivity()) and (getUserResponseIfSupervisor == null))
                    Toast(requireActivity()).showCustomToast("انتظر تحميل بيانات المكتب", requireActivity())
               else if (remainingPackageNumber == 0) {
                    Toast(requireActivity()).showCustomToast(
                        "يجب الاشتراك بباقة",
                        requireActivity()
                    )
                    if (!GeneralFunctions.ifUserIsSupervisor(requireActivity())) {
                        val intent = Intent(requireActivity(), PackageActivity::class.java)
                        startActivity(intent)
                    }
                    dismiss()
                } else {
                    findNavController().navigateUp()
                    (activity as HomeActivity).navController.navigate(R.id.mainAddNewAdsFragment)
                    (activity as HomeActivity).mustUpdateLocation = true
                    dismiss()
                }
            }
        }

        binding.updatePackageLinear.setOnClickListener {
            val intent = Intent(requireActivity(), PackageActivity::class.java)
            startActivity(intent)
            dismiss()
        }
    }

    private fun displayDataInAddEstateCard() {
        val string11 = getColoredSpanned(
            " هي خدمة تمكنك من عرض عقاراتك بالتطبيق ولديك عدد معين حسب الباقة ، المتبقي لعدد باقتك هو ",
            "#707070"
        )
        val string22 = getColoredSpanned("  $remainingPackageNumber إضافات ", "#00B483")
        binding.addEstateTv.text = Html.fromHtml(string11 + string22)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    fun expendDialog(activity: FragmentActivity?, logTag: String, performOnError: () -> Unit) {
        try {
            val bottomSheet = dialog!!.findViewById(R.id.design_bottom_sheet) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)
            behavior.peekHeight = displayMetrics.heightPixels
        } catch (e: NullPointerException) {
            Log.d(logTag, e.message ?: "NPE in onResume")
            performOnError()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

}