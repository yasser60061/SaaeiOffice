package com.saaei12092021.office.model.responseModel.lastContact

import java.io.Serializable

data class LastContactResponse(
    val data: List<Data>,
    val limit: Int,
    val links: Links,
    val page: Int,
    val pageCount: Int,
    val success: Boolean,
    val totalCount: Int
): Serializable