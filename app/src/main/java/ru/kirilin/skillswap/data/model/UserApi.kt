package ru.kirilin.skillswap.data.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*

interface UserApi {
    @POST("/v1/users")
    fun createUser(@Body user: User): Call<User>

    @GET("/v1/user/{id}")
    fun getUserById(@Path("id") id: UUID): User
}