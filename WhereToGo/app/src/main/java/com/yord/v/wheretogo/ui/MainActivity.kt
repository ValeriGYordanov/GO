package com.yord.v.wheretogo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yord.v.wheretogo.R
import com.yord.v.wheretogo.injection.Injection
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.ui.DeleteDialogFragment.OptionDialogListener
import com.yord.v.wheretogo.ui.PlaceListFragment.SelectedPlaceListener
import com.yord.v.wheretogo.viewmodel.PlaceViewModel
import com.yord.v.wheretogo.viewmodel.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import java.util.*

class MainActivity : AppCompatActivity(), OptionDialogListener, SelectedPlaceListener {

    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: PlaceViewModel
    private lateinit var mAdView : AdView

    private lateinit var places: MutableList<Place>
    private val disposable = CompositeDisposable()
    private var buttonVisibility = true

    private var latitude = ""
    private var longitude = ""

    private val mVisibilityObservable = PublishSubject.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textInputLayout.setTypeface(Typeface.createFromAsset(assets, getString(R.string.path_fonts)))
        place_txt.text = savedInstanceState?.getCharSequence("place")
        loadAd()

        requestPermission()

        factory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaceViewModel::class.java)

        syncUI()

        where_btn.setOnClickListener {
            val random = Random()
            if (places.count() == 0) {
                Toast.makeText(this, getString(R.string.empty_field), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(textInputLayout)

            } else {
                val randomPlace = random.nextInt(places.count())
                viewModel.assignCurrentPlace(places[randomPlace])
                place_txt.text = viewModel.showCurrentPlace().placeTitle
                placeAnimation()
            }
        }

        add_place_btn.setOnClickListener {
            val newPlaceTtl = add_place_txt.text.toString().trim()
            val newPlace = Place(Random().nextLong(), newPlaceTtl, latitude, longitude)
            var placeIsNotInList = true

            if (!newPlaceTtl.isEmpty()) {
                places.forEach { place: Place ->
                    run {
                        if (place.placeTitle.equals(newPlaceTtl, true)) {
                            placeIsNotInList = false
                        }
                    }
                }
                if (placeIsNotInList) {
                    viewModel.addNewPlace(newPlace)
                    places.plus(newPlace)
                    Toast.makeText(this, newPlace.placeTitle + getString(R.string.place_successfuly_added), LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, newPlace.placeTitle + getString(R.string.place_already_in_list), LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.missing_place_title), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(textInputLayout)
            }

            add_place_txt.text.clear()

        }

        place_image.setOnClickListener { _ ->
            if (!place_txt.text.isEmpty() && place_txt.text.toString() != "") {
                gotoPlace()
            } else {
                Toast.makeText(this, getString(R.string.delete_dialog_empty_place), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(place_image)
            }
        }

        place_image.setOnLongClickListener { _ ->
            if (!place_txt.text.isEmpty() && place_txt.text.toString() != "") {
                val bundle = Bundle()
                bundle.putCharSequence("place", viewModel.showCurrentPlace().placeTitle)
                val dialog = DeleteDialogFragment()
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, "delete")
                true
            } else {
                Toast.makeText(this, getString(R.string.delete_dialog_empty_place), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(place_image)
                true
            }
        }

        btn_tutorial.setOnClickListener { showTutorial() }

        fab_menu.setOnClickListener { view ->
            val allPlacesFragment = PlaceListFragment()
            val bundle = Bundle()
            val stringsOrNulls = arrayOfNulls<String>(places.size)
            for (i in places.indices){
                stringsOrNulls[i] = places[i].placeTitle
            }
            bundle.putStringArray("allPlaces", stringsOrNulls)
            allPlacesFragment.arguments = bundle
            allPlacesFragment.show(fragmentManager, "placess")
        }

    }

    /*
     * Loads the simple google's Ad
     * Testing...
     */
    private fun loadAd(){
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111")//Google testID
//        MobileAds.initialize(this, "ca-app-pub-1421901645146201~6738697501")//App-AdID
//        ads:adUnitId="ca-app-pub-1421901645146201/3627187116" // App adUnitID - replace in XML

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    /*
     * Loads the places from the ViewModel
     * And sets the visibility of the Tutorial
     * Button
     */
    private fun syncUI(){
        disposable.add(viewModel.loadPlaces()
        !!.subscribe({
            places = it
            buttonVisibility = places.count() <= 0
            mVisibilityObservable.onNext(buttonVisibility)
        }))
        mVisibilityObservable.observeOn(AndroidSchedulers.mainThread()).subscribe({ t ->
            if (t) {
                btn_tutorial.visibility = View.VISIBLE
                fab_menu.visibility = View.GONE
            } else {
                btn_tutorial.visibility = View.GONE
                fab_menu.visibility = View.VISIBLE
            }
        })
    }

    /*
     * Sets up the location manager
     * and listens for location changes so
     * it can update the latitude and longitude
     */
    @SuppressLint("MissingPermission")
    private fun setUpLocationManager() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, getString(R.string.error_loc_unavailable), LENGTH_LONG).show()
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

    /*
     * Pop up a request permission window
     * Check if the user has granted a permission
     * for location
     */
    private fun requestPermission() {
        val rxPermissions = RxPermissions(this)
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (granted) {
                        setUpLocationManager()
                    } else {
                        Toast.makeText(this, getString(R.string.permission_denial), LENGTH_LONG).show()
                        finish()
                    }
                }
    }

    /*
     * Handle the image button click
     * and opens google map with the place location
     */
    private fun gotoPlace() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${viewModel.showCurrentPlace().latitude},${viewModel.showCurrentPlace().longitude}"))
        startActivity(intent)
    }

    /*
     * Handles the orientation changes
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putCharSequence("place", place_txt.text.toString())
    }

    /*
     * Changes the location's latitude and longitude
     * depending on user's movement.
     */
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /*
     * Handles View's animations
     */
    private fun errorEmptyOutputAnimation(target: View) {
        YoYo.with(Techniques.Tada)
                .duration(700)
                .repeat(2)
                .playOn(target)
    }

    private fun placeAnimation() {
        YoYo.with(Techniques.DropOut)
                .duration(500)
                .playOn(place_txt)
        YoYo.with(Techniques.DropOut)
                .duration(500)
                .playOn(place_image)
    }

    /*
     * Keeps the delete dialog up if
     * screen rotation occurs
     */

    /*
     * Implemented method from dialog interface
     * to delete the place
     */
    override fun onPositive() {
        val idx = places.indexOf(viewModel.showCurrentPlace())
        places.removeAt(idx)
        viewModel.deletePlace(viewModel.showCurrentPlace())
        Toast.makeText(this, getString(R.string.delete_dialog_success), Toast.LENGTH_SHORT).show()
        place_txt.text = ""
    }

    private fun showTutorial(){
        val sequence = MaterialShowcaseSequence(this)
        val config = ShowcaseConfig()

        config.delay = 200
        config.shapePadding = 96
        config.renderOverNavigationBar = true

        sequence.setConfig(config)
        sequence.addSequenceItem(tutorialItem(textInputLayout, "Type your place title here!\n#Remember to add places when you are currently visiting it.", "OK", "rectangle"))
                .addSequenceItem(tutorialItem(add_place_btn, "Press to add your place in the database", "OK", "rectangle"))
                .addSequenceItem(tutorialItem(place_image, "Just press to start a navigation to this place!", "Sweeeet...", "circle"))
                .addSequenceItem(tutorialItem(place_image, "Long Press to delete the current place", "Ahm, Fine?", "circle"))
                .addSequenceItem(tutorialItem(where_btn, "Press when you have no idea for place", "Aha...", "rectangle"))
                .addSequenceItem(tutorialItem(place_txt, "And finally your proposal - WhereToGO", "Yay!!!", "circle"))
        sequence.start()
    }

    private fun tutorialItem(target: View, text: String, dismiss: String, shape: String): MaterialShowcaseView? {
        val materialShowCase = MaterialShowcaseView.Builder(this)
        return if (shape == "rectangle") {
            materialShowCase.setTarget(target)
                    .setDismissText(dismiss)
                    .setContentText(text)
                    .setDismissOnTouch(true)
                    .withRectangleShape(true)
                    .build()
        } else{
            materialShowCase.setTarget(target)
                    .setDismissText(dismiss)
                    .setContentText(text)
                    .setDismissOnTouch(true)
                    .build()
        }

    }

    override fun onPlaceSelected(placeTitle: String) {
        place_txt.text = placeTitle
        places.forEach { place: Place -> if (place.placeTitle.equals(placeTitle, true)) viewModel.assignCurrentPlace(place) }
    }

}
