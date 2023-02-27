package ir.baha.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.baha.local.dto.CoinsDto

@Database(
    entities = [CoinsDto::class],
    version = 1,
)
abstract class CoinsDatabase : RoomDatabase() {
    abstract val coinsDao: CoinsDao

    companion object {
        const val DATABASE_NAME = "coins_db"
    }
}