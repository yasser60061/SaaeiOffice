package com.saaei12092021.office.model.responseModel.paymentTearms

data class Data(
    val createdAt: String,
    val id: Int,
    val terms: String,
    val terms_ar: String,
    val terms_en: String,
    val type: String
)