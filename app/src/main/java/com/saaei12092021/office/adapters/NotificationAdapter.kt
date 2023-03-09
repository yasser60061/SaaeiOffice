package com.saaei12092021.office.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.NotificationItemBinding
import com.saaei12092021.office.model.responseModel.notificationsResponse.Data
import com.saaei12092021.office.util.Constant

class NotificationAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<NotificationAdapter.ArticleViewHolder>() {

    lateinit var mContext: Context

    inner class ArticleViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Data) {
            binding.apply {
                if (!notification.read)
                    binding.mainNotificationItemLinear.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                else binding.mainNotificationItemLinear.setBackgroundResource(R.drawable.rounded_edit_text)
                binding.notificationTitle.text = notification.title
                binding.notificationDate.text = Constant.dateAndTimeReformat(notification.createdAt)
                binding.notificationDetails.text = notification.description

                binding.root.setOnClickListener {
                    listener.onItemClick(notification , adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val notification = differ.currentList[position]
        holder.bind(notification)
    }

    val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.dateMilleSec == newItem.dateMilleSec
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnClickListener {
        fun onItemClick(
            notification: Data ,
            position: Int
        )
    }

}













