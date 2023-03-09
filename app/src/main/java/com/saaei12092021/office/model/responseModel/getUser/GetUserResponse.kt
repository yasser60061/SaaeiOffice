package com.saaei12092021.office.model.responseModel.getUser

import com.saaei12092021.office.model.responseModel.signInResponse2.Parent
import java.io.Serializable

data class GetUserResponse(
    val accountType: String,
    val adsCount: Int,
    val adsRequestCount: Int,
    val agencyType: String,
    val approved: Boolean,
    val availableAds: Int,
    val city: City,
    val commercialFile: List<String>,
    val completedAdsRequest: Int,
    val contactRequestCount: Int,
    val country: Country,
    val createdAt: String,
    val email: String,
    val fullname: String,
    val hasAdsPackage: Boolean,
    val adsPackage: AdsPackage,
    val packageStartDateMillSec: Long,
    val packageEndDateMillSec: Long,
    val hasPropertyPackage: Boolean,
    val id: Int,
    val img: String,
    val isVerified: Boolean,
    var enableNotif : Boolean ,
    val lastSeen: Long,
    val online: Boolean,
    val phone: String,
    val isMarketPlace : Boolean ,
    val marketPlace: MarketPlace? ,
    val rate: Float,
    val rateCount: Int,
    val rateNumbers: Int,
    val rentAdsCount: Int,
    val saleAdsCount: Int,
    val type: String,
    val unCompletedAdsRequest: Int,
    val viewsCount: Int,
    val workArea: List<WorkArea>,
    val workCategory: List<WorkCategory>,
    val workCity: List<WorkCity>,
    val workSubCategory: List<WorkSubCategory> ,
    val idType : String ,
    val idNumber : String ,
    val adNumber : String ,
    val parent: Parent? = null // if the user is supervisor
) : Serializable