package studios.devs.mobi

import android.app.Application
import studios.devs.mobi.di.AppComponent
import studios.devs.mobi.di.DaggerAppComponent
import studios.devs.mobi.di.modules.AppModule

class MainApplication: Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this, this)).build()
        appComponent.inject(this)
    }

}