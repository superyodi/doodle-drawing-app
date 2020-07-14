package com.example.doodlebot.retrofit

import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.create
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*


class RetrofitManager {
    val TAG: String = "로그"

    companion object {
        val instance = RetrofitManager()
    }

//    val httpCall: DoodleAPI? = RetrofitClient.getClient("http://10.0.2.2:5000")?.
    val httpCall: DoodleAPI? = RetrofitClient.getClient("http://192.168.43.137:5000")?.
    create(com.example.doodlebot.retrofit.DoodleAPI::class.java)

    fun getTestData() {
        val call = httpCall?.getTestData()

        call?.enqueue(object : retrofit2.Callback<DoodleLabel> {
            override fun onFailure(call: Call<DoodleLabel>, t: Throwable) {

                Log.d(TAG,
                    "RetrofitManager - getTest() - onFailure() called /t : ${t}")
            }

            override fun onResponse(call: Call<DoodleLabel>, response: Response<DoodleLabel>) {

                Log.d(
                    TAG,
                    "RetrofitManager - getTest() - onResponse() called /response : ${response.body()}"
                )
            }
        })
    }

    fun getDoodleLabel(file: File) {

        // create multipart
        val requestFile: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.getName(), requestFile)


        val call = httpCall?.getDoodleLabel(body)

        call?.enqueue(object : retrofit2.Callback<DoodleLabel?> {
            override fun onFailure(call: Call<DoodleLabel?>, t: Throwable) {

                Log.d(TAG,
                    "RetrofitManager - getDoodleLabel() - onFailure() called /t : ${t}")
            }

            override fun onResponse(call: Call<DoodleLabel?>, response: Response<DoodleLabel?>) {
                Log.d(
                    TAG,
                    "RetrofitManager - getDoodleLabel() - onResponse() called /response : ${response.raw()}"
                )
            }
        })
    }

}