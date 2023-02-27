package ir.baha.repository.repository

import ir.baha.coin_domain.entity.Chart
import ir.baha.coin_domain.entity.Coins
import ir.baha.coin_domain.entity.Resource
import ir.baha.coin_domain.repository.CoinsRepository
import ir.baha.local.database.CoinsDao
import ir.baha.local.mapper.toEntity
import ir.baha.local.mapper.toLocal
import ir.baha.remote.api.CoinsApiService
import ir.baha.remote.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinsRepositoryImpl @Inject constructor(
    private val api: CoinsApiService,
    private val dao: CoinsDao
) : CoinsRepository {

    override fun getCoins(): Flow<List<Coins>> = dao.getAllCoins().map { coins ->
        coins.map { coin -> coin.toEntity() }
    }

    override suspend fun getCoinSize(): Int = dao.coinsListSize()

    override suspend fun fetchCoins(): Resource<Boolean> = try {
        val res = api.getMarketData("usd", "market_cap_desc", 20, 1, false)
        val coinsDto = res.map { coin -> coin.toDomain().toLocal() }
        dao.deleteCoins()
        dao.insertCoins(coinsDto)
        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Error(e)
    }

    override suspend fun fetchChart(id: String): Resource<Chart> = try {
        val res = api.getMarketChart(id, "usd", 1)
        Resource.Success(res.toDomain())
    } catch (e: Exception) {
        Resource.Error(e)
    }

}