package com.yord.v.wheretogo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.yord.v.wheretogo.model.Spot
import com.yord.v.wheretogo.model.SpotDao

@Database(entities = [(Spot::class)], version = 5, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun spotDao(): SpotDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
        instance ?: synchronized(this){
            instance ?:
                    Room.databaseBuilder(context, AppDatabase::class.java, "spots.db")
                            .allowMainThreadQueries()//Since the queries won't be any long and heavy...
                            .fallbackToDestructiveMigration()
                            .build()
                            .also { instance = it }
        }

    }

}