package com.saaei12092021.office.model.responseModel.packagesResponse

data class Data(
    val available: Boolean,
    val availableAds: Int,
    val building: Boolean,
    val costs: List<Cost>,
    val createdAt: String,
    val defaultPackage: Boolean,
    val description: String,
    val description_ar: String,
    val description_en: String,
    val id: Int,
    val name: String,
    val name_ar: String,
    val name_en: String,
    val plan: Boolean,
    val supervisors: Int,
    val type: String,
    val usersCount: Int
)