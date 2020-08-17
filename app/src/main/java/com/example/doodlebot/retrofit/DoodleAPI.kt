package com.example.doodlebot.retrofit

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface DoodleAPI {


    @GET("/test/")
    fun checkConnection() : Call<Void>

    @Multipart
    @POST("/inspect/")
    fun getDoodleLabel(
        @Part image: MultipartBody.Part?
    ): Call<DoodleLabel?>

    @GET("/doodle/")
    fun getDoodleImage(
        @Query("label") label: String,
        @Query("index") index: String
    ): Call<ResponseBody>

    @GET("/draw/")
    fun sendDoodleIndex(
        @Query("label") label: String,
        @Query("index") index: String
    ): Call<Void>



//    request: 인덱스, 라벨
//    result: 이미지(bin)

}