package ir.baha.coin_domain.use_case

import ir.baha.coin_domain.entity.Chart
import ir.baha.coin_domain.entity.Resource
import ir.baha.coin_domain.repository.CoinsRepository
import javax.inject.Inject

class GetChartUseCase @Inject constructor(
    private val repo: CoinsRepository
) {

    suspend operator fun invoke(id: String): Resource<Chart> = repo.fetchChart(id)

}