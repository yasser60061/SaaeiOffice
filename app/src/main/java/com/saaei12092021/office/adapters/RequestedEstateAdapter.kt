package com.saaei12092021.office.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.RequestedEstatesItemBinding
import com.saaei12092021.office.model.responseModel.adsRequestedResponse.Data
import com.saaei12092021.office.util.GeneralFunctions

class RequestedEstateAdapter(private val listener: OnActionClickedListener) :
    RecyclerView.Adapter<RequestedEstateAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: RequestedEstatesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adsRequestedData: Data) {
            binding.apply {
                requestedAdsTitleTv.text = adsRequestedData.title
                try {
                    priceTv.text = GeneralFunctions.reformatPrice(toIntString(adsRequestedData.priceFrom.toString()).toFloat())
                } catch (e: Exception) {
                }

                cityNameTv.text = adsRequestedData.city.cityName
                if (adsRequestedData.area!!.isNotEmpty())
                    areaNameTv.text = adsRequestedData.area[0].areaName
                else areaNameTv.text = "-"
                //    priceTypeTv.text = adsRequestedData.priceType
                descriptionTv.text = adsRequestedData.description
                statusTv.text = GeneralFunctions.translateEnumToWord(adsRequestedData.status)

                if (adsRequestedData.status == "NEW")
                    statusTv.setBackgroundResource(R.drawable.rounded_button_blue)
                else if (adsRequestedData.status == "ON-PROGRESS")
                    statusTv.setBackgroundResource(R.drawable.rounded_button_orange)
                else if (adsRequestedData.status == "COMPLETED")
                    statusTv.setBackgroundResource(R.drawable.rounded_button_green)
                else if (adsRequestedData.status == "CANCELED")
                    statusTv.setBackgroundResource(R.drawable.rounded_button_red)
                else statusTv.setBackgroundResource(R.drawable.rounded_button_blue)
//                binding.sentContactRequestTv.visibility = View.GONE
//                binding.sentContactRequestTv.isEnabled = true

//                if (adsRequestedData.owner.id.toString() == adsRequestedData.myId)
//                    binding.sentContactRequestTv.visibility = View.GONE
//                else {
//                    binding.sentContactRequestTv.visibility = View.VISIBLE
//                    if (adsRequestedData.status == "NEW") {
//                        binding.sentContactRequestTv.visibility = View.GONE
//                        binding.sentContactRequestTv.setTextColor(Color.parseColor("#FF000000"))
//                        binding.sentContactRequestTv.text = "أرسل رسالة"
//                        binding.sentContactRequestTv.isEnabled = true
//                    } else {
//                        binding.sentContactRequestTv.setTextColor(Color.parseColor("#FF018786"))
//                        binding.sentContactRequestTv.text = "تم الارسال"
//                        binding.sentContactRequestTv.isEnabled = false
//                    }
//                }
            }

//            binding.sentContactRequestTv.setOnClickListener {
//                listener.adsRequestedAction(
//                    adsRequestedData, "sendContactRequest"
//                )
//                binding.sentContactRequestTv.isEnabled = false
//                binding.sentContactRequestTv.text = "تم الارسال"
//                binding.sentContactRequestTv.setTextColor(Color.parseColor("#FF018786"))
//            }

            binding.root.setOnClickListener {
                listener.adsRequestedAction(
                    adsRequestedData, "openThisRequestedEstateDetails"
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RequestedEstatesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val adsData = differ.currentList[position]
        holder.bind(adsData)
    }

    val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnActionClickedListener {
        fun adsRequestedAction(adsRequestedInfo: Data, action: String)
    }

    private fun toIntString(theString: String): String {
        val string = StringBuilder(theString).also {
            it.setCharAt(theString.length - 1, ' ')
            it.setCharAt(theString.length - 2, ' ')
        }
        return string.toString().trim()
    }

}













