package ir.baha.remote.mapper

import ir.baha.coin_domain.entity.Coins
import ir.baha.remote.response.CoinMarketResponse


fun CoinMarketResponse.toDomain() : Coins = Coins(
    id = id,
    name = name,
    price = currentPrice,
    icon = imageUrl,
    symbol = symbol
)