package ru.kirilin.skillswap.data.model.api

import retrofit2.http.*
import ru.kirilin.skillswap.data.model.User

interface UserApi {
    @POST("/v1/users")
    fun createUser(@Body user: User): User

    @POST("/v1/users")
    suspend fun createNewUser(@Body user: User): User

    @PUT("/v1/users")
    suspend fun updateUser(@Body user: User): User

    @GET("/v1/users/{id}")
    suspend fun getUserById(
        @Path("id") id: String,
        @Query("accountType"
        ) accountType: String = "GOOGLE"): User?
}