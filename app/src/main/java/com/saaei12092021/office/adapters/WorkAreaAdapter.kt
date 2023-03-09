package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.WorkAreaItemBinding
import com.saaei12092021.office.model.responseModel.areasResponse.Area

class WorkAreaAdapter(private val listener: OnChooseChangeListener?) :
    RecyclerView.Adapter<WorkAreaAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: WorkAreaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(area: Area) {
            binding.areaNameCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener?.workAreaOnItemClick(
                        areaPosition = adapterPosition,
                        areaId = area.id,
                        selected = true,
                    )
                } else {
                    listener?.workAreaOnItemClick(
                        areaPosition = adapterPosition,
                        areaId = area.id,
                        selected = false,
                    )
                }
            }
            binding.areaNameCb.text = area.areaName
            binding.areaNameCb.isChecked = area.selected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            WorkAreaItemBinding.inflate(
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
        val area = differ.currentList[position]
        holder.bind(area)
    }

    val differCallback = object : DiffUtil.ItemCallback<Area>() {
        override fun areItemsTheSame(oldItem: Area, newItem: Area): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Area, newItem: Area): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnChooseChangeListener {
        fun workAreaOnItemClick(
            areaPosition: Int,
            areaId: Int,
            selected: Boolean,
        )
    }
}
