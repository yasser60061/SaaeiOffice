package com.saaei12092021.office.api

import com.saaei12092021.office.model.requestModel.*
import com.saaei12092021.office.model.responseModel.adsRequestedResponse.AdsRequestedResponse
import com.saaei12092021.office.model.responseModel.areasResponse.AreasResponse
import com.saaei12092021.office.model.responseModel.citiesResponse.CitiesResponse
import com.saaei12092021.office.model.responseModel.contactRequesteResponse.ContactRequestResponse
import com.saaei12092021.office.model.responseModel.estateMainCategoryResponse.EstateMainCategoryResponse
import com.saaei12092021.office.model.responseModel.estateSubCategoryResponse.EstateSubCategoryResponse
import com.saaei12092021.office.model.responseModel.favoritesResponse.FavoritesResponse
import com.saaei12092021.office.model.responseModel.getAdsResponse.GetAdsResponse
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.MainFeaturesResponse
import com.saaei12092021.office.model.responseModel.notificationsResponse.NotificatiosResponse
import com.saaei12092021.office.model.VerificationUserResponse
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.AddNewAdsRequest
import com.saaei12092021.office.model.requestModel.rgeaAUTHValidation.RgeaAUTHValidationRequest
import com.saaei12092021.office.model.responseModel.*
import com.saaei12092021.office.model.responseModel.AnoncementsResponse.AnoncementsResponse
import com.saaei12092021.office.model.responseModel.SignUpResponse.SignUpResponse
import com.saaei12092021.office.model.responseModel.adRequestedByIdResponse.AdRequestedByIdResponse
import com.saaei12092021.office.model.responseModel.addBookingResponse.AddBookingResponse
import com.saaei12092021.office.model.responseModel.addNewAdsResponse.AddNewAdsResponse2
import com.saaei12092021.office.model.responseModel.addNewAdsResponse.UploadEstateImagesResponse
import com.saaei12092021.office.model.responseModel.adsById.AdsByIdResponse
import com.saaei12092021.office.model.responseModel.getAdsWithPaginationResponse.GetAdsWithPaginationResponse
import com.saaei12092021.office.model.responseModel.getContactRequestById.ContactRequestByIdResponse
import com.saaei12092021.office.model.responseModel.getUser.GetUserResponse
import com.saaei12092021.office.model.responseModel.getUserByTokenResponse.GetUserByTokenResponse
import com.saaei12092021.office.model.responseModel.lastContact.LastContactResponse
import com.saaei12092021.office.model.responseModel.liveSearchResponse.LiveSearchResponse
import com.saaei12092021.office.model.responseModel.marketPlaceResponse.AddToMarketPlaceResponse
import com.saaei12092021.office.model.responseModel.messagesInChatResponse.MessagesInChatResponse
import com.saaei12092021.office.model.responseModel.notificationUnreadCountResponse.NotificationUnreadCountResponse
import com.saaei12092021.office.model.responseModel.packagesResponse.PackagesResponse
import com.saaei12092021.office.model.responseModel.paymentTearms.PaymentsTermsResponse
import com.saaei12092021.office.model.responseModel.regionResponse.RegionResponse
import com.saaei12092021.office.model.responseModel.settingResponse.SettingResponse
import com.saaei12092021.office.model.responseModel.signInResponse2.SignInResponse2
import com.saaei12092021.office.model.responseModel.termsResponse.TermsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.Multipart

interface API {

    @POST("api/v1/signin")
    suspend fun signInUser(
        @Body user: SignInRequest,
        @Header(value = "Content-Type") type: String = "application/json",
    ): Response<SignInResponse2>

    @GET("api/v1/{userId}/getUser")
    suspend fun getUser(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Path("userId") userId: String,
    ): Response<GetUserResponse>

    @GET("api/v1/findById") /// get by token only
    suspend fun getUserByToken(
        @Header(value = "Authorization") token: String,
    ): Response<GetUserByTokenResponse>

    @PUT("api/v1/refreshToken")
    suspend fun refreshToken(
        @Body user: SignInRequest,
        @Header(value = "Authorization") oldToken: String,
        @Header(value = "Content-Type") type: String = "application/json",
    ): Response<SignInResponse2>

    @Headers("Content-Type:application/json", "Accept-Language:ar")
    @PUT("api/v1/verifyAccount")
    suspend fun verificationUser(
        @Body verificationUser: VerificationUserRequest
    ): Response<VerificationUserResponse>

    @POST("api/v1/sendCode-phone")
    suspend fun recoverPassword(
        @Body phoneNumber: ForgetPasswordByPhoneRequest
    ): Response<SuccessResponse>

