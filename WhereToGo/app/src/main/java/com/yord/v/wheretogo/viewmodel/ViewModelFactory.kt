package com.yord.v.wheretogo.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.yord.v.wheretogo.model.PlaceDao

/**
 * Created by Valery on 3/15/2018.
 */
class ViewModelFactory(private val dataSource: PlaceDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            return PlaceViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}