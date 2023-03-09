package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Owner(
    val city: CityX,
    val country: Country,
    val fullname: String,
    val phone: String,
    val email: String,
    val id: Int,
    val img: String,
    val lastSeen: Long,
    val online: Boolean,
    val rate: Int,
    val rateCount: Int,
    val rateNumbers: Int,
    val type: String
) : Serializable