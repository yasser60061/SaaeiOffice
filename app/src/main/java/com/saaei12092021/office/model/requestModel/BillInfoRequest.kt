package com.saaei12092021.office.model.requestModel

import com.google.gson.annotations.SerializedName

data class BillInfoRequest(
    val billType: String,
    val durationType: String? = null,

    @SerializedName("package")
    val package_1: Int? = null,
    val ads: Int? = null ,
    val days: Int? = null
)