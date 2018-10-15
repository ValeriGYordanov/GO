package studios.devs.mobi.di.modules

import dagger.Module
import dagger.Provides
import studios.devs.mobi.repositories.IMainRepository
import studios.devs.mobi.repositories.MainRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMainRepository(): IMainRepository {
        return MainRepository()
    }

}
