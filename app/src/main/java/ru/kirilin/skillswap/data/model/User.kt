package ru.kirilin.skillswap.data.model

import kotlinx.serialization.Serializable
import ru.kirilin.skillswap.config.UUIDSerializer
import java.util.*

@Serializable
data class User(
    val login: String?,
    val age: Int? = null,
    val gender: String? = null,
    val name: String?,
    @Serializable(with = UUIDSerializer::class)
    var logoId: UUID? = null,
    var id: UserId? = null
)

@Serializable
data class UserId (
    var accountNumber: String,
    var accountType: String = "GOOGLE"
)
