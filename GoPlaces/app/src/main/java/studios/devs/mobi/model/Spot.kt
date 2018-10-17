package studios.devs.mobi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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