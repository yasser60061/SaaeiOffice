package com.saaei12092021.office.model.requestModel

class UserSignupRequest(
    val country: String,
    val fullname: String,
    val phone: String,
    val email: String,
    val type: String,
    val agencyType : String ,
    val password: String,
    val idType: Int? = null,
    val idNumber: String? = null,
    val adNumber: Int? = null
)
