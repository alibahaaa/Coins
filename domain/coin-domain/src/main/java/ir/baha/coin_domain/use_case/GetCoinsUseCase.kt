package ir.baha.coin_domain.use_case

import ir.baha.coin_domain.entity.Coins
import ir.baha.coin_domain.entity.Resource
import ir.baha.coin_domain.repository.CoinsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetCoinsUseCase @Inject constructor(
    private val repo: CoinsRepository
) {

    suspend operator fun invoke(
        forceRefresh: Boolean
    ): Resource<Flow<List<Coins>>> {
        if (repo.getCoinSize() == 0 || forceRefresh) {
            return when (val res = repo.fetchCoins()) {
                is Resource.Error -> Resource.Error(res.exception!!)
                is Resource.Success -> Resource.Success(repo.getCoins())
            }
        }

        return Resource.Success(repo.getCoins())
    }

}