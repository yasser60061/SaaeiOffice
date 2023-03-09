package com.saaei12092021.office.model.responseModel.GeneralErrorBodyResponse

data class GeneralErrorResponse(
    val errors: List<Error>,
    val success: Boolean
)