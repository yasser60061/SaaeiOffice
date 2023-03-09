package com.saaei12092021.office.repository

import com.saaei12092021.office.api.RetrofitInstance
import com.saaei12092021.office.model.requestModel.*
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.AddNewAdsRequest
import com.saaei12092021.office.model.requestModel.rgeaAUTHValidation.RgeaAUTHValidationRequest
import com.saaei12092021.office.model.responseModel.SignUpResponse.SignUpResponse
import com.saaei12092021.office.model.responseModel.SuccessResponse
import com.saaei12092021.office.model.responseModel.UploadFileInChatResponse
import com.saaei12092021.office.model.responseModel.UploadImagesModel
import com.saaei12092021.office.model.responseModel.addNewAdsResponse.AddNewAdsResponse2
import com.saaei12092021.office.model.responseModel.addNewAdsResponse.UploadEstateImagesResponse
import com.saaei12092021.office.model.responseModel.liveSearchResponse.LiveSearchResponse
import com.saaei12092021.office.model.responseModel.marketPlaceResponse.AddToMarketPlaceResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.SignInResponse2
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class Repository {

    suspend fun signInUser(user: SignInRequest) = RetrofitInstance.api.signInUser(user)
    suspend fun getUser(token: String, userId: String) =
        RetrofitInstance.api.getUser(token = "Bearer $token", userId = userId)

    suspend fun getUserByToken(token: String) =
        RetrofitInstance.api.getUserByToken(token = "Bearer $token")

    suspend fun refreshToken(user: SignInRequest, oldToken: String) =
        RetrofitInstance.api.refreshToken(user, "Bearer $oldToken")

    suspend fun verificationUser(verificationUser: VerificationUserRequest) =
        RetrofitInstance.api.verificationUser(verificationUser)

    suspend fun recoverPassword(phoneNumber: ForgetPasswordByPhoneRequest) =
        RetrofitInstance.api.recoverPassword(phoneNumber)

    suspend fun resetPasswordByPhone(
        myLang: String,
        resetPasswordByPhoneRequest: ResetPasswordByPhoneRequest
    ) =
        RetrofitInstance.api.resetPasswordByPhone(
            myLang = myLang,
            phoneNumber = resetPasswordByPhoneRequest
        )

    suspend fun updatePassword(
        token: String,
        myLang: String,
        updatePasswordRequest: UpdatePasswordRequest
    ) =
        RetrofitInstance.api.updatePassword(
            token = "Bearer $token",
            myLang = myLang,
            updatePasswordRequest = updatePasswordRequest
        )

    suspend fun updatePhone(token: String, updatePhoneRequest: UpdatePhoneRequest) =
        RetrofitInstance.api.updatePhone(
            token = "Bearer $token",
            updatePhoneRequest = updatePhoneRequest
        )

    suspend fun checkPassword(token: String, checkPasswordRequest: CheckPasswordRequest) =
        RetrofitInstance.api.checkPassword(
            token = "Bearer $token",
            checkPasswordRequest = checkPasswordRequest
        )

    suspend fun sendCodeToNewPhone(token: String, newPhoneNumber: String) =
        RetrofitInstance.api.sendCodeToNewPhone(
            token = "Bearer $token",
            newPhoneRequest = NewPhoneRequest(newPhoneNumber)
        )

    suspend fun rgeaAUTHValidation(
        token: String,
        myLang: String,
        rgeaAUTHValidationRequest: RgeaAUTHValidationRequest
    ) =
        RetrofitInstance.api.rgeaAUTHValidation(
            token = "Bearer $token",
            myLang = myLang,
            rgeaAUTHValidationRequest = rgeaAUTHValidationRequest
        )

    //------------------------------------------------------------------------------------------
    suspend fun signUpUser(
        myLang: String,
        user: UserSignupRequest,
        logoImageFile: File?,
        commercialFile: File?
    ): Response<SignUpResponse> {
        val partsMap = HashMap<String, RequestBody>()
        partsMap["country"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.country)
        partsMap["phone"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.phone)
        partsMap["fullname"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.fullname)
        partsMap["email"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.email)
        partsMap["type"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.type)
        partsMap["agencyType"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.agencyType)
        partsMap["password"] = RequestBody.create("text/plain".toMediaTypeOrNull(), user.password)
        partsMap["idType"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), user.idType.toString())
        partsMap["idNumber"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), user.idNumber.toString())
        partsMap["adNumber"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), user.adNumber.toString())

        var imagePart: MultipartBody.Part? = null
        logoImageFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            imagePart = createFormData("img", it.name, imageRequestBody)
        }
        var commercialPart: MultipartBody.Part? = null
        commercialFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            commercialPart =
                createFormData("commercialFile", it.name, imageRequestBody)
        }
        return RetrofitInstance.api.signUpOffice(
            myLang = myLang,
            parts = partsMap,
            img = imagePart,
            commercialFile = commercialPart
        )
    }

    //------------------------------------------------------------------------------------------
    suspend fun completeProfile(
        myLang: String,
        completeProfileRequest: CompleteProfileRequest,
        userId: String,
        logoImageFile: File?,
        commercialFile: File?
    ): Response<SignInResponse2> {
        val partsMap = HashMap<String, RequestBody>()
        partsMap["country"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.country)
//        partsMap["enableNotif"] =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), true.toString())
        partsMap["phone"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.phone)
        partsMap["fullname"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.fullname)
        partsMap["email"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.email)
//        partsMap["type"] =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.type)
        partsMap["city"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                completeProfileRequest.city.toString()
            )
        partsMap["completeProfile"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                completeProfileRequest.completeProfile.toString()
            )

//        partsMap["idType"] = RequestBody.create(
//            "text/plain".toMediaTypeOrNull(),
//            completeProfileRequest.idType.toString()
//        )
        partsMap["idNumber"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            completeProfileRequest.idNumber.toString()
        )
        partsMap["adNumber"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            completeProfileRequest.adNumber.toString()
        )

        var imagePart: MultipartBody.Part? = null
        logoImageFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            imagePart = createFormData("img", it.name, imageRequestBody)
        }
        var commercialPart: MultipartBody.Part? = null
        commercialFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            commercialPart =
                createFormData("commercialFile", it.name, imageRequestBody)
        }

        val workCityList: MutableList<MultipartBody.Part> = ArrayList()
        workCityList.add(createFormData("workCity", completeProfileRequest.workCity.toString()))

        val workAreaList: MutableList<MultipartBody.Part> = ArrayList()
        workAreaList.add(createFormData("workArea", completeProfileRequest.workArea.toString()))

        val workCategoryList: MutableList<MultipartBody.Part> = ArrayList()
        workCategoryList.add(
            createFormData(
                "workCategory",
                completeProfileRequest.workCategory.toString()
            )
        )

        val workSubCategoryList: MutableList<MultipartBody.Part> = ArrayList()
        workSubCategoryList.add(
            createFormData(
                "workSubCategory",
                completeProfileRequest.workSubCategory.toString()
            )
        )

        return RetrofitInstance.api.completeProfile(
            myLang = myLang,
            parts = partsMap,
            userId = userId,
            img = imagePart,
            commercialFile = commercialPart,
            workCity = workCityList,
            workArea = workAreaList,
            workCategory = workCategoryList,
            workSubCategory = workSubCategoryList
        )
    }

    //------------------------------------------------------------------------------------------
    suspend fun updateProfile(
        myLang: String,
        token: String,
        completeProfileRequest: CompleteProfileRequest,
        userId: String,
        logoImageFile: File?,
        commercialFile: File?
    ): Response<SignInResponse2> {
        val partsMap = HashMap<String, RequestBody>()
        partsMap["country"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.country)
//        partsMap["enableNotif"] =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), true.toString())
        partsMap["phone"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.phone)
        partsMap["fullname"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.fullname)
        partsMap["email"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.email)
//        partsMap["type"] =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), completeProfileRequest.type)
        partsMap["city"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                completeProfileRequest.city.toString()
            )
        partsMap["completeProfile"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                completeProfileRequest.completeProfile.toString()
            )

//        partsMap["idType"] = RequestBody.create(
//            "text/plain".toMediaTypeOrNull(),
//            completeProfileRequest.idType.toString()
//        )
        partsMap["idNumber"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            completeProfileRequest.idNumber.toString()
        )
        partsMap["adNumber"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            completeProfileRequest.adNumber.toString()
        )

        var imagePart: MultipartBody.Part? = null
        logoImageFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            imagePart = createFormData("img", it.name, imageRequestBody)
        }
        var commercialPart: MultipartBody.Part? = null
        commercialFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            commercialPart =
                createFormData("commercialFile", it.name, imageRequestBody)
        }

        val workCityList: MutableList<MultipartBody.Part> = ArrayList()
        workCityList.add(createFormData("workCity", completeProfileRequest.workCity.toString()))

        val workAreaList: MutableList<MultipartBody.Part> = ArrayList()
        workAreaList.add(createFormData("workArea", completeProfileRequest.workArea.toString()))

        val workCategoryList: MutableList<MultipartBody.Part> = ArrayList()
        workCategoryList.add(
            createFormData(
                "workCategory",
                completeProfileRequest.workCategory.toString()
            )
        )

        val workSubCategoryList: MutableList<MultipartBody.Part> = ArrayList()
        workSubCategoryList.add(
            createFormData(
                "workSubCategory",
                completeProfileRequest.workSubCategory.toString()
            )
        )

        return RetrofitInstance.api.updateProfile(
            myLang = myLang,
            token = "Bearer $token",
            parts = partsMap,
            userId = userId,
            img = imagePart,
            commercialFile = commercialPart,
            workCity = workCityList,
            workArea = workAreaList,
            workCategory = workCategoryList,
            workSubCategory = workSubCategoryList
        )
    }

    suspend fun updateNotifyStatus(
        myLang: String,
        token: String,
        enableNotifyInUpdateProfileRequest: EnableNotifyInUpdateProfileRequest,
        userId: String,
    ): Response<SignInResponse2> {
        val partsMap = HashMap<String, RequestBody>()
        partsMap["enableNotif"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                enableNotifyInUpdateProfileRequest.enableNotif.toString()
            )
        return RetrofitInstance.api.updateNotifyStatus(
            myLang = myLang,
            token = "Bearer $token",
            parts = partsMap,
            userId = userId,
        )
    }

    suspend fun getAllCityWithoutPagination(token: String, regionId: String, myLang: String) =
        RetrofitInstance.api.getAllCityWithoutPagination(
            token = "Bearer $token",
            regionId = regionId,
            myLang = myLang,
        )

    suspend fun getAllCityInAllCountryWithoutPagination(
        token: String,
        regionId: String,
        myLang: String,
        country: String
    ) =
        RetrofitInstance.api.getAllCityInAllCountryWithoutPagination(
            token = "Bearer $token",
            regionId = regionId,
            myLang = myLang,
            country = country
        )

    suspend fun getAllAreaWithoutPagination(token: String, cityId: String, myLang: String) =
        RetrofitInstance.api.getAllAreaWithoutPagination(
            token = "Bearer $token",
            cityId = cityId,
            myLang = myLang
        )

    suspend fun getAllRegionWithoutPagination(token: String, countryId: String, myLang: String) =
        RetrofitInstance.api.getAllRegionWithoutPagination(
            token = "Bearer $token",
            countryId = countryId,
            myLang = myLang
        )

    suspend fun getMainCategory(myLang: String) =
        RetrofitInstance.api.getMainCategory(myLang)

    suspend fun getSubCategory(mainId: String, myLang: String) =
        RetrofitInstance.api.getSubCategory(mainId, myLang)

    suspend fun getAllSubCategory(myLang: String) =
        RetrofitInstance.api.getAllSubCategory(myLang)

    suspend fun getMainFeatures(myLang: String , category : Int) =
        RetrofitInstance.api.getMainFeatures(myLang = myLang , category = category.toString())

    suspend fun getMyFavorites(token: String, userId: String) =
        RetrofitInstance.api.getMyFavorites(
            token = "Bearer $token",
            userId = userId
        )

    suspend fun addToMyFavorites(token: String, adsId: String) =
        RetrofitInstance.api.addToMyFavorites(
            token = "Bearer $token",
            adsId = adsId
        )

    suspend fun removeFromMyFavorites(token: String, adsId: String) =
        RetrofitInstance.api.removeFromMyFavorites(
            token = "Bearer $token",
            adsId = adsId
        )

    suspend fun getNotifications(token: String, myLang: String) =
        RetrofitInstance.api.getNotifications(
            token = "Bearer $token",
            myLang = myLang,
        )

    suspend fun getNotificationsUnreadCount(token: String) =
        RetrofitInstance.api.getNotificationsUnreadCount(
            token = "Bearer $token",
        )

    suspend fun sendReadTheNotification(token: String, notificationId: String) =
        RetrofitInstance.api.sendReadTheNotification(
            token = "Bearer $token",
            notificationId = notificationId
        )

    suspend fun getAnnouncement() =
        RetrofitInstance.api.getAnnouncement()

    suspend fun getAds(token: String, myLang: String, mainCategory: Int?, owner: Int?) =
        RetrofitInstance.api.getAds(
            token = "Bearer $token",
            myLang = myLang,
            category = mainCategory,
            owner = owner,
        )

    suspend fun getAdsWithPagination(token: String, myLang: String, page: Int) =
        RetrofitInstance.api.getAdsWithPagination(
            token = "Bearer $token",
            myLang = myLang,
            page = page,
            limit = 20
        )

    suspend fun getRequestedAds(token: String, myLang: String) =
        RetrofitInstance.api.getRequestedAds(
            token = "Bearer $token",
            myLang = myLang
        )

    suspend fun getRequestedAdsById(adRequestedId: String, myLang: String) =
        RetrofitInstance.api.getRequestedAdsById(
            adRequestedId = adRequestedId,
            myLang = myLang
        )

    suspend fun deleteMyRequestedAd(token: String, requestId: String) =
        RetrofitInstance.api.deleteMyRequestedAd(
            token = "Bearer $token",
            requestId = requestId
        )

    suspend fun getContactRequest(token: String, myLang: String) =
        RetrofitInstance.api.getContactRequest(
            token = "Bearer $token",
            myLang = myLang
        )

    suspend fun getContactRequestById(token: String, myLang: String, contactRequestId: String) =
        RetrofitInstance.api.getContactRequestById(
            token = "Bearer $token",
            myLang = myLang,
            contactRequestId = contactRequestId
        )

    suspend fun deleteContactRequest(token: String, contactRequestId: String) =
        RetrofitInstance.api.deleteContactRequest(
            token = "Bearer $token",
            contactRequestId = contactRequestId
        )

    suspend fun getAdsById(adsId: String) =
        RetrofitInstance.api.getAdsById(adsId)

    suspend fun uploadImagesForAddNewEstate(
        token: String,
        myLang: String,
        estateImageFile1: File?,
        threeDFile: File?,
        adsFile: File?
    ): Response<UploadEstateImagesResponse> {

        var imagePart1: MultipartBody.Part? = null
        estateImageFile1?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            imagePart1 = createFormData("imgs", it.name, imageRequestBody)
        }

        var threeDPart1: MultipartBody.Part? = null
        threeDFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            threeDPart1 = createFormData("imgs3D", it.name, imageRequestBody)
        }

        var adsFilePart1: MultipartBody.Part? = null
        adsFile?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            adsFilePart1 = createFormData("adsFile", it.name, imageRequestBody)
        }

        return RetrofitInstance.api.uploadImagesForAddNewEstate(
            token = "Bearer $token",
            myLang = myLang,
            imgs = imagePart1,
            imgs3D = threeDPart1,
            adsFile = adsFilePart1
        )
    }

