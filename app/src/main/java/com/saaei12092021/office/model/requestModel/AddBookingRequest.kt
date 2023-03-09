package com.saaei12092021.office.model.requestModel

data class AddBookingRequest(
    val durationType: String,
    val endDate: String,
    val startDate: String,
    val type: String
)