package studios.devs.mobi.repositories

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.model.result.Result
import studios.devs.mobi.model.result.ResultError
import studios.devs.mobi.storage.AppDatabase
import java.lang.Exception

class MainRepository(private val appDatabase: AppDatabase) : IMainRepository {

    override fun insertSpot(spotEntity: SpotEntity): Observable<Result<SpotEntity>>{
        return Observable.create<Result<SpotEntity>> { emitter->
            // TODO What should happen if insertWallet fails?
            try {
                appDatabase.walletDao().insertSpot(spotEntity)
                emitter.onNext(Result.Success(spotEntity))
            }catch (e: Exception){
                emitter.onNext(Result.Error(ResultError("Insertion In DB Failed")))
            }
            emitter.onComplete()
        }
                .startWith(Result.Loading())
                .subscribeOn(Schedulers.io())

    }


    override fun getAllWallets(): Observable<Result<List<SpotEntity>>> {
        return Observable.create<Result<List<SpotEntity>>> { emitter ->
            val allWallets = appDatabase.walletDao().getAllSpots()
            emitter.onNext(Result.Success(allWallets))
            emitter.onComplete()
        }
                .startWith(Result.Loading())
                .subscribeOn(Schedulers.io())
    }


}
