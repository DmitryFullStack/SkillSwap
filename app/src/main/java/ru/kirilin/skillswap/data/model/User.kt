package ru.kirilin.skillswap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String?,
    val age: Int? = null,
    val gender: String? = null,
    val name: String?,
    val logo: String? = null,
    var id: UserId? = null
)

@Serializable
data class UserId (
    var accountNumber: String,
    var accountType: String = "GOOGLE"
)
