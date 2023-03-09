package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.PropertyInEstateDetailsItemBinding
import com.saaei12092021.office.model.responseModel.adsById.Property

class PropertiesInEstateDetailsAdapter :
    RecyclerView.Adapter<PropertiesInEstateDetailsAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: PropertyInEstateDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(property: Property) {
            binding.propertyNameTv.text = property.name
            Glide.with(mContext).load(property.img).into(binding.propertyLogoIv)
            if (property.type == "NUMBER")
                binding.propertyNumberTv.text = property.value.toString()
            else if (property.type == "LIST")
                binding.propertyNumberTv.text = property.optionName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            PropertyInEstateDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}