    @POST("api/v1/reset-password-phone")
    suspend fun resetPasswordByPhone(
        @Header(value = "Accept-Language") myLang: String,
        @Body phoneNumber: ResetPasswordByPhoneRequest
    ): Response<SuccessResponse>

    @PUT("api/v1/user/updatePassword")
    suspend fun updatePassword(
        @Body updatePasswordRequest: UpdatePasswordRequest,
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Header(value = "Content-Type") type: String = "application/json",
    ): Response<SuccessResponse>

    @PUT("api/v1/updatePhone")
    suspend fun updatePhone(
        @Body updatePhoneRequest: UpdatePhoneRequest,
        @Header(value = "Authorization") token: String,
        @Header(value = "Content-Type") type: String = "application/json",
    ): Response<SuccessResponse>

    @POST("api/v1/checkPassword")
    suspend fun checkPassword(
        @Body checkPasswordRequest: CheckPasswordRequest,
        @Header(value = "Authorization") token: String,
        @Header(value = "Content-Type") type: String = "application/json",
    ): Response<SuccessResponse>

    @POST(" api/v1/sendCode-updatePhone")
    suspend fun sendCodeToNewPhone(
        @Body newPhoneRequest: NewPhoneRequest,
        @Header(value = "Authorization") token: String,
        @Header(value = "Content-Type") type: String = "application/json",
    ): Response<SuccessResponse>

    @Multipart
    @POST("api/v1/signup")
    suspend fun signUpOffice(
        @Header(value = "Accept-Language") myLang: String,
        @PartMap parts: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?,
        @Part commercialFile: MultipartBody.Part?
    ): Response<SignUpResponse>

    @POST("api/v1/ads/RgeaAUTHValidation")
    suspend fun rgeaAUTHValidation(
        @Header(value = "Authorization") token: String,
        @Header(value = "Content-Type") type1: String = "application/json",
        @Header(value = "Accept-Language") myLang: String,
        @Body rgeaAUTHValidationRequest: RgeaAUTHValidationRequest
    ): Response<SuccessResponse>

    @Multipart
    @POST("api/v1/{userId}/completeProfile")
    suspend fun completeProfile(
        @Header(value = "Accept-Language") myLang: String,
        @Path("userId") userId: String,
        @PartMap parts: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?,
        @Part commercialFile: MultipartBody.Part?,
        @Part workCity: List<MultipartBody.Part>,
        @Part workArea: List<MultipartBody.Part>,
        @Part workCategory: List<MultipartBody.Part>,
        @Part workSubCategory: List<MultipartBody.Part>,
    ): Response<SignInResponse2>

    @Multipart
    @PUT("api/v1/user/{userId}/updateInfo")
    suspend fun updateProfile(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Path("userId") userId: String,
        @PartMap parts: HashMap<String, RequestBody>,
        @Part img: MultipartBody.Part?,
        @Part commercialFile: MultipartBody.Part?,
        @Part workCity: List<MultipartBody.Part>,
        @Part workArea: List<MultipartBody.Part>,
        @Part workCategory: List<MultipartBody.Part>,
        @Part workSubCategory: List<MultipartBody.Part>,
    ): Response<SignInResponse2>

    @Multipart
    @PUT("api/v1/user/{userId}/updateInfo")
    suspend fun updateNotifyStatus(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Path("userId") userId: String,
        @PartMap parts: HashMap<String, RequestBody>,
    ): Response<SignInResponse2>

    @GET("api/v1/countries/{regionId}/cities/withoutPagenation/get")
    suspend fun getAllCityWithoutPagination(
        @Header(value = "Authorization") token: String,
        @Path(("regionId")) regionId: String,
        @Header(value = "Accept-Language") myLang: String,
    ): Response<CitiesResponse>

    @GET("api/v1/countries/{regionId}/cities/withoutPagenation/get")
    suspend fun getAllCityInAllCountryWithoutPagination(
        @Header(value = "Authorization") token: String,
        @Path(("regionId")) regionId: String,
        @Header(value = "Accept-Language") myLang: String,
        @Query(value = "country") country: String,
    ): Response<CitiesResponse>

    @GET("api/v1/areas/{cityId}/withoutPagenation/get")
    suspend fun getAllAreaWithoutPagination(
        @Header(value = "Authorization") token: String,
        @Path(("cityId")) cityId: String,
        @Header(value = "Accept-Language") myLang: String,
    ): Response<AreasResponse>

    @GET("api/v1/regions/{countryId}/withoutPagenation/get")
    suspend fun getAllRegionWithoutPagination(
        @Header(value = "Authorization") token: String,
        @Path(("countryId")) countryId: String,
        @Header(value = "Accept-Language") myLang: String,
    ): Response<RegionResponse>

