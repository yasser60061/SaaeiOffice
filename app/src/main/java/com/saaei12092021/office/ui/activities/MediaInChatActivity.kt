package com.saaei12092021.office.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.ActivityMediaInChatBinding
import java.util.*

class MediaInChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityMediaInChatBinding
    private var dataType = ""
    private var theLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaInChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink_for_app)

        dataType = intent.getStringExtra("dataType").toString()
        theLink = intent.getStringExtra("theLink").toString()
        setUpLanguageViewAndDirection()

        if (dataType == "IMAGE") {
            binding.imageItemInChatIv.visibility = View.VISIBLE
            binding.urlInChatWb.visibility = View.GONE
            Glide.with(this).load(theLink).into(binding.imageItemInChatIv)
        }

        if (dataType == "URL") {
            binding.urlInChatWb.visibility = View.VISIBLE
            binding.imageItemInChatIv.visibility = View.GONE
            binding.urlInChatWb.apply {
                webViewClient = WebViewClient()
              //  invalidate()
              //  loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$theLink")
                loadUrl(theLink)
                settings.javaScriptEnabled = true
                settings.setSupportZoom(true)
//                settings.useWideViewPort = true
                settings.builtInZoomControls = true
            }
        }

        binding.backRl.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().displayLanguage
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
            binding.headerRelative.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
            binding.skipIv.rotation = 180F
      //  }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0, 0)
    }

}