package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.StatisticsViewsItemBinding
import com.saaei12092021.office.model.responseModel.adsById.Statistic

class StatisticsAndReviewsAdapter :
    RecyclerView.Adapter<StatisticsAndReviewsAdapter.ArticleViewHolder>() {

    lateinit var mContext: Context

    inner class ArticleViewHolder(private val binding: StatisticsViewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(statistic: Statistic) {
            binding.apply {
                val layoutParams = binding.drawTheBarTv.layoutParams as LinearLayout.LayoutParams
                // the 50 must remove it is for view only
                layoutParams.height = (statistic.city_percentage_for_total_views + 30).toInt()
//                if (position == 0)
//                    layoutParams.height = (statistic.city_percentage_for_total_views + 100).toInt()
                // layoutParams.setMargins(0, (100 - statistic.city_percentage_for_total_views).toInt(), 0, 0)
                binding.cityNameTx.text = statistic.city.cityName
                binding.viewsNumberTx.text = statistic.count.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            StatisticsViewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val statistic = differ.currentList[position]
        holder.bind(statistic)
    }

    val differCallback = object : DiffUtil.ItemCallback<Statistic>() {
        override fun areItemsTheSame(oldItem: Statistic, newItem: Statistic): Boolean {
            return oldItem.city.id == newItem.city.id
        }

        override fun areContentsTheSame(oldItem: Statistic, newItem: Statistic): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}













