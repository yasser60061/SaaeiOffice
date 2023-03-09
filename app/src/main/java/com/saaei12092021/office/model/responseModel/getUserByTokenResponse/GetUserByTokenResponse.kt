package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class GetUserByTokenResponse(
    val adsCount: Int,
    val adsRequestCount: Int,
    val completedAdsCount: Int,
    val completedAdsRequest: Int,
    val completedRentAdsCount: Int,
    val completedSaleAdsCount: Int,
    val contactRequestCount: Int,
    val rentAdsCount: Int,
    val saleAdsCount: Int,
    val success: Boolean,
    val unCompletedAdsRequest: Int,
    val unreadNotifs: Int,
    val user: User,
    val viewsCount: Int
) : Serializable