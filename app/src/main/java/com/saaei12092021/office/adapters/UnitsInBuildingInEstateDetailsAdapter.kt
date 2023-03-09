package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.FloorsAndUnitsInEstateDetailsItemBinding
import com.saaei12092021.office.model.responseModel.adsById.Unit

class UnitsInBuildingInEstateDetailsAdapter :
    RecyclerView.Adapter<UnitsInBuildingInEstateDetailsAdapter.MyViewHolder>() {

    lateinit var mContext: Context
    lateinit var unitsOrLndInBuildingOrPlaneAdapter: UnitsOrLndInBuildingOrPlaneAdapter

    inner class MyViewHolder(private var binding: FloorsAndUnitsInEstateDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(units: Unit) {

            binding.floorNameTv.text = "  الطابق رقم  " + units.floor

            unitsOrLndInBuildingOrPlaneAdapter = UnitsOrLndInBuildingOrPlaneAdapter()
            binding.unitsInBuildingRv.apply {
                adapter = unitsOrLndInBuildingOrPlaneAdapter
            }
            unitsOrLndInBuildingOrPlaneAdapter.differ.submitList(units.unit)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            FloorsAndUnitsInEstateDetailsItemBinding.inflate(
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

    val differCallback = object : DiffUtil.ItemCallback<Unit>() {
        override fun areItemsTheSame(oldItem: Unit, newItem: Unit): Boolean {
            return oldItem.floor == newItem.floor
        }

        override fun areContentsTheSame(oldItem: Unit, newItem: Unit): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}
