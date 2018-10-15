package studios.devs.mobi.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import studios.devs.mobi.activities.MainActivity

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeActivityInjector(): MainActivity
}
