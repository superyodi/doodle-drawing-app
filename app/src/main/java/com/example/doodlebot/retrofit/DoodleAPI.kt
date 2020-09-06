package com.example.doodlebot.retrofit

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface DoodleAPI {
    @GET("/test/")
    fun checkConnection(): Call<Void>

    @Multipart
    @POST("/inspect/")
    fun getObjectDetection(
        @Part image: MultipartBody.Part?
    ): Call<ResponseBody>

    @GET("/doodles/")
    fun getDoodlesImage(
        @Query("imgPath") label: String
    ): Call<ResponseBody>

    @GET("/doodle/")
    fun getDoodleImage(
        @Query("label") label: String,
        @Query("index") index: String
    ): Call<ResponseBody>

    @GET("/draw/")
    fun confirmDrawDoodle(): Call<Void>
}
