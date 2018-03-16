package com.yord.v.wheretogo.injection

import android.content.Context
import com.yord.v.wheretogo.db.AppDatabase
import com.yord.v.wheretogo.model.PlaceDao
import com.yord.v.wheretogo.viewmodel.ViewModelFactory

/**
 * Created by Valery on 3/15/2018.
 */
object Injection {

    fun provideUserDataSource(context: Context): PlaceDao {
        val database = AppDatabase.getInstance(context)
        return database.placeDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return ViewModelFactory(dataSource)
    }
}