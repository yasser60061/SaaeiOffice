package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.AllMainCategoryItemBinding
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category

class AllMainCategoryAdapter(private val listener: AllMainCategoryAdapter.OnItemClickListener) :
    RecyclerView.Adapter<AllMainCategoryAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: AllMainCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mainCategory: Category) {
            binding.root.setOnClickListener {
                //  binding.userProfileImage.borderColor = R.color.white
                listener.onItemClick(mainCategory, position)
            }
            binding.mainCategoryNameTv.text = mainCategory.categoryName
            if (mainCategory.isSelected) {
                binding.mainCategoryNameTv.setBackgroundResource(R.drawable.rounded_button_orange)
            } else {
                binding.mainCategoryNameTv.setBackgroundResource(R.drawable.rounded_button_elementary)
            }
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
        val mainCategory = differ.currentList[position]
        holder.bind(mainCategory)
    }

    val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnItemClickListener {
        fun onItemClick(mainCategory: Category, position: Int)
    }
}













