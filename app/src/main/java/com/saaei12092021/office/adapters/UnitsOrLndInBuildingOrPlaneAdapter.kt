package com.saaei12092021.office.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.UnitInBuildingInEstateDetailsItemBinding
import com.saaei12092021.office.model.responseModel.adsById.UnitX
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity

class UnitsOrLndInBuildingOrPlaneAdapter : RecyclerView.Adapter<UnitsOrLndInBuildingOrPlaneAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: UnitInBuildingInEstateDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(unit: UnitX) {
            binding.landOrUnitNumberTv.text = unit.id.toString()
            binding.landOrUnitTitleTv.text = unit.title.toString()

            binding.root.setOnClickListener {
                val intent = Intent(mContext, EstateDetailsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("adsId", unit.id.toString())
                mContext.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            UnitInBuildingInEstateDetailsItemBinding.inflate(
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

    val differCallback = object : DiffUtil.ItemCallback<UnitX>() {
        override fun areItemsTheSame(oldItem: UnitX, newItem: UnitX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UnitX, newItem: UnitX): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}
