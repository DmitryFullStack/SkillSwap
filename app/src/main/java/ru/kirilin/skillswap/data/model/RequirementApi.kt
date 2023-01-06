package ru.kirilin.skillswap.data.model

import retrofit2.http.*
import java.util.*

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

    @PUT("/v1/requirements/{id}")
    suspend fun updateRequirements(
        @Body req: Requirement,
        @Path("id") id: UUID): Requirement

    @DELETE("/v1/requirements/{id}")
    suspend fun removeRequirementById(
        @Path("id") id: UUID
    ): Int

}