    @GET("api/v1/categories")
    suspend fun getMainCategory(
        @Header(value = "Accept-Language") myLang: String,
    ): Response<EstateMainCategoryResponse>

    @GET("api/v1/categories/{id}/sub-categories")
    suspend fun getSubCategory(
        @Path(("id")) mainId: String,
        @Header(value = "Accept-Language") myLang: String
    ): Response<EstateSubCategoryResponse>

    @GET("api/v1/categories/subCategories")
    suspend fun getAllSubCategory(
        @Header(value = "Accept-Language") myLang: String
    ): Response<EstateSubCategoryResponse>

    @GET("api/v1/features/withoutPagenation/get")
    suspend fun getMainFeatures(
        @Header(value = "Accept-Language") myLang: String,
        @Query("category") category: String
    ): Response<MainFeaturesResponse>

    @GET("api/v1/favourites")
    suspend fun getMyFavorites(
        @Header(value = "Authorization") token: String,
        @Query("userId") userId: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
    ): Response<FavoritesResponse>

    @POST("api/v1/favourites/{adsId}/add")
    suspend fun addToMyFavorites(
        @Header(value = "Authorization") token: String,
        @Path("adsId") adsId: String,
    ): Response<SuccessResponse>

    @DELETE("api/v1/favourites/{adsId}")
    suspend fun removeFromMyFavorites(
        @Header(value = "Authorization") token: String,
        @Path("adsId") adsId: String,
    ): Response<SuccessResponse>

    @GET("api/v1/notif")
    suspend fun getNotifications(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Query("limit") limit: Int = 1000,
    ): Response<NotificatiosResponse>

    @GET("api/v1/notif/unreadCount")
    suspend fun getNotificationsUnreadCount(
        @Header(value = "Authorization") token: String,
    ): Response<NotificationUnreadCountResponse>

    @PUT("api/v1/notif/{notificationId}/read")
    suspend fun sendReadTheNotification(
        @Header(value = "Authorization") token: String,
        @Path("notificationId") notificationId: String,
    ): Response<SuccessResponse>

    @GET("api/v1/anoncement/withoutPagenation/get")
    suspend fun getAnnouncement(
    ): Response<AnoncementsResponse>

    @GET("api/v1/ads/withoutPagenation/get")
    suspend fun getAds(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Query("category") category: Int? = null,
        @Query("owner") owner: Int? = null,
        @Query("all") all: Boolean = true,
    ): Response<GetAdsResponse>

    @GET("api/v1/ads")
    suspend fun getAdsWithPagination(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("others") others: Boolean = true,
    ): Response<GetAdsWithPaginationResponse>

    @GET("api/v1/adsRequest?limit=1000")
    suspend fun getRequestedAds(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String
    ): Response<AdsRequestedResponse>

    @GET("api/v1/adsRequest/{adRequestedId}")
    suspend fun getRequestedAdsById(
        @Path("adRequestedId") adRequestedId: String,
        @Header(value = "Accept-Language") myLang: String
    ): Response<AdRequestedByIdResponse>

    @DELETE("api/v1/adsRequest/{requestId}")
    suspend fun deleteMyRequestedAd(
        @Header(value = "Authorization") token: String,
        @Path("requestId") requestId: String,
    ): Response<SuccessResponse>

    @GET("api/v1/contactRequest?limit=1000")
    suspend fun getContactRequest(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String
    ): Response<ContactRequestResponse>

    @GET("api/v1/contactRequest/{contactRequestId}")
    suspend fun getContactRequestById(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String,
        @Path("contactRequestId") contactRequestId: String
    ): Response<ContactRequestByIdResponse>

    @DELETE("api/v1/contactRequest/{contactRequestId}")
    suspend fun deleteContactRequest(
        @Header(value = "Authorization") token: String,
        @Path("contactRequestId") contactRequestId: String
    ): Response<SuccessResponse>

    @GET("api/v1/ads/{adsId}")
    suspend fun getAdsById(
        @Path(("adsId")) adsId: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
    ): Response<AdsByIdResponse>

    @Multipart
    @POST("api/v1/ads/uploadImgs")
    suspend fun uploadImagesForAddNewEstate(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Part imgs: MultipartBody.Part?,
        @Part imgs3D: MultipartBody.Part?,
        @Part adsFile: MultipartBody.Part? = null
    ): Response<UploadEstateImagesResponse>

    @POST("api/v1/ads")
    suspend fun addNewAds(
        @Header(value = "Authorization") token: String,
        @Header(value = "Content-Type") type1: String = "application/json",
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Body addNewAdsRequest: AddNewAdsRequest
    ): Response<AddNewAdsResponse2>

