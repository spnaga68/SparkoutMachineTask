package com.imtamila.sparkoutmachinetask.api

import android.content.Context
import com.imtamila.sparkoutmachinetask.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    fun getRetrofitClientWithUrl(
        context: Context?,
        baseUrl: String = "https://reqres.in/api/",
        connectTimeOut: Long = 30,
        readTimeOut: Long = 30,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): ApiClient {
        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder().connectTimeout(connectTimeOut, timeUnit)
            .readTimeout(readTimeOut, timeUnit).apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(logging)
            }

        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory()).client(httpClient.build())

        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(ApiClient::class.java)
    }
}