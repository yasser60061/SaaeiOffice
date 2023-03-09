package com.saaei12092021.office.model.requestModel

import com.google.maps.model.PlaceType

data class ServiceListOnMapModel(
    val serviceName: String,
    var isSelected: Boolean,
    val placeType: PlaceType?
)
