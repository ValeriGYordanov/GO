package com.yord.v.wheretogo

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.yord.v.wheretogo.injection.Injection
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.viewmodel.PlaceViewModel
import com.yord.v.wheretogo.viewmodel.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION = 1

    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: PlaceViewModel

    private lateinit var places: MutableList<Place>
    private lateinit var currentPlace: Place
    private val disposable = CompositeDisposable()

    private var latitude = ""
    private var longitude = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textInputLayout.setTypeface(Typeface.createFromAsset(assets, "fonts/Aclonica.ttf"))
        place_txt.text = savedInstanceState?.getCharSequence("place")

        factory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaceViewModel::class.java)

        disposable.add(viewModel.loadPlaces()
        !!.subscribe({ places = it }))

        where_btn.setOnClickListener {
            val random = Random()
            setUpLocationManager()
            if (places.count() == 0) {
                Toast.makeText(this, "You haven't inserted anything yet!", LENGTH_SHORT).show()
                errorEmptyOutputAnimation(textInputLayout)

            } else {
                val randomPlace = random.nextInt(places.count())
                currentPlace = places[randomPlace]
                place_txt.text = currentPlace.placeTitle
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
                    Toast.makeText(this, newPlace.placeTitle + ", added!", LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, newPlace.placeTitle + ", is already in the list. Add new one!", LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Type a Place title, please!", LENGTH_SHORT).show()
                errorEmptyOutputAnimation(textInputLayout)
            }

            add_place_txt.text.clear()

        }


        place_image.setOnClickListener { _ -> gotoPlace() }

        place_image.setOnLongClickListener { _ ->
            if (!place_txt.text.isEmpty()) {
                val alert = AlertDialog.Builder(
                        this)
                alert.setTitle("Delete Place???")
                alert.setIcon(android.R.drawable.ic_delete)
                alert.setMessage("Are you sure you want to delete : " + currentPlace.placeTitle)
                alert.setPositiveButton("Yes", { _, _ ->
                    var idx = places.indexOf(currentPlace)
                    places.removeAt(idx)
                    viewModel.deletePlace(currentPlace)
                    Toast.makeText(this, "Place removed", LENGTH_SHORT).show()
                    place_txt.text = ""
                })
                alert.setNegativeButton("No", { dialog, _ ->
                    dialog.dismiss()
                })
                alert.show()
                true
            } else {
                Toast.makeText(this, "No place selected...", LENGTH_SHORT).show()
                errorEmptyOutputAnimation(place_image)
                true
            }
        }

    }

    /*
     * Sets up the location manager
     * and listens for location changes so
     * it can update the latitude and longitude
     */
    @SuppressLint("MissingPermission")
    private fun setUpLocationManager() {

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission to location denied, please try again!", LENGTH_LONG).show()
            return
        }
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "NO LOCATION =X", LENGTH_LONG).show()
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
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

    /*
     * Handle the image button click
     * and opens google map with the place location
     */
    private fun gotoPlace() {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${currentPlace.latitude},${currentPlace.longitude}"))
        startActivity(intent)
    }

    /*
     * Handles the orientation changes
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putCharSequence("place", place_txt.text)
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

}
