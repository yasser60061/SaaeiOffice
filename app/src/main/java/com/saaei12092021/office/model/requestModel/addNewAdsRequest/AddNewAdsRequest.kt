package com.saaei12092021.office.model.requestModel.addNewAdsRequest

data class AddNewAdsRequest(
    val title_en: String,
    val title_ar: String,
    val description_en: String,
    val description_ar: String,
    val bestFeature_en: String,
    val bestFeature_ar: String,
    val address_en: String,
    val address_ar: String,
    val contractType: String, // SALE - RENT
    val rentType: String?,
    val size: Int,
    val priceType: String, // NORMAL - ON-SOOM
    val price: Int,
    val location: List<Double>?,
    val category: Int,
    val subCategory: Int,
    val city: Int,
    val area: Int,
    val imgs: ArrayList<String>?,
    val imgs3D: ArrayList<String>?,
    val link3D: ArrayList<String>? ,
    val adsFile: ArrayList<String>?,
    val properties: ArrayList<PropertiesItem>?,
    val features: ArrayList<Int>?,
    val enableInstallment: Boolean,
    val enablePhoneContact: Boolean,
    val region: Int,
    val contraindications: Boolean, // هل يوجد موانع تمنع التصرف والانتفاع بالعقار؟
    val contraindicationsDesc: String, // تفاصيل المانع
    val undocumentedRights: Boolean, // هل يوجد بعض الحقوق الغير موثقه؟
    val undocumentedRightsDesc: String, // تفاصيل الحقوق
    val influentialInformation: Boolean, // هل يوجد معلومات تؤثر على العقار؟
    val influentialInformationDesc: String, // تتفاصيل المعلومات
    val saudiBuildingCode: Boolean,
    val guarantees: Boolean, // هل هناك ضمانات
    val guaranteesDesc: String, // تفاصيل الضمانات
    val advertiserClass: String, // OWNER - COMMISSIONER صفه المعلن
    val commissionNumber: Int, // رقم التفويض اذا كان صفه المعلن COMMISSIONER
    val adNumber: String,
    val deposit: Int? = null,
    val hasDeposit: Boolean? = null,

    // for Building only and may be null
    val unitNumber: String? = null,
    val floors: Int? = null,
    val units: ArrayList<Unit>? = null,
    val oldUnits : ArrayList<Int>? = null ,

    //  for Plane only may be null
    val streetName: String?,
    var lands: ArrayList<Int>?,
    var oldLands : ArrayList<Int> ? = null ,
    var startSaleDate: String?
)
