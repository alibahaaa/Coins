package ir.baha.remote.api

import ir.baha.remote.response.CoinMarketResponse
import ir.baha.remote.response.MarketChartResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinsApiService {
    @GET("coins/markets")
    suspend fun getMarketData(
        @Query("vs_currency") currency: String,
        @Query("order") order: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("sparkline") sparkline: Boolean
    ): List<CoinMarketResponse>

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") currency: String,
        @Query("days") days: Int
    ): MarketChartResponse
}