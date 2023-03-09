package com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse

data class Data(
    val area: List<Area>? = null,
    val available: Boolean? = null,
    val category: Category? = null,
    val city: City? = null,
 //   val contactsOffices: List<Any>? = null,
    val contractType: String? = null,
    val createdAt: String? = null,
    val description: String? = null,
    val description_ar: String? = null,
    val description_en: String? = null,
    val enableInstallment: Boolean? = null,
    val enablePhoneContact: Boolean? = null,
    val features: List<Feature>? = null,
    val id: Int? = null,
    val owner: Owner? = null,
    val priceFrom: Int? = null,
    val priceType: String? = null,
    val properties: List<Property>,
  //  val refusedOffices: List<Any>? = null,
    val sizeFrom: Int? = null,
    val sizeTo: Int? = null,
    val status: String? = null,
    val subCategory: SubCategory? = null,
    val title: String? = null,
    val title_ar: String? = null,
    val title_en: String? = null,
    val withdrawOffices: List<Any> ? = null,
    val errors: List<Msg>? = null,
    val success: Boolean
    )