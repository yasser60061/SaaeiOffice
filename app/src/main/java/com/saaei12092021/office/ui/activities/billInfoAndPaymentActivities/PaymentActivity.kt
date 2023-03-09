package com.saaei12092021.office.ui.activities.billInfoAndPaymentActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.saaei12092021.office.databinding.ActivityPaymentBinding
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.User
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.Constant.BASE_URL
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.GeneralFunctions.deleteCountryCodeFromPhoneAndReformat
import com.saaei12092021.office.util.GeneralFunctions.getUserIdIfSupervisorOrNot
import com.saaei12092021.office.util.showCustomToast
import company.tap.gosellapi.GoSellSDK
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.Authorize
import company.tap.gosellapi.internal.api.models.Charge
import company.tap.gosellapi.internal.api.models.PhoneNumber
import company.tap.gosellapi.internal.api.models.Token
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.enums.CardType
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.CardsList
import company.tap.gosellapi.open.models.Customer
import company.tap.gosellapi.open.models.Customer.CustomerBuilder
import company.tap.gosellapi.open.models.Receipt
import company.tap.gosellapi.open.models.TapCurrency
import org.json.JSONObject
import java.math.BigDecimal

class PaymentActivity : AppCompatActivity(), SessionDelegate {

    lateinit var binding: ActivityPaymentBinding
    var sdkSession: SDKSession? = null
    private val metaDataHashMap: HashMap<String, String> = HashMap()
    lateinit var type: String
    lateinit var myId: String
    lateinit var packageId: String
    lateinit var packageDuration: String
    lateinit var cost: String
    lateinit var tax: String
    lateinit var totalCost: String
    lateinit var adsId: String
    lateinit var destination_id : String

    //  lateinit var paymentViewModel: PaymentViewModel
    var getUserResponse: GetUserResponse? = null
    var phoneNumberSuffix = ""

    // for Booking use
    var startDate = ""
    var endDate = ""
    var durationType = ""
    var bookingId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myId = getUserIdIfSupervisorOrNot(this)
        type = intent.getStringExtra("type").toString()
        cost = intent.getStringExtra("cost").toString()
        tax = intent.getStringExtra("tax").toString()
        totalCost = intent.getStringExtra("totalCost").toString()

        getStoreUserDataAndDisplayIt()

        when (type) {
            "1" -> {
                packageId = intent.getStringExtra("packageId").toString()
                packageDuration = intent.getStringExtra("packageDuration").toString()
            }
            "3" -> {
                adsId = intent.getStringExtra("adsId").toString()
                destination_id = intent.getStringExtra("destination_id").toString()
            }
            "4" -> {
                adsId = intent.getStringExtra("adsId").toString()
                bookingId = intent.getStringExtra("bookingId").toString()
                destination_id = intent.getStringExtra("destination_id").toString()
            }
        }

