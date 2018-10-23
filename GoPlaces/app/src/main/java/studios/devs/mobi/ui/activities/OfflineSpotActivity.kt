package studios.devs.mobi.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
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
    private lateinit var latitude: String
    private lateinit var longitude: String

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

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
        setUpLocationManager()
    }

    override fun onStart() {
        super.onStart()
        viewModel
                .bind(this)
                .addTo(compositeDisposable)
        viewModel.input.loadAllSpots()
    }

    fun askForLocation() {
        viewModel.input.locationSet(latitude, longitude)
    }

    fun askForSpotName() {
        showToast("Title should not be null or repeated!")
    }

    fun spotAlreadyIncluded() {
        showToast("Spot is already in list")
    }

    fun showAllSpots(allSpots: List<SpotEntity>) {
        if (allSpots.isEmpty()) {
            showToast("You haven't inserted anything, yet!")
        } else {
            val allSpotssFragment = AllSpotsDialog()
            val bundle = Bundle()
            val spotTitles = arrayListOf<String>()
            for (i in allSpots.indices) {
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

    @SuppressLint("MissingPermission")
    private fun setUpLocationManager() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showToast(getString(R.string.error_loc_unavailable))
        } else {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val lati = location.latitude
                val longi = location.longitude
                latitude = lati.toString()
                longitude = longi.toString()
            }
        }
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
                    .subscribe { activity.showToast(it.spotTitle + ", added!") },
            askForLocationStream.subscribe { activity.askForLocation() },
            askForSpotNameStream.subscribe { activity.askForSpotName() },
            spotIsAlreadyIncluded.subscribe { activity.spotAlreadyIncluded() },
            showAllSpotsStream.observeOn(AndroidSchedulers.mainThread())
                    .subscribe { activity.showAllSpots(it) },
            emptySpotListStream.subscribe { activity.showToast("You haven't inserted anything yet!") },
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




