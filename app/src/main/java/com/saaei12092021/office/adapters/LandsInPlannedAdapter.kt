package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.LandInPlannedItemBinding

import com.saaei12092021.office.model.responseModel.liveSearchResponse.Data

class LandsInPlannedAdapter(private val listener: OnChooseChangeListener?) :
    RecyclerView.Adapter<LandsInPlannedAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    // we will used work area layout because it is the similar view needed
    inner class MyViewHolder(private val binding: LandInPlannedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(landInfo: Data) {
            binding.landOrUnitNumberCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener?.landOnItemClick(
                        position = adapterPosition,
                        landInfo = landInfo,
                        isSelected = true,
                    )
                } else {
                    listener?.landOnItemClick(
                        position = adapterPosition,
                        landInfo = landInfo,
                        isSelected = false,
                    )
                }
            }
            binding.landOrUnitNumberCb.text = landInfo.unitNumber.toString()
            binding.landOrUnitNumberCb.isChecked = landInfo.isSelected!!
            binding.landOrUnitTitleTv.text = landInfo.title

            //  binding.landNumberTv.text = " القطعة رقم ${landInfo.unitNumber}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LandInPlannedItemBinding.inflate(
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
        val landInfo = differ.currentList[position]
        holder.bind(landInfo)
    }

    val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(
            oldItem: Data,
            newItem: Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Data,
            newItem: Data
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnChooseChangeListener {
        fun landOnItemClick(
            position: Int,
            landInfo: Data,
            isSelected: Boolean,
        )
    }
}
