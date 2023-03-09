package com.saaei12092021.office.ui.activities.estateDetailsActivity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.*
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.*
import com.saaei12092021.office.databinding.ActivityEstateDetailsBinding
import com.saaei12092021.office.model.responseModel.BookingCalenderResponse
import com.saaei12092021.office.model.responseModel.ToStartChatMainInfo
import com.saaei12092021.office.model.responseModel.adsById.*
import com.saaei12092021.office.model.responseModel.favoritesResponse.Data
import com.saaei12092021.office.model.socketRequest.ViewAdsRequest
import com.saaei12092021.office.model.socketRequest.chatRequest.AddContactRequest
import com.saaei12092021.office.model.socketResponse.addContactRequestResponse.AddContactRequestResponse
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.MediaInChatActivity
import com.saaei12092021.office.ui.activities.OfficeOrUserPageActivity
import com.saaei12092021.office.ui.activities.billInfoAndPaymentActivities.BillInfoActivity
import com.saaei12092021.office.ui.activities.chatActivity.ChatActivity
import com.saaei12092021.office.ui.activities.estateLocationOnMapActivity.EstateLocationOnMapActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.dateAndTimeReformat
import com.saaei12092021.office.util.Constant.dateAndTimeReformatToDateOnly
import com.saaei12092021.office.util.Constant.getDateWithServerTimeStamp
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.Transformer.DepthPageTransformer
import com.saaei12092021.office.util.showCustomToast
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class EstateDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityEstateDetailsBinding

    // lateinit var navController: NavController
    lateinit var adsResponse: AdsByIdResponse
    lateinit var bookingCalenderResponse: BookingCalenderResponse
    private lateinit var featureAdapter: FeaturesInEStateDetailsAdapter
    private lateinit var propertyAdapter: PropertiesInEstateDetailsAdapter
    private lateinit var unitsInBuildingInEstateDetailsAdapter: UnitsInBuildingInEstateDetailsAdapter
    private lateinit var unitsOrLndInBuildingOrPlaneAdapter: UnitsOrLndInBuildingOrPlaneAdapter
    private lateinit var statisticsAndReviewsAdapter: StatisticsAndReviewsAdapter
    private lateinit var featuresList: ArrayList<Feature>
    private lateinit var propertiesList: ArrayList<Property>
    private lateinit var statisticsList: ArrayList<Statistic>
    var favoriteList = ArrayList<Data>()
    lateinit var adsId: String
    lateinit var viewModel: MyViewModel
    lateinit var estateDetailsViewModel: EstateDetailsViewModel
    private lateinit var mMap: GoogleMap
    var imageList = ArrayList<String>()
    private var isInFavorite: Boolean = false
    lateinit var userId: String
    var token: String = ""
    lateinit var mSocket: Socket
    lateinit var myLang: String
    lateinit var myId: String
    var currentPosition = 0
    var mustUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        binding = ActivityEstateDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        setUpLanguageViewAndDirection()
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        estateDetailsViewModel = ViewModelProvider(this).get(EstateDetailsViewModel::class.java)

        //--------------------------------------------------------------------------
        userId = Constant.getUserId(this)
        myLang = Constant.getMyLanguage(this)
        myId = GeneralFunctions.getUserIdIfSupervisorOrNot(this)
        token = Constant.getToken(this)
        mSocket = GeneralFunctions.createSocket(this)
        viewModel.mSocket = mSocket
        estateDetailsViewModel.mSocket = mSocket
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        viewModel.mSocket.connect()
        estateDetailsViewModel.mSocket.connect()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.this_estate_on_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //   navController = findNavController(R.id.estate_details_nav_graph)

        adsId = (intent.getStringExtra("adsId").toString())
        Log.d("yasser_adsId", adsId)

        estateDetailsViewModel.sendViewAds(
            ViewAdsRequest(
                viewOnId = Integer.parseInt(adsId),
                userId = Integer.parseInt(userId),
                relatedTo = "ADS",
            )
        )

        featuresList = ArrayList()
        propertiesList = ArrayList()
        favoriteList = ArrayList()
        statisticsList = ArrayList()

        binding.backRl.setOnClickListener {
            finish()
        }

        binding.shareRl.setOnClickListener {
            generateSharingLink { shortLinkItem ->
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Saaei")
                val adsName =
                    adsResponse.advertisement.category.categoryName + " - " + adsResponse.advertisement.subCategory.categoryName
                var shareMessage = "اوصيك بهذا العقار \n $adsName \n"
                shareMessage += shortLinkItem
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "مشاركة مع .."))

            }
        }

        viewModel.error.observe(this, Observer {
            Toast(this).showCustomToast(it.toString(), this)
            hideAllProgressBar()
        })

        if ((myId != "0") and (token != ""))
            viewModel.getMyFavorites(Constant.getToken(this), userId)
        viewModel.favoritesResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    favoriteList = it.data?.data as ArrayList<Data>
                    favoriteList.forEach {
                        if (it.id.toString() == adsId) {
                            binding.favoriteEnabledIv.visibility = View.VISIBLE
                            isInFavorite = true
                        }
                    }
                    hideAllProgressBar()
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    hideAllProgressBar()
                }
                else -> {}
            }
        })

        binding.favoriteRl.setOnClickListener {
            if ((myId == "0") or (token == "")) {
                Toast(this).showCustomToast("لا تملك الصلاحيات", this)
            } else {
                if (isInFavorite) {
                    viewModel.removeFromMyFavorites(Constant.getToken(this), adsId)
                    viewModel.removeFromMyFavorites.observe(this, Observer {
                        when (it) {
                            is Resource.Success -> {
                                hideAllProgressBar()
                                isInFavorite = false
                                binding.favoriteEnabledIv.visibility = View.INVISIBLE
                            }
                            is Resource.Loading -> {
                                binding.favoritePb.visibility = View.VISIBLE
                            }
                            is Resource.Error -> {
                                hideAllProgressBar()
                            }
                            else -> {}
                        }
                    })
                } else {
                    viewModel.addToMyFavorites(Constant.getToken(this), adsId)
                    viewModel.addNewFavoriteResponse.observe(this, Observer {
                        when (it) {
                            is Resource.Success -> {
                                hideAllProgressBar()
                                isInFavorite = true
                                binding.favoriteEnabledIv.visibility = View.VISIBLE
                                binding.favoriteEnabledIv.bringToFront()
                            }
                            is Resource.Loading -> {
                                binding.favoritePb.visibility = View.VISIBLE
                            }
                            is Resource.Error -> {
                                hideAllProgressBar()
                            }
                            else -> {}
                        }
                    })
                }
            }
        }

        viewModel.listenToAddContactRequestInSocket()
        viewModel.addContactRequestInSocketLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data!!.data!!.ads.toString() == adsId) {
                        val currentContactRequestResponse = it.data.data
                        val toStartChatMainInfo = ToStartChatMainInfo(
                            contactRequest = currentContactRequestResponse!!.id!!,
                            toId = currentContactRequestResponse.sender!!.id,
                            fromId = currentContactRequestResponse.reciever!!.id
                        )
                        val intent = Intent(
                            this@EstateDetailsActivity,
                            ChatActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("toStartChatMainInfo", toStartChatMainInfo)
                        startActivity(intent)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
                else -> {}
            }
        })

        viewModel.getAdsById(adsId)
        viewModel.adsByIdLive.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.estateDetailsProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    hideAllProgressBar()
                    adsResponse = it.data as AdsByIdResponse
                    featuresList = adsResponse.advertisement.features as ArrayList<Feature>
                    propertiesList = adsResponse.advertisement.properties as ArrayList<Property>
                    hideAllProgressBar()
                    binding.allEstateDetailsLinear.visibility = View.VISIBLE
                    displayData()
                    binding.estateOnMapLinear.setOnClickListener {
                        val intent = Intent(
                            this@EstateDetailsActivity,
                            EstateLocationOnMapActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("adsResponse", adsResponse)
                        startActivity(intent)
                    }

                    binding.sentMessageBtn.setOnClickListener {
                        if ((myId == "0") or (token == "")) {
                            Toast(this).showCustomToast("لا تملك الصلاحيات", this)
                        } else {
                            val contactRequestObject = AddContactRequest(
                                sender = Integer.parseInt(myId),
                                adsRequest = null,
                                lang = myLang,
                                ads = Integer.parseInt(adsId)
                            )
                            viewModel.addContactRequestInSocket(contactRequestObject)
                            binding.sentMessageBtn.isEnabled = false
                            binding.sentMessageBtn.text = "تم الارسال"
                            binding.sendMessagePb.visibility = View.INVISIBLE
                        }
                    }
                    if (!adsResponse.advertisement.link3D.isNullOrEmpty()) {
                        binding.threeDImageRl.visibility = View.VISIBLE

                        val anim = RotateAnimation(
                            0f,
                            360f,
                            RotateAnimation.RELATIVE_TO_SELF,
                            .5f,
                            RotateAnimation.RELATIVE_TO_SELF,
                            .5f
                        )
                        anim.interpolator = LinearInterpolator()
                        anim.repeatCount = Animation.INFINITE
                        anim.duration = 3000

                        binding.threeDImageIv.startAnimation(anim)
                        // binding.threeDImageIv.animation = null

                        runSimpleAnimationCounterTimer()

                        binding.threeDImageRl.setOnClickListener {
                            if (adsResponse.advertisement.link3D[0].contains("storage.net")) {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(adsResponse.advertisement.link3D[0])
                                )
                                startActivity(browserIntent)
                            } else {
                                val intent = Intent(this, MediaInChatActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("theLink", adsResponse.advertisement.link3D[0])
                                intent.putExtra("dataType", "URL")
                                startActivity(intent)
                            }
                        }
                    }

                    if (adsResponse.advertisement.contractType == "RENT")
                        if (adsResponse.advertisement.rentType == "DAILY") {
                            if ((myId != "0") and (token != "")) {
                                estateDetailsViewModel.getBookingCalender(
                                    token = token,
                                    adsId = adsId,
                                    startDate = null,
                                    endDate = null
                                )
                                estateDetailsViewModel.bookingCalenderLive.observe(
                                    this,
                                    Observer { bookingResponse ->
                                        when (bookingResponse) {
                                            is Resource.Success -> {
                                                bookingCalenderResponse = bookingResponse.data!!
                                                if (adsResponse.advertisement.owner.id.toString() != myId) {
                                                    binding.rentBtn.visibility = VISIBLE
                                                    binding.rentBtn.setOnClickListener {
                                                        val intent =
                                                            Intent(
                                                                this@EstateDetailsActivity,
                                                                BillInfoActivity::class.java
                                                            )
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        intent.putExtra("type", "4")
                                                        intent.putExtra(
                                                            "adsByIdResponse",
                                                            adsResponse
                                                        )
                                                        intent.putExtra(
                                                            "bookingCalenderResponse",
                                                            bookingCalenderResponse
                                                        )
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                            is Resource.Error -> {}
                                            is Resource.Loading -> {}
                                            else -> {}
                                        }
                                    })
                            } else {
                                binding.rentBtn.visibility = View.VISIBLE
                                binding.rentBtn.setOnClickListener {
                                    Toast(this).showCustomToast(
                                        "لا تملك الصلاحيات للحجز",
                                        this
                                    )
                                }
                            }
                        }

                    if (adsResponse.advertisement.hasDeposit)
                        if (adsResponse.advertisement.deposit != 0.0)
                            if (adsResponse.advertisement.owner.id.toString() != myId) {
                                binding.depositBtn.visibility = View.VISIBLE
                                /*
                                if (adsResponse.advertisement.deposit.roundToInt() == Integer.parseInt(
                                        adsResponse.advertisement.price.toString()
                                    )
                                )
                                    binding.depositBtn.text = "إدفع"
                                 */
                                binding.depositBtn.setOnClickListener {
                                    val intent = Intent(this, BillInfoActivity::class.java)
                                    intent.putExtra("adsByIdResponse", adsResponse)
                                    intent.putExtra("type", "3")
                                    startActivity(intent)
                                }
                            }
                }
                is Resource.Error -> {
                    hideAllProgressBar()
                }
                else -> {}
            }
        })

        featureAdapter = FeaturesInEStateDetailsAdapter()
        binding.featureRv.apply {
            adapter = featureAdapter
            featureAdapter.differ.submitList(featuresList)
        }

        propertyAdapter = PropertiesInEstateDetailsAdapter()
        binding.propertiesRv.apply {
            adapter = propertyAdapter
            propertyAdapter.differ.submitList(propertiesList)
        }

        unitsInBuildingInEstateDetailsAdapter = UnitsInBuildingInEstateDetailsAdapter()
        binding.unitInFloorRv.apply {
            adapter = unitsInBuildingInEstateDetailsAdapter
            // propertyAdapter.differ.submitList(propertiesList)
        }

        unitsOrLndInBuildingOrPlaneAdapter = UnitsOrLndInBuildingOrPlaneAdapter()
        binding.landsInPlannedRv.apply {
            adapter = unitsOrLndInBuildingOrPlaneAdapter
            // propertyAdapter.differ.submitList(propertiesList)
        }

    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
