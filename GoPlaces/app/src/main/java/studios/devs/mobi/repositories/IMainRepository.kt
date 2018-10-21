package studios.devs.mobi.repositories

import io.reactivex.Observable
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.model.result.Result

interface IMainRepository {

    fun insertSpot(spotEntity: SpotEntity): Observable<Result<SpotEntity>>

    fun getAllWallets(): Observable<Result<List<SpotEntity>>>

}
