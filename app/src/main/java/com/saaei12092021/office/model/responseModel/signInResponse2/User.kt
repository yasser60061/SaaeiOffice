package com.saaei12092021.office.model.responseModel.signInResponse2

data class User(
    val accountType: String?, // SIGNUP-PROCESS','BLOCKED','ACTIVE','NOT-ACTIVE
    val adsCount: Int,
    val adsPackage: AdsPackage,
    val agencyType: String,
    val approved: Boolean,
    val availableAds: Int,
    val blockReason: String,
    val city: City,
    val commercialFile: List<String>,
    val country: Country,
    val createdAt: String,
    val email: String,
    val enableNotif: Boolean,
    val fullname: String,
    val hasAdsPackage: Boolean,
    val hasPropertyPackage: Boolean,
    val id: Int,
    val idNumber: String,
    val adNumber: String?,
    val idType: String,
    val img: String,
    val isMarketPlace: Boolean?, // in new response only
    val isVerified: Boolean?,
    val lastSeen: Long,
    val marketPlace: MarketPlace?, // in new response only
    val online: Boolean,
    val packageEndDateMillSec: Long,
    val packageStartDateMillSec: Long,
    val phone: String,
    val rate: Int,
    val rateCount: Int?, // in new response only
    val rateNumbers: Int?, // in new response only
    val type: String, // "OFFICE" , "SUPERVISOR"
    val updatedAt: String,
    val workArea: List<WorkArea>,
    val workCategory: List<WorkCategory>,
    val workCity: List<WorkCity>,
    val workSubCategory: List<WorkSubCategory>,

    val completProfile: Boolean?, // temp for delete in old response only
    val deleted: Boolean?,  // temp for delete in old response only
    val viewsCount: Int?, // temp for delete in old response only

    val parent: Parent // if the user is supervisor

)