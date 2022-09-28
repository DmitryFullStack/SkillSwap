package ru.kirilin.skillswap.data.model

import kotlinx.serialization.Serializable
import ru.kirilin.skillswap.config.BigDecimalSerializer
import ru.kirilin.skillswap.config.UUIDSerializer
import java.math.BigDecimal
import java.util.*

@Serializable
data class Skill (
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val user: User? = null,
    val name: String? = null,
    val level: Level? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal? = null
)