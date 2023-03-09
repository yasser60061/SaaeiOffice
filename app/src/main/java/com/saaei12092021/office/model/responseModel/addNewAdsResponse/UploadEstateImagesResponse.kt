package com.saaei12092021.office.model.responseModel.addNewAdsResponse

data class UploadEstateImagesResponse(
    val adsFile: List<String>? = null,
    val imgs: List<String>? = null,
    val imgs3D: List<String>? = null
)