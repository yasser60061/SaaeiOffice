package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.PropertiesItemBinding
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Property

class PropertiesInAdsInfoFromMapAdapter :
    RecyclerView.Adapter<PropertiesInAdsInfoFromMapAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: PropertiesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(property: Property) {
            binding.propertyNumberTv.text = toIntString(property.value.toString())
            Glide.with(mContext).load(property.img).into(binding.propertyLogoIv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            PropertiesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val property = differ.currentList[position]
        holder.bind(property)
    }

    val differCallback = object : DiffUtil.ItemCallback<Property>() {
        override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem.img == newItem.img
        }

        override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private fun toIntString(theString: String): String {
        val string = StringBuilder(theString).also {
            it.setCharAt(theString.length - 1, ' ')
            it.setCharAt(theString.length - 2, ' ')
        }
        return string.toString().trim()
    }

}













