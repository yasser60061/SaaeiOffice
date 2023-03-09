package com.saaei12092021.office.model.responseModel.tempResponse

data class User(
    val accountType: String,
    val adsCount: Int,
    val agencyType: String,
    val approved: Boolean,
    val blockReason: String,
    val city: Any,
    val country: Country,
    val createdAt: String,
    val email: String,
    val enableNotif: Boolean,
    val fullname: String,
    val id: Int,
    val idType: String,
    val img: String,
    val isVerified: Boolean,
    val lastSeen: Int,
    val online: Boolean,
    val phone: String,
    val type: String,
    val updatedAt: String
)