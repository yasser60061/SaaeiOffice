package com.saaei12092021.office.model.requestModel.addNewAdsRequest

import com.google.gson.annotations.SerializedName

data class PropertiesItem(
    @SerializedName("property")
    val property: String,
    @SerializedName("value")
    val value: String
)