package com.saaei12092021.office.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.FragmentMyProfileBinding
import com.saaei12092021.office.model.requestModel.EnableNotifyInUpdateProfileRequest
import com.saaei12092021.office.model.responseModel.getUserByTokenResponse.GetUserByTokenResponse
import com.saaei12092021.office.ui.UserViewModel
import com.saaei12092021.office.ui.activities.ChangPasswordActivity
import com.saaei12092021.office.ui.activities.CompleteProfileActivity
import com.saaei12092021.office.ui.activities.SignupActivity
import com.saaei12092021.office.ui.activities.SupervisorProfileActivity
import com.saaei12092021.office.ui.activities.addToMarketPlaceActivity.AddToMarketPlaceActivity
import com.saaei12092021.office.ui.activities.checkPasswordActivity.CheckPasswordActivity
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.ui.activities.packagesActivity.PackageActivity
import com.saaei12092021.office.util.*
import com.saaei12092021.office.util.Constant.MY_LANGUAGE
import com.saaei12092021.office.util.Constant.WEB_VIW_URL
import java.text.SimpleDateFormat
import java.util.*


class MyProfileFragment : Fragment() {
    private var _binding: FragmentMyProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var token: String
    lateinit var currentLanguage: String
    lateinit var userViewModel: UserViewModel
    var getUserByTokenResponse: GetUserByTokenResponse? = null
    var mustUpdateUserInfo = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chosenLanguageTv.paintFlags =
            binding.chosenLanguageTv.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG

