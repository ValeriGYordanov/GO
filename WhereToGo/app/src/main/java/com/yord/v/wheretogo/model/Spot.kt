package com.yord.v.wheretogo.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Spot(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long,
        @ColumnInfo(name = "title")
        var spotTitle: String,
        @ColumnInfo(name = "latitude")
        var latitude: String,
        @ColumnInfo(name = "longitude")
        var longitude: String
)