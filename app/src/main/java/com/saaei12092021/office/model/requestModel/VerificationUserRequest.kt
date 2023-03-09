package com.saaei12092021.office.model.requestModel

data class VerificationUserRequest(
    val phone: String,
    val verifycode: String
)