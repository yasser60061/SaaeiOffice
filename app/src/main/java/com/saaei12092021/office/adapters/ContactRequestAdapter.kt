package com.saaei12092021.office.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.ContactRequestItemBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.contactRequesteResponse.Data
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import com.saaei12092021.office.util.GeneralFunctions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ContactRequestAdapter(private val listener: OnActionClickedListener) :
    RecyclerView.Adapter<ContactRequestAdapter.ArticleViewHolder>() {

    lateinit var mContext: Context
    var mediaType: String = ""

    inner class ArticleViewHolder(private val binding: ContactRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contactRequest: Data) {
            binding.apply {
                if (contactRequest.myId == contactRequest.reciever.id.toString()) {
                    Glide.with(mContext).load(contactRequest.sender.img)
                        .placeholder(R.drawable.profile_image).into(userProfileImage)
                    nameTv.text = contactRequest.sender.fullname
                    when (contactRequest.sender.type) {
                        "OFFICE" -> {
                         //   ifOfficeContactRequest.visibility = View.VISIBLE
                            ifUserContactRequest.visibility = View.GONE
                            ratingBar.visibility = View.VISIBLE
                            numberOfReviews.visibility = View.VISIBLE
                            ratingBar.rating = contactRequest.sender.rate.toFloat()
                            numberOfReviews.text = "عدد التقييمات (${contactRequest.sender.rateNumbers})"
                        }
                        "USER" -> {
                            ifOfficeContactRequest.visibility = View.GONE
                            ifUserContactRequest.visibility = View.VISIBLE
                            ratingBar.visibility = View.GONE
                            numberOfReviews.visibility = View.GONE
                        }
                        else -> {
                            ifOfficeContactRequest.visibility = View.GONE
                            ifUserContactRequest.visibility = View.GONE
                            ifUserContactRequest.visibility = View.GONE
                            ratingBar.rating = contactRequest.reciever.rate.toFloat()
                            numberOfReviews.text = "عدد التقييمات (${contactRequest.reciever.rateNumbers})"
                        }
                    }

                } else {
                    Glide.with(mContext).load(contactRequest.reciever.img)
                        .placeholder(R.drawable.profile_image).into(userProfileImage)
                    nameTv.text = contactRequest.reciever.fullname
                    when {
                        contactRequest.reciever.type == "OFFICE" -> {
                        //    ifOfficeContactRequest.visibility = View.VISIBLE
                            ifUserContactRequest.visibility = View.GONE
                            ratingBar.rating = contactRequest.reciever.rate.toFloat()
                            numberOfReviews.text = "عدد التقييمات (${contactRequest.reciever.rateNumbers})"
                        }
                        contactRequest.sender.type == "USER" -> {
                            ifOfficeContactRequest.visibility = View.GONE
                            ifUserContactRequest.visibility = View.VISIBLE
                            ratingBar.visibility = View.GONE
                            numberOfReviews.visibility = View.GONE
                        }
                        else -> {
                            ifOfficeContactRequest.visibility = View.GONE
                            ifUserContactRequest.visibility = View.GONE
                            ifUserContactRequest.visibility = View.GONE
                            ratingBar.rating = contactRequest.reciever.rate.toFloat()
                            numberOfReviews.text = "عدد التقييمات (${contactRequest.reciever.rateNumbers})"
                        }
                    }
                }
                try {
                    val date = contactRequest.createdAt.getDateWithServerTimeStamp()
                    var formatter: DateTimeFormatter? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter =
                            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(
                                Locale.ENGLISH
                            )
                        val dateTime: LocalDateTime =
                            LocalDateTime.parse(date.toString(), formatter)
                        val formatter2: DateTimeFormatter =
                            DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy | hh:mm a",
                                Locale("ar") // For english use Locale.ENGLISH
                                //  Locale.ENGLISH
                                //  Locale.getDefault()
                            )
                        binding.dateTv.text = dateTime.format(formatter2)
                    } else binding.dateTv.text =
                        Constant.dateAndTimeReformat(contactRequest.createdAt)
                            .trim().replace(" ", "  ")
                } catch (e: Exception) {
                }

                statusTv.text = GeneralFunctions.translateEnumToWord(contactRequest.status)
                mediaType = ""

                binding.goToDetailsRl.visibility = View.INVISIBLE

                messageContentTypeTv.text = mediaType
                messageContentTv.text = ""

                if (contactRequest.status == "NEW") {
                    if (contactRequest.myId != contactRequest.sender.id.toString()) {
                        binding.goToDetailsRl.visibility = View.GONE
                        messageContentTv.text = ""
                    }
                    mainRl.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                } else {
                    binding.goToDetailsRl.visibility = View.VISIBLE
                    if (contactRequest.status == "ACCEPTED") {
                        if (contactRequest.lastMessage?.content.isNullOrEmpty()) {
                            messageContentTv.text = "بامكانك البدء بالمحادثة الان"
                            messageContentTv.setTextColor(Color.parseColor("#F45656"))
                            mainRl.setBackgroundResource(R.drawable.rounded_shape_with_green_border)
                        }
                    }
                    if (!contactRequest.lastMessage?.content.isNullOrEmpty()) {
                        if (contactRequest.lastMessage?.dataType == "VOICE")
                            mediaType = "ملف صوتي"
                        if (contactRequest.lastMessage?.dataType == "VIDEO")
                            mediaType = "مقطع فيديو"
                        if (contactRequest.lastMessage?.dataType == "PDF")
                            mediaType = "ملف وثيقة"
                        if (contactRequest.lastMessage?.dataType == "IMAGE")
                            mediaType = "ملف صورة"
                        messageContentTypeTv.text = mediaType
                        messageContentTv.text = contactRequest.lastMessage?.content

                        if ((contactRequest.unReedMessage > 0) and (contactRequest.status == "ACCEPTED")) {
                            messageContentTv.setTextColor(Color.parseColor("#FF000000"))
                            mainRl.setBackgroundResource(R.drawable.rounded_shape_with_green_border)

                        } else {
                            messageContentTv.setTextColor(Color.parseColor("#707070"))
                            mainRl.setBackgroundResource(R.drawable.rounded_edit_text)
                        }
                    }
                }

                binding.root.setOnClickListener {
                    if (contactRequest.status != "NEW") {
                        val toStartChatMainInfo = ToStartChatMainInfo(
                            contactRequest = contactRequest.id,
                            toId = contactRequest.reciever.id,
                            fromId = contactRequest.sender.id
                        )
                        listener.contactRequestAction(
                            contactRequestItemInfo = contactRequest,
                            action = "startChat"
                        )
                        val intent = Intent(
                            mContext,
                            ChatActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
                        mContext.startActivity(intent)
                        Log.d("contactReq_this_item", contactRequest.toString())
                        messageContentTv.setTextColor(Color.parseColor("#707070"))
                        mainRl.setBackgroundResource(R.drawable.rounded_edit_text)
                    } else {
                        listener.contactRequestAction(
                            contactRequestItemInfo = contactRequest,
                            action = "GoToContactRequestDetails"
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ContactRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val contactRequest = differ.currentList[position]
        holder.bind(contactRequest)
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

    interface OnActionClickedListener {
        fun contactRequestAction(contactRequestItemInfo: Data, action: String)
    }

}













