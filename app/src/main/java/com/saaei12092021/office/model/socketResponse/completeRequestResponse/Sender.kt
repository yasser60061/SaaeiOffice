package com.saaei12092021.office.model.socketResponse.completeRequestResponse

data class Sender(
    val city: CityX,
    val country: CountryX,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val rate: Int,
    val rateCount: Int,
    val rateNumbers: Int,
    val type: String
)