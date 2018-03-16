package com.yord.v.wheretogo.viewmodel

import android.arch.lifecycle.ViewModel
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.model.PlaceDao
import io.reactivex.Flowable

/**
 * Created by Valery on 3/15/2018.
 */
class PlaceViewModel(private val data: PlaceDao) : ViewModel(){

    private var livePlaces: Flowable<List<Place>>? = null

    fun loadPlaces(): Flowable<List<Place>>?{
        if (livePlaces == null) {
            livePlaces = data.getAllPlaces()
        }
        return livePlaces
    }



    fun addNewPlace(place: Place){
        data.insertNewPlace(place)
    }


//    private var livePlaces: List<Place>? = null
//    private var disposable = CompositeDisposable()
//    private var place: Place? = null
//
//    fun loadPlaces(): List<Place>?{
//        if (livePlaces == null) {
//            disposable.add(data.getAllPlaces().subscribe({livePlaces = it})
//            )
//        }
//        return livePlaces
//    }
//
//    fun showRandomPlace(): String {
//        if (place == null) {
//            place = livePlaces!![Random().nextInt(livePlaces!!.count())]
//        }
//        return place!!.placeTitle
//    }
//
//    fun addNewPlace(title: String): Boolean{
//        livePlaces!!.forEach { place ->
//            if (place.placeTitle.equals(title, true)){
//                return false
//            }
//        }
//        data.insertNewPlace(Place(Random().nextLong(), title))
//        return true
//    }


}



