package com.saaei12092021.office.adapters

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.UnitInBuildingItemBinding
import com.saaei12092021.office.model.responseModel.liveSearchResponse.Data
import com.saaei12092021.office.util.showCustomToast

class UnitsInFloorsInAddEstateAdapter(private val listener: OnUnitChooseChangeListener?) :
    RecyclerView.Adapter<UnitsInFloorsInAddEstateAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: UnitInBuildingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(unitData: Data) {

            binding.landOrUnitNumberCb.text = unitData.unitNumber.toString()
            binding.landOrUnitTitleTv.text = unitData.title
            if (unitData.selectedFloorNumber != 200)
                binding.floorNumberEt.setText(unitData.selectedFloorNumber.toString())
            else binding.floorNumberEt.setText("")
            binding.landOrUnitNumberCb.isChecked = unitData.isSelected

            binding.landOrUnitNumberCb.setOnCheckedChangeListener { buttonView, isChecked ->
                val value = binding.floorNumberEt.text.toString()
                if (isChecked) {
                    when {
                        TextUtils.isEmpty(value) -> {
                            binding.floorNumberEt.error = "اكتب رقم الطابق اولا"
                            binding.landOrUnitNumberCb.isChecked = false
                        }
                        unitData.totalFloorNumber < Integer.parseInt(value) -> {
                            Toast(mContext).showCustomToast("الطابق غير موجود", mContext as Activity)
                            binding.landOrUnitNumberCb.isChecked = false
                        }
                        else -> {
                            listener?.unitOnItemClick(
                                floorNumber = Integer.parseInt(value) ,
                                unitData = unitData,
                                isSelected = true,
                                unitPosition = adapterPosition
                            )
                        }
                    }
                } else {
                    binding.floorNumberEt.setText("")
                    listener?.unitOnItemClick(
                        floorNumber = 200,
                        unitData = unitData,
                        isSelected = false,
                        unitPosition = adapterPosition
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            UnitInBuildingItemBinding.inflate(
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
        val unit = differ.currentList[position]
        holder.bind(unit)
    }

    val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.unitNumber == newItem.unitNumber
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnUnitChooseChangeListener {
        fun unitOnItemClick(
            floorNumber: Int,
            unitData: Data,
            isSelected: Boolean,
            unitPosition: Int
        )
    }
}
