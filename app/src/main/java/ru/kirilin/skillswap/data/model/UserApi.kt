package ru.kirilin.skillswap.data.model

import retrofit2.http.*

interface UserApi {
    @POST("/v1/users")
    fun createUser(@Body user: User): User

    @POST("/v1/users")
    suspend fun createNewUser(@Body user: User): User

    @GET("/v1/users/{id}")
    suspend fun getUserById(@Path("id") id: String, @Query("accountType") accountType: String = "GOOGLE"): User?
}