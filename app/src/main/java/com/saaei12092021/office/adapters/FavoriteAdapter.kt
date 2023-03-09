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
import com.saaei12092021.office.model.responseModel.favoritesResponse.Data
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import com.saaei12092021.office.util.GeneralFunctions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class FavoriteAdapter(private val listener: FavoriteAdapter.OnClickListener) :
    RecyclerView.Adapter<FavoriteAdapter.ArticleViewHolder>() {

    lateinit var mContext: Context

    inner class ArticleViewHolder(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorites: Data) {
            binding.apply {

                try {
                    val date = favorites.createdAt.getDateWithServerTimeStamp()
                    var formatter: DateTimeFormatter? = null
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
                        binding.dateAndTimeCreatedTv.text = dateTime.format(formatter2)
                    } else binding.dateAndTimeCreatedTv.text =
                        Constant.dateAndTimeReformat(favorites.createdAt)
                            .trim().replace(" ", "  ")
                } catch (e: Exception) {
                }

                binding.mainCategoryTv.text = favorites.category.categoryName
                if (favorites.contractType == "SALE")
                    binding.contractTypeTv.text = GeneralFunctions.translateEnumToWord("SALE")
                else binding.contractTypeTv.text =
                    GeneralFunctions.translateEnumToWord("RENT") + " - " + GeneralFunctions.translateEnumToWord(
                        favorites.rentType!!
                    )
                estateId.text = favorites.id.toString()

                binding.titleTv.text = favorites.title
                binding.cityNameTv.text = favorites.city.cityName
                binding.areaNameTv.text = favorites.area.areaName
                binding.priceTv.text = GeneralFunctions.reformatPrice(favorites.price.toString().toFloat())
                binding.descriptionTv.text = favorites.description

                if (!(favorites.imgs.isNullOrEmpty()))
                    Glide.with(mContext).load(favorites.imgs[0]).into(binding.estateTv)

                val propertyAdapter = PropertiesAdapter()
                binding.propertyRv.apply {
                    adapter = propertyAdapter
                    propertyAdapter.differ.submitList(favorites.properties)
                }

                binding.root.setOnClickListener {
                    listener.onItemClick(favorites.id.toString())
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
            _adsId: String
        )
    }

}













