package com.yord.v.wheretogo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import com.google.android.gms.location.places.ui.PlacePicker
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yord.v.wheretogo.R
import com.yord.v.wheretogo.injection.Injection
import com.yord.v.wheretogo.model.Spot
import com.yord.v.wheretogo.ui.DeleteDialogFragment.OptionDialogListener
import com.yord.v.wheretogo.ui.SpotListFragment.SelectedSpotListener
import com.yord.v.wheretogo.viewmodel.SpotViewModel
import com.yord.v.wheretogo.viewmodel.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import java.util.*

class MainActivity : AppCompatActivity(), OptionDialogListener, SelectedSpotListener {

    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: SpotViewModel
    private lateinit var mAdView : AdView

    private lateinit var spots: MutableList<Spot>
    private val disposable = CompositeDisposable()
    private var buttonVisibility = true

    private var latitude = ""
    private var longitude = ""
    private var PLACE_PICKER_INTENT = 1
    private var newSpotTtl = ""

    private val mVisibilityObservable = PublishSubject.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textInputLayout.setTypeface(Typeface.createFromAsset(assets, getString(R.string.path_fonts)))
        spot_txt.text = savedInstanceState?.getCharSequence("spot")
//        loadAd()

        requestPermission()

        factory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(SpotViewModel::class.java)

        syncUI()

