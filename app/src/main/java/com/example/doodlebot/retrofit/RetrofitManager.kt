package com.example.doodlebot.retrofit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


class RetrofitManager {
    val TAG: String = "로그"

    // var yoloLabel: String? = null
    var connectResult: Boolean = false
    var httpCall: DoodleAPI? = null

    companion object {
        val instance = RetrofitManager()
    }

    fun makeHttpCall(url: String) {
        httpCall = RetrofitClient.getClient(url)
            ?.create(com.example.doodlebot.retrofit.DoodleAPI::class.java)
    }

    // 연결확인 함수. CheckIPActivity에서 입력받은 IP로 연결한다.
    fun checkConnection(onComplete: (Boolean) -> Unit) {
        val call = httpCall?.checkConnection()
        call?.enqueue(object : retrofit2.Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(
                    TAG,
                    "RetrofitManager - checkConnection() - onFailure() called /t : ${t}"
                )
                connectResult = false
                onComplete(connectResult)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(
                    TAG,
                    "RetrofitManager - checkConnection() - onResponse() called /response : ${response.body()}"
                )
                connectResult = true
                onComplete(connectResult)
            }
        })
    }

    fun getObjectDetection(file: File, onComplete: (Bitmap?) -> Unit) {
        // create multipart
        val requestFile: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.getName(), requestFile)

        val call = httpCall?.getObjectDetection(body)
        call?.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onComplete(null)
                Log.d(
                    TAG,
                    "RetrofitManager - getObjectDetection(file) - onFailure() called /t : ${t}"
                )
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val bytes = response.body()!!.bytes()
                onComplete(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

                Log.d(
                    TAG,
                    "RetrofitManager - getObjectDetection(file) - onResponse() called /response : ${response.body()}"
                )
            }
        })
    }

    fun getDoodlesImage(imgPath: String, onComplete: (Bitmap?) -> Unit) {
        val call = httpCall?.getDoodlesImage(imgPath)

        call?.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onComplete(null)
                Log.d(
                    TAG,
                    "RetrofitManager - getDoodlesImage(file) - onFailure() called /t : ${t}"
                )
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val bytes = response.body()!!.bytes()
                onComplete(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

                Log.d(
                    TAG,
                    "RetrofitManager - getDoodlesImage() - onResponse() called /response : ${response.body()}"
                )
            }
        })
    }

    fun confirmDrawDoodle(onComplete: (Boolean) -> Unit) {
        val call = httpCall?.confirmDrawDoodle()

        call?.enqueue(object : retrofit2.Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {

                Log.d(
                    TAG,
                    "RetrofitManager - confirmDrawDoodle() - onFailure() called /t : ${t}"
                )
                onComplete(false)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(
                    TAG,
                    "RetrofitManager - confirmDrawDoodle() - onResponse() called /response : ${response.body()}"
                )
                onComplete(true)
            }

        })
    }

}
