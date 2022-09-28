package ru.kirilin.skillswap.data.model

import retrofit2.http.*
import java.util.*

interface SkillApi {

    @POST("/v1/skills")
    suspend fun createNewSkill(@Body skill: Skill,
                               @Query("userId") id: String?,
                               @Query("accountType") accountType: String = "GOOGLE"): Skill

    @GET("/v1/skills")
    suspend fun getAllMySkills(
        @Query("id") id: String,
        @Query("accountType") accountType: String = "GOOGLE"): List<Skill>

//    @GET("/v1/skills/{id}")
//    suspend fun getSkillById(@Path("id") id: UUID): Skill?
}