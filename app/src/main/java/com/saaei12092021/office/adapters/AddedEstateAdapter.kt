package com.saaei12092021.office.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.AddedEstateItemBinding
import com.saaei12092021.office.model.responseModel.getAdsResponse.Ad
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import com.saaei12092021.office.util.GeneralFunctions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddedEstateAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<AddedEstateAdapter.ArticleViewHolder>() {

    lateinit var mContext: Context

    inner class ArticleViewHolder(private val binding: AddedEstateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(requestedEstateAdded: Ad) {
            binding.apply {
                try {
                    val date = requestedEstateAdded.createdAt.getDateWithServerTimeStamp()
                    val formatter: DateTimeFormatter?
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(
                            Locale.ENGLISH)
                        val dateTime: LocalDateTime =
                            LocalDateTime.parse(date.toString(), formatter)
                        val formatter2: DateTimeFormatter =
                            DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy | hh:mm a",
                                  Locale("ar") // For english use Locale.ENGLISH
                             //   Locale.ENGLISH
                              //  Locale.getDefault()
                            )
                        binding.createdAtTx.text = dateTime.format(formatter2)
                    } else binding.createdAtTx.text =
                        Constant.dateAndTimeReformat(requestedEstateAdded.createdAt)
                            .trim().replace(" ", "  ")
                } catch (e: Exception) {
                }

                binding.mainCategoryTv.text =
                    requestedEstateAdded.category.categoryName
                if (requestedEstateAdded.contractType == "SALE")
                    binding.contractTypeTv.text = GeneralFunctions.translateEnumToWord("SALE")
                else binding.contractTypeTv.text =
                    GeneralFunctions.translateEnumToWord("RENT") + " - " + GeneralFunctions.translateEnumToWord(
                        requestedEstateAdded.rentType
                    )

                idTv.text = requestedEstateAdded.id.toString()

                titleTv.text = requestedEstateAdded.title
                cityNameTv.text = requestedEstateAdded.city.cityName
                areaNameTv.text = requestedEstateAdded.area.areaName
                priceTv.text = GeneralFunctions.reformatPrice(requestedEstateAdded.price.toString().toFloat())
                descriptionTv.text = requestedEstateAdded.description

                if (!requestedEstateAdded.imgs.isNullOrEmpty()) {
                    binding.estateTempIv.visibility = View.GONE
                    binding.estateIv.visibility = View.VISIBLE
                    Glide.with(mContext).load(requestedEstateAdded.imgs[0]).into(binding.estateIv)
                } else {
                    binding.estateIv.visibility = View.GONE
                    binding.estateTempIv.visibility = View.VISIBLE
                }

                val propertyAdapter = PropertiesAdapter()
                propertyRv.apply {
                    adapter = propertyAdapter
                    propertyAdapter.differ.submitList(requestedEstateAdded.properties)
                }

                binding.root.setOnClickListener {
                    listener.onItemClick(requestedEstateAdded.id.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            AddedEstateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val favorites = differ.currentList[position]
        holder.bind(favorites)
    }

    val differCallback = object : DiffUtil.ItemCallback<Ad>() {
        override fun areItemsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnClickListener {
        fun onItemClick(
            ads_Id: String
        )
    }

}













