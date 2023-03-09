package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.WorkCityItemBinding
import com.saaei12092021.office.model.responseModel.citiesResponse.City

class WorkCityAdapter(private val listener: OnChooseChangeListener?) :
    RecyclerView.Adapter<WorkCityAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: WorkCityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City) {
            binding.cityNameCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener?.workCityOnItemChecked(
                        cityPosition = adapterPosition,
                        cityId = city.id,
                        isSelectedCity = true,
                    )
                } else {
                    listener?.workCityOnItemChecked(
                        cityPosition = adapterPosition,
                        cityId = city.id,
                        isSelectedCity = false,
                    )
                }
            }
            binding.cityNameCb.text = city.cityName
            binding.cityNameCb.isChecked = city.selectedWork
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            WorkCityItemBinding.inflate(
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
        val city = differ.currentList[position]
        holder.bind(city)
    }

    val differCallback = object : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnChooseChangeListener {
        fun workCityOnItemChecked(
            cityPosition: Int,
            cityId: Int,
            isSelectedCity: Boolean,
        )
    }
}
