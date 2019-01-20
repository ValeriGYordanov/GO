package studios.devs.mobi.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import studios.devs.mobi.extension.flip
import studios.devs.mobi.extension.whenError
import studios.devs.mobi.extension.whenLoading
import studios.devs.mobi.extension.whenSuccess
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.model.result.IResultError
import studios.devs.mobi.model.result.Result
import studios.devs.mobi.repositories.IMainRepository
import studios.devs.mobi.ui.dialogs.AllSpotsDialog
import studios.devs.mobi.viewmodels.base.LoadingViewModel
import studios.devs.mobi.viewmodels.base.LoadingViewModelOutput
import java.io.Serializable
import java.lang.Error
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


interface OfflineSpotViewModelInput {
    fun addNewSpot()
    fun newSpotText(text: String)
    fun showAllSpots()
    fun useCurrentLocationIsChecked()
    fun showRandomSpot()
    fun locationSet(latitude: String, longitude: String)
    fun loadAllSpots()
    fun navigate()
    fun navigateToConcreteSpot(spotname: String)
}

interface OfflineSpotViewModelOutput {
    val allSpotsStream: Observable<List<SpotEntity>>
    val errorStream: Observable<IResultError>
    val loadingViewModelOutput: LoadingViewModelOutput
    val shouldShowTutorialStream: Observable<Boolean>
    val randomSpotStream: Observable<String>
    val newSpotAddedStream: Observable<SpotEntity>
    val isCurrectLocationChecked: Observable<Boolean>
    val askForLocationStream: Observable<Unit>
    val askForSpotNameStream: Observable<Unit>
    val spotIsAlreadyIncluded: Observable<Unit>
    val showAllSpotsStream: Observable<List<SpotEntity>>
    val emptySpotListStream: Observable<Unit>
    val mapNavigationStream: Observable<SpotEntity>
}

interface OfflineSpotViewModelInputOutput {
    val input: OfflineSpotViewModelInput
    val output: OfflineSpotViewModelOutput
}

