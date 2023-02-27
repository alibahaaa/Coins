package ir.baha.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.baha.local.dto.CoinsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinsDao {

    @Query("SELECT * FROM coins")
    fun getAllCoins(): Flow<List<CoinsDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(news: List<CoinsDto>)

    @Query("DELETE FROM coins")
    suspend fun deleteCoins()

    @Query("SELECT COUNT(*) FROM coins")
    suspend fun coinsListSize(): Int

}