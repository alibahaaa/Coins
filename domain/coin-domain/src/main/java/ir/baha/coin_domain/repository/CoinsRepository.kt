package ir.baha.coin_domain.repository

import ir.baha.coin_domain.entity.Chart
import ir.baha.coin_domain.entity.Coins
import ir.baha.coin_domain.entity.Resource
import kotlinx.coroutines.flow.Flow

interface CoinsRepository {
    fun getCoins(): Flow<List<Coins>>
    suspend fun getCoinSize(): Int
    suspend fun fetchCoins(): Resource<Boolean>
    suspend fun fetchChart(id: String): Resource<Chart>
}