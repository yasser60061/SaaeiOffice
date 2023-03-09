package com.saaei12092021.office.model.socketRequest.addAdsRequest

import com.google.gson.annotations.SerializedName

data class AddAdsRequestForSocket(
    @SerializedName("area")
    val area: List<Int>,
    @SerializedName("category")
    val category: Int,
    @SerializedName("city")
    val city: Int,
    @SerializedName("contractType")
    val contractType: String,
    @SerializedName("rentType")
    val rentType: String? = null,
    @SerializedName("description_ar")
    val description_ar: String,
    @SerializedName("description_en")
    val description_en: String,
    @SerializedName("enableInstallment")
    val enableInstallment: Boolean,
    @SerializedName("enablePhoneContact")
    val enablePhoneContact: Boolean,
    @SerializedName("features")
    val features: List<Int>,
    @SerializedName("owner")
    val owner: Int,
    @SerializedName("priceFrom")
    val priceFrom: Int,
    @SerializedName("priceTo")
    val priceTo: Int,
    @SerializedName("priceType")
    val priceType: String,
    @SerializedName("properties")
    val properties: List<Property>,
    @SerializedName("sizeFrom")
    val sizeFrom: Int,
    @SerializedName("sizeTo")
    val sizeTo: Int,
    @SerializedName("subCategory")
    val subCategory: Int,
    @SerializedName("title_ar")
    val title_ar: String,
    @SerializedName("title_en")
    val title_en: String

)