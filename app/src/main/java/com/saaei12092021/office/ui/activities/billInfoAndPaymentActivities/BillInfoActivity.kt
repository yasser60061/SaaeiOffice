package com.saaei12092021.office.ui.activities.billInfoAndPaymentActivities

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.saaei12092021.office.R
import com.saaei12092021.office.adapters.PropertiesAdapter
import com.saaei12092021.office.databinding.ActivityBillInfoBinding
import com.saaei12092021.office.model.requestModel.AddBookingRequest
import com.saaei12092021.office.model.requestModel.BillInfoRequest
import com.saaei12092021.office.model.responseModel.BillInfoResponse
import com.saaei12092021.office.model.responseModel.BookingCalenderResponse
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.getAdsResponse.Property
import com.saaei12092021.office.model.responseModel.paymentTearms.PaymentsTermsResponse
import com.saaei12092021.office.ui.activities.estateDetailsActivity.EstateDetailsActivity
import com.saaei12092021.office.ui.activities.packagesActivity.BottomSheetForMoreInPackagesList
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BillInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityBillInfoBinding
    lateinit var adsByIdResponse: AdsByIdResponse
    lateinit var bookingCalenderResponse: BookingCalenderResponse
    lateinit var type: String
    lateinit var billType: String // DEPOSIT , PACKAGE , RENT , ADD
    var packageId: Int = 0
    lateinit var durationType: String
    lateinit var billInfoResponse: BillInfoResponse
    lateinit var billInfoAndPaymentViewModel: BillInfoAndPaymentViewModel
    lateinit var paymentsTermsResponse: PaymentsTermsResponse
    var destination_id = "00"

    var startDate = ""
    var endDate = ""
    var isValidBooking = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpLanguageViewAndDirection()
        billInfoAndPaymentViewModel =
            ViewModelProvider(this).get(BillInfoAndPaymentViewModel::class.java)

        type = intent.getStringExtra("type").toString()

        billInfoAndPaymentViewModel.error.observe(this, Observer {
            Toast(this).showCustomToast(it.toString(), this)
            binding.progressBar.visibility = View.GONE
            binding.payProgressBar.visibility = View.GONE
        })

        billInfoAndPaymentViewModel.getPaymentTerms(Constant.getMyLanguage(this))
        billInfoAndPaymentViewModel.getPaymentTermsLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    paymentsTermsResponse = it.data!!
                    var termsTitle = ""
                    var termsContent = ""
                    paymentsTermsResponse.data.forEach { termsItem ->
                        if ((type == "1") and (termsItem.type == "PACKAGE")) {
                            termsTitle = "شروط واحكام الاشتراك في باقة"
                            termsContent = termsItem.terms_ar
                        }
                        if ((type == "3") and (termsItem.type == "DEPOSIT")) {
                            termsTitle = "شروط واحكام دفع عربون"
                            termsContent = termsItem.terms_ar
                        }
                        if ((type == "4") and (termsItem.type == "BOOKING")) {
                            termsTitle = "شروط واحكام حجز عقار"
                            termsContent = termsItem.terms_ar
                        }
                    }
                    binding.termsTv.text = termsTitle
                    binding.termsTv.setOnClickListener {
                        // we used the sam bottomSheet for package in this cas
                        BottomSheetForMoreInPackagesList(
                            this,
                            termsTitle,
                            termsContent,
                            "TERMS"
                        )
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        })

        binding.backRl.setOnClickListener {
            finish()
        }

        if (type == "1") {
            packageId = Integer.parseInt(intent.getStringExtra("packageId").toString())
            durationType = intent.getStringExtra("durationType").toString()
            binding.packageInfoLinear.visibility = View.VISIBLE
            binding.packageNameTv.text = intent.getStringExtra("packageName")
            binding.packagePriceTv.text = intent.getStringExtra("cost")

            if (durationType == "YEARLY")
                binding.packagePeriodTv.text = "لمدة سنة"

            if (durationType == "MONTHLY")
                binding.packagePeriodTv.text = "لمدة شهر"

            billInfoAndPaymentViewModel.getBillInfo(
                BillInfoRequest(
                    package_1 = packageId,
                    billType = "PACKAGE",
                    durationType = durationType,
                )
            )
        }

        if (type == "3") {
            binding.adsLinear.visibility = View.VISIBLE
            adsByIdResponse = intent.getSerializableExtra("adsByIdResponse") as AdsByIdResponse
            displayAdsInfoInCard()
            billInfoAndPaymentViewModel.getBillInfo(
                BillInfoRequest(
                    billType = "DEPOSIT",
                    ads = adsByIdResponse.advertisement.id
                )
            )
            checkOfficeMarketPlaceToGetDestinationId()
        }

        if (type == "4") {
            adsByIdResponse = intent.getSerializableExtra("adsByIdResponse") as AdsByIdResponse
            bookingCalenderResponse =
                intent.getSerializableExtra("bookingCalenderResponse") as BookingCalenderResponse
            binding.adsLinear.visibility = View.VISIBLE
            displayAdsInfoInCard()
            binding.chooseRentTypeLinear.visibility = View.VISIBLE
            binding.rentInfoLinear.visibility = View.VISIBLE
            displayCalenderToDetermineTheRentDay()
            binding.rentCalenderTv.setOnClickListener {
                displayCalenderToDetermineTheRentDay()
            }
            binding.mainLinear.setOnClickListener {
                if (startDate == "")
                    Toast(this).showCustomToast("اختر ايام الحجز أولا", this)
            }
            binding.progressBar.visibility = View.GONE

            checkOfficeMarketPlaceToGetDestinationId()
        }

        billInfoAndPaymentViewModel.getBillInfoLive.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    billInfoResponse = it.data!!
                    binding.resultLinear.visibility = View.VISIBLE
                    binding.termsTv.paintFlags =
                        binding.termsTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    if (type == "1") {
                        binding.packageLinear.visibility = View.VISIBLE
                        binding.packageSubscribeValueTv.text = billInfoResponse.cost.toString()
                    }
                    if (type == "3") {
                        binding.depositeLinear.visibility = View.VISIBLE
                        binding.depositValueTv.text = billInfoResponse.cost.toString()
                    }
                    if (type == "4") {
                        binding.packageLinear.visibility = View.VISIBLE
                        binding.packageSubscribeTitleTv.text =
                            "سعر الحجز لمجموع الايام قبل الضريبة :"
                        binding.packageSubscribeValueTv.text = billInfoResponse.cost.toString()
                    }
                    binding.taxValueTv.text = billInfoResponse.tax.toString()
                    binding.totalValueTv.text = billInfoResponse.total.toString()

                    binding.payBtn.setOnClickListener {
                        if(((type == "3") or (type == "4")) and (destination_id == "00"))
                            Toast(this).showCustomToast("انتظر تحميل البيانات" , this)
                        else goToPaymentActivity()
                    }
                    binding.visaRl.setOnClickListener {
                        if(((type == "3") or (type == "4")) and (destination_id == "00"))
                            Toast(this).showCustomToast("انتظر تحميل البيانات" , this)
                        else goToPaymentActivity()
                    }

                    binding.madaRl.setOnClickListener {
                        if(((type == "3") or (type == "4")) and (destination_id == "00"))
                            Toast(this).showCustomToast("انتظر تحميل البيانات" , this)
                        else goToPaymentActivity()
                    }

                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }

    private fun checkOfficeMarketPlaceToGetDestinationId() {
        billInfoAndPaymentViewModel.checkOfficeMarketPlace(
            token = Constant.getToken(this),
            officeId = adsByIdResponse.advertisement.owner.id.toString()
        )

        billInfoAndPaymentViewModel.checkMarketPlaceLive.observe(this , Observer {
            when(it){
                is Resource.Loading -> {}
                is Resource.Error -> {}
                is Resource.Success -> {
                    if (it.data!!.isMarketPlace)
                    destination_id = it.data.destination_id
                    else destination_id = "0"
                }
            }
        })
    }

    private fun displayCalenderToDetermineTheRentDay() {

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(
                    DateValidatorPointForward.now()
                )
        val testDate1 = "2022-05-22T02:06:58.147Z"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ISO_INSTANT
            val listValidators = ArrayList<CalendarConstraints.DateValidator>()
            listValidators.add(DateValidatorPointForward.now())
            bookingCalenderResponse.bookingDays.forEach { bookingDate ->
                val reformatDate = bookingDate.replace("T00:00:00.000Z", "T02:06:58.147Z")
                val parsed1 =
                    ZonedDateTime.parse(reformatDate, formatter.withZone(ZoneId.of("UTC")))
                val parsed2 = parsed1.minusDays(1)
                // parsed.toLocalDateTime() // may use it in other case
                val timeInMilliseconds1 = parsed1.toInstant().toEpochMilli()
                val timeInMilliseconds2 = parsed2.toInstant().toEpochMilli()
                listValidators.add(object : CalendarConstraints.DateValidator {
                    override fun isValid(date: Long): Boolean {
                        var isValidDays = false
                        isValidDays =
                            isValidDays || !(timeInMilliseconds2 >= date || timeInMilliseconds1 <= date)
                        return !isValidDays
                    }

                    override fun writeToParcel(p0: Parcel?, p1: Int) {}
                    override fun describeContents(): Int {
                        return 0
                    }
                })
            }

//            listValidators.add(DateValidatorPointBackward.before(timeInMilliseconds2))
//            listValidators.add(DateValidatorPointForward.from(timeInMilliseconds1))

            val validators = CompositeDateValidator.allOf(listValidators)
            constraintsBuilder.setValidator(validators)

            val datePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("اختر تاريخ بدء الايجار ثم تاريخ انتهاء الايجار بالترتيب")
//                    .setSelection(
//                        Pair(
//                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
//                            MaterialDatePicker.todayInUtcMilliseconds()
//                        )
//                    )
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen)
                    .build()

            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener {
                bookingCalenderResponse.bookingDays.forEach { bookingDate ->
                    val reformatDate = bookingDate.replace("T00:00:00.000Z", "T02:06:58.147Z")
                    val df1 = SimpleDateFormat("yyyy-M-dd" , Locale.ENGLISH)
                    // val date1: Date = df1.parse(reformatDate)
                    val parsed1 =
                        ZonedDateTime.parse(reformatDate, formatter.withZone(ZoneId.of("UTC")))
                    // parsed.toLocalDateTime() // may use it in other case
                    val timeInMilliseconds1 = parsed1.toInstant().toEpochMilli()
                    val mDifference1 = it.first - timeInMilliseconds1
                    val mDifference2 = it.second - timeInMilliseconds1
                    val mDifferenceDates1 =
                        mDifference1 / (24 * 60 * 60 * 1000) // Converting milli seconds to dates
                    val mDifferenceDates2 =
                        mDifference2 / (24 * 60 * 60 * 1000) // Converting milli seconds to dates
                    // if (mDifferenceDates1 > 0 and mDifferenceDates2 < 0)
                    Log.d(
                        "bookingDateCompare_",
                        "${df1.format(it.first)}|${df1.format(it.second)}|$reformatDate"
                    )
                    Log.d(
                        "bookingDateCompare__",
                        "$mDifferenceDates1||$mDifferenceDates2"
                    )
                    Log.d(
                        "bookingDateCompare___",
                        ((mDifferenceDates1 < 0) and (mDifferenceDates2 >= 0)).toString()
                    )
                    if (((mDifferenceDates1 < 0) and (mDifferenceDates2 >= 0)))
                        isValidBooking = false

                }
                if (isValidBooking) {
                    val df2 = SimpleDateFormat("yyyy-MM-dd" , Locale.ENGLISH)
                    binding.rentCalenderTv.text = "إيجار يومي"
                    binding.rentStartDateTv.text = df2.format(it.first)
                    binding.rentEndDateTv.text = df2.format(it.second)
                    binding.noOfTheDayTv.text =
                        ((kotlin.math.abs(it.second - it.first)) / (24 * 60 * 60 * 1000) + 1).toString()
                    startDate = df2.format(it.first)
                    endDate = df2.format(it.second)
                    billInfoAndPaymentViewModel.getBillInfo(
                        BillInfoRequest(
                            billType = "RENT",
                            ads = adsByIdResponse.advertisement.id,
                            days = ((kotlin.math.abs(it.second - it.first)) / (24 * 60 * 60 * 1000) + 1).toInt()
                        )
                    )
                } else {
                    Toast(this).showCustomToast(
                        "الفترة المطلوبة للحجز تشمل حجوزات اخرى",
                        this
                    )
                    Toast(this).showCustomToast(
                        "اختر فترة مناسبة",
                        this
                    )
                    isValidBooking = true
                }
            }
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    private fun displayAdsInfoInCard() {
        binding.apply {
            titleTv.text = adsByIdResponse.advertisement.title
            cityNameTv.text = adsByIdResponse.advertisement.city.cityName
            areaNameTv.text = adsByIdResponse.advertisement.area.areaName
            priceTv.text = adsByIdResponse.advertisement.price.toString()
            descriptionTv.text = adsByIdResponse.advertisement.description

            try {
                Glide.with(this@BillInfoActivity)
                    .load(adsByIdResponse.advertisement.imgs[0])
                    .into(binding.estateTv)
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
            binding.adsLinear.setOnClickListener {
                val intent =
                    Intent(this@BillInfoActivity, EstateDetailsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("adsId", adsByIdResponse.advertisement.id.toString())
                startActivity(intent)
            }
        }
    }

    private fun setUpLanguageViewAndDirection() {
//        val deviceLanguage = Locale.getDefault().getDisplayLanguage()
//        Log.d("deviceLanguage", deviceLanguage)
//          if (deviceLanguage == "العربية") {
        binding.headerRl.layoutDirection = ViewPager.LAYOUT_DIRECTION_RTL
        binding.backIv.rotation = 180F
        // }
    }

    private fun goToPaymentActivity() {
        if (binding.termsCb.isChecked) {
            val intent = Intent(this@BillInfoActivity, PaymentActivity::class.java)
            intent.putExtra("cost", billInfoResponse.cost.toString())
            intent.putExtra("tax", billInfoResponse.tax.toString())
            intent.putExtra("totalCost", billInfoResponse.total.toString())

            if (type == "1") {
                intent.putExtra("type", "1")
                intent.putExtra("packageId", packageId.toString())
                intent.putExtra("packageDuration", durationType)
            }

            if (type == "3") {
                intent.putExtra("type", "3")
                intent.putExtra("adsId", adsByIdResponse.advertisement.id.toString())
                intent.putExtra("destination_id", destination_id)
            }

            if (type == "4") {
                intent.putExtra("type", "4")
                intent.putExtra("adsId", adsByIdResponse.advertisement.id.toString())
                intent.putExtra("destination_id", destination_id)

                billInfoAndPaymentViewModel.addBooking(
                    token = Constant.getToken(this),
                    adsId = adsByIdResponse.advertisement.id.toString(),
                    AddBookingRequest(
                        type = "INSIDE",
                        startDate = startDate,
                        endDate = endDate,
                        durationType = "DAILY"
                    )
                )

                Log.d("startAndEndDateDate", "$startDate||$endDate")

                billInfoAndPaymentViewModel.addBookingLive.observe(this, Observer {
                    when (it) {
                        is Resource.Loading -> {
                            binding.visaRl.isEnabled = false
                            binding.madaRl.isEnabled = false
                            binding.payBtn.isEnabled = false
                            binding.payProgressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            intent.putExtra("bookingId", it.data!!.data.id.toString())
                            startActivity(intent)
                            finish()
                        }
                        is Resource.Error -> {
                            binding.visaRl.isEnabled = true
                            binding.madaRl.isEnabled = true
                            binding.payBtn.isEnabled = true
                            binding.payProgressBar.visibility = View.GONE
                        }
                    }
                })
            } else {
                startActivity(intent)
                finish()
            }
        } else Toast(this).showCustomToast("الرجاء الموافقة على الشروط والأحكام أولا", this)

    }

}