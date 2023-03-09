package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.WorkCategoryItemBinding
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory

class WorkSubCategoryAdapter(private val listener: OnChooseChangeListener?) :
    RecyclerView.Adapter<WorkSubCategoryAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: WorkCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subCategory: SubCategory) {
            binding.categoryNameCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener?.subCategoryOnItemChecked(
                        subCategoryPosition = adapterPosition,
                        subCategoryId = subCategory.id,
                        isSelectedSubCategory = true,
                    )
                } else {
                    listener?.subCategoryOnItemChecked(
                        subCategoryPosition = adapterPosition,
                        subCategoryId = subCategory.id,
                        isSelectedSubCategory = false,
                    )
                }
            }
            binding.categoryNameCb.text = subCategory.categoryName
            binding.categoryNameCb.isChecked = subCategory.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            WorkCategoryItemBinding.inflate(
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

    interface OnChooseChangeListener {
        fun subCategoryOnItemChecked(
            subCategoryPosition: Int,
            subCategoryId: Int,
            isSelectedSubCategory: Boolean,
        )
    }
}
