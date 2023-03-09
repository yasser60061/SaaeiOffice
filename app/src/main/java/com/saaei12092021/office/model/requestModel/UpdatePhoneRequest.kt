package com.saaei12092021.office.model.requestModel

data class UpdatePhoneRequest(
    val currentPhone: String,
    val newPhone: String,
    val verifycode: String
)