//    suspend fun addNewEstate(
//        token: String,
//        myLang: String,
//        addNewAdsRequest: AddNewAdsRequest,
//    ): Response<AddNewAdsResponse> {
//        return RetrofitInstance.api.addNewAds(
//            addNewAdsRequest = addNewAdsRequest,
//            token = "Bearer $token",
//            myLang = myLang,
//        )
//    }

    suspend fun addNewEstate(
        token: String,
        myLang: String,
        addNewAdsRequest: AddNewAdsRequest,
    ): Response<AddNewAdsResponse2> {

        return RetrofitInstance.api.addNewAds(
            token = "Bearer $token",
            myLang = myLang,
            addNewAdsRequest = addNewAdsRequest,
        )
    }

    suspend fun getLiveSearch(
        token: String,
        myLang: String,
        owner: Int,
        category: String,
        related: Boolean,
        area: Int
    ): Response<LiveSearchResponse> {
        return RetrofitInstance.api.getLiveSearch(
            token = "Bearer $token",
            myLang = myLang,
            owner = owner,
            category = category,
            related = related,
            area = area
        )
    }

    suspend fun updateAds(
        token: String,
        myLang: String,
        addNewAdsRequest: AddNewAdsRequest,
        adsId: String
    ): Response<SuccessResponse> {

        return RetrofitInstance.api.updateAds(
            token = "Bearer $token",
            myLang = myLang,
            addNewAdsRequest = addNewAdsRequest,
            adsId = adsId
        )
    }

    suspend fun makeAdsAvailable(token: String, adsId: String) =
        RetrofitInstance.api.makeAdsAvailable(
            token = "Bearer $token",
            adsId = adsId
        )

    suspend fun makeAdsUnavailable(token: String, adsId: String) =
        RetrofitInstance.api.makeAdsUnavailable(
            token = "Bearer $token",
            adsId = adsId
        )

    suspend fun deleteAdsById(token: String, adsId: String) =
        RetrofitInstance.api.deleteAdsById(
            token = "Bearer $token",
            adsId = adsId
        )

    suspend fun getAllMessage(
        token: String,
        userId: String,
        friendId: String,
        contactRequest: String
    ) =
        RetrofitInstance.api.getAllMessages(
            token = "Bearer $token",
            userId = userId,
            friendId = friendId,
            contactRequest = contactRequest
        )

    suspend fun getLastContacts(token: String, id: String) =
        RetrofitInstance.api.getLastContacts(
            token = "Bearer $token",
            id = id
        )

    suspend fun uploadFileInMessage(
        token: String,
        fileInChat: File?
    ): Response<UploadFileInChatResponse> {

        var filePart1: MultipartBody.Part? = null
        fileInChat?.let {
            val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            filePart1 = createFormData("img", it.name, imageRequestBody)
        }

        return RetrofitInstance.api.uploadFileInMessage(
            token = "Bearer $token",
            img = filePart1,
        )
    }

    suspend fun getPackages(myLang: String) =
        RetrofitInstance.api.getPackages(
            myLang = myLang
        )

    suspend fun getBillInfo(billInfoRequest: BillInfoRequest) =
        RetrofitInstance.api.getBillInfo(
            billInfoRequest = billInfoRequest,
        )

    suspend fun getPaymentTerms(myLang: String) =
        RetrofitInstance.api.getPaymentTerms(
            myLang = myLang
        )

    suspend fun getTerms(myLang: String) =
        RetrofitInstance.api.getTerms(
            myLang = myLang
        )

    suspend fun getSetting() =
        RetrofitInstance.api.getSetting()

    suspend fun getBookingCalender(
        token: String,
        adsId: String,
        startDate: String?,
        endDate: String?
    ) =
        RetrofitInstance.api.getBookingCalender(
            token = "Bearer $token",
            adsId = adsId,
            startDate = startDate,
            endDate = endDate,
        )

    suspend fun addBooking(
        token: String,
        adsId: String,
        addBookingRequest: AddBookingRequest
    ) =
        RetrofitInstance.api.addBooking(
            token = "Bearer $token",
            adsId = adsId,
            addBookingRequest = addBookingRequest,
        )

    suspend fun checkOfficeMarketPlace(
        token: String,
        officeId: String,
    ) =
        RetrofitInstance.api.checkOfficeMarketPlace(
            token = "Bearer $token",
            officeId = officeId,
        )

    suspend fun addToMarketPlace(
        token: String,
        addToMarketPlaceRequest: AddToMarketPlaceRequest,
        licenseImageList: ArrayList<UploadImagesModel>,
        idImagesList: ArrayList<UploadImagesModel>,
    ): Response<AddToMarketPlaceResponse> {

        var licenseImagesPart: MultipartBody.Part? = null
        val licenseImagesPartArray: ArrayList<MultipartBody.Part> = ArrayList()

        licenseImageList.forEach { uploadImageItem ->
            if (uploadImageItem.imageFile != null) {
                uploadImageItem.imageFile.let { theFileItem ->
                    val imageRequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), theFileItem!!)
                    licenseImagesPart =
                        createFormData("entity_imgs", theFileItem.name, imageRequestBody)
                }
                licenseImagesPartArray.add(licenseImagesPart!!)
            }
        }

        var idImagesPart: MultipartBody.Part? = null
        val idImagesPartArray: ArrayList<MultipartBody.Part> = ArrayList()
        idImagesList.forEach { uploadImageItem ->
            if (uploadImageItem.imageFile != null) {
                uploadImageItem.imageFile.let { theFileItem ->
                    val imageRequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), theFileItem!!)
                    idImagesPart =
                        createFormData("identification_imgs", theFileItem.name, imageRequestBody)
                }
                idImagesPartArray.add(idImagesPart!!)
            }
        }

        val partsMap = HashMap<String, RequestBody>()
        partsMap["bank_iban"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                addToMarketPlaceRequest.bank_iban
            )
        partsMap["brand_name_ar"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                addToMarketPlaceRequest.brand_name_ar
            )
        partsMap["brand_name_en"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                addToMarketPlaceRequest.brand_name_en
            )

        partsMap["entity_number"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.entity_number
        )
        partsMap["entity_ssuing_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.entity_ssuing_date
        )
        partsMap["entity_expiry_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.entity_expiry_date
        )
        partsMap["owner_firstname"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_firstname
        )
        partsMap["owner_lastName"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_lastName
        )
        partsMap["owner_email"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_email
        )
        partsMap["owner_phone"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_phone
        )
        partsMap["identification_type"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.identification_type
        )
        partsMap["identification_issuing_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.identification_issuing_date
        )
        partsMap["identification_expiry_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.identification_expiry_date
        )
        partsMap["office"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), addToMarketPlaceRequest.office)
        partsMap["tax_number"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), addToMarketPlaceRequest.tax_number)

        return RetrofitInstance.api.addToMarketPlace(
            token = "Bearer $token",
            parts = partsMap,
            entity_imgs = licenseImagesPartArray,
            identification_imgs = idImagesPartArray
        )
    }

    suspend fun updateMarketPlace(
        marketPlaceId: String,
        token: String,
        addToMarketPlaceRequest: AddToMarketPlaceRequest,
        licenseImageList: ArrayList<UploadImagesModel>,
        idImagesList: ArrayList<UploadImagesModel>,
    ): Response<SuccessResponse> {

        var licenseImagesPart: MultipartBody.Part? = null
        val licenseImagesPartArray: ArrayList<MultipartBody.Part> = ArrayList()

        licenseImageList.forEach { uploadImageItem ->
            if (uploadImageItem.imageFile != null) {
                uploadImageItem.imageFile.let { theFileItem ->
                    val imageRequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), theFileItem!!)
                    licenseImagesPart =
                        createFormData("entity_imgs", theFileItem.name, imageRequestBody)
                }
                licenseImagesPartArray.add(licenseImagesPart!!)
            }
        }

        var idImagesPart: MultipartBody.Part? = null
        val idImagesPartArray: ArrayList<MultipartBody.Part> = ArrayList()
        idImagesList.forEach { uploadImageItem ->
            if (uploadImageItem.imageFile != null) {
                uploadImageItem.imageFile.let { theFileItem ->
                    val imageRequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), theFileItem!!)
                    idImagesPart =
                        createFormData("identification_imgs", theFileItem.name, imageRequestBody)
                }
                idImagesPartArray.add(idImagesPart!!)
            }
        }

        val partsMap = HashMap<String, RequestBody>()
        partsMap["bank_iban"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                addToMarketPlaceRequest.bank_iban
            )
        partsMap["brand_name_ar"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                addToMarketPlaceRequest.brand_name_ar
            )
        partsMap["brand_name_en"] =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                addToMarketPlaceRequest.brand_name_en
            )
        partsMap["entity_number"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.entity_number
        )
        partsMap["entity_ssuing_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.entity_ssuing_date
        )
        partsMap["entity_expiry_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.entity_expiry_date
        )
        partsMap["owner_firstname"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_firstname
        )
        partsMap["owner_lastName"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_lastName
        )
        partsMap["owner_email"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_email
        )
        partsMap["owner_phone"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.owner_phone
        )
        partsMap["identification_type"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.identification_type
        )
        partsMap["identification_issuing_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.identification_issuing_date
        )
        partsMap["identification_expiry_date"] = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            addToMarketPlaceRequest.identification_expiry_date
        )
        partsMap["office"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), addToMarketPlaceRequest.office)
        partsMap["tax_number"] =
            RequestBody.create("text/plain".toMediaTypeOrNull(), addToMarketPlaceRequest.tax_number)

        return RetrofitInstance.api.updateMarketPlace(
            token = "Bearer $token",
            parts = partsMap,
            marketPlaceId = marketPlaceId,
            entity_imgs = licenseImagesPartArray,
            identification_imgs = idImagesPartArray
        )
    }
}