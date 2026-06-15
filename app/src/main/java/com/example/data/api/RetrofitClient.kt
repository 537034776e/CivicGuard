package com.example.data.api

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var civicApi: CivicApi? = null
    private var currentBaseUrl: String? = null

    fun getApi(context: Context): CivicApi {
        val sharedPrefs = context.getSharedPreferences("civicguard_prefs", Context.MODE_PRIVATE)
        val serverIp = sharedPrefs.getString("pref_server_ip", "10.0.2.2") ?: "10.0.2.2"
        val serverPort = sharedPrefs.getString("pref_server_port", "8080") ?: "8080"
        val appName = sharedPrefs.getString("pref_server_app", "CivicServer") ?: "CivicServer"
        
        // Tomcat Servlet Base URL e.g. http://10.0.2.2:8080/CivicServer/
        val baseUrl = "http://$serverIp:$serverPort/$appName/"

        if (civicApi == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(MockInterceptor(context)) // Handles fallback mocking based on preference
                .addInterceptor(logging)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .build()

            civicApi = retrofit.create(CivicApi::class.java)
        }
        return civicApi!!
    }
}
