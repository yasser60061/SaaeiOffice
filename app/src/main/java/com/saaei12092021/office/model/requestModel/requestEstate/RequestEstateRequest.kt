package com.saaei12092021.office.model.requestModel.requestEstate

data class RequestEstateRequest(
    val area: List<Int>,
    val category: Int,
    val city: Int,
    val contractType: String,
    val description_ar: String,
    val description_en: String,
    val enableInstallment: Boolean,
    val enablePhoneContact: Boolean,
    val features: List<Int>,
    val owner: Int,
    val priceFrom: Int,
    val priceTo: Int,
    val priceType: String,
    val properties: List<Property>,
    val sizeFrom: Int,
    val sizeTo: Int,
    val subCategory: Int,
    val title_ar: String,
    val title_en: String
)