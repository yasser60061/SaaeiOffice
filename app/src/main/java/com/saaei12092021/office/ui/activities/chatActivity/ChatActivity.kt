package com.saaei12092021.office.ui.activities.chatActivity

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.devlomi.record_view.OnRecordListener
import com.devlomi.record_view.RecordPermissionHandler
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rygelouv.audiosensei.player.AudioSenseiListObserver
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.ChatAdapter
import com.saaei12092021.office.adapters.PropertiesAdapter
import com.saaei12092021.office.databinding.ActivityChatBinding
import com.saaei12092021.office.databinding.BottomSheetForChooseFileTypeInChatBinding
import com.saaei12092021.office.databinding.BottomSheetForPreviewThePickedFileInChatBinding
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.adRequestedByIdResponse.AdRequestedByIdResponse
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.getAdsResponse.Property
import com.saaei12092021.office.model.responseModel.getContactRequestById.ContactRequestByIdResponse
import com.saaei12092021.office.model.responseModel.messagesInChatResponse.MessagesInChatResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.model.socketRequest.*
import com.saaei12092021.office.model.socketRequest.chatRequest.SendNewMessageRequest
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.OfficeOrUserPageActivity
import com.saaei12092021.office.ui.activities.billInfoAndPaymentActivities.BillInfoActivity
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.util.*
import com.saaei12092021.office.util.Constant.VIDEO_FILE_CODE
import com.theartofdev.edmodo.cropper.CropImage
import io.socket.client.Socket
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    var contactRequest: Int = 0
    var dataType: String = "TEXT"   //TEXT - IMAGE - PDF - VOICE - LINK - VIDEO
    var toId: Int = 0
    var fromId: Int = 0
    lateinit var myId: String
    lateinit var myLang: String
    lateinit var viewModel: MyViewModel
    lateinit var chatViewModel: ChatViewModel
    var chatList: ArrayList<SendNewMessageRequest> = ArrayList()
    lateinit var chatAdapter: ChatAdapter
    lateinit var mSocket: Socket
    lateinit var token: String
    lateinit var chatFromServerResponse: MessagesInChatResponse
    lateinit var contactRequestInfo: ContactRequestByIdResponse
    lateinit var adsByIdResponse: AdsByIdResponse
    private var theRequestFileToSend: File? = null
    private var resultFileUri: Uri? = Uri.EMPTY
    var actionTyp: String = ""
    var ratingValue: Float? = 0f
    private var timer: Timer? = null
    private var isLoadedAdsInfo: Boolean = false

    // to display request details
    var renType: String = ""
    lateinit var currentRequestedEstateId: String
    lateinit var currentRequestedEstateDetails: AdRequestedByIdResponse
    private var audioPath: String? = null

    // for voice message
    private var permissions: Permissions? = null
    private var mediaRecorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myLang = Constant.getMyLanguage(this)
        setUpLanguageViewAndDirection()
        myId = GeneralFunctions.getUserIdIfSupervisorOrNot(this)
        fromId = Integer.parseInt(myId)
        token = Constant.getToken(this)
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        mSocket = GeneralFunctions.createSocket(this)
        viewModel.mSocket = mSocket
        chatViewModel.mSocket = mSocket
        viewModel.mSocket.connect()
        chatViewModel.mSocket.connect()
        permissions = Permissions()


        initView()
        //--------------------------------------------------------------------------------
        viewModel.error.observe(this, Observer {
            Constant.makeToastMessage(this, it.toString())
            binding.progressBar.visibility =
                View.INVISIBLE
            binding.adsInfoCard.visibility = View.GONE
        })

        // for audio player in chat list -- this is according to the documentation in github
        AudioSenseiListObserver.getInstance().registerLifecycle(lifecycle)

        //-------------------------------------------------------------------------
        initialAndEditChatRecyclerView()

        // requested details is used in another fragment and this is for customise it

        binding.tabLinear.visibility = View.GONE
        binding.requestDetailsId.root.visibility = View.GONE
        binding.requestDetailsId.headerLinear.visibility = View.GONE
        binding.chatLinear.visibility = View.VISIBLE

        binding.messageContentEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        chatViewModel.sendActionStopTypingInSocket(
                            TypingAndStoppingTypingAndOnLineRequest(
                                toId = toId,
                                contactRequestId = contactRequest
                            )
                        )
                    }
                }, 900)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                binding.depositIconIv.visibility = View.VISIBLE
                binding.depositTextTv.visibility = View.GONE
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                chatViewModel.sendActionTypingInSocket(
                    TypingAndStoppingTypingAndOnLineRequest(
                        toId = toId,
                        contactRequestId = contactRequest
                    )
                )
            }
        })

        chatViewModel.listenToTypingInSocket()
        chatViewModel.listenToStopTypingInSocket()

        chatViewModel.typingLive.observe(this, Observer {
            if (it is Resource.Success) {
                if ((it.data!!.toId.toString() == myId) and (it.data.contactRequestId == contactRequest))
                    binding.typingTv.visibility = View.VISIBLE
            }
        })

        chatViewModel.stopTypingLive.observe(this, Observer {
            if (it is Resource.Success) {
                if ((it.data!!.toId.toString() == myId) and (it.data.contactRequestId == contactRequest))
                    binding.typingTv.visibility = View.GONE
            }
        })

        binding.requestDetailsTv.setOnClickListener {
            binding.requestDetailsTv.setBackgroundResource(R.drawable.rounded_button)
            binding.messagesTv.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.requestDetailsTv.setTextColor(Color.parseColor("#FFFFFF"))
            binding.messagesTv.setTextColor(Color.parseColor("#000000"))
            binding.requestDetailsId.root.visibility = View.VISIBLE
            binding.chatLinear.visibility = View.GONE
            binding.actionButtonsLinear.visibility = View.GONE
            binding.withdrawalTv.visibility = View.GONE
        }

        binding.messagesTv.setOnClickListener {
            binding.messagesTv.setBackgroundResource(R.drawable.rounded_button)
            binding.requestDetailsTv.setBackgroundResource(R.drawable.nothing_shap_bg)
            binding.messagesTv.setTextColor(Color.parseColor("#FFFFFF"))
            binding.requestDetailsTv.setTextColor(Color.parseColor("#000000"))
            binding.requestDetailsId.root.visibility = View.GONE
            binding.chatLinear.visibility = View.VISIBLE
            if (contactRequestInfo.contactRequest.status == "ACCEPTED") {
                if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                    binding.withdrawalTv.visibility = View.VISIBLE
                else binding.actionButtonsLinear.visibility = View.VISIBLE
            } else {
                binding.withdrawalTv.visibility = View.GONE
                binding.actionButtonsLinear.visibility = View.GONE
            }

        }

        binding.completeTv.setOnClickListener {
            actionTyp = "CompleteRequest"
            displayActionDialog()
        }

        binding.closeTv.setOnClickListener {
            actionTyp = "CloseRequest"
            displayActionDialog()
        }

        binding.withdrawalTv.setOnClickListener {
            actionTyp = "WithdrawalRequest"
            buildAlertMessageWithdrawalRequest()
        }

        binding.depositIconIv.setOnClickListener {
            binding.depositIconIv.visibility = View.GONE
            binding.depositTextTv.visibility = View.VISIBLE
        }

        binding.depositTextTv.setOnClickListener {
            val intent = Intent(this, BillInfoActivity::class.java)
            intent.putExtra("adsByIdResponse", adsByIdResponse)
            intent.putExtra("type", "3")
            startActivity(intent)
        }
