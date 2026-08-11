package ec.edu.puce.pucemarket.services

import ec.edu.puce.pucemarket.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun api(tokenStore: TokenStore): ApiService {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder().addInterceptor(logger).addInterceptor { chain ->
            val request = chain.request().newBuilder().apply {
                tokenStore.token()?.takeIf { it.isNotBlank() }?.let { header("Authorization", "Bearer $it") }
                header("Accept", "application/json")
            }.build()
            chain.proceed(request)
        }.build()
        return Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}
