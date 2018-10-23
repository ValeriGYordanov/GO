package studios.devs.mobi.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import studios.devs.mobi.model.Screen
import studios.devs.mobi.repositories.IMainRepository
import java.io.Serializable
import javax.inject.Inject


interface OnboardingViewModelInput {
    fun goOffline()
    fun permissionsGranted()
}

interface OnboardingViewModelOutput {
    val startOfflineScreenStream: Observable<Screen<out Serializable>>
    val permissionsGrantedStream: Observable<Unit>
}

interface OnboardingViewModelInputOutput {
    val input: OnboardingViewModelInput
    val output: OnboardingViewModelOutput
}

class OnboardingViewModel @Inject constructor(private val repository: IMainRepository) : ViewModel(),
        OnboardingViewModelInput,
        OnboardingViewModelOutput,
        OnboardingViewModelInputOutput {

    //region CONST Exposed Input and Output
    override val input: OnboardingViewModelInput
        get() = this
    override val output: OnboardingViewModelOutput
        get() = this
    //endregion

    //region output

    override val startOfflineScreenStream: Observable<Screen<out Serializable>>
    override val permissionsGrantedStream: Observable<Unit>

    //endregion

    //region local
    private val compositeDisposable = CompositeDisposable()
    private val offlineSubject = PublishSubject.create<Unit>()
    private val permisionsGrantedSubject = PublishSubject.create<Unit>()
    //endregion

    init {
        startOfflineScreenStream = offlineSubject
                .map { Screen.OfflineSpot(null) }

        permissionsGrantedStream = permisionsGrantedSubject
    }

    //region Input

    override fun goOffline() {
        offlineSubject.onNext(Unit)
    }

    override fun permissionsGranted() {
        permisionsGrantedSubject.onNext(Unit)
    }

    //endregion

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}