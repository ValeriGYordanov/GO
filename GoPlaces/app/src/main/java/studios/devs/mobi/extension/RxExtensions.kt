package studios.devs.mobi.extension
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import studios.devs.mobi.model.result.IResultError
import studios.devs.mobi.model.result.Result
import studios.devs.mobi.model.result.ResultErrorException

public fun <T : Any> Observable<T?>.filterNotNull(): Observable<T> = filter { it != null }.map { it as T }

public fun <T : Any> Observable<T>.filterError(): Observable<T> = materialize().filter{ !it.isOnError }.dematerialize()

fun <T> Observable<Result<T>>.whenSuccess(stopAndThrowOnError: Boolean = false): Observable<T> =
        doOnNext {
            if (stopAndThrowOnError && it is Result.Error) {
                throw ResultErrorException(it.error)
            }
        }
                .filter { it -> it is Result.Success }
                .map { it -> it as Result.Success<T> }
                .map { it -> it.data }

fun <T> Observable<Result<T>>.whenError(): Observable<IResultError> =
        filter { it -> it is Result.Error }
                .map { it -> it as Result.Error<T> }
                .map { it -> it.error }


fun <T> Observable<Result<T>>.whenLoading(): Observable<Boolean> =
        map { it -> it is Result.Loading<T> }

fun BehaviorSubject<Boolean>.flip() {
    onNext(
            if (this.value != null) {
                !this.value!!
            } else {
                true
            })
}

fun <T> Observable<Result<T>>.relayError(): Observable<Result<T>> {
    return this.onErrorReturn {
        Result.Error((it as ResultErrorException).error)
    }
}
