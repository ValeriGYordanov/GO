package studios.devs.mobi.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import studios.devs.logging.Logger
import studios.devs.mobi.BuildConfig
import studios.devs.mobi.logging.DefaultLogger
import javax.inject.Singleton

@Module
class LoggerModule {

    @Provides
    @Singleton
    fun provideLogger(context: Context): Logger {
        val logger = DefaultLogger(context)
        if (BuildConfig.DEBUG) {
            logger
                    .configureAnalytics(false, true, "Debug")
                    .configureCrashlytics(false, true, "Debug")
        } else {
            logger
                    .configureAnalytics(true, false, "Release")
                    .configureCrashlytics(true, false, "Release")
        }
        return logger
    }

}
