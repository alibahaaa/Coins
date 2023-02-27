package ir.baha.local.dto

import androidx.room.Entity

@Entity(tableName = "coins", primaryKeys = ["id"])
data class CoinsDto(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val imageUrl: String,
)