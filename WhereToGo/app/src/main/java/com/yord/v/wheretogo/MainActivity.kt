package com.yord.v.wheretogo

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.yord.v.wheretogo.injection.Injection
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.viewmodel.PlaceViewModel
import com.yord.v.wheretogo.viewmodel.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var factory : ViewModelFactory
    private lateinit var viewModel : PlaceViewModel

    private lateinit var places: List<Place>
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaceViewModel::class.java)
        disposable.add(viewModel.loadPlaces()
                !!.subscribe({places = it})
        )

        where_btn.setOnClickListener {
            val random = Random()
            Log.e("RandomPlaceINT : ", " ".plus(places.count()))
            if (places.count() == 0){
                Toast.makeText(this, "You haven't inserted anything yet!", LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val randomPlace = random.nextInt(places.count())
            place_txt.text = places[randomPlace].placeTitle
        }

        add_place_btn.setOnClickListener {
            val newPlaceTtl = add_place_txt.text.toString()
            val newPlace = Place(Random().nextLong(),newPlaceTtl)
            if (!newPlaceTtl.isEmpty()){
                if (!places.contains(newPlace)) {
                    viewModel.addNewPlace(newPlace)
                    places.plus(newPlace)
                    Toast.makeText(this, newPlace.placeTitle + ", added!", LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, newPlace.placeTitle + ", is already in the list. Add new one!", LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Insert a new Place, please!", LENGTH_SHORT).show()
            }
            add_place_txt.text.clear()
        }
    }
}