class OfflineSpotViewModel @Inject constructor(private val repository: IMainRepository) : ViewModel(),
        OfflineSpotViewModelInput,
        OfflineSpotViewModelOutput,
        OfflineSpotViewModelInputOutput {

    //region CONST Exposed Input and Output
    override val input: OfflineSpotViewModelInput
        get() = this
    override val output: OfflineSpotViewModelOutput
        get() = this
    //endregion

    //region output


    override val loadingViewModelOutput: LoadingViewModelOutput

    override val allSpotsStream: Observable<List<SpotEntity>>
    override val errorStream: Observable<IResultError>
    override val shouldShowTutorialStream: Observable<Boolean>
    override val randomSpotStream: Observable<String>
    override val newSpotAddedStream: Observable<SpotEntity>
    override val isCurrectLocationChecked: Observable<Boolean>
    override val askForLocationStream: Observable<Unit>
    override val askForSpotNameStream: Observable<Unit>
    override val spotIsAlreadyIncluded: Observable<Unit>
    override val showAllSpotsStream: Observable<List<SpotEntity>>
    override val emptySpotListStream: Observable<Unit>
    override val mapNavigationStream: Observable<SpotEntity>

    //endregion

    //region local
    private val compositeDisposable = CompositeDisposable()
    private val loadFromDatabase = PublishSubject.create<Unit>()
    private val loadFromDatabaseRelay = PublishSubject.create<Unit>()
    private val randomSpotSubject = PublishSubject.create<Unit>()
    private val newSpotSubject = PublishSubject.create<Unit>()
    private val newSpotNameSubject = BehaviorSubject.create<String>()
    private val useCurrentLocationSubject = BehaviorSubject.createDefault(false)
    private val askForLocationSubject = PublishSubject.create<Unit>()
    private val locationSubject = PublishSubject.create<Pair<String, String>>()
    private val emptyNameSubject = PublishSubject.create<Unit>()
    private val spotInListSubject = PublishSubject.create<Unit>()
    private val showAllSpotsSubject = PublishSubject.create<Unit>()
    private val emptySpotListSubject = PublishSubject.create<Unit>()
    private val mapNavigationSubject = PublishSubject.create<Unit>()
    private val navigationSubject = PublishSubject.create<String>()
    private val lastSpotDisplayed = BehaviorSubject.create<SpotEntity>()
    //endregion

    init {

        val newSpot = newSpotSubject.withLatestFrom(useCurrentLocationSubject)
                .checkLocation(askForLocationSubject)
                .zipWith(locationSubject)
                .withLatestFrom(newSpotNameSubject)
                .filter {
                    if (it.second.isEmpty()) {
                        emptyNameSubject.onNext(Unit)
                    }
                    it.second.isNotEmpty()
                }
                .map { return@map Pair(it.first.second, it.second) }
                .flatMap {
                    repository.insertSpot(
                                    SpotEntity(
                                            spotTitle = it.second,
                                            latitude = it.first.first,
                                            longitude = it.first.second))
                }
                .share()

        val dataBaseLoad = Observable
                .merge(loadFromDatabase, loadFromDatabaseRelay.delay(300, TimeUnit.MILLISECONDS))
                .flatMap { repository.getAllWallets() }
                .share()

        allSpotsStream = dataBaseLoad.whenSuccess()

        spotIsAlreadyIncluded = spotInListSubject

        isCurrectLocationChecked = useCurrentLocationSubject
        askForLocationStream = askForLocationSubject
        askForSpotNameStream = emptyNameSubject
        emptySpotListStream = emptySpotListSubject

        val lastRandomShown = mapNavigationSubject.withLatestFrom(lastSpotDisplayed)
                .map { it.second }

        val listSelectedSpot = Observable.combineLatest(navigationSubject, allSpotsStream,
                BiFunction<String, List<SpotEntity>, SpotEntity> { name, list ->
                    return@BiFunction list.first {
                        it.spotTitle == name
                    }
                })

        mapNavigationStream = Observable.merge(lastRandomShown, listSelectedSpot)

        shouldShowTutorialStream = Observable.just(false)

        showAllSpotsStream = showAllSpotsSubject.withLatestFrom(allSpotsStream)
                .map { it.second }
                .observeOn(AndroidSchedulers.mainThread())

        randomSpotStream = randomSpotSubject
                .withLatestFrom(allSpotsStream)
                .checkIfListIsEmpty(emptyListAlert = emptySpotListSubject)
                .setLastSpot(lastSpot = lastSpotDisplayed)
                .map { it.spotTitle }

        newSpotAddedStream = newSpot.whenSuccess()
                .observeOn(AndroidSchedulers.mainThread())

        errorStream = Observable
                .merge(newSpot.whenError(), dataBaseLoad.whenError())
                .observeOn(AndroidSchedulers.mainThread())

        loadingViewModelOutput = LoadingViewModel(listOf(
                newSpot.whenLoading(), dataBaseLoad.whenLoading()
                .observeOn(AndroidSchedulers.mainThread())
        ))

    }

    //region Input

    override fun navigate() {
        mapNavigationSubject.onNext(Unit)
    }

    override fun addNewSpot() {
        newSpotSubject.onNext(Unit)
        loadFromDatabaseRelay.onNext(Unit)
    }

    override fun loadAllSpots() {
        loadFromDatabase.onNext(Unit)
    }

    override fun newSpotText(text: String) {
        newSpotNameSubject.onNext(text)
    }

    override fun showAllSpots() {
        showAllSpotsSubject.onNext(Unit)
    }

    override fun useCurrentLocationIsChecked() {
        useCurrentLocationSubject.flip()
    }

    override fun showRandomSpot() {
        randomSpotSubject.onNext(Unit)
    }

    override fun locationSet(latitude: String, longitude: String) {
        locationSubject.onNext(Pair(latitude, longitude))
    }

    override fun navigateToConcreteSpot(spotname: String) {
        navigationSubject.onNext(spotname)
    }

    //endregion

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}

private fun Observable<Pair<Unit, Boolean>>.checkLocation(askForLocationSubject: PublishSubject<Unit>): Observable<Boolean> {
    return this.doOnNext {
        if (!it.second) {
            askForLocationSubject.onNext(Unit)
        }
    }
            .map {
                return@map it.second
            }

}

private fun Observable<List<SpotEntity>>.setLastSpot(lastSpot: BehaviorSubject<SpotEntity>): Observable<SpotEntity> {
    return this.map {
        val randomPlace = it[Random().nextInt(it.size)]
        randomPlace
    }
            .doOnNext {
                lastSpot.onNext(it)
            }

}

private fun Observable<Pair<Unit, List<SpotEntity>>>.checkIfListIsEmpty(emptyListAlert: PublishSubject<Unit>): Observable<List<SpotEntity>> {
    return this.doOnNext {
        if (it.second.isEmpty()) {
            emptyListAlert.onNext(Unit)
        }
    }
            .filter { it.second.isNotEmpty() }
            .map { it.second }
}
