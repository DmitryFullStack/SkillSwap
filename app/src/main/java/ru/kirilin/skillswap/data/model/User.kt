package ru.kirilin.skillswap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String?,
    val age: Int?,
    val gender: String?,
    val name: String?
)