package com.yord.v.wheretogo.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface SpotDao{

    @Query("SELECT * FROM spot")
    fun getAllSpots(): Flowable<MutableList<Spot>>

    @Insert
    fun insertNewSpot(spot: Spot)

    @Delete
    fun deleteSpot(spot: Spot)
}