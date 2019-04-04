package studios.devs.mobi.viewmodels.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

interface LoadingViewModelInput {

}

interface LoadingViewModelOutput {
    val isLoading: Observable<Boolean>
}

interface LoadingViewModelInputOutput {
    val input: LoadingViewModelInput
    val output: LoadingViewModelOutput

}
//endregion

class LoadingViewModel constructor(observableList: List<Observable<Boolean>>, val scheduler: Scheduler = AndroidSchedulers.mainThread()) : ViewModel(),
        LoadingViewModelInput,
        LoadingViewModelOutput,
        LoadingViewModelInputOutput {

//region Input Output

    override val input = this
    override val output = this

//endregion

//region Subjects consuming from Input

//endregion

    //region output
    // Loading observable that has only two "next" events. true for show indicator and false to hide indicator.
    override val isLoading: Observable<Boolean>


//endregion

//region Locally used

    private val compositeDisposable = CompositeDisposable()

    /// Is loading event count
    private val isLoadingCount = BehaviorSubject.createDefault(0)

    /// Is Not loading event count
    private val isNotLoadingCount = BehaviorSubject.createDefault(0)

//endregion

    init {

        val isLoadingObservable = Observable.merge(observableList)

        isLoadingObservable
                .bind(isLoadingCount, true)
                .addTo(compositeDisposable)

        isLoadingObservable
                .delay(10, TimeUnit.MILLISECONDS, scheduler)
                .bind(isNotLoadingCount, false)
                .addTo(compositeDisposable)


        isLoading = isLoadingCount
                .and(isNotLoadingCount)
                .doOnDispose { isNotLoadingCount.increment() }
                .filter { !(it.first == 0 && it.second == 0) }
                .map { it.first > it.second }
                .distinctUntilChanged()
                .observeOn(scheduler)
                .share()


    }


//region Input methods
// endregion


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

private fun BehaviorSubject<Int>.increment() {
    this.value?.let { this.onNext(it+1) }
}

private fun Observable<Boolean>.bind(loadingCount: BehaviorSubject<Int>, isLoading: Boolean): Disposable {
    return filter { it == isLoading }
            .map {
                loadingCount.value?.plus(1)
            }.subscribe { result ->
                result?.let {
                    loadingCount.onNext(it)
                }
            }
}

fun <T, K> BehaviorSubject<T>.and(subject: BehaviorSubject<K>): Observable<Pair<T, K>> =
        Observable.combineLatest(this, subject, BiFunction { t1, t2 -> Pair(t1, t2) })
