package ru.kirilin.skillswap.data.model.api

import retrofit2.http.*
import ru.kirilin.skillswap.data.model.Skill
import java.util.*

interface SkillApi {

    @POST("/v1/skills")
    suspend fun createNewSkill(
        @Body skill: Skill,
        @Query("userId") id: String?,
        @Query("accountType") accountType: String = "GOOGLE"): Skill

    @GET("/v1/skills")
    suspend fun getAllMySkills(
        @Query("id") id: String,
        @Query("accountType") accountType: String = "GOOGLE"): List<Skill>

    @PUT("/v1/skills/{id}")
    suspend fun updateSkill(
        @Body skill: Skill,
        @Path("id") id: UUID): Skill

    @DELETE("/v1/skills/{id}")
    suspend fun removeSkill(
        @Path("id") id: UUID): Int

//    @GET("/v1/skills/{id}")
//    suspend fun getSkillById(@Path("id") id: UUID): Skill?
}