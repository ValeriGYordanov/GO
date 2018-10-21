package studios.devs.mobi.di.modules

import dagger.Module
import dagger.Provides
import studios.devs.mobi.repositories.IMainRepository
import studios.devs.mobi.repositories.MainRepository
import studios.devs.mobi.storage.AppDatabase
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMainRepository(database: AppDatabase): IMainRepository {
        return MainRepository(database)
    }

}
