package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.OptionInMainFeaturesItemBinding
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Option

class OptionsListInRequestEstateAdapter(private val listener: OnChooseChangeListener?) :
    RecyclerView.Adapter<OptionsListInRequestEstateAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: OptionInMainFeaturesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: Option) {
            binding.optionInMainFeatureNameCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener?.optionOnItemClick(
                        thisFeaturePosition = option.thisFeaturePosition,
                        thisFeatureId = option.thisFeatureId,
                        optionPosition = position,
                        optionId = option.id,
                        isSelectedOption = true,
                    )
                } else {
                    listener?.optionOnItemClick(
                        thisFeaturePosition = option.thisFeaturePosition,
                        thisFeatureId = option.thisFeatureId,
                        optionPosition = position,
                        optionId = 0 ,
                        isSelectedOption = false,
                    )
                }
            }
            binding.optionInMainFeatureNameCb.text = option.name
            binding.optionInMainFeatureNameCb.isChecked = option.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            OptionInMainFeaturesItemBinding.inflate(
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

    val differCallback = object : DiffUtil.ItemCallback<Option>() {
        override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnChooseChangeListener {
        fun optionOnItemClick(
            thisFeaturePosition: Int,
            thisFeatureId: Int,
            optionPosition: Int,
            optionId: Int,
            isSelectedOption: Boolean,
        )
    }
}
