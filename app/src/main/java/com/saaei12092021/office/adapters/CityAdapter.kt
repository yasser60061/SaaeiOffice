package com.saaei12092021.office.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.CityItemBinding
import com.saaei12092021.office.model.responseModel.citiesResponse.City

class CityAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CityAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: CityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(city: City) {
            binding.root.setOnClickListener {
              //  binding.userProfileImage.borderColor = R.color.white
                listener.onItemClick(city,position)
            }
            binding.cityNameTv.text = city.cityName
            binding.cityNameTv.bringToFront()
            if ((city.img != "") and (city.img != "https://saaei-api-v2.algorithms.ws/uploads/2ba60d3b-d723-4e98-b606-01c627e89763.png"))
           Glide.with(mContext).load(city.img).into(binding.cityIv)
            if (city.selected) {
                binding.backCityIv.borderColor = Color.parseColor("#00B483")
                binding.cityIv.borderColor = Color.parseColor("#FFFFFF")
            }
            else {
                binding.backCityIv.borderColor = Color.parseColor("#EFEEF3")
                binding.cityIv.borderColor = Color.parseColor("#ABA9AF")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            return oldItem.location == newItem.location
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnItemClickListener {
        fun onItemClick(city: City , position: Int)
    }

}













