package com.saaei12092021.office.ui

import android.content.Context
import android.content.res.Resources
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.BottomSheetForConditionAndTermsBinding
import com.saaei12092021.office.util.Constant.WEB_VIW_URL

class BottomSheetForConditionAndTerms(mContext: Context? , content : String) {
    var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(mContext!!)
    var binding: BottomSheetForConditionAndTermsBinding

    init {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_condition_and_terms)
        // bottomSheetDialog.setCanceledOnTouchOutside(false)
        binding =
            BottomSheetForConditionAndTermsBinding.inflate(
                LayoutInflater.from(mContext),
                null,
                false
            )
        bottomSheetDialog.setContentView(binding.root)

        // ---- for design and style only ---- //
        val frameLayout =
            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        if (frameLayout != null) {
            val bottomSheetBehavior = BottomSheetBehavior.from<View>(frameLayout)
            bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.show()

        binding.bottomSheetCloseRl.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        binding.textTv.text = content

     //   val webUrl = WEB_VIW_URL +"terms?view=mobile"

//        binding.conditionAndTermsWebView.apply {
//            webViewClient = WebViewClient()
//            settings.javaScriptEnabled = true
//            settings.allowContentAccess = true
//            settings.domStorageEnabled = true
//            settings.setSupportZoom(true)
//            loadUrl(webUrl)
//        }
//    }

//    inner class WebViewClient : android.webkit.WebViewClient() {
//
//        // Load the URL
//        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//            view.loadUrl(url)
//            return false
//        }
//
//        // ProgressBar will disappear once page is loaded
//        override fun onPageFinished(view: WebView, url: String) {
//            super.onPageFinished(view, url)
//            binding.progressBar.visibility = View.GONE
//        }
    }

}