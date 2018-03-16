package com.yord.v.wheretogo.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.yord.v.wheretogo.db.AppDatabase
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


}



