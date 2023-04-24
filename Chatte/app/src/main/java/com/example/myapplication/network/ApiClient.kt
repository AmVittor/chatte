package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient {
    private lateinit var retrofit: Retrofit;



     fun getClient(): Retrofit{
        if(retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }
         return retrofit
    }

}