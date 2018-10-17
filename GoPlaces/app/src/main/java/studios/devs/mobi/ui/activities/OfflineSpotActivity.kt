package studios.devs.mobi.ui.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.Disposable
import studios.devs.mobi.MainApplication
import studios.devs.mobi.R
import studios.devs.mobi.databinding.ActivityOfflineSpotBinding
import studios.devs.mobi.extension.addTo
import studios.devs.mobi.extension.rxClick
import studios.devs.mobi.extension.rxTextChanges
import studios.devs.mobi.viewmodels.OfflineSpotViewModel
import studios.devs.mobi.viewmodels.OfflineSpotViewModelInput
import studios.devs.mobi.viewmodels.OfflineSpotViewModelInputOutput
import studios.devs.mobi.viewmodels.OfflineSpotViewModelOutput
import javax.inject.Inject

class OfflineSpotActivity : BaseActivity() {


    lateinit var binding: ActivityOfflineSpotBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: OfflineSpotViewModelInputOutput by lazy {
        ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(OfflineSpotViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.appComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_offline_spot)
    }

    override fun onStart() {
        super.onStart()
        viewModel
                .bind(this)
                .addTo(compositeDisposable)
        viewModel.input.loadSpotsFromDatabase()
    }

}

private fun OfflineSpotViewModelInputOutput.bind(activity: OfflineSpotActivity): List<Disposable> {
    return listOf(
            output.bind(activity.binding),
            output.bind(activity),
            input.bind(activity.binding)
    ).flatten()
}

private fun OfflineSpotViewModelInput.bind(binding: ActivityOfflineSpotBinding): List<Disposable> {
    return listOf(
            binding.addSpotBtn.rxClick.subscribe { addNewSpot() },
            binding.addSpotTxt.rxTextChanges.subscribe { newSpotText(it) },
            binding.btnCollectionMenu.rxClick.subscribe { showAllSpots() },
            binding.btnTutorial.rxClick.subscribe { showTutorial() },
            binding.currentLocationBox.rxClick.subscribe { useCurrentLocationIsChecked() },
            binding.spotIcon.rxClick.subscribe { startNavigationToShownSpot() },
            binding.whereBtn.rxClick.subscribe { showRandomSpot() }
    )
}

private fun OfflineSpotViewModelOutput.bind(activity: OfflineSpotActivity): List<Disposable> {
    return listOf(

    )
}

private fun OfflineSpotViewModelOutput.bind(binding: ActivityOfflineSpotBinding): List<Disposable> {
    return listOf(

    )
}


