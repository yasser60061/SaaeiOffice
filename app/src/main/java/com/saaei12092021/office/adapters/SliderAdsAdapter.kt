package com.saaei12092021.office.adapters

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.saaei12092021.office.R

class SliderAdsAdapter(
   val imageList: ArrayList<String>,
   val mContext: Context
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflater!!.inflate(R.layout.images_slider_item_for_ads, container, false)
        val adsImageInSliderIv: ImageView
        adsImageInSliderIv = view.findViewById(R.id.ads_iv_item)
        Glide.with(mContext).load(imageList[position]).into(adsImageInSliderIv)
        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}