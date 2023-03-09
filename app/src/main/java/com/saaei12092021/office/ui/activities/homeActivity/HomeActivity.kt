package com.saaei12092021.office.ui.activities.homeActivity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.AllMainCategoryAdapter
import com.saaei12092021.office.adapters.AllSubCategoryAdapter
import com.saaei12092021.office.databinding.ActivityHomeBinding
import com.saaei12092021.office.model.requestModel.SignInRequest
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.Category
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.SubCategory
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.model.socketResponse.acceptContactRequestResponse.AcceptContactRequestResponse
import com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse.AddAdsRequestFromSocketResponse
import com.saaei12092021.office.model.socketResponse.addContactRequestResponse.AddContactRequestResponse
import com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse.Data
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.*
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.fragments.*
import com.saaei12092021.office.ui.fragments.addAndRequestEstateDialogFragment.AddAndRequestEstateDialogFragment
import com.saaei12092021.office.ui.fragments.notificationsFragment.NotificationsViewModel
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.BASE_URL
import com.saaei12092021.office.util.Constant.FCM_TOKEN
import com.saaei12092021.office.util.Constant.getFcmInfo
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.mapUtil.MapPresenter
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeActivity : AppCompatActivity(), AllMainCategoryAdapter.OnItemClickListener,
    AllSubCategoryAdapter.OnItemSubClickListener {

    // private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityHomeBinding
    var token: String = ""
    lateinit var navController: NavController
    lateinit var viewModel: MyViewModel
    lateinit var notificationsViewModel: NotificationsViewModel
    lateinit var homeViewModel: HomeViewModel
    lateinit var mSocket: Socket
    var allMainCategoryList = ArrayList<Category>()
    var subCategoryList: ArrayList<SubCategory> = ArrayList()
    lateinit var allMainCategoryAdapter: AllMainCategoryAdapter
    lateinit var allSubCategoryAdapter: AllSubCategoryAdapter
    lateinit var clickedAdsData: Data
    var adsListFromSocket = ArrayList<Data>()
    lateinit var myLang: String
    var deviceLanguage = ""
    lateinit var myId: String
    lateinit var userId: String
    lateinit var myCountry: String
    lateinit var adsByCategoryMapUpdated: HashMap<String, Any>
    lateinit var currentNewAdsRequestFromSocketResponse: AddAdsRequestFromSocketResponse
    lateinit var currentContactRequestResponse: AddContactRequestResponse
    lateinit var currentAcceptedContactRequest: AcceptContactRequestResponse
    var loginResponse: User? = null
    lateinit var signUpResponse: com.saaei12092021.office.model.responseModel.SignUpResponse.User
    var getUserResponse: GetUserResponse? = null
    var parentResponse: GetUserResponse? = null
    var currentContactRequest: Int = 0
    var currentMyEstatePage: String = ""
    var isNewMessageInCurrentMyEstatePage = false
    lateinit var currentRequestedEstateId: String
    var currentLat: Double? = null
    var currentLong: Double? = null
    var mustUpdateLocation = false
    var adsByIdResponse: AdsByIdResponse? = null
    var notificationInfo: String = ""
    var unReadNotificationNumber = 0
    var messageReceiver: BroadcastReceiver? = null
    var currentWebViewLinke = ""
    var currentWebViewPageName = ""
    var adsIdFromDeepLink = ""
    var mustCompleteProfile = true
    val presenter = MapPresenter(this)

    // for use if choose city or neighborhood in filter fragment must move camera to it
    var moveCameraInGoogleMapLatLong = com.google.android.gms.maps.model.LatLng(0.0, 0.0)

    // when add ads must move camera to the chosen city or neighborhood the ads on it .
    var moveCameraGoogleMapToEstateLocationLatLong =
        com.google.android.gms.maps.model.LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //   overridePendingTransition(0, 0)

        binding.contactRequestTitle.paintFlags =
            binding.contactRequestTitle.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.adsRequestTitle.paintFlags =
            binding.adsRequestTitle.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        deviceLanguage = Locale.getDefault().displayLanguage
        setUpLanguageViewAndDirection()
        myLang = Constant.getMyLanguage(this)
        userId = Constant.getUserId(this)
        myId = GeneralFunctions.getUserIdIfSupervisorOrNot(this)
        myCountry = Constant.getMyCountryId(this)
        token = Constant.getToken(this)

        //  Toast(this).showCustomToast(token!!, this)
        Log.d("yasser_token", token)
        Log.d("user_id", myId)
        Log.d("device_language", Locale.getDefault().toString())

        if ((userId == "0") or (token == "")) {
            binding.contactRequestLinear.visibility = View.INVISIBLE
            binding.requestEstateNoLinear.visibility = View.INVISIBLE
        }

        receiveShareAdsOrOfficeDeepLink()

//        val window = this.window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.green_for_app)

        //   GoSellSDK.init(this, "sk_live_eOHxsZmSu9tWa4AwM7BD0L8Q", "com.saaei12092021.office")

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        getNotificationCountAndDisplayTheNumber()

        //   checkIfGetFirebaseTokenOrNot()

        try {
            val uri: URI = URI.create(BASE_URL + "chat")
            //  val uri: URI = URI.create("https://api.saaei.co/chat")
            val option = IO.Options.builder().build()
            option.query = "id=$myId"
            mSocket = IO.socket(uri, option)
            viewModel.mSocket = mSocket
        } catch (e: URISyntaxException) {
            throw RuntimeException()
        }

        adsListFromSocket = ArrayList()
        currentNewAdsRequestFromSocketResponse = AddAdsRequestFromSocketResponse()

        adsByCategoryMapUpdated = HashMap()
//        adsByCategoryMapUpdated["limit"] = 50
//        viewModel.getAdsFromSocket(adsByCategoryMapUpdated)

        //--------------------------------------------------------------------------

        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        viewModel.mSocket.connect()
        //--------------------------------------------------------------------------

        viewModel.error.observe(this, Observer {
            Constant.makeToastMessage(this@HomeActivity, it)
            binding.contentHomeId.homeProgressBar.visibility = View.INVISIBLE
            if (it == "user غير موجود") { // if the user delete from dash board
                Constant.editor(this).apply {
                    clear()
                    apply()
                    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })

        viewModel.listenToAdsRequestFromSocket()
        viewModel.addAdsRequestRequestFromSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    currentNewAdsRequestFromSocketResponse = it.data!!
                    if (it.data.data!!.owner!!.id.toString() != myId) {
                        NewAdsRequestFragment().apply {
                            show(supportFragmentManager, "tag")
                        }
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        viewModel.getUserLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (((GeneralFunctions.ifUserIsSupervisor(this)) and (it.data!!.type == "SUPERVISOR")) or
                        ((!GeneralFunctions.ifUserIsSupervisor(this)) and (it.data.type != "SUPERVISOR"))
                    ) {
                        getUserResponse = it.data
                        Log.d("user_enableNotif", getUserResponse!!.enableNotif.toString())
                        binding.contactRequestNoTv.text =
                            getUserResponse!!.contactRequestCount.toString()
                        binding.adsRequestNoTv.text = getUserResponse!!.adsRequestCount.toString()
                        Constant.editor(this).apply {
                            putString(Constant.USER_BY_ID_RESPONSE, Gson().toJson(it.data))
                            apply()
                            Log.d("USER_BY_ID", Gson().toJson(it.data))
                            if (it.data.type == "OFFICE")
                                displayNewDataForGetUserResponse(true)
                            else displayNewDataForGetUserResponse(false)
                            if (token != "")
                                checkIfWeNeedToRefreshTokenAndGo()
                        }
                    } else {
                        parentResponse = it.data
                        binding.contactRequestNoTv.text =
                            parentResponse!!.contactRequestCount.toString()
                        binding.adsRequestNoTv.text = parentResponse!!.adsRequestCount.toString()
                        Constant.editor(this).apply {
                            putString(Constant.PARENT_USER_BY_ID_RESPONSE, Gson().toJson(it.data))
                            apply()
                            Log.d("parentByIdRs_", Gson().toJson(it.data))
                        }
                    }
                    if (it.data.type == "SUPERVISOR")
                        viewModel.getUser(token = token, userId = myId)
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        viewModel.listenToAddContactRequestInSocket()
        viewModel.addContactRequestInSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    currentContactRequestResponse = it.data!!
                    if (currentContactRequestResponse.data!!.sender!!.id.toString() != myId) {
                        NewContactRequestFragment().apply {
                            show(supportFragmentManager, "tag")
                        }
                    }
                    viewModel.addContactRequestInSocketLive.postValue(Resource.Error(""))
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        viewModel.listenToAcceptContactRequestInSocket()
        viewModel.acceptContactRequestSocketLive.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.success) {
                        if (it.data.data.reciever.id.toString() != myId) {
                            currentAcceptedContactRequest = it.data
                            NewAcceptContactRequestFragment().apply {
                                show(supportFragmentManager, "tag")
                            }
                        }
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }

        viewModel.listenToNewMessageInSocket()
        viewModel.newMessageSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    val toStartChatMainInfo = ToStartChatMainInfo(
                        contactRequest = it.data!!.contactRequest,
                        toId = it.data.toId!!,
                        fromId = it.data.fromId
                    )
                    if (currentContactRequest != toStartChatMainInfo.contactRequest) {
                        val snack =
                            Snackbar.make(binding.root, "لديك رسالة جديدة", Snackbar.LENGTH_LONG)
                        snack.setAction("فتح المحادثة") { view ->
                            currentContactRequest = toStartChatMainInfo.contactRequest
                            val intent = Intent(this@HomeActivity, ChatActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
                            startActivity(intent)
                            // to stop display alert to this message if start chat
                        }
                        snack.show()
//                        try {
//                            val notification: Uri =
//                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                            val r = RingtoneManager.getRingtone(this@HomeActivity, notification)
//                            r.play()
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
                    }

                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        allMainCategoryAdapter = AllMainCategoryAdapter(this)
        allSubCategoryAdapter = AllSubCategoryAdapter(this)
        allMainCategoryList = ArrayList()
        displayMainCategoryList()
        displaySubCategoryList()

        viewModel.getMainCategory(myLang)
        viewModel.mainCategoryResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    allMainCategoryList.clear()
                    allMainCategoryList = it.data?.categories as ArrayList<Category>
                    allMainCategoryList.forEach { listItem ->
                        listItem.isSelected = false
                    }
                    allMainCategoryList.add(
                        0, Category(
                            categoryName = "الكل",
                            categoryName_ar = "الكل",
                            categoryName_en = "all",
                            createdAt = "",
                            hasChild = false,
                            id = 0,
                            priority = 0,
                            type = "",
                            isSelected = true,
                            child = null
                        )
                    )
                    //   if (deviceLanguage == "العربية") {
                    binding.contentHomeId.allMainCategoryRv.layoutDirection =
                        View.LAYOUT_DIRECTION_RTL
                    binding.contentHomeId.allSubCategoryRv.layoutDirection =
                        View.LAYOUT_DIRECTION_RTL

                    //     }
                    allMainCategoryAdapter.differ.submitList(allMainCategoryList)

                    if ((currentLat != null) and (currentLong != null)) {
                        getAdsAccordingLocationAndKm(
                            lat = currentLat!!,
                            long = currentLong!!,
                            km = 20,
                            CategoryId = null,
                            subCategoryId = null
                        )
                    }
                }
                is Resource.Loading -> {
                    binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                }
            }
        })

        viewModel.subCategoryResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    subCategoryList =
                        it.data?.subSubCategory as ArrayList<SubCategory>
                    subCategoryList.forEach { subCategoryItem ->
                        subCategoryItem.isSelected = false
                    }
                    allSubCategoryAdapter.differ.submitList(subCategoryList)
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
            }
        })

        navController = findNavController(R.id.nav_host_fragment_content_home)
        //----------------------------------------------------------------------
        // if we want open edit ads info we need to custom Home Activity View and fill get the ads info
        if (intent.getSerializableExtra("adsByIdResponse") != null) {
            adsByIdResponse = intent.getSerializableExtra("adsByIdResponse") as AdsByIdResponse
            binding.infoAndUserSettingRl.visibility = View.GONE
            navController.navigate(R.id.mainAddNewAdsFragment)
        } else adsByIdResponse = null

        //----------------------------------------------------------------------

        binding.settingIv.setOnClickListener {
            navController.navigate(R.id.myProfileFragment)
            mustUpdateLocation = true
            binding.contentHomeId.mainToolsRl.visibility = View.GONE
        }

        binding.backIv.setOnClickListener {
            GeneralFunctions.hideKeyboard(this)
            if (adsByIdResponse != null)
                finish()
            else
                onBackPressed()
        }

        binding.userProfileImage.setOnClickListener {
            if (((userId == "0") or (token == "")) and (getUserResponse == null)) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else {
                if (getUserResponse != null) {
                    if ((getUserResponse!!.approved) and (getUserResponse!!.accountType == "ACTIVE")) {
                        if (getUserResponse!!.type == "OFFICE") {
                            val intent = Intent(this, CompleteProfileActivity::class.java)
                            intent.putExtra("getUserResponse", getUserResponse)
                            startActivity(intent)
                        }
                        if (getUserResponse!!.type == "SUPERVISOR") {
                            val intent = Intent(this, SupervisorProfileActivity::class.java)
                            intent.putExtra("getUserResponse", getUserResponse)
                            startActivity(intent)
                        }
                    } else {
                        SignInDialogFragment().apply {
                            show(supportFragmentManager, "tag")
                        }
                    }
                }
            }
        }

        binding.officeInfoIfRegisterLinear.setOnClickListener {
            if (((userId == "0") or (token == "")) and (getUserResponse == null)) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else {
                if (getUserResponse != null) {
                    if ((getUserResponse!!.approved) and (getUserResponse!!.accountType == "ACTIVE")) {
                        if (getUserResponse!!.type == "OFFICE") {
                            val intent = Intent(this, CompleteProfileActivity::class.java)
                            intent.putExtra("getUserResponse", getUserResponse)
                            startActivity(intent)
                        }
                        if (getUserResponse!!.type == "SUPERVISOR") {
                            val intent = Intent(this, SupervisorProfileActivity::class.java)
                            intent.putExtra("getUserResponse", getUserResponse)
                            startActivity(intent)
                        }
                    } else {
                        SignInDialogFragment().apply {
                            show(supportFragmentManager, "tag")
                        }
                    }
                }
            }
        }

        binding.officeIfNotRegisterLinear.setOnClickListener {
            if ((userId == "0") or (token == "")) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            }
        }

        binding.notificationRl.setOnClickListener {
            if ((userId == "0") or (token == "")) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else {
                navController.navigate(R.id.notificationsFragment)
                mustUpdateLocation = true
                binding.notificationRl.visibility = View.GONE
                binding.backRl.visibility = View.VISIBLE
            }
        }

        binding.contentHomeId.homeLinear.setOnClickListener {
            navController.navigate(R.id.mapsFragment)
            viewModel.googleMapType.postValue("normal")
            ifErrorInLoading()
        }

        binding.contentHomeId.addLinear.setOnClickListener {
            if ((userId == "0") or (token == "")) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else
                AddAndRequestEstateDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
        }

        binding.contentHomeId.myRequestLinear.setOnClickListener {
            if ((userId == "0") or (token == "")) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else {
                currentMyEstatePage = "myRequestEstate"
                navController.navigate(R.id.mainMyEstateFragment)
                mustUpdateLocation = true
            }
        }

        binding.requestEstateNoLinear.setOnClickListener {
            if ((userId == "0") or (token == "")) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else {
                currentMyEstatePage = "requestEstate"
                navController.navigate(R.id.mainMyEstateFragment)
                mustUpdateLocation = true
            }
        }
        binding.contactRequestLinear.setOnClickListener {
            if ((userId == "0") or (token == "")) {
                SignInDialogFragment().apply {
                    show(supportFragmentManager, "tag")
                }
            } else {
                currentMyEstatePage = "contactRequest"
                mustUpdateLocation = true
                navController.navigate(R.id.mainMyEstateFragment)
            }
        }

        if (intent.getStringExtra("info").toString() != "") {
            notificationInfo = intent.getStringExtra("info").toString()
        }
