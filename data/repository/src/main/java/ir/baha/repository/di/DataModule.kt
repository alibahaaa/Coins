package ir.baha.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.baha.coin_domain.repository.CoinsRepository
import ir.baha.repository.repository.CoinsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindCoinsRepository(
        coinsRepository: CoinsRepositoryImpl,
    ): CoinsRepository
}