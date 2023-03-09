package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.AllSubCategoryItemBinding
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory

class AllSubCategoryAdapter(private val listener: AllSubCategoryAdapter.OnItemSubClickListener) :
    RecyclerView.Adapter<AllSubCategoryAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: AllSubCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

         fun bind(subCategory: SubCategory) {
            binding.root.setOnClickListener {
              //  binding.userProfileImage.borderColor = R.color.white
                listener.onItemSubClick(subCategory,position)
            }
             binding.subCategoryNameTv.text = subCategory.categoryName
            if (subCategory.isSelected)
                binding.subCategoryNameTv.setBackgroundResource(R.drawable.rounded_button_orange_light)
            else binding.subCategoryNameTv.setBackgroundResource(R.drawable.rounded_button_elementary)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            AllSubCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subCategory = differ.currentList[position]
        holder.bind(subCategory)
    }

    val differCallback = object : DiffUtil.ItemCallback<SubCategory>() {
        override fun areItemsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnItemSubClickListener {
        fun onItemSubClick(subCategory: SubCategory , position: Int)
    }
}