//        if (deviceLanguage == "العربية") {
        binding.headerRl.layoutDirection = LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        //       }
    }

    private fun hideAllProgressBar() {
        binding.favoritePb.visibility = View.INVISIBLE
        binding.estateDetailsProgressBar.visibility = View.INVISIBLE
        binding.sendMessagePb.visibility = View.INVISIBLE
    }

    private fun displayData() {
        val totalViewFromServer = adsResponse.advertisement.totalViews
        if (myId == adsResponse.advertisement.owner.id.toString()) {
            binding.ifOwnerLinear.visibility = View.VISIBLE
            binding.deleteRl.visibility = View.VISIBLE
            binding.modifyRl.visibility = View.VISIBLE
            binding.deleteRl.setOnClickListener {
                if (adsResponse.advertisement.depositPaidUser == null) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("هل أنت متأكد من الحذف")
                        .setCancelable(true)
                        .setPositiveButton("نعم") { dialog, id ->
                            estateDetailsViewModel.deleteAdsById(Constant.getToken(this), adsId)
                            estateDetailsViewModel.deleteAdsByIdLive.observe(this, Observer {
                                when (it) {
                                    is Resource.Success -> {
                                        Constant.makeToastMessage(this, "تم حذف العقار")
                                        finish()
                                    }
                                    is Resource.Loading -> {
                                        binding.estateDetailsProgressBar.visibility = View.VISIBLE
                                    }
                                    is Resource.Error -> {
                                        hideAllProgressBar()
                                    }
                                    else -> {}
                                }
                            })
                            dialog.cancel()
                        }
                        .setNegativeButton("لا") { dialog, id -> dialog.cancel() }
                    val alert = builder.create()
                    alert.show()
                } else {
                    Toast(this).showCustomToast("حذف هذا العقار أصبح غير ممكن بسبب دفع عربون", this)
                }
            }

            binding.modifyRl.setOnClickListener {
                if (adsResponse.advertisement.depositPaidUser == null) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("adsByIdResponse", adsResponse)
                    startActivity(intent)
                    mustUpdate = true
                    overridePendingTransition(0, 0)
                } else {
                    Toast(this).showCustomToast(
                        "التعديل على هذا عقار اصبح غير ممكن بسبب دفع عربون",
                        this
                    )
                }
            }

            binding.availableAdsRl.visibility = View.VISIBLE

            if (adsResponse.advertisement.available) {
                binding.availableOrNotSwitch.isChecked = true
                binding.availableOrNotTv.visibility = View.VISIBLE
                binding.availableOrNotTv.text = "العقار متاح"
                binding.availableOrNotTv.setTextColor(Color.parseColor("#B32857"))
            } else {
                binding.availableOrNotSwitch.isChecked = false
                binding.availableOrNotTv.visibility = View.VISIBLE
                binding.availableOrNotTv.text = "العقار غير متاح"
                binding.availableOrNotTv.setTextColor(Color.parseColor("#21305D"))
            }

            binding.availableOrNotSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    estateDetailsViewModel.makeAdsAvailable(Constant.getToken(this), adsId)
                    binding.availableOrNotTv.text = "العقار متاح"
                    binding.availableOrNotTv.setTextColor(Color.parseColor("#B32857"))
                } else {
                    estateDetailsViewModel.makeAdsUnavailable(Constant.getToken(this), adsId)
                    binding.availableOrNotTv.text = "العقار غير متاح"
                    binding.availableOrNotTv.setTextColor(Color.parseColor("#21305D"))
                }
                viewModel.adsByIdLive.postValue(Resource.Error(""))
            }

            statisticsAndReviewsAdapter = StatisticsAndReviewsAdapter()
            binding.statisticsViewsRv.apply {
                adapter = statisticsAndReviewsAdapter
                statisticsAndReviewsAdapter.differ.submitList(statisticsList)
            }

            adsResponse.advertisement.statistics.forEach {
                if (totalViewFromServer != 0)
                    it.city_percentage_for_total_views =
                        ((it.count.toFloat() / totalViewFromServer.toFloat()) * 100)
            }
            val tempListForView = adsResponse.advertisement.statistics
            binding.totalViewsTv.text = totalViewFromServer.toString()
            statisticsAndReviewsAdapter.differ.submitList(tempListForView)
        } else {
            binding.ifNotOwnerLinear.visibility = View.VISIBLE
            binding.ownerNameTv.text = adsResponse.advertisement.owner.fullname
            if (adsResponse.advertisement.adNumber != null)
                binding.adNumberTv.text = adsResponse.advertisement.adNumber
            else binding.adNumberLinear.visibility = View.GONE
            Glide.with(this@EstateDetailsActivity)
                .load(adsResponse.advertisement.owner.img).placeholder(R.drawable.profile_image)
                .into(binding.userProfileImage)
            binding.sentMessageBtn.visibility = View.VISIBLE

            if (adsResponse.advertisement.enablePhoneContact) {
                binding.callNowLinear.visibility = View.VISIBLE
                binding.callNowLinear.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(
                        "tel:" + adsResponse.advertisement.owner.phone
                    )
                    if (intent.resolveActivity(packageManager) != null) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }
            }
            adsResponse.advertisement.contactsOffices!!.forEach {
                if (myId == it.toString()) {
                    binding.sentMessageBtn.isEnabled = false
                    binding.sentMessageBtn.text = "تم الارسال"
                    binding.sendMessagePb.visibility = View.INVISIBLE
                }
            }
        }

        if (adsResponse.advertisement.hasDeposit) {
            binding.depositLinear.visibility = View.VISIBLE
            binding.depositValueTv.text = adsResponse.advertisement.deposit.toString()
        }

        if (adsResponse.advertisement.imgs.isNotEmpty()) {
            imageList = ArrayList()
            imageList.clear()
            imageList.addAll(adsResponse.advertisement.imgs)
        }

        val sliderAdsAdapter = SliderAdsAdapter(imageList as ArrayList<String>, this)
        binding.adsImageViewPager.adapter = sliderAdsAdapter
        binding.adsImageViewPager.setPageTransformer(true, DepthPageTransformer())
        createIndicator(imageList.size)

        binding.adsImageViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                changeIndicator(position)
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

