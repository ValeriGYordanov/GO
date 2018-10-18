package studios.devs.mobi.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import studios.devs.mobi.model.Spot
import studios.devs.mobi.repositories.IMainRepository
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
}

interface OfflineSpotViewModelOutput {
    val allSpotsStream: Observable<List<Spot>>
    val shouldShowTutorialStream: Observable<Boolean>
    val randomSpotStream: Observable<String>
    val newSpotAddedStream: Observable<Boolean>
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

    //endregion

    //region local
    private val compositeDisposable = CompositeDisposable()
    private val loadFromDatabase = PublishSubject.create<Unit>()
    private val randomSpotSubject = PublishSubject.create<Unit>()
    private val newSpotSubject = PublishSubject.create<Unit>()
    private val newSpotNameSubject = PublishSubject.create<String>()
    //endregion

    init {
        allSpotsStream = loadFromDatabase.map {
            listOf<Spot>()
        }

        shouldShowTutorialStream = Observable.just(false)

        randomSpotStream = randomSpotSubject
                .map { "AnyPlace" }

        newSpotAddedStream = newSpotSubject.withLatestFrom(newSpotNameSubject)
                .map {
                    return@map !it.second.isEmpty()
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

    }

    override fun startNavigationToShownSpot() {

    }

    override fun showRandomSpot() {
        randomSpotSubject.onNext(Unit)
    }

    //endregion

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}