package com.saaei12092021.office.model.requestModel

data class SignInRequest(
    val phone: String,
    val password: String,
    val token: String,
    val udId: String
)