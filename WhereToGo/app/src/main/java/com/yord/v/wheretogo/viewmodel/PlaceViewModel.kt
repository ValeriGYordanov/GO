package com.yord.v.wheretogo.viewmodel

import android.arch.lifecycle.ViewModel
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.model.PlaceDao
import io.reactivex.Flowable

class PlaceViewModel(private val data: PlaceDao) : ViewModel(){

    private var livePlaces: Flowable<MutableList<Place>>? = null
    private lateinit var currentPlace: Place
    private lateinit var allPlaces : List<Place>

    fun loadPlaces(): Flowable<MutableList<Place>>?{
        if (livePlaces == null) {
            livePlaces = data.getAllPlaces()
        }
        livePlaces!!.concatMap { results -> Flowable
                .fromIterable(results)
                .map { allPlaces = results.toList()}}
        return livePlaces
    }



    fun addNewPlace(place: Place){
        data.insertNewPlace(place)
    }

    fun deletePlace(place: Place){
        data.deletePlace(place)
    }

    fun showCurrentPlace(): Place{
        return currentPlace
    }

    fun assignCurrentPlace(currentPlace: Place){
        this.currentPlace = currentPlace
    }

    fun getLoadedPlaces(): List<Place> {
        return allPlaces
    }

}



