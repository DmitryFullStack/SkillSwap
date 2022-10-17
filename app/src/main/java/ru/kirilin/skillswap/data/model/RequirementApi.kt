package ru.kirilin.skillswap.data.model

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RequirementApi {

    @POST("/v1/requirements")
    suspend fun createNewRequirement(
        @Body req: Requirement,
        @Query("userId") id: String?,
        @Query("accountType") accountType: String = "GOOGLE"): Requirement

    @GET("/v1/requirements")
    suspend fun getAllMyRequirements(
        @Query("id") id: String,
        @Query("accountType") accountType: String = "GOOGLE"): List<Requirement>

}
