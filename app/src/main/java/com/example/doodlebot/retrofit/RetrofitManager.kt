package com.example.doodlebot.retrofit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.create
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*


class RetrofitManager {
    val TAG: String = "로그"
//    var yoloLabel: String? = null

    companion object {
        val instance = RetrofitManager()

    }

//    val httpCall: DoodleAPI? = RetrofitClient.getClient("http://10.0.2.2:5000")?.
    val httpCall: DoodleAPI? = RetrofitClient.getClient("http://192.168.43.137:5000")?.
    create(com.example.doodlebot.retrofit.DoodleAPI::class.java)

    fun getDoodleLabel(file: File, onComplete: (String?) -> Unit) {

        // create multipart
        val requestFile: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.getName(), requestFile)


        val call = httpCall?.getDoodleLabel(body)

        call?.enqueue(object : retrofit2.Callback<DoodleLabel?> {
            override fun onFailure(call: Call<DoodleLabel?>, t: Throwable) {
                onComplete(null)
                Log.d(TAG,
                    "RetrofitManager - getDoodleLabel() - onFailure() called /t : ${t}")
            }

            override fun onResponse(call: Call<DoodleLabel?>, response: Response<DoodleLabel?>) {

                val yoloLabel = response.body()?.label
                onComplete(yoloLabel)
                Log.d(TAG,
                    "RetrofitManager - getDoodleLabel() - onResponse() called /response : ${response.body()}")
            }
        })
    }

    fun getDoodleImage(label: String, index: String, onComplete: (Bitmap?) -> Unit) {
        val call = httpCall?.getDoodleImage(label, index)

        call?.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onComplete(null)
                Log.d(TAG,
                    "RetrofitManager - getDoodleImage(label, index) - onFailure() called /t : ${t}")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val bytes = response.body()!!.bytes()
                onComplete(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

                Log.d(TAG,
                    "RetrofitManager - getDoodleImage() - onResponse() called /response : ${response.body()}")
            }
        })
    }

    fun sendDoodleIndex(label: String, index: String) {
        val call = httpCall?.sendDoodleIndex(label, index)

        call?.enqueue(object : retrofit2.Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG,
                    "RetrofitManager - sendDoodleIndex(label, index) - onFailure() called /t : ${t}")
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG,
                    "RetrofitManager - sendDoodleIndex(label, index) - onResponse() called /response : ${response.body()}")

            }
        })
    }
}


