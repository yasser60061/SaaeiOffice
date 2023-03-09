package com.saaei12092021.office.model.responseModel.getAdsWithPaginationResponse

data class Owner(
    val city: CityX,
    val country: Country,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val type: String
)