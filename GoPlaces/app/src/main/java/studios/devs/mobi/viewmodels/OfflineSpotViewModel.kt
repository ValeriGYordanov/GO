package studios.devs.mobi.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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
import javax.inject.Inject


interface OfflineSpotViewModelInput {
    fun addNewSpot()
    fun newSpotText(text: String)
    fun showAllSpots()
    fun showTutorial()
    fun useCurrentLocationIsChecked()
    fun startNavigationToShownSpot()
    fun showRandomSpot()
    fun locationSet(latitude: String, longitude: String)
    fun loadAllSpots()
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

    //endregion

    //region local
    private val compositeDisposable = CompositeDisposable()
    private val allSpotsSubject = BehaviorSubject.create<List<SpotEntity>>()
    private val loadFromDatabase = PublishSubject.create<Unit>()
    private val randomSpotSubject = PublishSubject.create<Unit>()
    private val newSpotSubject = PublishSubject.create<Unit>()
    private val newSpotNameSubject = BehaviorSubject.create<String>()
    private val useCurrentLocationSubject = BehaviorSubject.createDefault(false)
    private val askForLocationSubject = PublishSubject.create<Unit>()
    private val locationSubject = PublishSubject.create<Pair<String, String>>()
    private val emptyNameSubject = PublishSubject.create<Unit>()
    private val spotInListSubject = PublishSubject.create<Unit>()
    private val spotNotInSubject = PublishSubject.create<Unit>()
    private val showAllSpotsSubject = PublishSubject.create<Unit>()
    private val emptySpotListSubject = PublishSubject.create<Unit>()
    //endregion

    init {

        val newSpot = newSpotSubject.withLatestFrom(useCurrentLocationSubject)
                .map {
                    if (!it.second){
                        askForLocationSubject.onNext(Unit)
                    }else{
                        locationSubject.onNext(Pair("Current", "Location"))
                    }
                    return@map it.second
                }
                .zipWith(locationSubject)
                .withLatestFrom(newSpotNameSubject)
                .filter{
                    if (it.second.isEmpty()){
                        emptyNameSubject.onNext(Unit)
                    }
                    it.second.isNotEmpty() }
                .map { return@map Pair(it.first.second, it.second) }
                .map {
                    //Perform Add to Database
                    var inList = 0
                    allSpotsSubject.value?.forEach {fromList ->
                        if (it.second.equals(fromList.spotTitle, true)){
                            inList++
                        }
                    }
                    if (inList != 0){
                        spotInListSubject.onNext(Unit)
                    }else{
                        spotNotInSubject.onNext(Unit)
                    }
                    return@map it
                }
                .zipWith(spotNotInSubject)
                .flatMap {
                    repository
                            .insertSpot(SpotEntity(it.first.second, it.first.first.first, it.first.first.second))
                }
                .share()


        val dataBaseLoad = loadFromDatabase
                .flatMap { repository.getAllWallets() }
                .share()

        allSpotsStream = dataBaseLoad.whenSuccess()

        spotIsAlreadyIncluded = spotInListSubject

        isCurrectLocationChecked = useCurrentLocationSubject
        askForLocationStream = askForLocationSubject
        askForSpotNameStream = emptyNameSubject
        emptySpotListStream = emptySpotListSubject

        shouldShowTutorialStream = Observable.just(false)

        showAllSpotsStream = showAllSpotsSubject.withLatestFrom(allSpotsStream)
                .map { it.second }

        randomSpotStream = randomSpotSubject.withLatestFrom(allSpotsStream)
                .filter{
                    emptySpotListSubject.onNext(Unit)
                    it.second.isNotEmpty()
                }
                .map { it.second[Random().nextInt(it.second.size)].spotTitle }

        newSpotAddedStream = newSpot.whenSuccess()

        errorStream = Observable
                .merge(newSpot.whenError(), dataBaseLoad.whenError())

        loadingViewModelOutput = LoadingViewModel(listOf(
                newSpot.whenLoading(), dataBaseLoad.whenLoading()
        ))

    }

    //region Input

    override fun addNewSpot() {
        newSpotSubject.onNext(Unit)
        loadFromDatabase.onNext(Unit)
    }

    override fun loadAllSpots(){
        loadFromDatabase.onNext(Unit)
    }

    override fun newSpotText(text: String) {
        newSpotNameSubject.onNext(text)
    }

    override fun showAllSpots() {
        showAllSpotsSubject.onNext(Unit)
    }

    override fun showTutorial() {

    }

    override fun useCurrentLocationIsChecked() {
        useCurrentLocationSubject.flip()
    }

    override fun startNavigationToShownSpot() {

    }

    override fun showRandomSpot() {
        loadFromDatabase
                .flatMap { repository.getAllWallets() }
                .share()
        randomSpotSubject.onNext(Unit)
    }

    override fun locationSet(latitude: String, longitude: String){
        locationSubject.onNext(Pair(latitude, longitude))
    }

    //endregion

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}