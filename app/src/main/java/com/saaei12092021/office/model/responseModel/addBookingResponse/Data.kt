package com.saaei12092021.office.model.responseModel.addBookingResponse

data class Data(
    val ads: Int,
    val createdAt: String,
    val dateMilleSec: Long,
    val deleted: Boolean,
    val durationType: String,
    val endDate: String,
    val endDateMillSec: Long,
    val id: Int,
    val startDate: String,
    val startDateMillSec: Long,
    val status: String,
    val type: String,
    val updatedAt: String,
    val user: Int
)