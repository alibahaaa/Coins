package ir.baha.remote.mapper

import ir.baha.coin_domain.entity.Chart
import ir.baha.remote.response.MarketChartResponse


fun MarketChartResponse.toDomain(): Chart = Chart(
    prices = prices.map { pair -> Pair(pair[0].toInt(), pair[1]) }
)