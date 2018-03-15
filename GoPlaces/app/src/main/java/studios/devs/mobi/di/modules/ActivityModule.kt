package studios.devs.mobi.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import studios.devs.mobi.ui.activities.OfflineSpotActivity

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeActivityInjector(): OfflineSpotActivity
}
