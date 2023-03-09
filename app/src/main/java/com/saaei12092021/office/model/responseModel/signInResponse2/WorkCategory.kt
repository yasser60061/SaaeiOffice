package com.saaei12092021.office.model.responseModel.signInResponse2

data class WorkCategory(
    val categoryName: String?, // in new response only
    val id: Int,
    val type: String ,

    val categoryName_ar: String?,// temp for delete in old response only
    val categoryName_en: String?,// temp for delete in old response only
    val child: List<Int>?,// temp for delete in old response only
    val createdAt: String?,// temp for delete in old response only
    val deleted: Boolean?,// temp for delete in old response only
    val details: String?,// temp for delete in old response only
    val hasChild: Boolean?,// temp for delete in old response only
    val img: String?,// temp for delete in old response only
    val main: Boolean?,// temp for delete in old response only
    val priority: Int?,// temp for delete in old response only
)