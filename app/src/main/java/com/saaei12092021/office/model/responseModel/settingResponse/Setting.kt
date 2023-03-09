package com.saaei12092021.office.model.responseModel.settingResponse

data class Setting(
    val adsCost: Int,
    val adsNumbers: Int,
    val adsRememberDays: Int,
    val androidAppVersion: String,
    val archivedAds: Int,
    val createdAt: String,
    val deleted: Boolean,
    val expireAfter: Int,
    val id: Int,
    val iosAppVersion: String,
    val maxCities: Int,
    val officeNumbers: Int,
    val searchKm: Int,
    val taxRatio: Int,
    val updatedAt: String
)