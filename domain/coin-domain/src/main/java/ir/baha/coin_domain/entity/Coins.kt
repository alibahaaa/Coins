package ir.baha.coin_domain.entity

data class Coins(
    val id: String,
    val name: String,
    val price: Double,
    val icon: String,
    val symbol: String,
)