package com.saaei12092021.office.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.PackagesSliderItemBinding
import com.saaei12092021.office.model.responseModel.packagesResponse.Data
import com.saaei12092021.office.ui.activities.billInfoAndPaymentActivities.BillInfoActivity
import com.saaei12092021.office.ui.activities.packagesActivity.BottomSheetForMoreInPackagesList

class PackageSliderAdapter :
    RecyclerView.Adapter<PackageSliderAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: PackagesSliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(thePackage: Data) {
            binding.morTv.paintFlags = binding.morTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding.packageNameTv.text = thePackage.name
            if (thePackage.plan)
                binding.canAddPlannedTv.text = "نعم"
            else binding.canAddPlannedTv.text = "لا"
            if (thePackage.building)
                binding.canAddBuildingTv.text = "نعم"
            else binding.canAddBuildingTv.text = "لا"

            binding.numberOfAdsTv.text = thePackage.availableAds.toString()
            binding.supervisorNumberTv.text = thePackage.supervisors.toString()

            thePackage.costs.forEach {
                if (it.durationType == "YEARLY")
                    binding.yearlyPriceTv.text = it.cost.toString() + " " + "ريال"
                if (it.durationType == "MONTHLY")
                    binding.monthlyPriceTv.text = it.cost.toString() + " " + "ريال"
            }

            binding.yearlyPriceLinear.setOnClickListener {
                thePackage.costs.forEach { costItem ->
                    if (costItem.durationType == "YEARLY") {
                        val intent = Intent(mContext, BillInfoActivity::class.java)
                        intent.putExtra("type", "1")
                        intent.putExtra("packageId", thePackage.id.toString())
                        intent.putExtra("durationType", "YEARLY")
                        intent.putExtra("packageName", thePackage.name)
                        intent.putExtra("cost", costItem.cost.toString())
                        mContext.startActivity(intent)
                    }
                }
            }
            binding.monthlyPriceLinear.setOnClickListener {
                thePackage.costs.forEach { costItem ->
                    if (costItem.durationType == "MONTHLY") {
                        val intent = Intent(mContext, BillInfoActivity::class.java)
                        intent.putExtra("type", "1")
                        intent.putExtra("packageId", thePackage.id.toString())
                        intent.putExtra("durationType", "MONTHLY")
                        intent.putExtra("packageName", thePackage.name)
                        intent.putExtra("cost", costItem.cost.toString())
                        mContext.startActivity(intent)
                    }
                }
            }

            binding.morTv.setOnClickListener {
                BottomSheetForMoreInPackagesList(
                    mContext,
                    thePackage.name_ar,
                    thePackage.description_ar ,
                    "PACKAGE"
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            PackagesSliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val thePackage = differ.currentList[position]
        holder.bind(thePackage)
    }

    val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}