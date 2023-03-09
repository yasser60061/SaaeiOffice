package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class PropertyPackage(
    val available: Boolean,
    val availableAds: Int,
    val building: Boolean,
    val costs: List<CostX>,
    val createdAt: String,
    val defaultPackage: Boolean,
    val deleted: Boolean,
    val description_ar: String,
    val description_en: String,
    val id: Int,
    val name_ar: String,
    val name_en: String,
    val plan: Boolean,
    val supervisors: Int,
    val type: String,
    val updatedAt: String,
    val usersCount: Int
): Serializable