        (activity as HomeActivity).binding.notificationRl.visibility = View.GONE
        (activity as HomeActivity).binding.backRl.visibility = View.VISIBLE
        (activity as HomeActivity).binding.contentHomeId.mainToolsRl.visibility = View.INVISIBLE
        (activity as HomeActivity).binding.homeTitleTv.text = "ملفي"
        (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility = View.GONE
        token = (activity as HomeActivity).token
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        if (((activity as HomeActivity).myId == "0") or ((activity as HomeActivity).token == "")) {
            binding.notificationRl.visibility = View.GONE
            binding.updateMyProfileRl.visibility = View.GONE
            binding.favoriteRl.visibility = View.GONE
            binding.changePhoneNumberRl.visibility = View.GONE
            binding.changePasswordRl.visibility = View.GONE
            binding.packageLinear.visibility = View.GONE
            binding.upgradeThePackageLinear.visibility = View.GONE
            binding.logoutBtn.visibility = View.GONE
            binding.technicalSupportRl.visibility = View.GONE
            binding.addOfficeToMarketPlaceRl.visibility = View.GONE
            binding.sharMyAdsPageRl.visibility = View.GONE
        } else {
            if ((activity as HomeActivity).getUserResponse != null) {
                if ((activity as HomeActivity).getUserResponse!!.type == "SUPERVISOR") {
                    binding.addOfficeToMarketPlaceRl.visibility = View.GONE
                    binding.upgradeThePackageLinear.visibility = View.GONE
                }
                if ((activity as HomeActivity).getUserResponse!!.hasAdsPackage) {
                    binding.packageNameTv.text =
                        (activity as HomeActivity).getUserResponse!!.adsPackage.name
                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val dateString =
                        simpleDateFormat.format((activity as HomeActivity).getUserResponse!!.packageEndDateMillSec)
                    binding.dateOfPackageExpiredTv.text = dateString
                    binding.logoutBtn.visibility = View.VISIBLE
                    binding.availableAdsTv.text =
                        (activity as HomeActivity).getUserResponse!!.availableAds.toString()
                } else {
                    binding.packageLinear.visibility = View.GONE
                }
            }

            userViewModel.getUserByToken(token)
            userViewModel.userByTokenLive.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        getUserByTokenResponse = it.data!!
                        if (getUserByTokenResponse!!.user.marketPlace != null) {
                        if (getUserByTokenResponse!!.user.isMarketPlace){
                            binding.marketPlaceTitleTv.text = "تم اضافة مكتبك الى المتجر"
                            binding.marketPlaceTitleTv.setTextColor(Color.parseColor("#FF025C43"))
                            binding.marketPlaceStatusTv.visibility = View.GONE
                        }

                        else if (getUserByTokenResponse!!.user.marketPlace?.status != null)
                            binding.marketPlaceStatusTv.text =
                                GeneralFunctions.translateEnumToWordInMarketPlace(getUserByTokenResponse!!.user.marketPlace?.status!!)
                        else binding.marketPlaceStatusTv.visibility = View.GONE
                        Log.d(
                            "enableNotifStatus",
                            getUserByTokenResponse!!.user.enableNotif.toString()
                        )
                    }
                        binding.notificationOnOrOffSwitch.isChecked = getUserByTokenResponse!!.user.enableNotif
                        binding.notificationOnOrOffSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                userViewModel.updateNotifyStatus(
                                    enableNotifyInUpdateProfileRequest = EnableNotifyInUpdateProfileRequest(true),
                                    token = (activity as HomeActivity).token,
                                    userId = (activity as HomeActivity).userId,
                                    myLang = (activity as HomeActivity).myLang
                                )
                            } else {
                                userViewModel.updateNotifyStatus(
                                    enableNotifyInUpdateProfileRequest = EnableNotifyInUpdateProfileRequest(false),
                                    token = (activity as HomeActivity).token,
                                    userId = (activity as HomeActivity).userId,
                                    myLang = (activity as HomeActivity).myLang
                                )
                            }
                        }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            })
        }

        currentLanguage = Constant.getMyLanguage(requireContext())

        if (currentLanguage == "ar")
            binding.chosenLanguageTv.text = "العربية"

        if (currentLanguage == "en")
            binding.chosenLanguageTv.text = "English"


        binding.changePasswordRl.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(activity, ChangPasswordActivity::class.java)
                intent.putExtra("token", token)
                startActivity(intent)
            }
        }

        binding.sharMyAdsPageRl.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                generateSharingLink { shortLinkItem ->
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Saaei")
                    val officeName = (activity as HomeActivity).getUserResponse!!.fullname
                    var shareMessage = " قائمة عقارات\n $officeName \n"
                    shareMessage += shortLinkItem
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "مشاركة مع .."))

                }
            }
        }

        binding.languageRl.setOnClickListener {
            Constant.makeToastMessage(requireContext(), "اللغة المعتمدة الآن هي اللغة العربية")
            //  displayLanguageDialog()
        }

        binding.contactUsRl.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_contactUsFragment)
        }

        binding.updateMyProfileRl.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                if ((activity as HomeActivity).getUserResponse != null) {
                    if ((activity as HomeActivity).getUserResponse!!.type == "OFFICE") {
                        val intent = Intent(requireActivity(), CompleteProfileActivity::class.java)
                        intent.putExtra(
                            "getUserResponse",
                            (activity as HomeActivity).getUserResponse
                        )
                        startActivity(intent)
                    }
                    if ((activity as HomeActivity).getUserResponse!!.type == "SUPERVISOR") {
                        val intent =
                            Intent(requireActivity(), SupervisorProfileActivity::class.java)
                        intent.putExtra(
                            "getUserResponse",
                            (activity as HomeActivity).getUserResponse
                        )
                        startActivity(intent)
                    }
                }
            }
        }

        binding.upgradeThePackageLinear.setOnClickListener {
            val intent = Intent(requireActivity(), PackageActivity::class.java)
            startActivity(intent)
        }

        binding.technicalSupportRl.setOnClickListener {
            //  Constant.makeToastMessage(requireActivity(), "سيتم تفعيلها قريبا")
            FreshchatConfig(
                "6171ee20-b94c-4df7-bbfe-2c6d4f3656e6",
                "34f55908-9b0f-4812-8815-e15b4a75c70a"
            ).apply {
                domain = "msdk.freshchat.com"
                isCameraCaptureEnabled = true
                isGallerySelectionEnabled = true
                isResponseExpectationEnabled = true
                Freshchat.getInstance(requireContext()).init(this)

                val freshchatUser = Freshchat.getInstance(requireContext()).user

                freshchatUser.firstName = (activity as HomeActivity).getUserResponse!!.fullname
                freshchatUser.lastName = (activity as HomeActivity).getUserResponse!!.fullname
                freshchatUser.email = (activity as HomeActivity).getUserResponse!!.email
                freshchatUser.setPhone(
                    "+966",
                    GeneralFunctions.deleteCountryCodeFromPhoneAndReformat((activity as HomeActivity).getUserResponse!!.phone)
                )
                Freshchat.getInstance(requireContext()).user = freshchatUser
                Log.d("suffexPhon", freshchatUser.toString())
//                val userMeta: MutableMap<String, String> = HashMap()
//                userMeta["userLoginType"] = "Facebook"
//                userMeta["city"] = "SpringField"
//                userMeta["age"] = "22"
//                userMeta["userType"] = "premium"
//                userMeta["numTransactions"] = "5"
//                userMeta["usedWishlistFeature"] = "yes"
//                Freshchat.getInstance(requireContext()).setUserProperties(userMeta)
//
//                val eventName = "Visited Order Details page"
//                val properties: HashMap<String, Any> = HashMap()
//                properties["Order Id"] = 3223232332
//                properties["Order Date"] = "24 Jan 2020"
//                properties["Order Status "] = "In-Transit"
//                Freshchat.trackEvent(requireContext(), eventName, properties)

                Freshchat.showConversations(requireContext())
                Freshchat.notifyAppLocaleChange(requireActivity())

            }
        }

        binding.favoriteRl.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                findNavController().navigate(R.id.action_myProfileFragment_to_favoriteFragment)
            }
        }

        binding.aboutUsRl.setOnClickListener {
            //  findNavController().navigate(R.id.action_myProfileFragment_to_aboutUsAndTermsFragment)
            findNavController().navigate(R.id.action_myProfileFragment_to_FAQFragment)
            (activity as HomeActivity).currentWebViewLinke = WEB_VIW_URL + "about?view=mobile"
            (activity as HomeActivity).currentWebViewPageName = "عن التطبيق"
        }

        binding.noteFaqRel.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_FAQFragment)
            (activity as HomeActivity).currentWebViewLinke = WEB_VIW_URL + "faq?view=mobile"
            (activity as HomeActivity).currentWebViewPageName = "الأسئلة الشائعة"
        }

        binding.estateNewsRel.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_FAQFragment)
            (activity as HomeActivity).currentWebViewLinke =
                WEB_VIW_URL + "news/list?view=mobile"
            (activity as HomeActivity).currentWebViewPageName = "أخبار العقار"
        }

        binding.changePhoneNumberRl.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(activity, CheckPasswordActivity::class.java)
                startActivity(intent)
            }
        }

        binding.addOfficeToMarketPlaceRl.setOnClickListener {
            if ((activity as HomeActivity).myId == "0") {
                val intent = Intent(activity, SignupActivity::class.java)
                startActivity(intent)
            } else {
                if (getUserByTokenResponse!!.user.marketPlace?.status != null) {
                    if (getUserByTokenResponse == null)
                        Toast(requireActivity()).showCustomToast(
                            "إنتظر تحميل البيانات",
                            requireActivity()
                        )
                    else {
                        showDialogForModify()
                    }
                } else {
                    if (getUserByTokenResponse == null)
                        Toast(requireActivity()).showCustomToast(
                            "إنتظر تحميل البيانات",
                            requireActivity()
                        ) else {
                        mustUpdateUserInfo = true
                        val intent = Intent(activity, AddToMarketPlaceActivity::class.java)
                        intent.putExtra("getUserByTokenResponse", getUserByTokenResponse)
                        intent.putExtra("typeOfMarketPlaceRequest", "ADD")
                        startActivity(intent)
                        getUserByTokenResponse = null
                    }
                }
            }
        }

        binding.logoutBtn.setOnClickListener {
            Constant.editor(requireContext()).apply {
                clear()
                apply()
                val intent = Intent(requireActivity(), SignupActivity::class.java)
                startActivity(intent)
                (activity as HomeActivity).finish()
            }
        }
    }

    private fun showDialogForModify() {
        val dialog = Dialog(requireContext(), R.style.DialogStyle)
        dialog.setContentView(R.layout.modify_market_place_dialog_item)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_shape_white)
        val goMessageTv = dialog.findViewById<TextView>(R.id.market_place_status_message_tv)
        val goToModifyTv = dialog.findViewById<TextView>(R.id.go_to_modify_tv)
        dialog.show()
        val intent = Intent(activity, AddToMarketPlaceActivity::class.java)

        when (getUserByTokenResponse!!.user.marketPlace?.status) {
            "PENDING" -> {
                goMessageTv.text = "طلبك في الإنتظار يرجى الانتظار"
                goToModifyTv.text = "موافق"
                goToModifyTv.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "REFUSED" -> {
                goMessageTv.text = "تم رفض طلبك هل تريد التقدم من جديد ؟"
                intent.putExtra("typeOfMarketPlaceRequest", "ADD")
                goToModifyTv.text = "تقديم طلب جديد"
                goToModifyTv.setOnClickListener {
                    mustUpdateUserInfo = true
                    startActivity(intent)
                    dialog.dismiss()
                    getUserByTokenResponse = null
                }
            }
            "FIXING-PHASE" -> {
                goMessageTv.text = " طلبك يحتاج لتعديل , هل تريد التعديل عليه الان ؟"
                intent.putExtra("typeOfMarketPlaceRequest", "UPDATE")
                intent.putExtra("getUserByTokenResponse", getUserByTokenResponse)
                goToModifyTv.text = "التعديل الان"
                goToModifyTv.setOnClickListener {
                    mustUpdateUserInfo = true
                    startActivity(intent)
                    dialog.dismiss()
                    getUserByTokenResponse = null
                }}
                "ACCEPTED" -> {
                    goMessageTv.text = "لديك طلب مفعل , هل تريد التقدم من جديد ؟"
                    intent.putExtra("typeOfMarketPlaceRequest", "ADD")
                    goToModifyTv.text = "تقديم طلب جديد"
                    goToModifyTv.setOnClickListener {
                        mustUpdateUserInfo = true
                        startActivity(intent)
                        dialog.dismiss()
                        getUserByTokenResponse = null
                    }
            }
            else -> dialog.dismiss()
        }
    }

    private fun displayLanguageDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val login_layout: View = inflater.inflate(R.layout.language_popup_item, null)
        val englishRb = login_layout.findViewById<RadioButton>(R.id.language_english_rb_item)
        val arabicRb = login_layout.findViewById<RadioButton>(R.id.language_arabic_rb_item)
        val applyBtn = login_layout.findViewById<Button>(R.id.language_apply_btn_item)
        dialog.setView(login_layout)

        if (currentLanguage == "ar")
            arabicRb.isChecked = true
        if (currentLanguage == "en")
            englishRb.isChecked = true

        applyBtn.setOnClickListener {
            if (englishRb.isChecked) {
                Constant.editor(requireContext()).apply {
                    putString(MY_LANGUAGE, "en")
                    apply()
                    setLocale("en")
                }

            } else if (arabicRb.isChecked) {
                Constant.editor(requireContext()).apply {
                    putString(MY_LANGUAGE, "ar")
                    apply()
                    setLocale("ar")
                }
            } else Toast.makeText(
                requireActivity(),
                R.string.choose_the_language,
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.show()
    }

    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLayoutDirection(myLocale)
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(activity, HomeActivity::class.java)
        refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        requireActivity().finish()
        startActivity(refresh)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (mustUpdateUserInfo) {
            userViewModel.getUserByToken(token)
            mustUpdateUserInfo = false
        }
    }

    private fun generateSharingLink(
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            // What is this link parameter? You will get to know when we will actually use this function.
            val myOfficeId = (activity as HomeActivity).myId
            link = Uri.parse("https://api.saaei.co/office?officeId=$myOfficeId")

            // [domainUriPrefix] will be the domain name you added when setting up Dynamic Links at Firebase Console.
            // You can find it in the Dynamic Links dashboard.
            domainUriPrefix = "https://appsaaei.page.link"

            // Pass your preview Image Link here;

            if (!(activity as HomeActivity).getUserResponse!!.img.isNullOrEmpty())
            setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setImageUrl(Uri.parse((activity as HomeActivity).getUserResponse!!.img))
                    .build()
            ) else setSocialMetaTagParameters(
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
                // This lambda will be triggered when short link generation is successful

                // Retrieve the newly created dynamic link so that we can use it further for sharing via Intent.
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {
                // This lambda will be triggered when short link generation failed due to an exception

                // Handle
            }
        }
    }

}