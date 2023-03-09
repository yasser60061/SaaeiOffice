package com.saaei12092021.office.model.requestModel

class CompleteProfileRequest(
    val country: String,
    val phone: String,
    val fullname: String,
    val email: String,
  //  val type: String = "OFFICE",
   // val password: String,
    val city: Int,
    val completeProfile: Boolean,
    val workCity: ArrayList<Int>,
    val workArea: ArrayList<Int>,
    val workCategory: ArrayList<Int>,
    val workSubCategory: ArrayList<Int>,
  //  val idType: Int?,
    val idNumber: String ?,
    val adNumber: Int ?
)
