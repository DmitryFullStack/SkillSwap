package ru.kirilin.skillswap.data.model.api

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.File
import java.util.*

interface FileApi {

    @Multipart
    @POST("/v1/files")
    suspend fun uploadNewFile(
        @Part file: MultipartBody.Part
    ): UUID

    @Streaming
    @GET("/v1/files/{id}")
    suspend fun getFileById(
        @Path("id") id: UUID
    ): ResponseBody


    companion object {
        fun createMultipartBodyPart(file: File): MultipartBody.Part {
            val requestFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            return MultipartBody.Part
                .createFormData("file", file.name, requestFile)
        }
    }
}
