package com.saaei12092021.office.model.responseModel.signInResponse2

data class AdsPackage(
    val availableAds: Int,
    val building: Boolean,
    val defaultPackage: Boolean,
    val description: String,
    val description_ar: String,
    val description_en: String,
    val id: Int,
    val name: String,
    val name_ar: String,
    val name_en: String,
    val plan: Boolean,
    val supervisors: Int
)