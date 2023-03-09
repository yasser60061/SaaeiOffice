package com.saaei12092021.office.model

data class VerificationUserResponse(
    val success: Boolean,
   // val user: UserX
)
{
data class UserX(
    val accountType: String,
    val adsCount: Int,
    val approved: Boolean,
    val availableAds: Int,
    val blockReason: String,
    val commercialFile: String,
    val completProfile: Boolean,
    val country: CountryX,
    val createdAt: String,
    val deleted: Boolean,
    val enableNotif: Boolean,
    val hasAdsPackage: Boolean,
    val hasPropertyPackage: Boolean,
    val id: Int,
    val isVerified: Boolean,
    val online: Boolean,
    val phone: String,
    val rate: Int,
    val type: String,
    val updatedAt: String,
    val viewsCount: Int,
//    val workArea: List<Any>,
//    val workCategory: List<Any>,
//    val workCity: List<Any>,
//    val workSubCategory: List<Any>
)

data class CountryX(
    val countryCode: String,
    val countryName_ar: String,
    val countryName_en: String,
    val hint: String,
    val id: Int,
    val img: String,
    val isoCode: String,
    val numbersCount: Int
)}