package com.saaei12092021.office.model.responseModel.SignUpResponse

data class SignUpResponse(
    val success: Boolean,
    val token: String?, // in new response only
    val user: User
)