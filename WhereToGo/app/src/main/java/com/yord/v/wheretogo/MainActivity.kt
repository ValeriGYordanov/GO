package com.yord.v.wheretogo

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

    private lateinit var factory : ViewModelFactory
    private lateinit var viewModel : PlaceViewModel

    private lateinit var places: List<Place>
    private val disposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textInputLayout.setTypeface(Typeface.createFromAsset(assets, "fonts/Aclonica.ttf"))
        place_txt.text = savedInstanceState?.getCharSequence("place")

        factory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaceViewModel::class.java)
        disposable.add(viewModel.loadPlaces()
                !!.subscribe({places = it})
        )

        where_btn.setOnClickListener {
            val random = Random()
            if (places.count() == 0) {
                Toast.makeText(this, "You haven't inserted anything yet!", LENGTH_SHORT).show()

                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(2)
                        .playOn(textInputLayout)

            } else {
                val randomPlace = random.nextInt(places.count())
                place_txt.text = places[randomPlace].placeTitle
                YoYo.with(Techniques.DropOut)
                        .duration(500)
                        .playOn(place_txt)
                YoYo.with(Techniques.DropOut)
                        .duration(500)
                        .playOn(imageView)

            }
        }
        add_place_btn.setOnClickListener {
            val newPlaceTtl = add_place_txt.text.toString().trim()
            val newPlace = Place(Random().nextLong(),newPlaceTtl)
            var doesNotExist = true
            if (!newPlaceTtl.isEmpty()){
                places.forEach { place: Place ->
                    run {
                        if (place.placeTitle.equals(newPlaceTtl, true)) {
                            doesNotExist = false
                        }
                    }
                }
                if (doesNotExist) {
                    viewModel.addNewPlace(newPlace)
                    places.plus(newPlace)
                    Toast.makeText(this, newPlace.placeTitle + ", added!", LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, newPlace.placeTitle + ", is already in the list. Add new one!", LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Type a new Place, please!", LENGTH_SHORT).show()
            }
            add_place_txt.text.clear()
        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putCharSequence("place", place_txt.text)
    }
}
