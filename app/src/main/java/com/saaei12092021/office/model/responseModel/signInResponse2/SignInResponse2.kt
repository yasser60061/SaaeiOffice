package com.saaei12092021.office.model.responseModel.signInResponse2

data class SignInResponse2(
    val success: Boolean,
    val token: String,
    val user: User ,
    val isVerified : Boolean ? , // temp for delete
    val accountType : String ? // temp for delete
)