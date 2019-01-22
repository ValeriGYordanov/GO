package studios.devs.mobi.repositories

import android.database.sqlite.SQLiteConstraintException
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.model.result.DatabaseException
import studios.devs.mobi.model.result.Result
import studios.devs.mobi.model.result.ResultError
import studios.devs.mobi.storage.AppDatabase
import java.lang.Exception

class MainRepository(private val appDatabase: AppDatabase) : IMainRepository {

    override fun getSpotByName(name: String): Observable<Result<SpotEntity>> {
        // implement
        return Observable.create<Result<SpotEntity>>{ emitter->
            try {
                val spot = appDatabase.walletDao().getSpot(name)
                emitter.onNext(Result.Success(spot))
            }catch (e: Exception){
                emitter.onNext(Result.Error(DatabaseException.LoadingFailed()))
            }
            emitter.onComplete()
        }
                .startWith(Result.Loading())
                .subscribeOn(Schedulers.io())
    }

    override fun insertSpot(spotEntity: SpotEntity): Observable<Result<SpotEntity>>{
        return Observable.create<Result<SpotEntity>> { emitter->
            try {
                appDatabase.walletDao().insertSpot(spotEntity)
                emitter.onNext(Result.Success(spotEntity))
            }catch (e: Exception){
                if (e is SQLiteConstraintException){
                    emitter.onNext(Result.Error(DatabaseException.DuplicateName()))
                }
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


    override fun deleteSpotByName(name: String) {
        appDatabase.walletDao().deleteSpot(name)
    }

}
