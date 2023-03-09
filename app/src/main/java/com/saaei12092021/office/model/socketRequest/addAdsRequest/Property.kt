package com.saaei12092021.office.model.socketRequest.addAdsRequest

import com.google.gson.annotations.SerializedName

data class Property(
    @SerializedName("from")
    val from: String?,
    @SerializedName("property")
    val property: String,
    @SerializedName("to")
    val to: String ?,
    @SerializedName("value")
    val value : ArrayList<String>?
)