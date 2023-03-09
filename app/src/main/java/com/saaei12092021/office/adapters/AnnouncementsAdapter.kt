package com.saaei12092021.office.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.saaei12092021.office.model.responseModel.AnoncementsResponse.Anoncement
import androidx.recyclerview.widget.RecyclerView
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.AnnouncementItemBinding
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.showCustomToast
import java.util.ArrayList

class AnnouncementsAdapter(
    private var announcementList: ArrayList<Anoncement>,
    private val mContext: Context,
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return announcementList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflater!!.inflate(R.layout.announcement_item, container, false)
        val titleTv: TextView = view.findViewById(R.id.announcement_title_tv)
        val descriptionTv: TextView = view.findViewById(R.id.announcement_description_tv)
        val linkTv: TextView = view.findViewById(R.id.announcement_link_tv)
        val adsImageInSliderIv: ImageView = view.findViewById(R.id.announcement_iv)
        Glide.with(mContext).load(announcementList[position].img).into(adsImageInSliderIv)
        titleTv.text = announcementList[position].title_ar
        descriptionTv.text = announcementList[position].description_ar
        linkTv.text = announcementList[position].link

        linkTv.setOnClickListener {
            if (GeneralFunctions.isValidLink(announcementList[position].link.trim())) {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(announcementList[position].link))
                mContext.startActivity(browserIntent)
            } else Toast(mContext).showCustomToast("الرابط غير صالح", mContext as Activity)
        }

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}