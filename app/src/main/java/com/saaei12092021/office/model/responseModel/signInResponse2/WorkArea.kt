package com.saaei12092021.office.model.responseModel.signInResponse2

data class WorkArea(
    val areaName: String?, // in new response only
    val city: Int,
    val id: Int ,

    val areaName_ar: String ?, // temp for delete in old response only
    val areaName_en: String ?,// temp for delete in old response only
    val location: List<Double>? // temp for delete in old response only
)