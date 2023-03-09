package com.saaei12092021.office.model.requestModel

data class ResetPasswordByPhoneRequest(
    val newPassword: String,
    val phone: String
)