package com.saaei12092021.office.model.responseModel.termsResponse

data class Terms(
    val createdAt: String,
    val id: Int,
    val privacy: String,
    val privacy_ar: String,
    val privacy_en: String,
    val terms: String,
    val terms_ar: String,
    val terms_en: String
)