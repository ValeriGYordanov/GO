package com.yord.v.wheretogo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.yord.v.wheretogo.model.Place
import com.yord.v.wheretogo.model.PlaceDao

/**
 * Created by Valery on 3/15/2018.
 */
@Database(entities = arrayOf(Place::class), version = 3, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
        instance ?: synchronized(this){
            instance ?:
                    Room.databaseBuilder(context, AppDatabase::class.java, "places.db")
                            .allowMainThreadQueries()//Since the queries won't be any long and heavy...
                            .fallbackToDestructiveMigration()
                            .build()
                            .also { instance = it }
        }

    }

}