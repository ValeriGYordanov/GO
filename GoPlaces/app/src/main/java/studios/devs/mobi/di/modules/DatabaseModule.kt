package studios.devs.mobi.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import studios.devs.mobi.storage.AppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        // For testing just create a new module with Room.inMemoryDatabaseBuilder(...)
        return Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
    }

}
