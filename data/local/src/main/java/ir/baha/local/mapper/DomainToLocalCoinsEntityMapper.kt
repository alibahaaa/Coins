package ir.baha.local.mapper

import ir.baha.coin_domain.entity.Coins
import ir.baha.local.dto.CoinsDto

fun Coins.toLocal(): CoinsDto = CoinsDto(
    id = id,
    name = name,
    currentPrice = price,
    imageUrl = icon,
    symbol = symbol
)