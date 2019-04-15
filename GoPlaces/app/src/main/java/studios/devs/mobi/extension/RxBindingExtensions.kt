package studios.devs.mobi.extension

import android.view.View
import android.widget.Adapter
import android.widget.EditText
import android.widget.GridView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

var View.rxClick: Observable<Unit>
    get() = RxView.clicks(this)
            .throttleFirst(2, TimeUnit.SECONDS)
            .map { Unit }
            .observeOn(AndroidSchedulers.mainThread())
    set(value) {}

var EditText.rxTextChanges: Observable<String>
    get() = RxTextView.textChanges(this)
            .map { it.toString() }
    set(value) {}


var GridView.rxItemClick: Observable<Pair<Adapter, Int>>
    get() = RxAdapterView.itemClicks(this).map { Pair(this.adapter, it) }
    set(value) {}
