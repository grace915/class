package com.eos.airqualitylayout_permission.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    companion object {
        // api 서버의 주소가 base url이 됩니다
        private const val BASE_URL = "https://api.airvisual.com/v2/"
        private var INSTANCE: Retrofit? = null

        fun getInstance(): Retrofit {
            if(INSTANCE === null){
                // null인 경우에만 생성
                // 컨버터 팩토리는 서버에서 온 json 응답을 데이터 클래스 객체로 변환해준다!
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}