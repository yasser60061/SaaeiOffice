package com.saaei12092021.office.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.saaei12092021.office.databinding.FragmentFaqBinding
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity


class FAQFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).binding.homeTitleTv.text =
            (activity as HomeActivity).currentWebViewPageName
        val webUrl = (activity as HomeActivity).currentWebViewLinke

        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility = View.VISIBLE

        binding.faqWebView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.allowContentAccess = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(true)
            loadUrl(webUrl)
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            try {
                (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                    View.GONE
            } catch (e: Exception) {
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}