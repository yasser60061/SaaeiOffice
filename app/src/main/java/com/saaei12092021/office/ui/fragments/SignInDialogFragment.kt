package com.saaei12092021.office.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.view.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.saaei12092021.office.databinding.FragmentSignInDialogeBinding
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.ui.activities.LoginActivity

class SignInDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSignInDialogeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInDialogeBinding.inflate(inflater, container, false)
        dialog!!.requestWindowFeature(STYLE_NORMAL)
        dialog!!.setCancelable(true)
        // dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val win = dialog!!.window
        win!!.setGravity(Gravity.CENTER)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ((activity as HomeActivity).getUserResponse != null) {
            if ((!(activity as HomeActivity).getUserResponse!!.approved) or ((activity as HomeActivity).getUserResponse!!.accountType != "ACTIVE")) {
                binding.loginAndOfficeStatusTv.text = "حسابك بانتظار التفعيل , يرجى الإنتظار"
                binding.loginOrRegisterBtn.text = "لديك حساب مفعل ؟"
            } else if (((activity as HomeActivity).getUserResponse!!.approved) or ((activity as HomeActivity).getUserResponse!!.accountType == "ACTIVE")) {
                (activity as HomeActivity).showDialogForCompleteProfile()
                dismiss()
            } else {
                binding.loginAndOfficeStatusTv.text =
                    "يجب عليك تسجيل الدخول أولا للتمتع بجميع ميزات التطبيق"
            }
        } else if ((activity as HomeActivity).loginResponse != null) {
            if (!(activity as HomeActivity).loginResponse!!.approved) {
                binding.loginAndOfficeStatusTv.text = "حسابك بانتظار التفعيل , يرجى الإنتظار"
                binding.loginOrRegisterBtn.text = "لديك حساب مفعل ؟"
            } else {
                binding.loginAndOfficeStatusTv.text =
                    "يجب عليك تسجيل الدخول أولا للتمتع بجميع ميزات التطبيق"
            }
        }

        binding.rootRl.setOnClickListener {
            dismiss()
        }

        binding.loginOrRegisterBtn.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}