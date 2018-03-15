package studios.devs.mobi.extension

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun List<Disposable>.addTo(compositeDisposable: CompositeDisposable) {
    this.forEach { compositeDisposable.add(it) }
}

fun <R> Observable<R>.debug(tag: String): Observable<R> {

    return doOnSubscribe {
        Log.d(tag, "Subscribed")
    }.doOnNext {
        Log.d(tag, "On Next")
    }
        .doOnComplete {
            Log.d(tag, "Completed")
        }
        .doOnError {
            Log.d(tag, "Error")
        }
        .doOnDispose {
            Log.d(tag, "Disposed")
        }
}