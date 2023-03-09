package com.saaei12092021.office.model.responseModel.tempResponse

data class SignUpResponse(
    val success: Boolean,
    val token: String,
    val user: User
)