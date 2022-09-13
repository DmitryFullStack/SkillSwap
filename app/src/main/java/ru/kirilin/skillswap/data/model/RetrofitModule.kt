package ru.kirilin.skillswap.data.model

import com.fasterxml.jackson.databind.DeserializationFeature
import retrofit2.converter.jackson.JacksonConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.kirilin.skillswap.ui.BaseFragment

object RetrofitModule {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        encodeDefaults = true
    }

    val jsonMapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    //        @Suppress("EXPERIMENTAL_API_USAGE")
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BaseFragment.getBaseUrl())
        .addConverterFactory(JacksonConverterFactory.create(jsonMapper))
        .build()

    val userApi: UserApi = retrofit.create(UserApi::class.java)
}
