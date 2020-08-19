package com.example.doodlebot.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// 싱글톤 패턴 적용
object RetrofitClient {
    val TAG: String = "로그"

    // 레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? = null
    private var prevBaseUrl: String = ""

    // 레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String): Retrofit? {

        // 만약 레트로핏 클라이언트가 없을때 새로 만들어서 return, 이미 있다면 있는 것 return
        if (retrofitClient == null || prevBaseUrl != baseUrl) {
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        prevBaseUrl = baseUrl
        return retrofitClient
    }
}
