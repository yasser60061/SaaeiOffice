package com.saaei12092021.office.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.FavoriteItemBinding
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import com.saaei12092021.office.util.GeneralFunctions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AdsFromSocketAsListAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<AdsFromSocketAsListAdapter.ArticleViewHolder>() {

    lateinit var mContext: Context

    inner class ArticleViewHolder(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adsData: Data) {
            binding.apply {

                try {
                    val date = adsData.createdAt.getDateWithServerTimeStamp()
                    var formatter: DateTimeFormatter?
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(
                            Locale.ENGLISH)
                        val dateTime: LocalDateTime =
                            LocalDateTime.parse(date.toString(), formatter)
                        val formatter2: DateTimeFormatter =
                            DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy | hh:mm a",
                                 Locale("ar") // For english use Locale.ENGLISH
                              //  Locale.ENGLISH
                               // Locale.getDefault()
                            )
                        binding.dateAndTimeCreatedTv.text = dateTime.format(formatter2)
                    } else binding.dateAndTimeCreatedTv.text =
                        Constant.dateAndTimeReformat(adsData.createdAt)
                            .trim().replace(" ", "  ")
                } catch (e: Exception) {
                }

                binding.mainCategoryTv.text = adsData.category.categoryName
                if (adsData.contractType == "SALE")
                    binding.contractTypeTv.text = GeneralFunctions.translateEnumToWord("SALE")
                else binding.contractTypeTv.text =
                    GeneralFunctions.translateEnumToWord("RENT") + " - " + GeneralFunctions.translateEnumToWord(
                        adsData.rentType
                    )

                binding.estateId.text = adsData.id.toString()
                binding.titleTv.text = adsData.title
                binding.cityNameTv.text = adsData.city.cityName
                binding.areaNameTv.text = adsData.area.areaName
                binding.priceTv.text = GeneralFunctions.reformatPrice(adsData.price)
                binding.descriptionTv.text = adsData.description

                if (adsData.imgs.isNotEmpty())
                    Glide.with(mContext).load(adsData.imgs[0]).into(binding.estateTv)

                val propertyAdapter = PropertiesInAdsInfoFromMapAdapter()
                binding.propertyRv.apply {
                    adapter = propertyAdapter
                    propertyAdapter.differ.submitList(adsData.properties)
                }

                binding.root.setOnClickListener {
                    listener.onItemClick(adsData.id.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
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













