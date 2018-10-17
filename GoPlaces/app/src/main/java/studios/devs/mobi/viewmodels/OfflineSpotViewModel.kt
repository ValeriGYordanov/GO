package studios.devs.mobi.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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
    val allSpots: Observable<List<Spot>>
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

    override val allSpots: Observable<List<Spot>>

    //endregion

    //region local
    private val compositeDisposable = CompositeDisposable()
    private val loadFromDatabase = PublishSubject.create<Unit>()
    //endregion

    init {
        allSpots = loadFromDatabase.map {
            listOf<Spot>()
        }
    }

    //region Input

    override fun loadSpotsFromDatabase() {
        loadFromDatabase.onNext(Unit)
    }

    override fun addNewSpot() {

    }

    override fun newSpotText(text: String) {

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

    }

    //endregion

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}