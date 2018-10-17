package studios.devs.mobi.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import studios.devs.mobi.model.Spot


@Dao
interface SpotDAO {

    @Insert
    fun insertSpot(spot: Spot)

    @Update
    fun updateSpot(spot: Spot)

    @Query("SELECT * FROM spot")
    fun getAllSpots(): List<Spot>

    @Query("SELECT * FROM spot WHERE title LIKE :spotTitle")
    fun getSpot(spotTitle: String): Spot

}