//        else if (getFcmToken(this) != "") {
//            notificationInfo = getFcmInfo(this)
//            Constant.editor(this).apply {
//                putString(Constant.FCM_INFO, "")
//            }
//        }

//        messageReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent) {
//                notificationInfo = intent.extras?.getString("info").toString()
//                Log.d("getFcmInfo",getFcmInfo(this@HomeActivity))
//            }
//        }

        Log.d("getFcmInfo", getFcmInfo(this))

        if ((userId != "0") or (token != "")) {
            if (notificationInfo == "MESSAGE") {
                currentMyEstatePage = "contactRequest"
                isNewMessageInCurrentMyEstatePage = true
                navController.navigate(R.id.mainMyEstateFragment)
                mustUpdateLocation = true
            }

            if (notificationInfo == "ADS-REQUEST") {
                currentMyEstatePage = "requestEstate"
                navController.navigate(R.id.mainMyEstateFragment)
                mustUpdateLocation = true
            }

            if (notificationInfo == "CONTACT-REQUEST") {
                currentMyEstatePage = "contactRequest"
                navController.navigate(R.id.mainMyEstateFragment)
                mustUpdateLocation = true
            }
        }
        NotificationManagerCompat.from(applicationContext).cancelAll()

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_home)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

    }

    private fun checkIfWeNeedToRefreshTokenAndGo() {
        val tokenGeneratedDate = Constant.getTokenGenerateTime(this)
        //  "Thu Mar 15 01:26:51 GMT+03:00 2022" // i used this date for test
        val datAsLong1 = Date(tokenGeneratedDate)

        // if we wand to display the date time with format
        // val sdf1 = SimpleDateFormat("dd/MM/yyyy")
        // val date1 = sdf1.format(datAsLong1)

        val currentTime = Calendar.getInstance().time.toString()
        val datAsLong2 = Date(currentTime)

        // if we wand to display the date time with format
        // val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        //val date2 = sdf2.format(datAsLong2)

        // Finding the absolute difference between  // the two dates.time (in milli seconds)
        val mDifference = kotlin.math.abs(datAsLong2.time - datAsLong1.time)
        val mDifferenceDates =
            mDifference / (24 * 60 * 60 * 1000) // Converting milli seconds to dates

        Log.d("compare_", mDifferenceDates.toString())
        if (mDifferenceDates > 3) {
            homeViewModel.refreshToken(
                SignInRequest(
                    phone = getUserResponse!!.phone,
                    password = "",
                    token = "",
                    udId = ""
                ), token
            )
            homeViewModel.refreshTokenLive.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        Constant.editor(this).apply {
                            putString(Constant.TOKEN, it.data?.token)
                            putString(Constant.TOKEN_GENERATE_DATE, currentTime)
                            apply()
                        }
                        Log.d("token_refresh", it.data?.token + " - " + currentTime)
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            })
        }
    }

    private fun setUpLanguageViewAndDirection() {
        //   if (deviceLanguage == "العربية") {
        binding.headerRelative.layoutDirection = View.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //   }
    }

    private fun getNotificationCountAndDisplayTheNumber() {
        if ((myId != "0") and (token != "")) {
            notificationsViewModel.getNotificationsUnreadCount(token)
            notificationsViewModel.notificationsCountLive.observe(this, Observer {
                when (it) {
                    is Resource.Success -> {
                        unReadNotificationNumber = it.data?.unread!!
                        binding.notificationUnreadCountTv.text = unReadNotificationNumber.toString()
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            })
        }
    }

    private fun checkIfGetFirebaseTokenOrNot() {
        if (Constant.getFcmToken(this) == "") {
            FirebaseApp.initializeApp(this)
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("token_failed", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Constant.editor(this).apply {
                    putString(FCM_TOKEN, token)
                    apply()
                }

                // Log and toast
                Log.d("the_fcm_token", token)

            })
        }
    }

    private fun getStoreUserDataAndDisplayIt() {
        val userJsonSignUpData = Constant.getSignUpResponse(this)
        val userJsonLoginData = Constant.getLoginResponse(this)
        val userByIdData = Constant.getUserByIdResponse(this)

        if (userByIdData != "") {
            getUserResponse =
                Gson().fromJson(userByIdData, GetUserResponse::class.java) as GetUserResponse
            displayNewDataForGetUserResponse(false)
        } else if (userJsonSignUpData != "") {
            signUpResponse = Gson().fromJson(
                userJsonSignUpData,
                com.saaei12092021.office.model.responseModel.SignUpResponse.User::class.java
            ) as com.saaei12092021.office.model.responseModel.SignUpResponse.User
            if (signUpResponse.accountType == "SIGNUP-PROCESS") {
                binding.officeName.text = signUpResponse.fullname
                Glide.with(this).load(signUpResponse.img).placeholder(R.drawable.profile_image)
                    .into(binding.userProfileImage)
                binding.officeInfoIfRegisterLinear.visibility = View.VISIBLE
                binding.officeIfNotRegisterLinear.visibility = View.GONE
                binding.cityTv.text = "بانتظار تفعيل الحساب"
                binding.cityTv.setTextColor(Color.parseColor("#F45656"))
                binding.locationIconIv.visibility = View.GONE
            }
        } else if (userJsonLoginData != "") {
            binding.officeInfoIfRegisterLinear.visibility = View.VISIBLE
            binding.officeIfNotRegisterLinear.visibility = View.GONE
            loginResponse = Gson().fromJson(userJsonLoginData, User::class.java) as User
            binding.officeName.text = loginResponse!!.fullname
            binding.cityTv.text = loginResponse!!.city?.cityName_ar
            Glide.with(this).load(loginResponse!!.img).placeholder(R.drawable.profile_image)
                .into(binding.userProfileImage)

            if ((!loginResponse!!.approved) and (loginResponse!!.accountType == "SIGNUP-PROCESS")) {
                binding.cityTv.text = "بانتظار تفعيل الحساب"
                binding.cityTv.setTextColor(Color.parseColor("#F45656"))
                binding.locationIconIv.visibility = View.GONE
            }
        } else {
            binding.officeInfoIfRegisterLinear.visibility = View.GONE
            binding.officeIfNotRegisterLinear.visibility = View.VISIBLE
            binding.signUpTv.setOnClickListener {
                val intent = Intent(this@HomeActivity, SignupActivity::class.java)
                startActivity(intent)
            }
            binding.signInTv.setOnClickListener {
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun displayNewDataForGetUserResponse(goToCompleteIfNeed: Boolean) {
        if ((!getUserResponse!!.approved) or (getUserResponse!!.accountType == "SIGNUP-PROCESS")) {
            binding.cityTv.text = "بانتظار تفعيل الحساب"
            binding.cityTv.setTextColor(Color.parseColor("#F45656"))
            binding.locationIconIv.visibility = View.GONE
        } else if ((((getUserResponse!!.accountType == "ACTIVE") and (getUserResponse!!.approved)) and getUserResponse!!.workCity.isNullOrEmpty())
            and goToCompleteIfNeed
        ) {
            if (mustCompleteProfile)
                showDialogForCompleteProfile()
            binding.cityTv.text = "أكمال بياناتك"
            binding.cityTv.setTextColor(Color.parseColor("#FF3700B3"))
        } else if ((getUserResponse!!.accountType == "ACTIVE") and (getUserResponse!!.approved)) {
            binding.officeIfNotRegisterLinear.visibility = View.GONE
            try {
                binding.cityTv.text = getUserResponse!!.city.cityName
                binding.cityTv.setTextColor(Color.parseColor("#FF000000"))
            } catch (e: Exception) {
            }
        }
        binding.officeInfoIfRegisterLinear.visibility = View.VISIBLE
        binding.officeName.text = getUserResponse!!.fullname
        Glide.with(this).load(getUserResponse!!.img).placeholder(R.drawable.profile_image)
            .into(binding.userProfileImage)

    }

    fun displayMapsMenuDialogFragment() {
        MapsMenuDialogFragment().apply {
            show(supportFragmentManager, "tag")
        }
    }

    private val onConnectError = Emitter.Listener {
        runOnUiThread {
            //  binding.homeTitleTv.text = "Connection Error"
        }
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread {
            //   Constant.makeToastMessage(this, "غير متصل")
        }
    }

    private var onConnect = Emitter.Listener {
        runOnUiThread {
            //  binding.homeTitleTv.text = "connected"
        }
    }

    private fun displayMainCategoryList() {
        binding.contentHomeId.allMainCategoryRv.apply {
            adapter = allMainCategoryAdapter
        }
    }

    private fun displaySubCategoryList() {
        binding.contentHomeId.allSubCategoryRv.apply {
            adapter = allSubCategoryAdapter
        }
    }

    override fun onItemClick(mainCategory: Category, position: Int) {
        allMainCategoryList.forEach {
            it.isSelected = it.id == mainCategory.id
        }
        allMainCategoryAdapter.notifyDataSetChanged()

        if (mainCategory.id == 0) {
            if ((currentLat != null) and (currentLong != null))
                getAdsAccordingLocationAndKm(
                    lat = currentLat!!,
                    long = currentLong!!,
                    km = 20,
                    CategoryId = null,
                    subCategoryId = null
                )
            binding.contentHomeId.allSubCategoryRv.visibility = View.GONE
        } else {
            if ((currentLat != null) and (currentLong != null))
                getAdsAccordingLocationAndKm(
                    lat = currentLat!!,
                    long = currentLong!!,
                    km = 20,
                    CategoryId = mainCategory.id,
                    subCategoryId = null
                )

            binding.contentHomeId.allSubCategoryRv.visibility = View.VISIBLE
            getSubCategory(mainCategory.id.toString())
        }
        subCategoryList.clear()
        allSubCategoryAdapter.differ.submitList(subCategoryList)
    }

    override fun onItemSubClick(subCategory: SubCategory, position: Int) {
        subCategoryList.forEach {
            it.isSelected = it.id == subCategory.id
        }
        allSubCategoryAdapter.notifyDataSetChanged()

        if ((currentLat != null) and (currentLong != null))
            getAdsAccordingLocationAndKm(
                lat = currentLat!!,
                long = currentLong!!,
                km = 20,
                CategoryId = subCategory.parent,
                subCategoryId = subCategory.id
            )
    }

    fun uncheckedAllCategory(unselectedAllSubCategory: Boolean) {
        if (!allMainCategoryList.isNullOrEmpty()) {
            allMainCategoryList.forEach {
                it.isSelected = false
            }
            if (unselectedAllSubCategory) {
                allMainCategoryAdapter.notifyDataSetChanged()
            } else {
                allMainCategoryList[0].isSelected = true
                allMainCategoryAdapter.notifyDataSetChanged()
            }
        }
        subCategoryList.clear()
        allSubCategoryAdapter.differ.submitList(subCategoryList)
    }

    fun displayAdsInfoFromMap() {
        EstateDetailsFromMapMarkerFragment().apply {
            show(supportFragmentManager, "tag")
        }
    }

    fun ifErrorInLoading() {
        if (allMainCategoryList.size < 2) {
            val refresh = Intent(this, HomeActivity::class.java)
            refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(refresh)
        }
    }

    fun showBottomSheetEstateFilterAndSearch() {
        EstateFilterFragment().apply {
            show(supportFragmentManager, "tag")
        }
    }

    fun clearAllStackFragmentExceptMapFragment() {
        navController.navigate(R.id.mapsFragment)
    }

    fun getAdsAccordingLocationAndKm(
        lat: Double,
        long: Double,
        km: Int,
        CategoryId: Int?,
        subCategoryId: Int?
    ) {
        adsByCategoryMapUpdated.clear()
        adsByCategoryMapUpdated["limit"] = 50
        adsByCategoryMapUpdated["lat"] = lat
        adsByCategoryMapUpdated["long"] = long
        adsByCategoryMapUpdated["km"] = 4
        if (CategoryId != null)
            adsByCategoryMapUpdated["category"] = CategoryId
        if (subCategoryId != null)
            adsByCategoryMapUpdated["subCategory"] = subCategoryId
        viewModel.getAdsFromSocket(adsByCategoryMapUpdated)
    }

    override fun onResume() {
        super.onResume()
        currentContactRequest = 0
        getStoreUserDataAndDisplayIt()
        if (userId != "0")
            viewModel.getUser(token = token, userId = userId)
        Log.d("token_myId_", "$token--$userId")
        if (mustCompleteProfile == false)
            token = Constant.getToken(this)
    }

    override fun onBackPressed() {
        if (adsByIdResponse != null)
            finish()
        else super.onBackPressed()
        mustUpdateLocation = true
        GeneralFunctions.hideKeyboard(this)
        //    LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver!!, IntentFilter("NotificationData"))

    }

//    override fun onNewNotification(nothing : String) {
//        binding.notificationUnreadCountTv.text = (++unReadNotificationNumber).toString()
//    }

    override fun onStop() {
        super.onStop()
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver!!)
    }

    fun showDialogForCompleteProfile() { // we used the same design in update market place as view only
        val dialog = Dialog(this, R.style.DialogStyle)
        dialog.setContentView(R.layout.modify_market_place_dialog_item)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_shape_white)
        val goMessageTv = dialog.findViewById<TextView>(R.id.market_place_status_message_tv)
        val goToModifyTv = dialog.findViewById<TextView>(R.id.go_to_modify_tv)
        dialog.show()
        mustCompleteProfile = false
        goMessageTv.text = "ملفك بحاجة لاكمال بعض البيانات المهمة "
        goToModifyTv.text = "الإكمال الان ؟"
        goToModifyTv.setOnClickListener {
            val intent = Intent(this, CompleteProfileActivity::class.java)
            if (getUserResponse != null) {
                intent.putExtra("getUserResponse", getUserResponse)
            }
            startActivity(intent)
            dialog.dismiss()
        }
    }

    private fun getSubCategory(mainCategoryId: String) {
        viewModel.getSubCategory(
            mainId = mainCategoryId,
            myLang
        )
    }

    private fun receiveShareAdsOrOfficeDeepLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                deepLink?.let { uri ->
                    val path = uri.toString().substring(deepLink.toString().lastIndexOf("=") + 1)

                    Log.d("deep_link_url_", uri.toString())

                    when {
                        uri.toString().contains("adsId") -> {
                            val postId = path
                            Log.d("deep_link_adsId_", postId)
                            val intent = Intent(this@HomeActivity, EstateDetailsActivity::class.java)
                            intent.putExtra("adsId", postId)
                            startActivity(intent)
                        }

                        uri.toString().contains("officeId") -> {
                            val postId = path
                            Log.d("deep_link_officeId_", postId)
                            val intent = Intent(this@HomeActivity, OfficeOrUserPageActivity::class.java)
                            intent.putExtra("owner", Integer.parseInt(postId))
                            startActivity(intent)
                        }

                        uri.toString().contains("reqId") -> {
                            val postId = path
                            Log.d("deep_link_Req_Id_", postId)
                            currentRequestedEstateId = postId
                            val dialogFragment = RequestedEstateDetailsFragment()
                            dialogFragment.show(supportFragmentManager, "signature")
                        }
                    }
                }
            }.addOnFailureListener {

            }
    }

}
