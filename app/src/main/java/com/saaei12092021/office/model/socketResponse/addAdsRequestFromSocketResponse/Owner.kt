package com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse

data class Owner(
    val city: City,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val type: String
)