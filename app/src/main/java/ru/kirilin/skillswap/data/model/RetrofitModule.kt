package ru.kirilin.skillswap.data.model

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.kirilin.skillswap.ui.BaseFragment

object RetrofitModule {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    //        @Suppress("EXPERIMENTAL_API_USAGE")
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BaseFragment.getBaseUrl())
        .addConverterFactory(json.asConverterFactory("application/hal+json".toMediaType()))
        .build()

    val userApi: UserApi = retrofit.create(UserApi::class.java)
}
