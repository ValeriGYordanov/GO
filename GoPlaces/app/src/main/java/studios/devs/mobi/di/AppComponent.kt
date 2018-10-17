package studios.devs.mobi.di

import android.content.Context
import dagger.Component
import studios.devs.mobi.MainApplication
import studios.devs.mobi.ui.activities.OfflineSpotActivity
import studios.devs.mobi.di.modules.*
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AppModule::class,
    ViewModelModule::class,
    DatabaseModule::class,
    ServiceModule::class,
    RepositoryModule::class,
    LoggerModule::class])
interface AppComponent {
    fun context(): Context

    /**
     * Inject your activities like this :
     * fun inject(activity: AnyActivity)
     */
    fun inject(app: MainApplication)

    //region Activities
    fun inject(clazz: OfflineSpotActivity)
    //endregion


}
