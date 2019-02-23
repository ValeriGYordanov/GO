package studios.devs.mobi.repositories

import io.reactivex.Observable
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.model.result.Result

interface IMainRepository {

    fun insertSpot(spotEntity: SpotEntity): Observable<Result<SpotEntity>>

    fun getAllSpots(): Observable<Result<List<SpotEntity>>>

    fun getSpotByName(name: String): Observable<Result<SpotEntity>>

    fun deleteSpotByName(name: String): Observable<Result<Unit>>

}
