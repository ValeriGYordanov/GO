package com.yord.v.wheretogo.injection

import android.content.Context
import com.yord.v.wheretogo.db.AppDatabase
import com.yord.v.wheretogo.model.PlaceDao
import com.yord.v.wheretogo.viewmodel.ViewModelFactory

object Injection {

    private fun provideUserDataSource(context: Context): PlaceDao {
        val database = AppDatabase.getInstance(context)
        return database.placeDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return ViewModelFactory(dataSource)
    }
}