        startSDK()

    }

    /**
     * Integrating SDK.
     */
    private fun startSDK() {
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        configureApp()

        /**
         * Optional step
         * Here you can configure your app theme (Look and Feel).
         */
        //    configureSDKThemeObject()

        /**
         * Required step.
         * Configure SDK Session with all required data.
         */
        configureSDKSession()

        /**
         * Required step.
         * Choose between different SDK modes
         */
        configureSDKMode()

        /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
        //     initPayButton()
    }

    private fun configureApp() {
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */

        if (BASE_URL == "https://api.saaei.co/")
            GoSellSDK.init(this, "sk_live_eOHxsZmSu9tWa4AwM7BD0L8Q", "com.saaei12092021.office")
        else if (BASE_URL == "https://saaei-api-oci.algorithms.ws/")
            GoSellSDK.init(this, "sk_test_w2UJdsmqgK7WPonECR5pNLuB", "com.saaei12092021.office")

        // GoSellSDK.setLocale("en")//  if you don't pass locale then default locale EN will be used // i so in arabic always
    }

    private fun configureSDKSession() {

        metaDataHashMap["type"] = type
        metaDataHashMap["cost"] = cost
        metaDataHashMap["tax"] = tax
        metaDataHashMap["totalCost"] = totalCost
        metaDataHashMap["client"] = myId

        when (type) {
            ("1") -> {
                metaDataHashMap["description"] = "اشتراك في باقة"
                metaDataHashMap["packageId"] = packageId
                metaDataHashMap["packageDuration"] = packageDuration
            }
            "3" -> {
                metaDataHashMap["adsId"] = adsId
                if (destination_id != "0")
                metaDataHashMap["destination_id"] = destination_id
            }
            "4" -> {
                metaDataHashMap["adsId"] = adsId
                metaDataHashMap["bookingId"] = bookingId
                if (destination_id != "0")
                metaDataHashMap["destination_id"] = destination_id
            }
        }

        // Instantiate SDK Session
        //   if (sdkSession!! == null)

        sdkSession = SDKSession() //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession!!.addSessionDelegate(this) //** Required **

        // initiate PaymentDataSource
        sdkSession!!.instantiatePaymentDataSource() //** Required **

        // set transaction currency associated to your account
        sdkSession!!.setTransactionCurrency(TapCurrency("SAR")) //** Required **

        sdkSession!!.transactionMode = TransactionMode.PURCHASE//** Required **

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession!!.setCustomer(customer) //** Required **
        Log.d("getCustomer", customer.toString())
        Log.d("metaDataHashMap", "" + metaDataHashMap)

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession!!.setAmount(BigDecimal(totalCost)) //** Required **

        // Set Payment Items array list
        sdkSession!!.setPaymentItems(ArrayList()) // ** Optional ** you can pass empty array list

        sdkSession!!.setPaymentType("CARD");   //** Merchant can pass paymentType

        // Set Taxes array list
        sdkSession!!.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list

        // Set Shipping array list
        sdkSession!!.setShipping(ArrayList()) // ** Optional ** you can pass empty array list

        // Post URL
        sdkSession!!.setPostURL(BASE_URL + "api/v1/payment") // ** Optional **
        //  sdkSession!!.setPostURL("https://api.saaei.co/api/v1/payment") // ** Optional **

        // Payment Description
        sdkSession!!.setPaymentDescription("") //** Optional **

        // Payment Extra Info
        sdkSession!!.setPaymentMetadata(metaDataHashMap) // ** Optional ** you can pass empty array hash map

        // Payment Reference
        sdkSession!!.setPaymentReference(null) // ** Optional ** you can pass null

        // Payment Statement Descriptor
        sdkSession!!.setPaymentStatementDescriptor("") // ** Optional **

        // Enable or Disable Saving Card
        sdkSession!!.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean

        // Enable or Disable 3DSecure
        sdkSession!!.isRequires3DSecure(true)

        //Set Receipt Settings [SMS - Email ]
        sdkSession!!.setReceiptSettings(
            Receipt(
                false,
                false
            )
        ) // ** Optional ** you can pass Receipt object or null

        // Set Authorize Action
        sdkSession!!.setAuthorizeAction(null) // ** Optional ** you can pass AuthorizeAction object or null
        sdkSession!!.setDestination(null) // ** Optional ** you can pass Destinations object or null
        sdkSession!!.setMerchantID(null) // ** Optional ** you can pass merchant id or null
        sdkSession!!.setCardType(CardType.CREDIT) // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.

        sdkSession!!.setDefaultCardHolderName("") // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
        sdkSession!!.isUserAllowedToEnableCardHolderName(false) // ** Optional ** you can enable/ disable  default CardHolderName .

        /**
         * Use this method where ever you want to show TAP SDK Main Screen.
         * This method must be called after you configured SDK as above
         * This method will be used in case of you are not using TAP PayButton in your activity.
         */

        sdkSession!!.start(this)
    }

    private fun configureSDKMode() {

        /**
         * You have to choose only one Mode of the following modes:
         * Note:-
         *      - In case of using PayButton, then don't call sdkSession!!.start(this) because the SDK will start when user clicks the tap pay button.
         */
        /**
         *  Start using  SDK features through SDK main activity (With Tap CARD FORM)
         */
        //   StartSDKUI()
    }

    override fun paymentSucceed(charge: Charge) {
        val jsonString = Gson().toJson(charge)
        val jsonData: JSONObject = JSONObject(jsonString)
        Log.d("y-payment", "paymentSucceed--$jsonData")
        var message: String = ""
        when (type) {
            "1" -> message = "تم حجز الباقة بنجاح"
            "3" -> message = "تم دفع العربون بنجاح"
            "4" -> message = "تمت عملية الحجز بنجاح"
        }
        Toast(this).showCustomToast(message, this)
        val intent = Intent(this@PaymentActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun paymentFailed(charge: Charge?) {
        val jsonString = Gson().toJson(charge)
        val jsonData: JSONObject = JSONObject(jsonString)
        Log.d("y-payment", "paymentFailed--$jsonData")
        Toast(this).showCustomToast("العملية فشلت", this)
        finish()
    }

    override fun authorizationSucceed(authorize: Authorize) {
        Log.d("y-payment", authorize.toString())
    }

    override fun authorizationFailed(authorize: Authorize?) {
        Log.d("y-payment", authorize.toString())
    }

    override fun cardSaved(charge: Charge) {
        Log.d("y-payment", charge.toString())
    }

    override fun cardSavingFailed(charge: Charge) {
        Log.d("y-payment", "cardSavingFailed" + charge.toString())
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        Log.d("y-payment", token.toString())
    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {
        Log.d("y-payment", saveCardEnabled.toString())
    }

    override fun savedCardsList(cardsList: CardsList) {
        Log.d("y-payment", cardsList.toString())
    }

    override fun sdkError(goSellError: GoSellError?) {
        Log.d("y-payment", "goSellError")
    }

    override fun sessionIsStarting() {
        Log.d("y-payment", "sessionIsStarting")
    }

    override fun sessionHasStarted() {
        Log.d("y-payment", "sessionHasStarted")
        binding.progressBar.visibility = View.GONE
    }

    override fun sessionCancelled() {
        Log.d("y-payment", "sessionCancelled")
        finish()
    }

    override fun sessionFailedToStart() {
        Log.d("y-payment", "sessionFailedToStart")
        binding.progressBar.visibility = View.GONE
        Constant.makeToastMessage(this, "حدث خطا غير متوقع")
        finish()
    }

    override fun invalidCardDetails() {
        Log.d("y-payment", "invalidCardDetails")
    }

    override fun backendUnknownError(message: String?) {
        Log.d("y-payment", message.toString())
        binding.progressBar.visibility = View.GONE
        Toast(this).showCustomToast(message.toString(), this)
        finish()
    }

    override fun invalidTransactionMode() {
        Log.d("y-payment", "invalidTransactionMode")
    }

    override fun invalidCustomerID() {
        Log.d("y-payment", "invalidCustomerID")
    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        Log.d("y-payment", saveCardEnabled.toString())
    }

    private val customer: Customer
        get() {
            val phoneNumber = PhoneNumber("966", phoneNumberSuffix)
            //   Toast(this).showCustomToast(phoneNumber.number, this)
            return CustomerBuilder(null).email(getUserResponse!!.email)
                .firstName(getUserResponse!!.fullname)
                .lastName(null)
                .phone(phoneNumber)
                .middleName(null).build()
        }

    private fun getStoreUserDataAndDisplayIt() {
        val userByIdData = Constant.getUserByIdResponse(this)
        if (userByIdData != "") {
            getUserResponse =
                Gson().fromJson(userByIdData, GetUserResponse::class.java) as GetUserResponse
            phoneNumberSuffix = deleteCountryCodeFromPhoneAndReformat(getUserResponse!!.phone)
        }
    }


}
