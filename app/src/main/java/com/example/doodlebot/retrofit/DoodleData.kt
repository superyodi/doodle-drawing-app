package com.example.doodlebot.retrofit

import com.google.gson.annotations.SerializedName




data class DoodleLabel(
    @SerializedName("success")
    var success: Boolean? = false,

    @SerializedName("label")
    var label: String = ""
)