        where_btn.setOnClickListener {
            val random = Random()
            if (spots.count() == 0) {
                Toast.makeText(this, getString(R.string.empty_field), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(textInputLayout)

            } else {
                val randomSpot = random.nextInt(spots.count())
                viewModel.assignCurrentSpot(spots[randomSpot])
                spot_txt.text = viewModel.showCurrentSpot().spotTitle
                spotAnimation()
            }
        }

        add_spot_btn.setOnClickListener {
            newSpotTtl = add_spot_txt.text.toString().trim()
            val newSpot = Spot(Random().nextLong(), newSpotTtl, latitude, longitude)
            var spotIsNotInList = true

            if (!newSpotTtl.isEmpty()) {
                spots.forEach { spot: Spot ->
                    run {
                        if (spot.spotTitle.equals(newSpotTtl, true)) {
                            spotIsNotInList = false
                        }
                    }
                }
                if (spotIsNotInList) {
                    if (current_location_box.isChecked) {
                        viewModel.addNewSpot(newSpot)
                        spots.plus(newSpot)
                        Toast.makeText(this, newSpot.spotTitle + getString(R.string.spot_successfuly_added), LENGTH_SHORT).show()
                    }else{
                        val builder = PlacePicker.IntentBuilder()
                        startActivityForResult(builder.build(this), PLACE_PICKER_INTENT)
                    }
                } else {
                    Toast.makeText(this, newSpot.spotTitle + getString(R.string.spot_already_in_list), LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.missing_spot_title), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(textInputLayout)
            }

            add_spot_txt.text!!.clear()

        }

        spot_image.setOnClickListener { _ ->
            if (!spot_txt.text.isEmpty() && spot_txt.text.toString() != "") {
                gotoSpot()
            } else {
                Toast.makeText(this, getString(R.string.delete_dialog_empty_spot), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(spot_image)
            }
        }

        spot_image.setOnLongClickListener { _ ->
            if (!spot_txt.text.isEmpty() && spot_txt.text.toString() != "") {
                val bundle = Bundle()
                bundle.putCharSequence("spot", viewModel.showCurrentSpot().spotTitle)
                val dialog = DeleteDialogFragment()
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, "delete")
                true
            } else {
                Toast.makeText(this, getString(R.string.delete_dialog_empty_spot), LENGTH_SHORT).show()
                errorEmptyOutputAnimation(spot_image)
                true
            }
        }

        btn_tutorial.setOnClickListener { showTutorial() }

        fab_menu.setOnClickListener { _ ->
            if (spots.count() < 1){
                Toast.makeText(this, "You haven't inserted anything, yet!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val allSpotssFragment = SpotListFragment()
            val bundle = Bundle()
            val stringsOrNulls = arrayOfNulls<String>(spots.size)
            for (i in spots.indices){
                stringsOrNulls[i] = spots[i].spotTitle
            }
            bundle.putStringArray("allSpots", stringsOrNulls)
            allSpotssFragment.arguments = bundle
            allSpotssFragment.show(fragmentManager, "spots")
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
     * Loads the spots from the ViewModel
     * And sets the visibility of the Tutorial
     * Button
     */
    private fun syncUI(){
        disposable.add(viewModel.loadSpots()
        !!.subscribe({
            spots = it
            buttonVisibility = spots.count() <= 0
            mVisibilityObservable.onNext(buttonVisibility)
        }))
        mVisibilityObservable.observeOn(AndroidSchedulers.mainThread()).subscribe({ t ->
            if (t) btn_tutorial.visibility = View.VISIBLE
            else btn_tutorial.visibility = View.GONE
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
    private fun gotoSpot() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${viewModel.showCurrentSpot().latitude},${viewModel.showCurrentSpot().longitude}"))
        startActivity(intent)
    }

    /*
     * Handles the orientation changes
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putCharSequence("spot", spot_txt.text.toString())
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

    private fun spotAnimation() {
        YoYo.with(Techniques.DropOut)
                .duration(500)
                .playOn(spot_txt)
        YoYo.with(Techniques.DropOut)
                .duration(500)
                .playOn(spot_image)
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
        val idx = spots.indexOf(viewModel.showCurrentSpot())
        spots.removeAt(idx)
        viewModel.deleteSpot(viewModel.showCurrentSpot())
        Toast.makeText(this, getString(R.string.delete_dialog_success), Toast.LENGTH_SHORT).show()
        spot_txt.text = ""
    }

    /*
     * Begin the tutorial session
     * explaining the buttons
     */
    private fun showTutorial(){
        val sequence = MaterialShowcaseSequence(this)
        val config = ShowcaseConfig()

        config.delay = 200
        config.shapePadding = 96
        config.renderOverNavigationBar = true

        sequence.setConfig(config)
        sequence.addSequenceItem(tutorialItem(textInputLayout, "Type your place title here!\n#Remember to add spots when you are currently visiting it.", "OK", "rectangle"))
                .addSequenceItem(tutorialItem(current_location_box, "Check the box, if you want to use your current location for late navigation route, or leave unchecked and choose the location yourself", "Mhm", "circle"))
                .addSequenceItem(tutorialItem(add_spot_btn, "Press to add your place in the database", "OK", "rectangle"))
                .addSequenceItem(tutorialItem(spot_image, "Just press to start a navigation to this place!", "Sweeeet...", "circle"))
                .addSequenceItem(tutorialItem(spot_image, "Long Press to delete the current place", "Ahm, Fine?", "circle"))
                .addSequenceItem(tutorialItem(fab_menu, "See All your spots here", "Nice...", "circle"))
                .addSequenceItem(tutorialItem(where_btn, "Press when you have no idea for place", "Aha...", "rectangle"))
                .addSequenceItem(tutorialItem(spot_txt, "And finally your proposal - WhereToGO", "Yay!!!", "circle"))
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

    override fun onSpotSelected(spotTitle: String) {
        spot_txt.text = spotTitle
        spots.forEach { spot: Spot -> if (spot.spotTitle.equals(spotTitle, true)) viewModel.assignCurrentSpot(spot) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_INTENT){
            if (resultCode == Activity.RESULT_OK){
                val place = PlacePicker.getPlace(this, data)
                latitude = place.latLng.latitude.toString()
                longitude = place.latLng.longitude.toString()
                val newSpot = Spot(Random().nextLong(), newSpotTtl, latitude, longitude)
                viewModel.addNewSpot(newSpot)
                spots.plus(newSpot)
                Toast.makeText(this, newSpot.spotTitle + getString(R.string.spot_successfuly_added), LENGTH_SHORT).show()
            }
        }
    }

}
