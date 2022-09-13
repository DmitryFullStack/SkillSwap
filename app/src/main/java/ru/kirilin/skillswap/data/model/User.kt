package ru.kirilin.skillswap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String?,
    val age: Int? = null,
    val gender: String? = null,
    val name: String?,
    val logo: String? = null,
    val id: UserId? = null
)

@Serializable
data class UserId (
    val accountNumber: String,
    val accountType: String = "GOOGLE"
)
