package com.yord.v.wheretogo.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

/**
 * Created by Valery on 3/15/2018.
 */
@Dao
interface PlaceDao{

    @Query("SELECT * FROM place")
    fun getAllPlaces(): Flowable<List<Place>>

    @Insert
    fun insertNewPlace(place: Place)
}