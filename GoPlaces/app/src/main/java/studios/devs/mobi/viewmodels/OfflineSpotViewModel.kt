package studios.devs.mobi.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import studios.devs.mobi.extension.flip
import studios.devs.mobi.model.Spot
import studios.devs.mobi.repositories.IMainRepository
import java.util.*
import javax.inject.Inject


interface OfflineSpotViewModelInput {
    fun loadSpotsFromDatabase()
    fun addNewSpot()
    fun newSpotText(text: String)
    fun showAllSpots()
    fun showTutorial()
    fun useCurrentLocationIsChecked()
    fun startNavigationToShownSpot()
    fun showRandomSpot()
    fun locationSet(latitude: String, longitude: String)
}

interface OfflineSpotViewModelOutput {
    val allSpotsStream: Observable<List<Spot>>
    val shouldShowTutorialStream: Observable<Boolean>
    val randomSpotStream: Observable<String>
    val newSpotAddedStream: Observable<Boolean>
    val isCurrectLocationChecked: Observable<Boolean>
    val askForLocationStream: Observable<Unit>
    val askForSpotNameStream: Observable<Unit>
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

    override val allSpotsStream: Observable<List<Spot>>
    override val shouldShowTutorialStream: Observable<Boolean>
    override val randomSpotStream: Observable<String>
    override val newSpotAddedStream: Observable<Boolean>
    override val isCurrectLocationChecked: Observable<Boolean>
    override val askForLocationStream: Observable<Unit>
    override val askForSpotNameStream: Observable<Unit>

    //endregion

    //region local
    private val compositeDisposable = CompositeDisposable()
    private val loadFromDatabase = PublishSubject.create<Unit>()
    private val randomSpotSubject = PublishSubject.create<Unit>()
    private val newSpotSubject = PublishSubject.create<Unit>()
    private val newSpotNameSubject = BehaviorSubject.create<String>()
    private val useCurrentLocationSubject = BehaviorSubject.createDefault(false)
    private val askForLocationSubject = PublishSubject.create<Unit>()
    private val locationSubject = PublishSubject.create<Pair<String, String>>()
    private val emptyNameSubject = PublishSubject.create<Unit>()
    //endregion

    init {

        val newSpot = newSpotSubject.withLatestFrom(useCurrentLocationSubject)
                .map {
                    if (!it.second){
                        askForLocationSubject.onNext(Unit)
                    }else{
                        locationSubject.onNext(Pair("Current", "Location"))
                    }
                }
                .zipWith(locationSubject).withLatestFrom(newSpotNameSubject)
                .filter{
                    if (it.second.isEmpty()){
                        emptyNameSubject.onNext(Unit)
                    }
                    it.second.isNotEmpty() }
                .map {
                    //Perform Add to Database
                }

        allSpotsStream = loadFromDatabase.map {
            listOf<Spot>()
        }

        isCurrectLocationChecked = useCurrentLocationSubject
        askForLocationStream = askForLocationSubject
        askForSpotNameStream = emptyNameSubject

        shouldShowTutorialStream = Observable.just(false)

        randomSpotStream = randomSpotSubject.withLatestFrom(allSpotsStream)
                .map { it.second[Random().nextInt(it.second.size)].spotTitle }

        newSpotAddedStream = newSpot.map {
            false
        }

    }

    //region Input

    override fun loadSpotsFromDatabase() {
        loadFromDatabase.onNext(Unit)
    }

    override fun addNewSpot() {
        newSpotSubject.onNext(Unit)
    }

    override fun newSpotText(text: String) {
        newSpotNameSubject.onNext(text)
    }

    override fun showAllSpots() {

    }

    override fun showTutorial() {

    }

    override fun useCurrentLocationIsChecked() {
        useCurrentLocationSubject.flip()
    }

    override fun startNavigationToShownSpot() {

    }

    override fun showRandomSpot() {
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