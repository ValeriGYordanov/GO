package studios.devs.mobi.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import studios.devs.mobi.di.ViewModelFactory
import studios.devs.mobi.di.ViewModelKey
import studios.devs.mobi.viewmodels.OfflineSpotViewModel
import studios.devs.mobi.viewmodels.OnboardingViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(OfflineSpotViewModel::class)
    internal abstract fun offlineSpotViewModel(offlineSpotViewModel: OfflineSpotViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    internal abstract fun onboardingViewModel(onboardingViewModel: OnboardingViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


}
