package com.saaei12092021.office.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.ChatItemBinding
import com.saaei12092021.office.model.socketRequest.chatRequest.SendNewMessageRequest
import com.saaei12092021.office.ui.activities.MediaInChatActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

// mix panel ,, pay load
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    lateinit var mContext: Context

    inner class ChatViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(theMessage: SendNewMessageRequest) {
            binding.apply {

                binding.receivedMessageRl.visibility = View.GONE
                binding.myMessageRl.visibility = View.GONE
                binding.myImageIv.visibility = View.GONE
                binding.receivedImageIv.visibility = View.GONE
                binding.receivedMessageRl.visibility = View.GONE
                binding.myPdfTv.visibility = View.GONE
                binding.receivedPdfLinear.visibility = View.GONE
                binding.myPdfLinear.visibility = View.GONE
                binding.receivedVideoLinear.visibility = View.GONE
                binding.myVideoLinear.visibility = View.GONE
                binding.myAudioPlayer.visibility = View.GONE
                binding.receivedAudioPlayer.visibility = View.GONE
                binding.isNotSeenIv.visibility = View.GONE
                binding.isSeenIv.visibility = View.GONE
                binding.myMessageTv.setTextColor(Color.parseColor("#21305D"))
                binding.receivedMessageTv.setTextColor(Color.parseColor("#FFFFFF"))
                binding.usedAsMarkerTv.text = ""

                if (theMessage.user?.fullname != null) {
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myNameTv.visibility = View.VISIBLE
                        binding.friendNameTv.visibility = View.GONE
                        binding.myNameTv.text = theMessage.user.fullname
                    } else {
                        binding.myNameTv.visibility = View.GONE
                        binding.friendNameTv.visibility = View.VISIBLE
                        binding.friendNameTv.text = theMessage.user.fullname
                    }
                }

                if (theMessage.dataType == "TEXT")
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myMessageRl.visibility = View.VISIBLE
                        binding.myMessageTv.text = theMessage.content.trim()
                    } else {
                        binding.receivedMessageRl.visibility = View.VISIBLE
                        binding.receivedMessageTv.text = theMessage.content.trim()
                    }

                if (theMessage.dataType == "IMAGE")
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myImageIv.visibility = View.VISIBLE
                        Glide.with(mContext).load(theMessage.content)
                            .into(binding.myImageIv)
                    } else {
                        binding.receivedImageIv.visibility = View.VISIBLE
                        Glide.with(mContext).load(theMessage.content)
                            .into(binding.receivedImageIv)
                    }

                if (theMessage.dataType == "PDF")
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myPdfLinear.visibility = View.VISIBLE
                        binding.myPdfTv.visibility = View.VISIBLE
                        //      binding.myPdfTv.text = theMessage.content
                    } else {
                        binding.receivedPdfLinear.visibility = View.VISIBLE
                        binding.receivedPdfTv.visibility = View.VISIBLE
                        //  binding.receivedPdfTv.text = theMessage.content
                    }

                if (theMessage.dataType == "VIDEO")
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myVideoLinear.visibility = View.VISIBLE
                        binding.myVideoTv.visibility = View.VISIBLE
                        //   binding.myVideoTv.text = theMessage.content
                    } else {
                        binding.receivedVideoLinear.visibility = View.VISIBLE
                        binding.receivedVideoTv.visibility = View.VISIBLE
                        //   binding.receivedVideoTv.text = theMessage.content
                    }

                if (theMessage.dataType == "LINK")
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myMessageRl.visibility = View.VISIBLE
                        binding.myMessageTv.text = theMessage.content.trim()
                        binding.myMessageTv.setTextColor(Color.parseColor("#FF6200EE"))
                    } else {
                        binding.receivedMessageRl.visibility = View.VISIBLE
                        binding.receivedMessageTv.text = theMessage.content.trim()
                        binding.receivedMessageTv.setTextColor(Color.parseColor("#FF6200EE"))
                    }

                if (theMessage.dataType == "VOICE") {
                    if (theMessage.myId == theMessage.fromId) {
                        binding.myAudioPlayer.visibility = View.VISIBLE
                        binding.myAudioPlayer.setAudioTarget(theMessage.content)
                    } else {
                        binding.receivedAudioPlayer.visibility = View.VISIBLE
                        binding.receivedAudioPlayer.setAudioTarget(theMessage.content)
                    }
                }

                val URL_REGEX =
                    "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
                val p: Pattern = Pattern.compile(URL_REGEX)
                val m: Matcher = p.matcher(theMessage.content) //replace with string to compare

                if (m.find()) {
                    if ((theMessage.dataType != "PDF") and (theMessage.dataType != "IMAGE") and (theMessage.dataType != "VOICE")) {
                        binding.usedAsMarkerTv.text = "URL"
                        if (theMessage.myId == theMessage.fromId) {
                            binding.myMessageTv.setTextColor(Color.parseColor("#21305D"))
                        } else {
                            binding.receivedMessageTv.setTextColor(Color.parseColor("#21305D"))
                        }
                    }
                }

                try {
                    val dateString = theMessage.createdAt
                    val date = dateString!!.getDateWithServerTimeStamp()
                    var formatter: DateTimeFormatter?
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(
                            Locale.ENGLISH)
                        val dateTime: LocalDateTime =
                            LocalDateTime.parse(date.toString(), formatter)
                        val formatter2: DateTimeFormatter =
                            DateTimeFormatter.ofPattern(
                                "dd MMM hh:mm yyyy a",
                                  Locale("ar") // For english use Locale.ENGLISH
                                //Locale.ENGLISH
                              //  Locale.getDefault()
                            )
                        // "EEE dd MMM HH:mm:ss yyyy", //// other date time patern
                        Log.e("Date", "" + dateTime.format(formatter2))
                        binding.messageDateAndTimeTv.text = dateTime.format(formatter2)
                    } else binding.messageDateAndTimeTv.text =
                        Constant.dateAndTimeReformat(theMessage.createdAt!!)
                            .trim().replace(" ", "  ")
                } catch (e: Exception) {
                }

                if (theMessage.fromId == theMessage.myId) {
                    if (theMessage.seen == true)
                        binding.isSeenIv.visibility = View.VISIBLE
                    else binding.isNotSeenIv.visibility = View.VISIBLE
                }
                Log.d("theMessage", theMessage.toString())

                binding.root.setOnClickListener {
                    if (theMessage.dataType == "IMAGE") {
                        val intent = Intent(mContext, MediaInChatActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("theLink", theMessage.content)
                        intent.putExtra("dataType", "IMAGE")
                        mContext.startActivity(intent)
                    }
                    if ((theMessage.dataType == "PDF") or
                        (theMessage.dataType == "LINK") or
                        (theMessage.dataType == "VIDEO") or
                        (binding.usedAsMarkerTv.text == "URL")
                    ) {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(theMessage.content))
                        mContext.startActivity(browserIntent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val theMessage = differ.currentList[position]
        holder.bind(theMessage)
    }

    val differCallback = object : DiffUtil.ItemCallback<SendNewMessageRequest>() {
        override fun areItemsTheSame(
            oldItem: SendNewMessageRequest,
            newItem: SendNewMessageRequest
        ): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(
            oldItem: SendNewMessageRequest,
            newItem: SendNewMessageRequest
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

}













