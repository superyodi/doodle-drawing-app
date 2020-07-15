package com.example.doodlebot.retrofit

import com.google.gson.annotations.SerializedName




data class DoodleLabel(
    @SerializedName("success")
    var success: Boolean? = false,

    @SerializedName("label")
    var label: String = ""
)


//data class DoodleImage(
//    @SerializedName("label")
//    var label: String = "",
//
//    @SerializedName("index")
//    var index: String = ""
//)


