package studios.devs.mobi.di

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import studios.devs.mobi.MainApplication
import studios.devs.mobi.di.modules.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    AndroidInjectionModule::class,
    ActivityModule::class,
    ViewModelModule::class,
    RepositoryModule::class,
    LoggerModule::class])
interface AppComponent: AndroidInjector<MainApplication> {}