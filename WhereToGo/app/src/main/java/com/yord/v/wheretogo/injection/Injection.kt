package com.yord.v.wheretogo.injection

import android.content.Context
import com.yord.v.wheretogo.db.AppDatabase
import com.yord.v.wheretogo.model.SpotDao
import com.yord.v.wheretogo.viewmodel.ViewModelFactory

object Injection {

    private fun provideUserDataSource(context: Context): SpotDao {
        val database = AppDatabase.getInstance(context)
        return database.spotDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideUserDataSource(context)
        return ViewModelFactory(dataSource)
    }
}