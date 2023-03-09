package com.saaei12092021.office.model.socketResponse.completeRequestResponse

data class Reciever(
    val city: City,
    val country: Country,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val rate: Int,
    val rateCount: Int,
    val rateNumbers: Int,
    val type: String
)