    @PUT("api/v1/ads/{adsId}")
    suspend fun updateAds(
        @Header(value = "Authorization") token: String,
        @Header(value = "Content-Type") type1: String = "application/json",
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Body addNewAdsRequest: AddNewAdsRequest,
        @Path("adsId") adsId: String,
    ): Response<SuccessResponse>

    @PUT("api/v1/ads/{adsId}/available")
    suspend fun makeAdsAvailable(
        @Header(value = "Authorization") token: String,
        @Path(("adsId")) adsId: String,
    ): Response<SuccessResponse>

    @PUT("api/v1/ads/{adsId}/dis-available")
    suspend fun makeAdsUnavailable(
        @Header(value = "Authorization") token: String,
        @Path(("adsId")) adsId: String,
    ): Response<SuccessResponse>

    @DELETE("api/v1/ads/{adsId}")
    suspend fun deleteAdsById(
        @Header(value = "Authorization") token: String,
        @Path(("adsId")) adsId: String,
    ): Response<SuccessResponse>

    @GET("api/v1/ads/liveSearch/get")
    suspend fun getLiveSearch(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Query("owner") owner: Int,
        @Query("related") related: Boolean,
        @Query("category") category: String,
        @Query("area") area: Int,
    ): Response<LiveSearchResponse>

    @GET("api/v1/messages")
    suspend fun getAllMessages(
        @Header(value = "Authorization") token: String,
        @Query("userId") userId: String,
        @Query("friendId") friendId: String,
        @Query("limit") limit: String = "2000",
        @Query("contactRequest") contactRequest: String,
    ): Response<MessagesInChatResponse>

    @Multipart
    @POST("api/v1/messages/upload")
    suspend fun uploadFileInMessage(
        @Header(value = "Authorization") token: String,
        @Part img: MultipartBody.Part?,
    ): Response<UploadFileInChatResponse>

    @GET("api/v1/messages/lastContacts")
    suspend fun getLastContacts(
        @Header(value = "Authorization") token: String,
        @Query("id") id: String,
    ): Response<LastContactResponse>

    @GET("api/v1/packages")
    suspend fun getPackages(
        @Header(value = "Accept-Language") myLang: String,
    ): Response<PackagesResponse>

    @POST("api/v1/ads/billInfo/get")
    suspend fun getBillInfo(
        @Body billInfoRequest: BillInfoRequest,
    ): Response<BillInfoResponse>

    @GET("api/v1/paymentTerms/withoutPagenation/get")
    suspend fun getPaymentTerms(
        @Header(value = "Accept-Language") myLang: String,
    ): Response<PaymentsTermsResponse>

    @GET("api/v1/terms")
    suspend fun getTerms(
        @Header(value = "Accept-Language") myLang: String,
    ): Response<TermsResponse>

    @GET("api/v1/setting")
    suspend fun getSetting(
    ): Response<SettingResponse>

    @GET("api/v1/ads/booking/{adsId}/getCalendar")
    suspend fun getBookingCalender(
        @Header(value = "Authorization") token: String,
        @Path("adsId") adsId: String,
        @Query(value = "startDate") startDate: String? = null,
        @Query(value = "endDate") endDate: String? = null,
    ): Response<BookingCalenderResponse>

    @POST("api/v1/ads/{adsId}/addBooking")
    suspend fun addBooking(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Path("adsId") adsId: String,
        @Body addBookingRequest: AddBookingRequest,
    ): Response<AddBookingResponse>

    @GET("api/v1/marketPlace/{officeId}/checkOfficeMarket")
    suspend fun checkOfficeMarketPlace(
        @Header(value = "Authorization") token: String,
        @Path("officeId") officeId: String,
    ): Response<CheckOfficeMarketPlaceResponse>

    @Multipart
    @POST("api/v1/marketPlace")
    suspend fun addToMarketPlace(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
        @PartMap parts: HashMap<String, RequestBody>,
        @Part entity_imgs: ArrayList<MultipartBody.Part>?,
        @Part identification_imgs: ArrayList<MultipartBody.Part>?
    ): Response<AddToMarketPlaceResponse>

    @Multipart
    @PUT("api/v1/marketPlace/{marketPlaceId}")
    suspend fun updateMarketPlace(
        @Header(value = "Authorization") token: String,
        @Header(value = "Accept-Language") myLang: String = "ar",
        @Path("marketPlaceId") marketPlaceId: String,
        @PartMap parts: HashMap<String, RequestBody>,
        @Part entity_imgs: ArrayList<MultipartBody.Part>?,
        @Part identification_imgs: ArrayList<MultipartBody.Part>?
    ): Response<SuccessResponse>

}