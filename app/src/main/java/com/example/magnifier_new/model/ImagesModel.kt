package com.example.magnifier_new.model

import android.net.Uri

data class ImagesModel(
    val uri: Uri,
    val name: String,
    var isSelected:Boolean =false
)