//------------- to set data and time according to the locally date and time  ---
        try {
            val date = adsResponse.advertisement.createdAt.getDateWithServerTimeStamp()
            var formatter: DateTimeFormatter? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(
                    Locale.ENGLISH
                )
                val dateTime: LocalDateTime = LocalDateTime.parse(date.toString(), formatter)
                val formatter2: DateTimeFormatter =
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy  |  hh:mm a",
                        Locale("ar") // For english use Locale.ENGLISH
                        // Locale.getDefault()
                    )
                binding.createdAtTx.text = dateTime.format(formatter2)
            } else binding.createdAtTx.text =
                dateAndTimeReformat(adsResponse.advertisement.createdAt).trim().replace(" ", "  ")
        } catch (e: Exception) {
        }
//---------------------------------

        binding.mainCategoryTv.text =
            adsResponse.advertisement.category.categoryName + " - " + adsResponse.advertisement.subCategory.categoryName
        if (adsResponse.advertisement.contractType == "SALE")
            binding.contractTypeTv.text = GeneralFunctions.translateEnumToWord("SALE")
        else {
            binding.contractTypeTv.text =
                GeneralFunctions.translateEnumToWord("RENT") + " - " + GeneralFunctions.translateEnumToWord(
                    adsResponse.advertisement.rentType!!
                )
            when (adsResponse.advertisement.rentType) {
                "DAILY" -> binding.priceTitleTv.text = "سعر الايجار اليومي : "
                "MONTHLY" -> binding.priceTitleTv.text = "سعر الايجار الشهري : "
                "YEARLY" -> binding.priceTitleTv.text = "سعر الايجار السنوي : "
                "OPENING" -> binding.priceTitleTv.text = "سعر الايجار حسب الفترة المحددة : "
            }
            //   binding.priceTitleTv.text = "سعر الإيجار : "
        }
        binding.estateIdTv.text = adsResponse.advertisement.id.toString()
        binding.titleTv.text = adsResponse.advertisement.title
        binding.cityNameTv.text = adsResponse.advertisement.city.cityName
        binding.areaNameTv.text = adsResponse.advertisement.area.areaName
        binding.priceTv.text = GeneralFunctions.reformatPrice(adsResponse.advertisement.price)
        binding.realEstateAreaValueTv.text = adsResponse.advertisement.size.toString()
        binding.addressTv.text = adsResponse.advertisement.address
        if (adsResponse.advertisement.enableInstallment) {
            binding.ifInstalmentTv.visibility = View.VISIBLE
            binding.ifInstalmentTv.text = "هذا العقار متاح للتقسيط"
            binding.ifInstalmentTv.setTextColor(Color.parseColor("#FF018786"))
        }
        if (adsResponse.advertisement.streetName != null)
            binding.streetNameTv.text = adsResponse.advertisement.streetName
        binding.descriptionTv.text = adsResponse.advertisement.description
        binding.bestFeatureTv.text = adsResponse.advertisement.bestFeature
        if (adsResponse.advertisement.contraindications) {
            binding.contraindicationsAnswerTv.text = "نعم"
            binding.contraindicationsDescriptionTv.visibility = View.VISIBLE
            binding.contraindicationsDescriptionTv.text =
                adsResponse.advertisement.contraindicationsDesc
        } else binding.contraindicationsAnswerTv.text = "لا"
        if (adsResponse.advertisement.undocumentedRights) {
            binding.undocumentedRightsAnswerTv.text = "نعم"
            binding.undocumentedRightsDescriptionTv.visibility = View.VISIBLE
            binding.undocumentedRightsDescriptionTv.text =
                adsResponse.advertisement.undocumentedRightsDesc
        } else binding.undocumentedRightsAnswerTv.text = "لا"
        if (adsResponse.advertisement.influentialInformation) {
            binding.influentialInformationAnswerTv.text = "نعم"
            binding.influentialInformationDescriptionTv.visibility = View.VISIBLE
            binding.influentialInformationDescriptionTv.text =
                adsResponse.advertisement.influentialInformationDesc
        } else binding.influentialInformationAnswerTv.text = "لا"
        if (adsResponse.advertisement.guarantees) {
            binding.guaranteesAnswerTv.text = "نعم"
            binding.guaranteesDescriptionTv.visibility = View.VISIBLE
            binding.guaranteesDescriptionTv.text = adsResponse.advertisement.guaranteesDesc
        } else binding.guaranteesAnswerTv.text = "لا"
        if (adsResponse.advertisement.saudiBuildingCode) {
            binding.saudiBuildingCodeAnswerTv.text = "نعم"
        } else binding.saudiBuildingCodeAnswerTv.text = "لا"

        if (adsResponse.advertisement.owner.type == "OFFICE") {
            binding.ratingLinear.visibility = View.VISIBLE
            binding.estateRatingValueRb.rating = adsResponse.advertisement.owner.rate.toFloat()
            binding.ratingNoTv.text = "(${adsResponse.advertisement.owner!!.rateNumbers})"
        }
        binding.headerRl.bringToFront()
        featureAdapter.differ.submitList(featuresList)
        propertyAdapter.differ.submitList(propertiesList)

        if (adsResponse.advertisement.category.type == "BUILDING") {
            if (!adsResponse.advertisement.units.isNullOrEmpty()) {
                binding.ifEstateIsBuildingLinear.visibility = View.VISIBLE
                unitsInBuildingInEstateDetailsAdapter.differ.submitList(adsResponse.advertisement.units)
//                binding.priceLinear.visibility = View.GONE
//                binding.depositLinear.visibility = View.GONE
                binding.buildOrPlanedOrLandNumberLinear.visibility = View.VISIBLE
                binding.buildOrPlanedOrLandNumberTitleTv.text = "رقم العمارة"
                binding.buildOrPlanedOrLandNumberTv.text =
                    adsResponse.advertisement.unitNumber.toString()
            } else binding.ifEstateIsBuildingLinear.visibility = View.GONE
        }

        if (adsResponse.advertisement.category.type == "PLANNED") {
            if (!adsResponse.advertisement.lands.isNullOrEmpty()) {
//                binding.priceLinear.visibility = View.GONE
//                binding.depositLinear.visibility = View.GONE
                binding.ifEstateIsPlannedLinear.visibility = View.VISIBLE
                val landNewFormat: ArrayList<UnitX> = ArrayList()
                adsResponse.advertisement.lands!!.forEach {
                    landNewFormat.add(
                        UnitX(
                            id = it.id,
                            status = it.status,
                            title = it.title,
                            unitNumber = it.unitNumber
                        )
                    )
                }
                unitsOrLndInBuildingOrPlaneAdapter.differ.submitList(landNewFormat)
            } else binding.ifEstateIsPlannedLinear.visibility = View.GONE
            binding.buildOrPlanedOrLandNumberLinear.visibility = View.VISIBLE
            binding.buildOrPlanedOrLandNumberTitleTv.text = "رقم المخطط"
            binding.buildOrPlanedOrLandNumberTv.text =
                adsResponse.advertisement.unitNumber.toString()
            if (adsResponse.advertisement.contractType == "SALE")
                binding.priceTitleTv.text = "سعر المتر داخل المخطط : "
            binding.startSaleDateLinear.visibility = View.VISIBLE
            binding.startSaleDateTv.text =
                dateAndTimeReformatToDateOnly(adsResponse.advertisement.startSaleDate.toString()).trim()
        }
        if (adsResponse.advertisement.category.type == "LAND") {
            binding.buildOrPlanedOrLandNumberLinear.visibility = View.VISIBLE
            binding.buildOrPlanedOrLandNumberTitleTv.text = "رقم الأرض"
            binding.buildOrPlanedOrLandNumberTv.text =
                adsResponse.advertisement.unitNumber.toString()
            if (adsResponse.advertisement.contractType == "SALE")
                binding.priceTitleTv.text = "سعر المتر : "
        }

        val estateLocationOnTheMap = LatLng(
            adsResponse.advertisement.location.coordinates[1],
            adsResponse.advertisement.location.coordinates[0]
        )
        mMap.addMarker(
            MarkerOptions().position(estateLocationOnTheMap).title(adsResponse.advertisement.title)
        )
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(estateLocationOnTheMap))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(estateLocationOnTheMap, 9f))
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.setOnMapClickListener {
            val intent = Intent(this@EstateDetailsActivity, EstateLocationOnMapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("adsResponse", adsResponse)
            startActivity(intent)
        }
        binding.theAveragePriceTv.text =
            GeneralFunctions.reformatPrice((adsResponse.advertisement.theAverage.toString()).toFloat())
        if (adsResponse.advertisement.priceAvg == "DOWN")
            binding.theAveragePriceIv.setBackgroundResource(R.drawable.ic_arrow_downward_white)
        else
            binding.theAveragePriceIv.setBackgroundResource(R.drawable.ic_arrow_upward_white)

        binding.ifNotOwnerLinear.setOnClickListener {
            if ((myId == "0") or (token == "")) {
                Toast(this).showCustomToast("لا تملك الصلاحيات", this)
            } else {
                val intent = Intent(this, OfficeOrUserPageActivity::class.java)
                intent.putExtra("owner", adsResponse.advertisement.owner.id)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun createIndicator(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        layoutParams.height = 20
        layoutParams.width = 20
        for (i in indicators.indices) {
            indicators[i] = ImageView(this.baseContext)
            indicators[i]!!
                .setImageDrawable(
                    ContextCompat.getDrawable(
                        this.applicationContext,
                        R.drawable.background_slider_indicator_inactive
                    )
                )
            indicators[i]!!.layoutParams = layoutParams
            binding.layoutSliderIndicators.addView(indicators[i])
            changeIndicator(0)
        }
    }

    private fun changeIndicator(currentPosition: Int) {
        val childCount = binding.layoutSliderIndicators.childCount
        for (i in 0 until childCount) {
            try {
                val imageView = binding.layoutSliderIndicators.getChildAt(i) as ImageView
                if (i == currentPosition) {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            this.applicationContext,
                            R.drawable.background_slider_indecator_active
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            this.applicationContext,
                            R.drawable.background_slider_indicator_inactive
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startShow(count: Int) {
        binding.adsImageViewPager.setCurrentItem(++currentPosition, true)
        val handler = Handler()
        val runnable = Runnable {
            for (i in 0 until count) {
                if (currentPosition == count) {
                    currentPosition = 0
                    changeIndicator(0)
                }
                if (currentPosition == i) {
                    changeIndicator(currentPosition)
                }
            }
            binding.adsImageViewPager.setCurrentItem(++currentPosition, true)
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 3000, 3000)
    }

    private val onConnectError = Emitter.Listener {
        runOnUiThread {
            //   Constant.makeToastMessage(this, "Connection Error")
        }
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread {
            //   Constant.makeToastMessage(this, "Disconnected")
        }
    }

    private var onConnect = Emitter.Listener {
        //  mSocket.emit("getAds", {})
        runOnUiThread {
            //  Constant.makeToastMessage(this, "connected")
        }
    }

    override fun onResume() {
        super.onResume()
        if (mustUpdate) {
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun runSimpleAnimationCounterTimer() {

        val handler = Handler()
        var counter = 0
        handler.post(object : Runnable {
            override fun run() {

                if (counter.mod(2) == 0) {
                    binding.threeDImageNormalTv.visibility = View.VISIBLE
                    binding.threeDImageBoldTv.visibility = View.GONE
                } else {
                    binding.threeDImageNormalTv.visibility = View.GONE
                    binding.threeDImageBoldTv.visibility = View.VISIBLE
                }
                if (counter.mod(5) == 0)
                    animatethreeDRl()
                handler.postDelayed(this, 1000)
                counter++
            }
        })
    }

    private fun animatethreeDRl() {
        Handler().postDelayed({
            binding.threeDImageRl.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.recyclerview_animation
                )
            )
        }, 1500)
    }

    private fun generateSharingLink(
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            link = Uri.parse("https://appsaaei.page.link/advertisement?adsId=$adsId")
            domainUriPrefix = "https://appsaaei.page.link"

            if (!adsResponse.advertisement.imgs.isNullOrEmpty())
                setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setImageUrl(Uri.parse(adsResponse.advertisement.imgs[0]))
                        .build()
                )
            else setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .build()
            )

            androidParameters("com.saaei12092021.office") {
                build()
            }

            iosParameters("com.office.saaeiApp") {
                appStoreId = "1586113986"
            }

            // Finally
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {
            }
        }
    }
}