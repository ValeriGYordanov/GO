package com.yord.v.wheretogo.viewmodel

import android.arch.lifecycle.ViewModel
import android.support.v4.app.ActivityCompat
import com.yord.v.wheretogo.MainActivity
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.model.PlaceDao
import io.reactivex.Flowable

/**
 * Created by Valery on 3/15/2018.
 */
class PlaceViewModel(private val data: PlaceDao) : ViewModel(){

    private var livePlaces: Flowable<MutableList<Place>>? = null
    private lateinit var currentPlace: Place

    fun loadPlaces(): Flowable<MutableList<Place>>?{
        if (livePlaces == null) {
            livePlaces = data.getAllPlaces()
        }
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


}



