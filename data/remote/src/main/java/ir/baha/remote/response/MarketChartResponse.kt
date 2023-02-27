package ir.baha.remote.response

import com.google.gson.annotations.SerializedName

data class MarketChartResponse(
    @SerializedName("prices") val prices: List<List<Double>>,
    @SerializedName("market_caps") val marketCaps: List<List<Double>>,
    @SerializedName("total_volumes") val totalVolumes: List<List<Double>>
)