package studios.devs.mobi.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SpotEntity(
        @PrimaryKey(autoGenerate = false)
        @NonNull
        @ColumnInfo(name = "title")
        var spotTitle: String,
        @ColumnInfo(name = "latitude")
        var latitude: String,
        @ColumnInfo(name = "longitude")
        var longitude: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(spotTitle)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpotEntity> {
        override fun createFromParcel(parcel: Parcel): SpotEntity {
            return SpotEntity(parcel)
        }

        override fun newArray(size: Int): Array<SpotEntity?> {
            return arrayOfNulls(size)
        }
    }
}