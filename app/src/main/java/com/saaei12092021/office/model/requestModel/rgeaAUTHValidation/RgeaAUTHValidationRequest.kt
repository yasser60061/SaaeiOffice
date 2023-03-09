package com.saaei12092021.office.model.requestModel.rgeaAUTHValidation

data class RgeaAUTHValidationRequest(
    val idType: String,
    val idNumber: String,
    val adNumber: String ,
    val authNumber : String ? = null
)