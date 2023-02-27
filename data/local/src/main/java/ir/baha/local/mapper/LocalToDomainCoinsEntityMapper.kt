package ir.baha.local.mapper

import ir.baha.coin_domain.entity.Coins
import ir.baha.local.dto.CoinsDto


fun CoinsDto.toEntity(): Coins = Coins(
    id = id,
    name = name,
    price = currentPrice,
    icon = imageUrl,
    symbol = symbol
)