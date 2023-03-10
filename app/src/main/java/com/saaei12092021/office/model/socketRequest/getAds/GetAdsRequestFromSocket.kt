package com.saaei12092021.office.model.socketRequest.getAds

data class GetAdsRequestFromSocket(
    val actionUser: Int? = null,
    val area: Int? = null,
    val category: Int? = null,
    val city: Int? = null,
    val contractType: String? = null,
    val enableInstallment: Boolean? = null,
    val enablePhoneContact: Boolean? = null,
    val features: List<Int>? = null,
    val id: String? = null,
    val km: Int? = null,
    val lang: String? = null,
    val lat: String? = null,
    val long: String? = null,
    val orderByKm: String? = null,
    val orderByRate: Boolean? = null,
    val others: String? = null,
    val owner: Int? = null,
    val priceFrom: Int? = null,
    val priceTo: Int? = null,
    val priceType: String? = null,
    val properties: List<Property>? = null,
    val related: String? = null,
    val rentType: String? = null,
    val sizeFrom: Int? = null,
    val sizeTo: Int? = null,
    val sortByPrice: String? = null,
    val status: String? = null,
    val subCategory: Int? = null,
)