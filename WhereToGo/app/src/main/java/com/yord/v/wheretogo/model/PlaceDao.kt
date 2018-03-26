package com.yord.v.wheretogo.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface PlaceDao{

    @Query("SELECT * FROM place")
    fun getAllPlaces(): Flowable<MutableList<Place>>

    @Insert
    fun insertNewPlace(place: Place)

    @Delete
    fun deletePlace(place: Place)
}