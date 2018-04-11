package com.yord.v.wheretogo.viewmodel

import android.arch.lifecycle.ViewModel
import com.yord.v.wheretogo.model.Spot
import com.yord.v.wheretogo.model.SpotDao
import io.reactivex.Flowable

class SpotViewModel(private val data: SpotDao) : ViewModel(){

    private var liveSpots: Flowable<MutableList<Spot>>? = null
    private lateinit var currentSpot: Spot
    private lateinit var allSpots : List<Spot>

    fun loadSpots(): Flowable<MutableList<Spot>>?{
        if (liveSpots == null) {
            liveSpots = data.getAllSpots()
        }
        liveSpots!!.concatMap { results -> Flowable
                .fromIterable(results)
                .map { allSpots = results.toList()}}
        return liveSpots
    }



    fun addNewSpot(spot: Spot){
        data.insertNewSpot(spot)
    }

    fun deleteSpot(spot: Spot){
        data.deleteSpot(spot)
    }

    fun showCurrentSpot(): Spot{
        return currentSpot
    }

    fun assignCurrentSpot(currentSpot: Spot){
        this.currentSpot = currentSpot
    }

    fun getLoadedSpots(): List<Spot> {
        return allSpots
    }

}



