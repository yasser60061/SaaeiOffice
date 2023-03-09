package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.WorkCategoryItemBinding
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category

class WorkMainCategoryAdapter(private val listener: OnChooseChangeListener?) :
    RecyclerView.Adapter<WorkMainCategoryAdapter.MyViewHolder>() {
    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: WorkCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryNameCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listener?.mainCategoryOnItemChecked(
                        categoryPosition = adapterPosition,
                        categoryId = category.id,
                        isSelectedCategory = true,
                    )
                } else {
                    listener?.mainCategoryOnItemChecked(
                        categoryPosition = adapterPosition,
                        categoryId = category.id,
                        isSelectedCategory = false,
                    )
                }
            }
            binding.categoryNameCb.text = category.categoryName
            binding.categoryNameCb.isChecked = category.isSelected
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
        val category = differ.currentList[position]
        holder.bind(category)
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

    interface OnChooseChangeListener {
        fun mainCategoryOnItemChecked(
            categoryPosition: Int,
            categoryId: Int,
            isSelectedCategory: Boolean,
        )
    }
}