//--------------------------------------------------------------------------------
        binding.backRl.setOnClickListener {
            chatViewModel.mSocket.disconnect()
            finish()
        }

        binding.addFileRl.setOnClickListener {
            bottomSheetForChooseFileTypeInChat()
        }

        //------------------------------------------------------------------------
        val toStartChatMainInfo =
            intent.getSerializableExtra("toStartChatMainInfo") as ToStartChatMainInfo
        contactRequest = toStartChatMainInfo.contactRequest
        if (toStartChatMainInfo.fromId == fromId)
            toId = toStartChatMainInfo.toId
        else toId = toStartChatMainInfo.fromId

        getAllMessageForThisContact()
        getThisContactRequestInfo()

//-------------------------------------------------------------------------
        viewModel.listenToNewMessageInSocket()
        viewModel.newMessageSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (contactRequest == it.data!!.contactRequest) {
                        val tempItem = it.data
                        tempItem.myId = Integer.parseInt(myId)
                        chatList.add(tempItem)
                        chatAdapter.differ.submitList(chatList)
                        binding.chatRv.scrollToPosition(chatList.size - 1)
                        Log.d("newMessage2-response", it.toString())
                        if (it.data.fromId.toString() != myId)
                            chatViewModel.sendSeenTheMessageInSocket(
                                SeenMessageInChatRequest(
                                    myId = Integer.parseInt(myId),
                                    friendId = toId,
                                    contactRequest = contactRequest
                                )
                            )
                    }
                    binding.progressBar.visibility = View.GONE
                    changeAdsInfoViewIfTheChatListSizeIsBig()
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

//---------------------------- send file when upload file success-------------------
        viewModel.uploadFileInChatLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    val newLink = it.data!!.link
                    Log.d("uploadFile", it.data.link)
                    if (dataType == "IMAGE")
                        viewModel.sendNewMessageInSocket(
                            SendNewMessageRequest(
                                seen = null,
                                id = null,
                                contactRequest = contactRequest,
                                dataType = "IMAGE",
                                content = newLink,
                                fromId = fromId,
                                toId = toId,
                                createdAt = null,
                                myId = null,
                                user = null
                            )
                        )
                    if (dataType == "PDF")
                        viewModel.sendNewMessageInSocket(
                            SendNewMessageRequest(
                                seen = null,
                                id = null,
                                contactRequest = contactRequest,
                                dataType = "PDF",
                                content = newLink,
                                fromId = fromId,
                                toId = toId,
                                createdAt = null,
                                myId = null,
                                user = null
                            )
                        )
                    if (dataType == "VOICE")
                        viewModel.sendNewMessageInSocket(
                            SendNewMessageRequest(
                                seen = null,
                                id = null,
                                contactRequest = contactRequest,
                                dataType = "VOICE",
                                content = newLink,
                                fromId = fromId,
                                toId = toId,
                                createdAt = null,
                                myId = null,
                                user = null
                            )
                        )
                    if (dataType == "VIDEO")
                        viewModel.sendNewMessageInSocket(
                            SendNewMessageRequest(
                                seen = null,
                                id = null,
                                contactRequest = contactRequest,
                                dataType = "VIDEO",
                                content = newLink,
                                fromId = fromId,
                                toId = toId,
                                createdAt = null,
                                myId = null,
                                user = null
                            )
                        )
                    //  resetMessageInfo()
                }
                is Resource.Error -> {}
            }
        })
