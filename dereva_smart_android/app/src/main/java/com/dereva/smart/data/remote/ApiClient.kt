package com.dereva.smart.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class NetworkConnectionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException, is ConnectException -> {
                    throw IOException("No internet connection. Please check your network and try again.")
                }
                is SocketTimeoutException -> {
                    throw IOException("Connection timed out. Please try again later.")
                }
                is IOException -> {
                    throw IOException("A network error occurred. Please try again.")
                }
                else -> {
                    throw e
                }
            }
        }
    }
}

object ApiClient {
    
    private const val BASE_URL = "https://dereva-smart-backend.pngobiro.workers.dev/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val networkInterceptor = NetworkConnectionInterceptor()
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(networkInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: DerevaApiService = retrofit.create(DerevaApiService::class.java)
}
