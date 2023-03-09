package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.AllMainCategoryItemBinding
import com.saaei12092021.office.model.requestModel.ServiceListOnMapModel

class ServiceListOnMapAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ServiceListOnMapAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: AllMainCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: ServiceListOnMapModel) {
            binding.root.setOnClickListener {
                listener.onItemClick(service, adapterPosition)
            }
            binding.mainCategoryNameTv.text = service.serviceName
            if (service.isSelected)
                binding.mainCategoryNameTv.setBackgroundResource(R.drawable.rounded_button_orange)
            else binding.mainCategoryNameTv.setBackgroundResource(R.drawable.rounded_button_elementary)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AllMainCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val service = differ.currentList[position]
        holder.bind(service)
    }

    val differCallback = object : DiffUtil.ItemCallback<ServiceListOnMapModel>() {
        override fun areItemsTheSame(
            oldItem: ServiceListOnMapModel,
            newItem: ServiceListOnMapModel
        ): Boolean {
            return oldItem.serviceName == newItem.serviceName
        }

        override fun areContentsTheSame(
            oldItem: ServiceListOnMapModel,
            newItem: ServiceListOnMapModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnItemClickListener {
        fun onItemClick(service: ServiceListOnMapModel, position: Int)
    }
}













