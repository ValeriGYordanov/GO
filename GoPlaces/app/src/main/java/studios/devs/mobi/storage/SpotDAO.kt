package studios.devs.mobi.storage

import androidx.room.*
import studios.devs.mobi.model.SpotEntity


@Dao
interface SpotDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSpot(spotEntity: SpotEntity)

    @Update
    fun updateSpot(spotEntity: SpotEntity)

    @Query("SELECT * FROM spotentity")
    fun getAllSpots(): List<SpotEntity>

    @Query("SELECT * FROM spotentity WHERE title LIKE :spotTitle")
    fun getSpot(spotTitle: String): SpotEntity

}
