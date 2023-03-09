package com.saaei12092021.office.model.responseModel.adRequestedByIdResponse

data class Owner(
    val city: CityX,
    val country: Country,
    val email: String,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val phone: String,
    val type: String
)