//-------------------------------------------------------------------------------------------
        binding.sendMessageRl.setOnClickListener {
            if (TextUtils.isEmpty(binding.messageContentEt.text.toString()))
                Constant.makeToastMessage(this, "اكتب محتوى الرسالة أولا")
            else {
                viewModel.sendNewMessageInSocket(
                    SendNewMessageRequest(
                        seen = null,
                        id = null,
                        contactRequest = contactRequest,
                        dataType = "TEXT",
                        content = binding.messageContentEt.text.toString(),
                        fromId = fromId,
                        toId = toId,
                        createdAt = null,
                        myId = null,
                        user = null
                    )
                )
                binding.messageContentEt.setText("")
                //  resetMessageInfo()

            }
        }

//-------------------------------------------------------------------------
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //    }
    }

    private fun changeAdsInfoViewIfTheChatListSizeIsBig() {
        if (isLoadedAdsInfo)
            if ((chatList.size > 10) and (contactRequestInfo.contactRequest.contactOn == "ADS")) {
                binding.adsInfoCardMini.visibility = View.VISIBLE
                binding.adsInfoCard.visibility = View.GONE
            }
    }

    private fun initialAndEditChatRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.chatRv.apply {
            adapter = chatAdapter
        }
    }

//    private fun resetMessageInfo() {
//        binding.messageContentEt.setText("")
//        binding.progressBar.visibility = View.GONE
//    //    theRequestFileToSend = null
//      //  dataType = "TEXT"
//    }

    private fun getThisContactRequestInfo() {
        chatViewModel.getContactRequestById(token, myLang, contactRequest.toString())
        chatViewModel.contactRequestByIdResponseResponseLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    contactRequestInfo = it.data!!

                    if (contactRequestInfo.contactRequest.status == "ACCEPTED") {
                        binding.messageContentContainerRl.visibility = View.VISIBLE
                        binding.statusDescriptionTv.visibility = View.GONE
                        if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                            binding.withdrawalTv.visibility = View.VISIBLE
                        else binding.actionButtonsLinear.visibility = View.VISIBLE
                    } else {
                        binding.statusDescriptionTv.visibility = View.VISIBLE
                        binding.messageContentContainerRl.visibility = View.INVISIBLE
                        if (contactRequestInfo.contactRequest.status == "COMPLETED") {
                            if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                                binding.statusDescriptionTv.text =
                                    "تم إتمام المعاملة من جهة الإتصال"
                            else binding.statusDescriptionTv.text =
                                "تم إتمام المعاملة من قبلك"
                        }

                        if (contactRequestInfo.contactRequest.status == "CLOSED") {
                            if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                                binding.statusDescriptionTv.text =
                                    "تم الإغلاق بدون اتفاق من جهة الإتصال"
                            else binding.statusDescriptionTv.text =
                                "تم الإغلاق بدون اتفاق من طرفك"
                        }
                        if (contactRequestInfo.contactRequest.status == "WITHDRAWAL") {
                            if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                                binding.statusDescriptionTv.text = "تم الإنسحاب من طرفك"
                            else binding.statusDescriptionTv.text =
                                "تم الإنسحاب من من جهة الإتصال"
                        }
                        if (contactRequestInfo.contactRequest.status == "REFUSED") {
                            if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                                binding.statusDescriptionTv.text =
                                    "تم رفض التواصل من قبل جهة الإتصال"
                            else binding.statusDescriptionTv.text = "تم رفض التواصل من قبلك"
                        }
                        binding.chatRv.visibility = View.GONE
                        binding.chatRv.visibility = View.VISIBLE

                    }

                    if (contactRequestInfo.contactRequest.contactOn == "ADS") {
                        viewModel.getAdsById(contactRequestInfo.contactRequest.ads.toString())
                        getAdsByIdIfExist()
                    } else if (contactRequestInfo.contactRequest.contactOn == "ADS-REQUEST") {
                        binding.tabLinear.visibility = View.VISIBLE
                        binding.requestDetailsId.progressBar.visibility = View.GONE
                        currentRequestedEstateId = contactRequestInfo.contactRequest.ads.toString()
                        viewModel.getRequestedAdsById(
                            currentRequestedEstateId,
                            myLang
                        )
                    }
                    if (contactRequestInfo.contactRequest.sender.id.toString() == myId) {
                        binding.contactNameTv.text =
                            contactRequestInfo.contactRequest.reciever.fullname
                        Glide.with(this@ChatActivity)
                            .load(contactRequestInfo.contactRequest.reciever.img)
                            .into(binding.contactIv)
                        binding.contactNameRl.setOnClickListener{
                            val intent = Intent(this, OfficeOrUserPageActivity::class.java)
                            intent.putExtra("owner", contactRequestInfo.contactRequest.reciever.id)
                            startActivity(intent)
                        }
                        binding.contactRl.setOnClickListener{
                            val intent = Intent(this, OfficeOrUserPageActivity::class.java)
                            intent.putExtra("owner", contactRequestInfo.contactRequest.reciever.id)
                            startActivity(intent)
                        }
                    } else {
                        binding.contactNameTv.text =
                            contactRequestInfo.contactRequest.sender.fullname
                        Glide.with(this@ChatActivity)
                            .load(contactRequestInfo.contactRequest.sender.img)
                            .into(binding.contactIv)
                        binding.contactNameRl.setOnClickListener{
                            val intent = Intent(this, OfficeOrUserPageActivity::class.java)
                            intent.putExtra("owner", contactRequestInfo.contactRequest.sender.id)
                            startActivity(intent)
                        }
                        binding.contactRl.setOnClickListener{
                            val intent = Intent(this, OfficeOrUserPageActivity::class.java)
                            intent.putExtra("owner", contactRequestInfo.contactRequest.sender.id)
                            startActivity(intent)
                        }
                    }

                    if (contactRequestInfo.contactRequest.sender.id.toString() == myId) {
                        listenToCloseRequestInSocket()
                        listenToCompleteRequestInSocket()
                    } else {
                        listenToWithDrawlInSocket()
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility =
                        View.GONE
                }
            }
        })

        // for requested details display
        viewModel.requestedEstateByIdResponseLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility =
                        View.VISIBLE
                    binding.requestDetailsId.mainScroll.visibility =
                        View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility =
                        View.GONE
                    currentRequestedEstateDetails = it.data!!
                    Log.d("RequestedEstateDetails", currentRequestedEstateDetails.toString())
                    if (contactRequestInfo.contactRequest.status == "ACCEPTED")
                        if (contactRequestInfo.contactRequest.sender.id.toString() == myId)
                            binding.withdrawalTv.visibility = View.VISIBLE
                        else binding.actionButtonsLinear.visibility = View.VISIBLE
                    if (currentRequestedEstateDetails.success)
                        displayThisRequestData()
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })

    }

    private fun listenToCompleteRequestInSocket() {
        viewModel.listenToCompleteRequestInSocket()
        viewModel.completeRequestInSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.data.id == contactRequest) {
                        binding.statusDescriptionTv.visibility = View.VISIBLE
                        binding.messageContentContainerRl.visibility = View.INVISIBLE
                        binding.statusDescriptionTv.text =
                            "تم إتمام المعاملة من جهة الإتصال"
                        binding.withdrawalTv.visibility = View.GONE
                        binding.actionButtonsLinear.visibility = View.GONE
                        contactRequestInfo.contactRequest.status = "COMPLETED"
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })
    }

    private fun listenToCloseRequestInSocket() {
        viewModel.listenToCloseRequestInSocket()
        viewModel.closeRequestInSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.data.id == contactRequest) {
                        binding.statusDescriptionTv.visibility = View.VISIBLE
                        binding.messageContentContainerRl.visibility = View.INVISIBLE
                        binding.statusDescriptionTv.text =
                            "تم الإغلاق بدون اتفاق من جهة الإتصال"
                        binding.withdrawalTv.visibility = View.GONE
                        binding.actionButtonsLinear.visibility = View.GONE
                        contactRequestInfo.contactRequest.status = "CLOSED"
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })
    }

    private fun listenToWithDrawlInSocket() {
        viewModel.listenToWithdrawalRequestInSocket()
        viewModel.withdrawalRequestInSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.data.id == contactRequest) {
                        binding.statusDescriptionTv.visibility = View.VISIBLE
                        binding.messageContentContainerRl.visibility = View.INVISIBLE
                        binding.statusDescriptionTv.text =
                            "تم الإنسحاب من من جهة الإتصال"
                        binding.withdrawalTv.visibility = View.GONE
                        binding.actionButtonsLinear.visibility = View.GONE
                        contactRequestInfo.contactRequest.status = "WITHDRAWAL"
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })
    }

    private fun getAdsByIdIfExist() {
        viewModel.adsByIdLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adsByIdResponse = it.data!!
                    binding.adsInfoCard.visibility = View.VISIBLE
                    binding.chatRv.visibility = View.GONE
                    binding.chatRv.visibility = View.VISIBLE
                    binding.apply {
                        titleTv.text = adsByIdResponse.advertisement.title
                        titleMiniTv.text = adsByIdResponse.advertisement.title
                        cityNameTv.text = adsByIdResponse.advertisement.city.cityName
                        areaNameTv.text = adsByIdResponse.advertisement.area.areaName
                        priceTv.text = GeneralFunctions.reformatPrice(adsByIdResponse.advertisement.price)
                        descriptionTv.text = adsByIdResponse.advertisement.description
                        descriptionMiniTv.text = adsByIdResponse.advertisement.description
                        try {
                            Glide.with(this@ChatActivity)
                                .load(adsByIdResponse.advertisement.imgs[0])
                                .into(binding.estateIv)
                            Glide.with(this@ChatActivity)
                                .load(adsByIdResponse.advertisement.imgs[0])
                                .into(binding.adsImageMiniIv)
                        } catch (e: Exception) {
                        }
                        val propertyList: ArrayList<Property> = ArrayList()
                        adsByIdResponse.advertisement.properties.forEach { propertyItem ->
                            propertyList.add(
                                Property(
                                    propertyItem.id!!,
                                    propertyItem.img!!,
                                    propertyItem.name!!,
                                    propertyItem.type!!,
                                    propertyItem.value!!
                                )
                            )
                        }
                        val propertyAdapter = PropertiesAdapter()
                        propertyRv.apply {
                            adapter = propertyAdapter
                            propertyAdapter.differ.submitList(propertyList)
                        }
                        binding.addEstateLinear.setOnClickListener {
                            val intent =
                                Intent(this@ChatActivity, EstateDetailsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("adsId", adsByIdResponse.advertisement.id.toString())
                            startActivity(intent)
                        }
                        binding.adsInfoRelativeMini.setOnClickListener {
                            val intent =
                                Intent(this@ChatActivity, EstateDetailsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("adsId", adsByIdResponse.advertisement.id.toString())
                            startActivity(intent)
                        }
                        isLoadedAdsInfo = true
                    }
                    if (chatList.isNotEmpty())
                        binding.chatRv.scrollToPosition(chatList.size - 1)
                    if (adsByIdResponse.advertisement.hasDeposit)
                        if (adsByIdResponse.advertisement.deposit != 0.0)
                            if (adsByIdResponse.advertisement.owner.id.toString() != myId) {
                                binding.depositRl.visibility = View.VISIBLE
                            }
                    changeAdsInfoViewIfTheChatListSizeIsBig()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.adsInfoCard.visibility = View.GONE
                }
            }
        })
    }

    private fun getAllMessageForThisContact() {
        viewModel.getAllMessage(token, myId, toId.toString(), contactRequest.toString())
        viewModel.allChatMessagesLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    chatFromServerResponse = it.data!!
                    Log.d("chatFromServerResponse", chatFromServerResponse.toString())
                    chatFromServerResponse.data.forEach { chatItem ->
                        if (chatItem.contactRequest == contactRequest) // i don't need this condition when i pass contactRequestId as query in api
                            chatList.add(
                                0,
                                SendNewMessageRequest(
                                    seen = chatItem.seen,
                                    id = chatItem.id,
                                    contactRequest = chatItem.contactRequest,
                                    content = chatItem.content,
                                    dataType = chatItem.dataType,
                                    fromId = chatItem.user.id,
                                    toId = null,
                                    createdAt = chatItem.createdAt,
                                    myId = Integer.parseInt(myId),
                                    user = chatItem.user
                                )
                            )
                    }
                    chatAdapter.differ.submitList(chatList)
                    binding.chatRv.scrollToPosition(chatList.size - 1)
                    binding.progressBar.visibility = View.GONE
                    changeAdsInfoViewIfTheChatListSizeIsBig()
                    Log.d("newMessage1-response", it.data.data.toString())
                    if (!chatList.isNullOrEmpty())
                        if (chatList[chatList.size - 1].fromId.toString() != myId)
                            chatViewModel.sendSeenTheMessageInSocket(
                                SeenMessageInChatRequest(
                                    myId = Integer.parseInt(myId),
                                    friendId = toId,
                                    contactRequest = contactRequest
                                )
                            )
                    chatViewModel.listenToSeenTheMessageInSocket()
                    chatViewModel.seenMessageLive.observe(this, Observer {
                        when (it) {
                            is Resource.Success -> {
                                if (it.data!!.contactRequest == contactRequest) {
                                    if (!chatList.isNullOrEmpty())
                                        chatList.forEach { chatItem ->
                                            if (it.data.success)
                                                chatItem.seen = true
                                        }
                                    chatAdapter.differ.submitList(chatList)
                                    chatAdapter.notifyDataSetChanged()
                                }
                            }
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                        }
                    })
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((resultCode == Activity.RESULT_OK) && ( data != null)) {
            when (requestCode) {
                Constant.PDF_FILE_CODE -> {
                    tryOpenDocument(requestCode, resultCode, data)
                    bottomSheetForPreviewThePickedFileAndSend()
                }
                VIDEO_FILE_CODE -> {
                    theRequestFileToSend = null
                    theRequestFileToSend = File(resultFileUri!!.path)
                    tryOpenDocument(requestCode, resultCode, data)
                    bottomSheetForPreviewThePickedFileAndSend()
                }
                Constant.IMAGE_FILE_CODE -> {
                    resultFileUri = data.data!!
                    launchImageCrop(resultFileUri!!)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultFileUri = result.uri

                    if (dataType == "IMAGE") {
                        theRequestFileToSend = File(resultFileUri!!.path)
                        bottomSheetForPreviewThePickedFileAndSend()
                    }
                    resultFileUri = Uri.EMPTY
                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .start(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        chatViewModel.mSocket.disconnect()
        finish()
    }

    private fun bottomSheetForChooseFileTypeInChat() {
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_choose_file_type_in_chat)
        val binding: BottomSheetForChooseFileTypeInChatBinding =
            BottomSheetForChooseFileTypeInChatBinding.inflate(
                LayoutInflater.from(this),
                null,
                false
            )
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()

        binding.chooseImageLinear.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                chooseImage()
            else {
                GeneralFunctions.pickFromGallery(this)
                dataType = "IMAGE"
            }

            bottomSheetDialog.dismiss()
        }

        binding.choosePdfLinear.setOnClickListener {
            choosePdf()
            bottomSheetDialog.dismiss()
        }

        binding.chooseVideoFileLinear.setOnClickListener {
            chooseVideoFileType()
            dataType = "VIDEO"
            bottomSheetDialog.dismiss()
        }
    }

    private fun bottomSheetForPreviewThePickedFileAndSend() {

        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_preview_the_picked_file_in_chat)
        val binding: BottomSheetForPreviewThePickedFileInChatBinding =
            BottomSheetForPreviewThePickedFileInChatBinding.inflate(
                LayoutInflater.from(this),
                null,
                false
            )

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()

        if (dataType == "IMAGE") {
            binding.pickedFileIv.visibility = View.VISIBLE
            binding.pickedFileIv.setImageURI(Uri.fromFile(theRequestFileToSend))
        }

        if (dataType == "PDF") {
            binding.pickedFileIv.visibility = View.VISIBLE
            binding.pickedFileIv.setImageResource(R.drawable.pdf_image)
        }

        if (dataType == "VIDEO") {
            viewModel.uploadFileInMessage(
                token = token,
                fileInChat = theRequestFileToSend,
            )
            bottomSheetDialog.dismiss()
        }

        binding.fileNameTv.text = theRequestFileToSend!!.name

        binding.sendFileLinear.setOnClickListener {
            viewModel.uploadFileInMessage(
                token = token,
                fileInChat = theRequestFileToSend,
            )
            bottomSheetDialog.dismiss()
        }
    }

    private fun chooseImage() {
        CropImage.activity(resultFileUri)
            .start(this@ChatActivity)
        dataType = "IMAGE"
    }

    private fun choosePdf() {
        openDocumentAndPickPdf(Constant.PDF_FILE_CODE)
        dataType = "PDF"
    }

    private fun chooseVideoFileType() {
        chooseVideoFromGallery()
        dataType = "VIDEO"
    }

    private fun tryOpenDocument(requestCode: Int, resultCode: Int, data: Intent?) {
        when (val openFileResult =
            this@ChatActivity.tryHandleOpenDocumentResult(requestCode, resultCode, data)) {
            is OpenFileResult.FileWasOpened -> {
                theRequestFileToSend = getFileFromInputStream(openFileResult.content)
            }
            OpenFileResult.DifferentResult -> {}
            OpenFileResult.ErrorOpeningFile -> {}
            OpenFileResult.OpenFileWasCancelled -> {}
        }
    }

    private fun getFileFromInputStream(inputStream: InputStream): File {
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        var tempFile: File? = null
        if (dataType == "PDF")
            tempFile = File.createTempFile("pdf", ".pdf", this@ChatActivity.cacheDir)
        if (dataType == "VIDEO")
            tempFile = File.createTempFile("pdf", ".mp4", this@ChatActivity.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        outputStream.write(buffer)
        inputStream.close()
        outputStream.close()
        return tempFile!!
    }

    private fun displayThisRequestData() {

        binding.requestDetailsId.mainScroll.visibility = View.VISIBLE
        binding.requestDetailsId.actionRl.visibility = View.GONE

        Glide.with(this).load(currentRequestedEstateDetails.advertisement.owner.img)
            .into(binding.requestDetailsId.ownerIv)
        binding.requestDetailsId.ownerNameTv.text =
            currentRequestedEstateDetails.advertisement.owner.fullname

        if (currentRequestedEstateDetails.advertisement.owner.id.toString() != myId) {
            if (currentRequestedEstateDetails.advertisement.enablePhoneContact) {
                binding.requestDetailsId.callNowLinear.visibility = View.VISIBLE
                binding.requestDetailsId.callNowLinear.setOnClickListener {
                    createPhoneCall()
                }
                binding.requestDetailsId.allowCommunicationViaTv.setOnClickListener {
                    createPhoneCall()
                }
            }
        }

        if (currentRequestedEstateDetails.advertisement.contractType == "RENT") {
            if (currentRequestedEstateDetails.advertisement.rentType == "DAILT")
                renType = " -" + getString(R.string.daily)
            if (currentRequestedEstateDetails.advertisement.rentType == "MONTHLY")
                renType = " -" + getString(R.string.monthly)
            if (currentRequestedEstateDetails.advertisement.rentType == "YEARLY")
                renType = " -" + getString(R.string.yearly)
            if (currentRequestedEstateDetails.advertisement.rentType == "OPINING")
                renType = " -" + getString(R.string.opining)
        }
        binding.requestDetailsId.requestNoTv.text =
            currentRequestedEstateDetails.advertisement.id.toString()

        binding.requestDetailsId.statusTv.text =
            GeneralFunctions.translateEnumToWord(currentRequestedEstateDetails.advertisement.status)
        when (currentRequestedEstateDetails.advertisement.status) {
            "NEW" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_blue)
            "ON-PROGRESS" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_orange)
            "COMPLETED" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_green)
            "CANCELED" -> binding.requestDetailsId.statusTv.setBackgroundResource(R.drawable.rounded_button_red)
        }
        binding.requestDetailsId.cityNameTv.text =
            currentRequestedEstateDetails.advertisement.city.cityName
        if (!currentRequestedEstateDetails.advertisement.area.isNullOrEmpty()) {
            var areasName = ""
            currentRequestedEstateDetails.advertisement.area!!.forEach {
                areasName = if (areasName.isNotEmpty())
                    areasName + " - " + it.areaName
                else areasName + " [ " + it.areaName
            }
            areasName += " ]"
            binding.requestDetailsId.neighborhoodNameTv.text = areasName
        }
        binding.requestDetailsId.mainCategoryTv.text =
            currentRequestedEstateDetails.advertisement.category.categoryName
        binding.requestDetailsId.subCategoryTv.text =
            currentRequestedEstateDetails.advertisement.subCategory.categoryName
        if (currentRequestedEstateDetails.advertisement.contractType == "SALE")
            binding.requestDetailsId.typeOfContractTv.text = getString(R.string.sale)
        if (currentRequestedEstateDetails.advertisement.contractType == "RENT")
            binding.requestDetailsId.typeOfContractTv.text = getString(R.string.rent) + renType
        binding.requestDetailsId.realEstateAriaTv.text =
            " من " + toIntString(currentRequestedEstateDetails.advertisement.sizeFrom.toString()) + " الى " + toIntString(
                currentRequestedEstateDetails.advertisement.sizeTo.toString()
            )

        " من " + currentRequestedEstateDetails.advertisement.sizeFrom.toString() + " الى " + currentRequestedEstateDetails.advertisement.sizeTo.toString()

        var tempFeatureText = "["
        currentRequestedEstateDetails.advertisement.features.forEachIndexed { index, element ->
            if (index != currentRequestedEstateDetails.advertisement.features.size - 1) {
                if (tempFeatureText.length > 2)
                    tempFeatureText += "," + element.name
                else tempFeatureText += "" + element.name
            } else {
                tempFeatureText += "]"
            }
        }

        if (tempFeatureText.trim().length == 1)
            binding.requestDetailsId.featuresTv.text = ""
        else binding.requestDetailsId.featuresTv.text = (tempFeatureText)

        var concatenationProperties = ""
        currentRequestedEstateDetails.advertisement.properties.forEachIndexed { featureIndex, featureItem ->
            if (featureItem.type == "NUMBER") {
                concatenationProperties =
                    concatenationProperties + featureItem.name + " : " +
                            toIntString(featureItem.from.toString()) + " " + getString(R.string.to) + " " + toIntString(
                        featureItem.to.toString()
                    )
            } else if (featureItem.type == "LIST") {
                concatenationProperties =
                    concatenationProperties + featureItem.name + " : "
                featureItem.optionsName.forEachIndexed { optionIndex, optionItem ->
                    concatenationProperties =
                        concatenationProperties + " " + optionItem
                }
            }
            if (currentRequestedEstateDetails.advertisement.properties.size - 1 != featureIndex)
                if (concatenationProperties.isNotEmpty())
                    concatenationProperties += "\n"
        }

        binding.requestDetailsId.subPropertySpecificationsTv.text = concatenationProperties
        binding.requestDetailsId.morInfoTv.text =
            currentRequestedEstateDetails.advertisement.description_ar

        if (currentRequestedEstateDetails.advertisement.enablePhoneContact)
            binding.requestDetailsId.allowCommunicationViaIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.requestDetailsId.allowCommunicationViaIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

        if (currentRequestedEstateDetails.advertisement.enableInstallment)
            binding.requestDetailsId.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.green_circle_bg)
        else binding.requestDetailsId.doYouNeedInstallmentIv.setBackgroundResource(R.drawable.elementary1_circle_bg)

    }

    private fun createPhoneCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(
            "tel:" + currentRequestedEstateDetails.advertisement.owner.phone
                .toString()
        )
        if (intent.resolveActivity(this.packageManager) != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun buildAlertMessageWithdrawalRequest() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("هل أنت متأكد من الإنسحاب")
            .setCancelable(true)
            .setPositiveButton("نعم",
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.sendActionWithdrawalRequestInSocket(
                        WithdrawalRequest(
                            userId = Integer.parseInt(myId),
                            contactRequest = contactRequest,
                            lang = myLang
                        )
                    )
                    dialog.cancel()
                    viewModel.listenToWithdrawalRequestInSocket()
                    viewModel.withdrawalRequestInSocketLive.observe(this, Observer {
                        when (it) {
                            is Resource.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Resource.Success -> {
                                if (it.data!!.data.id == contactRequest) {
                                    Toast(this).showCustomToast("شكرا لتعاملكم مع ساعي", this)
                                    chatViewModel.mSocket.disconnect()
                                    finish()
                                }
                            }
                            is Resource.Error -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    })
                })
            .setNegativeButton("لا",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }

    private fun displayActionDialog() {

        val dialog = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val actionDialog: View = inflater.inflate(R.layout.complete_action_in_chat_popup_item, null)
        val titleTv = actionDialog.findViewById<TextView>(R.id.chat_action_title_tv)
        val commentEt = actionDialog.findViewById<EditText>(R.id.add_a_comment_et_item)
        val reasonEt = actionDialog.findViewById<EditText>(R.id.add_a_reason_et_item)
        val ratingBar = actionDialog.findViewById<RatingBar>(R.id.add_rating_rb_item)
        val applySubmitBtn = actionDialog.findViewById<TextView>(R.id.submit_action_btn_item)
        val progressbar = actionDialog.findViewById<ProgressBar>(R.id.action_pb_item)
        dialog.setView(actionDialog)

        if (actionTyp == "CompleteRequest")
            titleTv.text = "أكمال المعاملة بقبول الطرفين"

        if (actionTyp == "CloseRequest") {
            titleTv.text = "إقفال المعاملة بدون اتفاق"
            reasonEt.visibility = View.VISIBLE
        }

        dialog.show()

        if (contactRequestInfo.contactRequest.sender.type != "OFFICE")
            ratingBar.visibility = View.GONE

        applySubmitBtn.setOnClickListener {

            if (actionTyp == "CompleteRequest") {
                ratingValue = ratingBar.rating
                if (ratingBar.rating <= 1.0) ratingValue = null
                if (ratingBar.rating > 1.0) ratingValue = ratingBar.rating
                // Toast(this).showCustomToast("اترك تقييم من فضلك", this)
                if (TextUtils.isEmpty(commentEt.text.toString())) Toast(this).showCustomToast(
                    "الرجاء كتابة تعليق حول المعاملة",
                    this
                )
                else {
                    viewModel.sendActionCompleteRequestInSocket(
                        CompleteRequestRequest(
                            userId = Integer.parseInt(myId),
                            contactRequest = contactRequest,
                            lang = myLang,
                            comment = commentEt.text.toString(),
                            rate = ratingValue,
                            office = toId
                        )
                    )
                    viewModel.listenToCompleteRequestInSocket()
                    viewModel.completeRequestInSocketLive.observe(this, Observer {
                        when (it) {
                            is Resource.Loading -> {
                                progressbar.visibility = View.VISIBLE
                                applySubmitBtn.isEnabled = false
                            }
                            is Resource.Success -> {
                                if (it.data!!.data.id == contactRequest) {
                                    Toast(this).showCustomToast("شكرا لتعاملكم مع ساعي", this)
                                    chatViewModel.mSocket.disconnect()
                                    finish()
                                }
                            }
                            is Resource.Error -> {
                                progressbar.visibility = View.GONE
                                applySubmitBtn.isEnabled = true
                            }
                        }
                    })
                }
            }

            if (actionTyp == "CloseRequest") {
                ratingValue = ratingBar.rating
                if (ratingBar.rating <= 1.0) ratingValue = null
                if (ratingBar.rating > 1.0) ratingValue = ratingBar.rating
                // Toast(this).showCustomToast("اترك تقييم من فضلك", this)
                when {
                    TextUtils.isEmpty(commentEt.text.toString()) -> Toast(this).showCustomToast(
                        "الرجاء كتابة تعليق حول المعاملة",
                        this
                    )
                    TextUtils.isEmpty(reasonEt.text.toString()) -> Toast(this).showCustomToast(
                        "الرجاء كتابة سبب الإغلاق",
                        this
                    )
                    else -> {
                        viewModel.sendActionCloseRequestInSocket(
                            CloseRequestRequest(
                                userId = Integer.parseInt(myId),
                                contactRequest = contactRequest,
                                lang = myLang,
                                comment = commentEt.text.toString(),
                                reason = reasonEt.text.toString(),
                                rate = ratingValue,
                                office = toId
                            )
                        )
                        viewModel.listenToCloseRequestInSocket()
                        viewModel.closeRequestInSocketLive.observe(this, Observer {
                            when (it) {
                                is Resource.Loading -> {
                                    progressbar.visibility = View.VISIBLE
                                    applySubmitBtn.isEnabled = false
                                }
                                is Resource.Success -> {
                                    if (it.data!!.data.id == contactRequest) {
                                        Toast(this).showCustomToast("شكرا لتعاملكم مع ساعي", this)
                                        chatViewModel.mSocket.disconnect()
                                        finish()
                                    }
                                }
                                is Resource.Error -> {
                                    progressbar.visibility = View.GONE
                                    applySubmitBtn.isEnabled = true
                                }
                            }
                        })
                    }

                }
                Log.d("ratingBar", ratingBar.rating.toString())
            }
        }

}

private fun initView() {
    binding.recordButton.setRecordView(binding.recordView)
    permissions!!.requestStoragePermissions(this@ChatActivity)
    //  binding.recordButton.isListenForRecord = false

    binding.recordButton.setOnClickListener { view ->
        if (permissions!!.isRecordingOk(this@ChatActivity)) if (permissions!!.isStoragePermissionAllow(
                this@ChatActivity
            )
        ) binding.recordButton.isListenForRecord =
            true else permissions!!.requestStoragePermissions(this@ChatActivity) else permissions!!.requestRecording(
            this@ChatActivity
        )
    }

    binding.recordView.setRecordPermissionHandler(RecordPermissionHandler {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return@RecordPermissionHandler true
        }
        val recordPermissionAvailable = ContextCompat.checkSelfPermission(
            this@ChatActivity,
            Manifest.permission.RECORD_AUDIO
        ) == PERMISSION_GRANTED
        if (recordPermissionAvailable) {
            return@RecordPermissionHandler true
        }
        ActivityCompat.requestPermissions(
            this@ChatActivity, arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        false
    })

    binding.recordView.setOnRecordListener(object : OnRecordListener {
        override fun onStart() {
            //Start Recording..
            Log.d("RecordView", "onStart")
            setUpRecording()
            try {
                mediaRecorder!!.prepare()
                mediaRecorder!!.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding.recordView.visibility = View.VISIBLE
        }

        override fun onCancel() {
            //On Swipe To Cancel
            Log.d("RecordView", "onCancel")
            mediaRecorder!!.reset()
            mediaRecorder!!.release()
            val file: File = File(audioPath)
            if (file.exists()) file.delete()
            binding.recordView.visibility = View.GONE
        }

        override fun onFinish(recordTime: Long, limitReached: Boolean) {
            //Stop Recording..
            Log.d("RecordView", "onFinish")
            try {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            binding.recordView.visibility = View.GONE
            //   binding.messageLayout.setVisibility(View.VISIBLE)
            //    sendRecodingMessage(audioPath)

            sendRecodingMessage()
        }

        override fun onLessThanSecond() {
            //When the record time is less than One Second
            Log.d("RecordView", "onLessThanSecond")
            mediaRecorder!!.reset()
            mediaRecorder!!.release()
            val file: File = File(audioPath!!)
            if (file.exists()) file.delete()
            binding.recordView.visibility = View.GONE
        }
    })
}

private fun setUpRecording() {
    mediaRecorder = MediaRecorder()
    mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
    mediaRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
    val file =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Saaei/Media/Recording") // this is work with all android version  because  we can in all public directories that are already there.
     //   File(Environment.getExternalStorageDirectory().absolutePath, "Saaei/Media/Recording") // not work with >> On An Android 11 device you cannot create directories or files in root of external storage.
    if (!file.exists())
        file.mkdirs()
    audioPath = file.absolutePath + File.separator + System.currentTimeMillis() + ".3gp"
    mediaRecorder!!.setOutputFile(audioPath)
    Log.d("mediaRecorder", audioPath.toString())
}

private fun sendRecodingMessage() {
    theRequestFileToSend = File(audioPath!!)
    val audioFile = Uri.fromFile(File(audioPath))
    viewModel.uploadFileInMessage(
        token = token,
        fileInChat = theRequestFileToSend,
    )
    dataType = "VOICE"
}

private fun chooseVideoFromGallery() {
    val intent = Intent()
    intent.type = "video/*"
    intent.action = Intent.ACTION_PICK
    startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_FILE_CODE)
}

private fun toIntString(theString: String): String {
    val string = StringBuilder(theString).also {
        it.setCharAt(theString.length - 1, ' ')
        it.setCharAt(theString.length - 2, ' ')
    }
    return string.toString().trim()
}
}