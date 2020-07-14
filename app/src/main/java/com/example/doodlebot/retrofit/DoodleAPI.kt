package com.example.doodlebot.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface DoodleAPI {
    @GET("/test/")
    fun getTestData() : Call<DoodleLabel>

//    @GET("/inspect/")
//    fun getDoodleData(
//        @Query("success") success: Boolean,
//        @Query("label") label: String
//    ) : Call<JsonElement>

    @Multipart
    @POST("/inspect/")
    fun getDoodleLabel(
        @Part image: MultipartBody.Part?
    ): Call<DoodleLabel?>


//    @Multipart
//    @POST("/uploadFile")
//    fun uploadPhoto(@Part("file\"; filename=\"photo.jpg\" ") photo: RequestBody?): Call<DoodleLabel?>?
//

}