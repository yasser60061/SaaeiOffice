package com.saaei12092021.office.model.responseModel

import java.io.File

data class UploadImagesModel(
    val imageNumber : Int ,
    var imageName : String? = "" ,
    var imageFile : File? = null ,
    var imageUrlIfUpdate : String = ""
)
