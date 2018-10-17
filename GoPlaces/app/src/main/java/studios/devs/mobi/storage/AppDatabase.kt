package studios.devs.mobi.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import studios.devs.mobi.model.Spot

@Database(entities = [Spot::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun walletDao(): SpotDAO
}
