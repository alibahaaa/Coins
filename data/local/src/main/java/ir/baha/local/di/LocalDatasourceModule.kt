package ir.baha.local.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.baha.local.database.CoinsDao
import ir.baha.local.database.CoinsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatasourceModule {

    @Singleton
    @Provides
    fun provideCoinsDatabase(app: Application): CoinsDatabase =
        Room.databaseBuilder(
            app,
            CoinsDatabase::class.java,
            CoinsDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideCoinsDao(db: CoinsDatabase): CoinsDao =
        db.coinsDao

}