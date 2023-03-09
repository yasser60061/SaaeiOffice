package com.saaei12092021.office.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.SubFeaturesInAddAdsItemBinding
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Feature

class SubFeaturesFromListAdapter(private val listener: OnCheckedChangeListener) :
    RecyclerView.Adapter<SubFeaturesFromListAdapter.MyViewHolder>() {

    private lateinit var mContext: Context

    inner class MyViewHolder(private val binding: SubFeaturesInAddAdsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(feature: Feature) {
            binding.mainFeatureNameCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener.subFeatureOnItemClick(feature, position, true)
                } else {
                    listener.subFeatureOnItemClick(feature, position, false)
                }
            }
            binding.mainFeatureNameCb.text = feature.name_ar
            binding.mainFeatureNameCb.isChecked = feature.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            SubFeaturesInAddAdsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val feature = differ.currentList[position]
        holder.bind(feature)
    }

    val differCallback = object : DiffUtil.ItemCallback<Feature>() {
        override fun areItemsTheSame(oldItem: Feature, newItem: Feature): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feature, newItem: Feature): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnCheckedChangeListener {
        fun subFeatureOnItemClick(feature: Feature, position: Int, isSelected: Boolean)
    }

}
