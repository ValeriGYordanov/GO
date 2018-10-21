package studios.devs.mobi.ui.activities

import android.os.Bundle
import android.os.Parcelable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import studios.devs.mobi.MainApplication
import studios.devs.mobi.R
import studios.devs.mobi.databinding.ActivityOfflineSpotBinding
import studios.devs.mobi.extension.addTo
import studios.devs.mobi.extension.rxClick
import studios.devs.mobi.extension.rxTextChanges
import studios.devs.mobi.extension.toVisibility
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.ui.dialogs.AllSpotsDialog
import studios.devs.mobi.ui.dialogs.SPOTS_LIST
import studios.devs.mobi.viewmodels.OfflineSpotViewModel
import studios.devs.mobi.viewmodels.OfflineSpotViewModelInput
import studios.devs.mobi.viewmodels.OfflineSpotViewModelInputOutput
import studios.devs.mobi.viewmodels.OfflineSpotViewModelOutput
import java.util.ArrayList
import javax.inject.Inject

class OfflineSpotActivity : BaseActivity(), AllSpotsDialog.SelectedSpotListener {

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
        viewModel.input.loadAllSpots()
    }

    fun askForLocation(){
        showToastWithArgument(true, "AskingForLocation", "AskingForLocation")
        //viewModel.input.locationSet("lat", "long")
    }
    fun askForSpotName(){
        showToast("Title should not be null or repeated!")
    }
    fun spotAlreadyIncluded(){
        showToast("Spot is already in list")
    }
    fun showAllSpots(allSpots: List<SpotEntity>){
        if (allSpots.isEmpty()){
            showToast("You haven't inserted anything, yet!")
        }else{
            val allSpotssFragment = AllSpotsDialog()
            val bundle = Bundle()
            val spotTitles = arrayListOf<String>()
            for (i in allSpots.indices){
                spotTitles.add(allSpots[i].spotTitle)
            }
            bundle.putParcelableArrayList(SPOTS_LIST, allSpots as ArrayList<out Parcelable>)
            allSpotssFragment.arguments = bundle
            allSpotssFragment.show(supportFragmentManager, "spots")
        }
    }

    override fun onSpotSelected(spotTitle: String) {
        showToast("Clicked on : $spotTitle")
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
            binding.addSpotTxt.rxTextChanges.subscribe { newSpotText(it.trim()) },
            binding.btnCollectionMenu.rxClick.subscribe { showAllSpots() },
            binding.btnTutorial.rxClick.subscribe { showTutorial() },
            binding.currentLocationBox.rxClick.subscribe { useCurrentLocationIsChecked() },
            binding.spotIcon.rxClick.subscribe { startNavigationToShownSpot() },
            binding.whereBtn.rxClick.subscribe { showRandomSpot() }
    )
}

private fun OfflineSpotViewModelOutput.bind(activity: OfflineSpotActivity): List<Disposable> {
    return listOf(
            newSpotAddedStream.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { activity.showToast(it.spotTitle +", added!") },
            askForLocationStream.subscribe { activity.askForLocation() },
            askForSpotNameStream.subscribe { activity.askForSpotName() },
            spotIsAlreadyIncluded.subscribe { activity.spotAlreadyIncluded() },
            showAllSpotsStream.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { activity.showAllSpots(it) },
            errorStream.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { activity.renderError(it.description) },
            loadingViewModelOutput.isLoading.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { activity.renderLoading(it) }
    )
}

private fun OfflineSpotViewModelOutput.bind(binding: ActivityOfflineSpotBinding): List<Disposable> {
    return listOf(
            shouldShowTutorialStream.subscribe { binding.btnTutorial.visibility = it.toVisibility() },
            randomSpotStream.subscribe { binding.spotTxt.text = it },
            isCurrectLocationChecked.subscribe { binding.currentLocationBox.isChecked = it }
    )
}




