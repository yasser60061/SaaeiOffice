package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class AdsByIdResponse(
    val advertisement: Advertisement,
    val success: Boolean
) : Serializable